//
//  EHIMenuViewModel.m
//  Enterprise
//
//  Created by Pawel Bragoszewski on 23.03.2015.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIMenuViewModel.h"
#import "EHIMenuAnimationProgress.h"
#import "EHIUserManager.h"
#import "EHIConfiguration.h"
#import "EHIDealsConfiguration.h"

@interface EHIMenuViewModel () <EHIUserListener, EHIMenuAnimationProgressListener>
@property (assign, nonatomic) BOOL isVisible;
@property (assign, nonatomic) BOOL selectedSameIndex;
@property (strong, nonatomic) NSArray *menuItems;
@end

@implementation EHIMenuViewModel

- (instancetype)init
{
    if(self = [super init]) {
        [self rebuildMenuItems];
        
        [[EHIUserManager sharedInstance] addListener:self];
        [[EHIMenuAnimationProgress sharedInstance] addListener:self];
        
        void (^readinessHandler)(BOOL) = ^(BOOL isReady) {
            if(isReady) {
                [self rebuildMenuItems];
            }
        };
        
        [[EHIConfiguration configuration] onReady:readinessHandler];
        [[EHIDealsConfiguration configuration] onReady:readinessHandler];
    }
    
    return self;
}

# pragma mark - Lifecycle

- (void)didBecomeActive
{
    [super didBecomeActive];
    
    // observe countries endpoint to make sure that we are always up to date with the weekend specials object
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(rebuildMenuItems) name:EHICountriesRefreshedNotification object:nil];
}

- (void)didResignActive
{
    [super didResignActive];
    
    [[NSNotificationCenter defaultCenter] removeObserver:self name:EHICountriesRefreshedNotification object:nil];
}

- (void)rebuildMenuItems
{
    NSMutableIndexSet *filterItems = [NSMutableIndexSet new];
    
    [filterItems addIndex:[EHIUser currentUser] ? EHIMenuItemRowSignIn : EHIMenuItemRowSignout];
    
    BOOL isLogged = [EHIUser currentUser] != nil;
    if(isLogged) {
        [filterItems addIndex:EHIMenuItemRowSignIn];
        [filterItems addIndex:EHIMenuItemRowRentalLookUp];
    } else {
        [filterItems addIndex:EHIMenuItemRowSignout];
        [filterItems addIndex:EHIMenuItemRowRentals];
    }
    
    // hide profile if unauthenticated or EC user
    BOOL hideProfile = ![EHIUser currentUser] || [EHIUserManager sharedInstance].isEmeraldUser;
    if(hideProfile) {
        [filterItems addIndex:EHIMenuItemRowProfile];
    }
    
    BOOL hideWeekendSpecial = [NSLocale ehi_country].weekendSpecial == nil;
    if(hideWeekendSpecial) {
        [filterItems addIndex:EHIMenuItemRowPromotion];
    }
    
    BOOL hideFeedbackMenu = [NSLocale ehi_hideFeedbackMenu];
    if (hideFeedbackMenu) {
        [filterItems addIndex:EHIMenuItemRowFeedback];
    }
    
    BOOL hideDeals = ![EHIDealsConfiguration configuration].enabled;
    if(hideDeals) {
        [filterItems addIndex:EHIMenuItemRowDeals];
    }
    
    // hide debug elements in production build!
    #if !(defined(DEBUG) || defined(UAT))
    [filterItems addIndex:EHIMenuItemRowDebug];
    #endif
     
    self.menuItems = [EHIMenuItem items].select(^(EHIMenuItem *menuItem) {
        return ![filterItems containsIndex:menuItem.row];
    });
    
    [self rebuildHeaders];
}

- (void)rebuildHeaders
{
    [[self.groupedMenuItems[@(EHIMenuItemHeaderNone)] lastObject] setHideDivider:YES];
    
    [self setupHeaderForSection:EHIMenuItemHeaderEnterprisePlus];
    [self setupHeaderForSection:EHIMenuItemHeaderReservation];
    [self setupHeaderForSection:EHIMenuItemHeaderSupport];
}

- (void)setupHeaderForSection:(EHIMenuItemHeader)section
{
    [[self.groupedMenuItems[@(section)] firstObject] setShowHeader:YES];
    [[self.groupedMenuItems[@(section)] lastObject] setHideDivider:YES];
}

- (NSDictionary *)groupedMenuItems
{
    return (self.menuItems ?: @[]).groupBy(^(EHIMenuItem *menuItem) {
        return @(menuItem.header);
    });
}

# pragma mark - Actions

- (BOOL)shouldSelectItemAtIndex:(NSInteger)index
{
    // ensure this item has a valid transition to run
    EHIMenuItem *item = [self.menuItems ehi_safelyAccess:index];
    return item.action != nil || item.transition != nil;
}

- (void)selectItemAtIndexPath:(NSIndexPath *)indexPath
{
    EHIMenuItem *item = [self.menuItems ehi_safelyAccess:indexPath.row];
    
    // fire the appropriate tracking call if possible
    if(item.analyticsAction) {
        [EHIAnalytics trackAction:item.analyticsAction handler:nil];
    }
    
    BOOL aHackToForceDashboardToTrack = _highlightedIndexPath == nil && indexPath.row == EHIMenuItemRowHome;
    self.selectedSameIndex = aHackToForceDashboardToTrack || [_highlightedIndexPath isEqual:indexPath];
    
    // if we don't have an action, but do have a transition, then run the transition 
    if(!item.action && item.transition) {
        [self transitionForMenuItem:item];
    }
    // otherwise, run that the action to see if we should transition when it completes
    else if(item.action) {
        item.action(^(BOOL success) {
            if(success) {
                [self transitionForMenuItem:item];
            }
        });
    }
    
    _highlightedIndexPath = indexPath;
}

- (void)transitionForMenuItem:(EHIMenuItem *)item
{
    // during a transition, if we made an analytics tracking call pre-emptively clear this flag
    // so that the "MenuHide" action doesn't fire
    if(item.analyticsAction) {
        self.isVisible = NO;
    }
    
    NAVTransitionBuilder *transition = self.dismissMenuTransition;
    ehi_call(item.transition)(transition);
    
    // run the combined transition
    transition.start(nil);
}

//
// Helpers
//

- (NAVTransitionBuilder *)dismissMenuTransition
{
    return self.router.transition
        .animateWithOptions(EHIScreenMenu, NAVAnimationOptionsHidden | NAVAnimationOptionsAsync);
}

- (void)didTapPromotionGetStarted
{
    [EHIAnalytics trackAction:EHIAnalyticsMenuActionWkndSpecial handler:nil];
    
    __weak __typeof(self) welf = self;
    self.router.transition.root(EHIScreenDashboard).animated(NO).start(^{
        NAVTransitionBuilder *transition = welf.dismissMenuTransition;
        transition.push(EHIScreenPromotionDetails).start(nil);
    });
}

# pragma mark - EHIUserListener

- (void)manager:(EHIUserManager *)manager didChangeAuthenticationForUser:(EHIUser *)user
{
    if(user) {
        // update the highlighted indexPath if user is logging from learn more screen
        // if we dont do that, the user will see `My Rentals` highlighted in the menu
        NSInteger currentRow = self.highlightedIndexPath.row;
        if(currentRow == EHIMenuItemRowRentals) {
            NSIndexPath *authIndexPath = [NSIndexPath indexPathForRow:currentRow + 1 inSection:0];
            _highlightedIndexPath = authIndexPath;
        }
    }
    
    [self rebuildMenuItems];
}

# pragma mark - EHIMenuAnimationProgressListener

- (void)menuAnimationDidFinishAnimating:(EHIMenuAnimationProgress *)progress
{
    self.isVisible = progress.percentComplete == 1.0f;
}

# pragma mark - MTRReactive

+ (NSArray *)nonreactiveProperties:(EHIMenuViewModel *)object
{
    return @[
        @key(object.isVisible),
    ];
}

@end
