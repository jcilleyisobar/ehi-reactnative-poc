//
//  EHIDashboardViewController.m
//  Enterprise
//
//  Created by Ty Cobb on 1/9/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIDashboardViewController.h"
#import "EHIDashboardViewModel.h"
#import "EHIDashboardLoyaltyHeader.h"
#import "EHIDashboardHeroImageCell.h"
#import "EHIDashboardSearchCell.h"
#import "EHIDashboardQuickstartCell.h"
#import "EHIDashboardHistoryCell.h"
#import "EHIDashboardLoyaltyPromptCell.h"
#import "EHIDashboardLoadingCell.h"
#import "EHIDashboardActiveRentalCell.h"
#import "EHIDashboardUpcomingRentalCell.h"
#import "EHIDashboardNewFeatureCell.h"
#import "EHIDashboardLayoutDelegate.h"
#import "EHILocationsViewController.h"
#import "EHIListCollectionView.h"
#import "EHICollectionTitleView.h"
#import "EHIBarButtonItem.h"
#import "EHILocationManager.h"
#import "EHIUserManager.h"
#import "EHISettings.h"
#import "EHIDashboardPromotionCell.h"
#import "EHIPromotionView.h"

#define EHIDashboardParallaxThrottle 0.3f

@interface EHIDashboardViewController () <EHIDashboardLayoutDelegate, EHIDashboardNewFeatureCellActions, EHIDashboardSearchActions, EHIPromotionViewActions, EHIListCollectionViewDelegate, EHIUserListener>
@property (strong, nonatomic) EHIDashboardViewModel *viewModel;
@property (assign, nonatomic) BOOL willRefreshRentals;
@property (weak  , nonatomic) EHIDashboardSearchCell *searchCell;
@property (strong, nonatomic) UIGestureRecognizer *quickstartHeaderGesture;
@property (strong, nonatomic) UIGestureRecognizer *quickstartFooterGesture;
@property (weak  , nonatomic) IBOutlet EHIListCollectionView *collectionView;
@property (assign, nonatomic) BOOL wasHidenOnce;
@end

@implementation EHIDashboardViewController

- (instancetype)initWithCoder:(NSCoder *)decoder
{
    if(self = [super initWithCoder:decoder]) {
        self.viewModel = [EHIDashboardViewModel new];
    }
    _wasHidenOnce = NO;
    
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [self configureCollectionViewSections];
    
     // allow snapping behavior to work nicely
    self.collectionView.decelerationRate = UIScrollViewDecelerationRateFast;
    // register as a user listener to update your background color
    [[EHIUserManager sharedInstance] addListener:self];
   
    // create the gesture for the quickstart header/footer
    self.quickstartHeaderGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(didTapQuickstartHeader:)];
    self.quickstartFooterGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(didTapQuickstartFooter:)];
}

- (void)viewDidLayoutSubviews
{
    [super viewDidLayoutSubviews];
   
    // update the hero metrics
    self.collectionView.sections[EHIDashboardSectionHero].metrics = [self heroMetrics];
}

- (void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];

    _wasHidenOnce = YES;
}

- (void)configureCollectionViewSections
{
    self.collectionView.refreshControlSection = EHIDashboardSectionRefresh;
    
    [self.collectionView.sections construct:@{
        @(EHIDashboardSectionHeader)        : [EHIDashboardLoyaltyHeader class],
        @(EHIDashboardSectionHero)          : [EHIDashboardHeroImageCell class],
        @(EHIDashboardSectionSearch)        : [EHIDashboardSearchCell class],
        @(EHIDashboardSectionPromotion)     : [EHIDashboardPromotionCell class],
        @(EHIDashboardSectionLoyaltyPrompt) : [EHIDashboardLoyaltyPromptCell class],
    }];

    // add placeholder models for fixed sections
    self.collectionView.sections[EHIDashboardSectionHeader].model = [EHIModel placeholder];
    self.collectionView.sections[EHIDashboardSectionSearch].model = [EHIModel placeholder];
   
    // add dynamic sizing to the appropriate sections
    self.collectionView.sections[EHIDashboardSectionContent].isDynamicallySized       = YES;
    self.collectionView.sections[EHIDashboardSectionLoyaltyPrompt].isDynamicallySized = YES;
    
    self.collectionView.sections[EHIDashboardSectionContent].klass = [self contentClassForType:self.viewModel.contentType];
    
    // configure the quickstart section
    EHIListDataSourceSection *quickstart = self.collectionView.sections[EHIDashboardSectionQuickstart];
    quickstart.isDynamicallySized = YES;
   
    quickstart.header.klass   = EHICollectionTitleView.class;
    quickstart.header.metrics = self.quickstartHeaderMetrics;
    quickstart.header.model   = self.viewModel.quickstartTitle;
    
    quickstart.footer.klass   = EHICollectionTitleView.class;
    quickstart.footer.metrics = self.quickstartFooterMetrics;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIDashboardViewModel *)model
{
    [super registerReactions:model];

    [MTRReactor autorun:self action:@selector(invalidateContentSection:)];
    [MTRReactor autorun:self action:@selector(invalidateQuickstartSection:)];
   
    EHIRefreshControlViewModel *refreshControl = self.collectionView.ehiRefreshControl;
    EHIListDataSourceSection   *loyaltyPrompt  = self.collectionView.sections[EHIDashboardSectionLoyaltyPrompt];
    EHIListDataSourceSection   *promotion      = self.collectionView.sections[EHIDashboardSectionPromotion];
    
    model.bind.map(@{
        source(model.loyaltyPromptModel) : dest(loyaltyPrompt,  .model),
        source(model.promotionModel)     : dest(promotion, .model),
        source(model.isLoading)          : dest(refreshControl, .isRefreshing),
    });
}

- (void)invalidateContentSection:(MTRComputation *)compuation
{
    // udpate the cell type of the content section
    self.collectionView.sections[EHIDashboardSectionContent].klass = [self contentClassForType:self.viewModel.contentType];
    
    // capture the models for reactivity
    EHIModel *heroModel    = self.viewModel.heroImageModel;
    EHIModel *contentModel = self.viewModel.contentModel;
  
    [self.collectionView performAnimated:NO batchUpdates:^{
        // animate changes to the hero / content section
        self.collectionView.sections[EHIDashboardSectionHero].model    = heroModel;
        self.collectionView.sections[EHIDashboardSectionContent].model = contentModel;
    } completion:nil];
}

- (void)invalidateQuickstartSection:(MTRComputation *)computation
{
    EHIListDataSourceSection *quickstart = self.collectionView.sections[EHIDashboardSectionQuickstart];

    // dynamically update the class based on whether we have anything to show
    quickstart.klass = self.viewModel.showsHistoryFallback ? [EHIDashboardHistoryCell class] : [EHIDashboardQuickstartCell class];
    // and bind whatever models we have
    quickstart.models = self.viewModel.quickstartModels;
    // update the title for the clear activity accessory
    quickstart.footer.model = self.viewModel.clearActivityTitle;
}

//
// Helpers
//

- (Class<EHIListCell>)contentClassForType:(EHIContentSectionType)type
{
    switch(type) {
        case EHIContentSectionTypeNone:
        case EHIContentSectionTypeLoading:
            return [EHIDashboardLoadingCell class];
        case EHIContentSectionTypeCurrent:
            return [EHIDashboardActiveRentalCell class];
        case EHIContentSectionTypeUpcoming:
            return [EHIDashboardUpcomingRentalCell class];
        case EHIContentSectionTypeNotifications:
            return [EHIDashboardNewFeatureCell class];
    }
}

# pragma mark - EHIViewController

- (BOOL)showsPhoneButton
{
    return YES;
}

# pragma mark - Layout

- (EHILayoutMetrics *)heroMetrics
{
    CGFloat loyaltyHeight    = [EHIDashboardLoyaltyHeader metrics].fixedSize.height;
    CGFloat searchHeight     = [EHIDashboardSearchCell metrics].fixedSize.height;
    CGFloat quickstartHeight = self.collectionView.sections[EHIDashboardSectionQuickstart].header.metrics.fixedSize.height;
    
    EHILayoutMetrics *metrics = [EHIDashboardHeroImageCell.metrics copy];
    metrics.fixedSize = (CGSize){
        .width  = EHILayoutValueNil,
        .height = self.collectionView.bounds.size.height - loyaltyHeight - searchHeight - quickstartHeight
    };
    
    return metrics;
}

- (EHILayoutMetrics *)quickstartHeaderMetrics
{
    EHILayoutMetrics *metrics = [self quickstartAccessoryMetrics];
    metrics.fixedSize = (CGSize){ .width = EHILayoutValueNil, .height = 88.0f };
    return metrics;
}

- (EHILayoutMetrics *)quickstartFooterMetrics
{
    EHILayoutMetrics *metrics = [self quickstartAccessoryMetrics];
    metrics.fixedSize = (CGSize){ .width = EHILayoutValueNil, .height = 68.0f };
    return metrics;
}

- (EHILayoutMetrics *)quickstartAccessoryMetrics
{
    EHILayoutMetrics *metrics = [EHICollectionTitleView.metrics copy];
   
    metrics.primaryColor    = [UIColor ehi_greenColor];
    metrics.backgroundColor = [UIColor ehi_grayColor0];
    metrics.primaryFont     = [UIFont ehi_fontWithStyle:EHIFontStyleBold size:18.0f];
    
    return metrics;
}

# pragma mark - UICollectionViewDelegate

- (BOOL)collectionView:(UICollectionView *)collectionView shouldSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    return [self.viewModel shouldSelectItemAtIndexPath:indexPath];
}

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    [self.viewModel selectItemAtIndexPath:indexPath];
}

//
// EHIListCollectionViewDelegate
//

- (void)collectionView:(UICollectionView *)collectionView didDequeueCell:(EHICollectionViewCell *)cell atIndexPath:(NSIndexPath *)indexPath
{
    if(indexPath.section == EHIDashboardSectionSearch) {
        cell.layer.zPosition = 1000;
        self.searchCell = (EHIDashboardSearchCell *)cell;
    }
}

- (void)collectionView:(UICollectionView *)collectionView didDequeueReusableView:(EHICollectionReusableView *)reusableView kind:(NSString *)kind atIndexPath:(NSIndexPath *)indexPath
{
    // we only want to attach gestures to the quickstart header/footer
    if(indexPath.section == EHIDashboardSectionQuickstart) {
        UIGestureRecognizer *gesture = kind == UICollectionElementKindSectionHeader ? self.quickstartHeaderGesture : self.quickstartFooterGesture;
        // if this gesture is not already attached to the view, then attach it
        if(gesture.view != reusableView) {
            [gesture.view removeGestureRecognizer:gesture];
            [reusableView addGestureRecognizer:gesture];
        }
    }
}

- (void)collectionViewDidPullToRefresh:(UICollectionView *)collectionView
{
    self.willRefreshRentals = YES;
}

# pragma mark - EHIDashboardLayoutDelegate

- (BOOL)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)layout shouldSnapWithProposedOffset:(CGPoint)offset
{
    return self.viewModel.contentType != EHIContentSectionTypeCurrent
        && self.viewModel.contentType != EHIContentSectionTypeUpcoming
        && self.viewModel.contentType != EHIContentSectionTypeNotifications;
}

- (void)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)layout willSnapToOffset:(CGPoint)point
{
    collectionView.decelerationRate = UIScrollViewDecelerationRateFast;
}

# pragma mark - UIScrollViewDelegate

- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate
{
    if(!decelerate) {
        [self scrollViewDidFinishScrolling:scrollView];
    }
}

- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView
{
    [self scrollViewDidFinishScrolling:scrollView];
}

//
// Helpers
//

- (void)scrollViewDidFinishScrolling:(UIScrollView *)scrollView
{
    scrollView.decelerationRate = UIScrollViewDecelerationRateNormal;

    // refresh rentals once scrolling stops
    if(self.willRefreshRentals) {
        [self.viewModel refreshRentals];
        [self setWillRefreshRentals:NO];
    }
    
    [self invalidateRefreshDisabled];
}

# pragma mark - EHIUserListener

- (void)manager:(EHIUserManager *)manager didChangeAuthenticationForUser:(EHIUser *)user
{
    [self setBackgroundColor:[self backgroundColorForUser:user]];
    [self invalidateRefreshDisabled];
}

- (UIColor *)backgroundColorForUser:(EHIUser *)user
{
    BOOL isEmerald = [[EHIUserManager sharedInstance] isEmeraldUser];
    return user && !isEmerald ? [UIColor whiteColor] : [UIColor colorWithPatternImage:[UIImage imageNamed:@"eplus_tilepattern"]];
}

# pragma mark - EHIDashboardNewFeatureCellActions

- (void)didTapAcceptButtonForDashboardNewFeatureCell:(EHIDashboardNewFeatureCell *)sender
{
    [self.viewModel acceptNotifications];
}

- (void)didTapDenyButtonForDashboardNewFeatureCell:(EHIDashboardNewFeatureCell *)sender
{
    [self.viewModel denyNotifications];
}

- (void)didTapCloseButtonForDashboardNewFeatureCell:(EHIDashboardNewFeatureCell *)sender
{
    [self.viewModel denyNotifications];
}

# pragma mark - Actions

- (void)didTapQuickstartHeader:(UIGestureRecognizer *)gesture
{
    [self scrollToSearchCell];
}

- (void)didTapQuickstartFooter:(UIGestureRecognizer *)gesture
{
    [self.viewModel clearQuickstart];
}

- (void)searchCellDidTapScrollButton:(EHIDashboardSearchCell *)searchCell
{
    [self scrollToSearchCell];
}

- (void)didTapPromotionGetStarted
{
    [self.viewModel pushPromotionDetails];
}

//
// Helpers
//

- (void)scrollToSearchCell
{
    NSIndexPath *indexPath = [NSIndexPath indexPathForItem:0 inSection:EHIDashboardSectionSearch];
    [self.collectionView scrollToItemAtIndexPath:indexPath atScrollPosition:UICollectionViewScrollPositionTop animated:YES];
}

# pragma mark - Refreshing

- (void)invalidateRefreshDisabled
{
    self.collectionView.ehiRefreshControl.isDisabled = !EHIUser.currentUser || self.collectionView.contentOffset.y != 0.0f;
}

# pragma mark - Transitions

- (BOOL)executesCustomAnimationsForTransitionToViewController:(EHIViewController *)controller isEntering:(BOOL)isEntering
{
    return [controller executesCustomAnimationsForTransitionToViewController:self isEntering:!isEntering];
}

- (NSArray *)animationsForTransitionToViewController:(EHIViewController *)controller isEntering:(BOOL)isEntering
{
    NSArray *animations = [super animationsForTransitionToViewController:controller isEntering:isEntering];
    
    // pull out the navigation bar we're animating the search view to
    UIView *navigationBar = self.navigationController.navigationBar;
    
    // calculate frames in the window space for various views we're animating
    CGRect barFrame   = [navigationBar convertRect:navigationBar.bounds toView:navigationBar.window];
    CGRect titleFrame = [self.searchCell.titleLabel convertRect:self.searchCell.titleLabel.bounds toView:navigationBar.window];

    // build up our animation sequence
    animations = animations.concat(@[
        EHINavigationAnimation.target(self.view)
            .alpha(1.0f)
            .duration(0.35),

        EHINavigationAnimation.proxy(self.searchCell.searchField)
            .reverseFrame(CGRectOffset(CGRectInsetWithOffset(barFrame, EHISearchFieldInsets), 0.0f, -6.0f))
            .block(^(EHITextField *textField, CGFloat percent) {
                self.searchCell.borderOpacity = percent;
                textField.actionButton.alpha  = percent;
            })
            .delay(0.35).duration(0.35),
        
        EHINavigationAnimation.proxy(self.searchCell.titleLabel)
            .reverseTranslation((EHIFloatVector){ .y = -CGRectGetMaxY(titleFrame) })
            .alpha(1.0f)
            .delay(0.3).duration(0.4),
    ]);
    
    if(!isEntering) {
        animations = animations.concat(@[
            EHINavigationAnimation.target(navigationBar)
                .autoreversingNavigationBar
        ]);
    }
    
    return animations;
}

# pragma mark - Analytics

- (BOOL)automaticallyInvalidatesAnalyticsContext
{
    return ![[EHISettings sharedInstance] isFirstRun] || self.wasHidenOnce;
}

- (void)prepareToUpdateAnalyticsContext
{
    [EHIAnalytics changeScreen:EHIScreenDashboard];
}

-(void)updateAnalyticsContext:(EHIAnalyticsContext *)context
{
    [super updateAnalyticsContext:context];

    context.state = [self.viewModel currentStatus];
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:^(EHIAnalyticsContext *context) {
        context.macroEvent = EHIAnalyticsMacroEventDashboard;
    }];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenDashboard;
}

@end
