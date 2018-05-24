//
//  EHIRentalsViewModel.m
//  Enterprise
//
//  Created by fhu on 4/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIRentalsViewModel.h"
#import "EHIRentalsFooterViewModel.h"
#import "EHIRentalsFallbackViewModel.h"
#import "EHIConfirmationViewModel.h"
#import "EHIUserManager+Analytics.h"
#import "EHIInvoiceViewModel.h"

@interface EHIRentalsViewModel () <EHIUserListener>
@property (assign, nonatomic) EHIRentalsMode mode;
@property (assign, nonatomic) BOOL isLoading;
@property (assign, nonatomic) BOOL shouldHideUnauth;
@property (copy  , nonatomic) NSArray *pastRentals;
@property (copy  , nonatomic) NSArray *upcomingRentals;
@property (copy  , nonatomic) NSString *currentRentalText;
@property (copy  , nonatomic) NSArray *segmentedControlItems;
@property (strong, nonatomic) EHIRentalsFallbackViewModel *fallbackViewModel;
@property (strong, nonatomic) EHIRentalsFooterViewModel *footerViewModel;
@property (strong, nonatomic) EHIModel *pagingModel;
@end

@implementation EHIRentalsViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _title = EHILocalizedString(@"rentals_navigation_title", @"My Rentals", @"navigation bar title for My Rentals page");
        _currentRentalText = EHILocalizedString(@"current_rentals_cell_header", @"CURRENT RENTALS", @"Section header for current rentals headers in My Rentals");
        _segmentedControlItems = @[
            EHILocalizedString(@"rentals_segment_upcoming_title", @"UPCOMING RENTALS", @"my rentals upcoming rentals tab title"),
            EHILocalizedString(@"rentals_segment_past_title", @"PAST RENTALS", @"my rentals past rentals tab title"),
        ];
    }
    
    return self;
}

- (void)didBecomeActive
{
    [super didBecomeActive];
    
    // show/hide the unauth view when signin changes 
    [[EHIUserManager sharedInstance] addListener:self];
    // listen to application lifecycle
    [self registerForApplicationNotifications:YES];
}

- (void)didResignActive
{
    [super didResignActive];
   
    // stop listening to application lifecycle
    [self registerForApplicationNotifications:NO];
}

# pragma mark - Invalidation

- (void)invalidateVisibleRentals
{
    EHIUser *user = [EHIUser currentUser];
    
    if(self.mode == EHIRentalsModeUpcoming) {
        // join current rentals to upcoming rentals, and sort upcoming by date
        self.upcomingRentals   = @[].concat(user.currentRentals.all).concat(user.upcomingRentals.all);
        self.pagingModel       = user.upcomingRentals.hasMoreAvailable ? [EHIModel placeholder] : nil;
        
        // append lookup rental button if needed, otherwise show the fallback
        if(self.upcomingRentals.count) {
            self.footerViewModel = [EHIRentalsFooterViewModel viewModelWithMode:self.mode];
        } else {
            self.fallbackViewModel = [[EHIRentalsFallbackViewModel alloc] initWithMode:EHIRentalsModeUpcoming];
        }
    }
    else {
        self.pastRentals = user.pastRentals.all;
        
        // append lookup rental button if needed, otherwise show the fallback
        if(self.pastRentals.count) {
            self.footerViewModel = [EHIRentalsFooterViewModel viewModelWithMode:self.mode];
        } else {
            self.fallbackViewModel = [[EHIRentalsFallbackViewModel alloc] initWithMode:EHIRentalsModePast];
        }
    }
}

- (BOOL)isEmeraldClubUser
{
    return [EHIUserManager sharedInstance].isEmeraldUser;
}

//
// Helpers
//

- (void)resetRentals
{
    // destroy old data
    self.pastRentals       = nil;
    self.upcomingRentals   = nil;
    self.pagingModel       = nil;
    self.fallbackViewModel = nil;
    self.footerViewModel   = [EHIRentalsFooterViewModel viewModelWithMode:self.mode];
}

- (void)refreshRentals
{
    BOOL skipRefresh = self.isLoading || ![EHIUser currentUser];
    if(skipRefresh) {
        return;
    }
    
    BOOL isNonLoyalty = !EHIUser.currentUser.loyaltyNumber;
    if (isNonLoyalty){
        [self setIsLoading:NO];
        [self resetRentals];
        [self invalidateVisibleRentals];
        return;
    }
   
    // throw away the old rentals
    [self resetRentals];
   
    // prepare to fetch rentals
    [self setIsLoading:YES];

    dispatch_group_t group = dispatch_group_create();
    
    dispatch_group_enter(group);
    [[EHIUserManager sharedInstance] refreshPastRentalsWithHandler:^(EHIUser *user, EHIServicesError *error) {
        dispatch_group_leave(group);
    }];
    
    dispatch_group_enter(group);
    [[EHIUserManager sharedInstance] refreshCurrentAndUpcomingRentalsWithHandler:^(EHIUser *user, EHIServicesError *error) {
        dispatch_group_leave(group);
    }];
    
    dispatch_group_notify(group, dispatch_get_main_queue(), ^{
        [self setIsLoading:NO];
        [self resetRentals];
        [self invalidateVisibleRentals];
    });
}

# pragma mark - Actions

- (void)switchToMode:(EHIRentalsMode)mode
{
    [self setMode:mode];
    
    [self resetRentals];
    [self invalidateVisibleRentals];
}

- (BOOL)shouldSelectRentalAtIndexPath:(NSIndexPath *)indexPath
{
    return (indexPath.section == EHIRentalsSectionUpcomingRental && ![self rentalAtIndexPath:indexPath].isCurrent)
    || indexPath.section == EHIRentalsSectionPastRental;
}

- (void)selectRentalAtIndexPath:(NSIndexPath *)indexPath
{
    EHIUserRental *userRental = [self rentalAtIndexPath:indexPath];
 
    if(indexPath.section == EHIRentalsSectionPastRental) {
        EHIInvoiceViewModel *model = [[EHIInvoiceViewModel alloc] initFetchingRental:userRental];
        self.router.transition.present(EHIScreenInvoiceDetails).object(model).start(nil);
        return;
    }
    
    [EHIAnalytics trackAction:EHIAnalyticsRentalsActionReceipt handler:nil];
    
    self.router.transition
        .present(EHIScreenReservation).object(userRental).start(nil);
}

# pragma mark - EHIUserListener

- (void)manager:(EHIUserManager *)manager didChangeAuthenticationForUser:(EHIUser *)user
{
    self.shouldHideUnauth = user != nil;
    
    [self refreshRentals];
}

# pragma mark - Notifications

- (void)registerForApplicationNotifications:(BOOL)shouldRegister
{
    if(shouldRegister) {
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didBecomeActive:) name:UIApplicationDidBecomeActiveNotification object:nil];
    } else {
        [[NSNotificationCenter defaultCenter] removeObserver:self name:UIApplicationDidBecomeActiveNotification object:nil];
    }
}

- (void)didBecomeActive:(NSNotification *)notification
{
    [self refreshRentals];
}

# pragma mark - Accessors

- (EHIUserRental *)rentalAtIndexPath:(NSIndexPath *)indexPath
{
    switch((EHIRentalsSection)indexPath.section) {
        case EHIRentalsSectionUpcomingRental:
            return self.upcomingRentals[indexPath.item];
        case EHIRentalsSectionPastRental:
            return self.pastRentals[indexPath.item];
        default: return nil;
    }
}

# pragma mark - Analytics

- (void)updateAnalyticsContext:(EHIAnalyticsContext *)context
{
    [super updateAnalyticsContext:context];
    // encode the "sign-in" dictionary"
    [[EHIUserManager sharedInstance] updateAnalyticsContext:context];
}

@end
