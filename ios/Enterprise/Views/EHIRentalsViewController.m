//
//  EHIRentalsViewController.m
//  Enterprise
//
//  Created by fhu on 4/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIRentalsViewController.h"
#import "EHIRentalsViewModel.h"
#import "EHIRentalsPastRentalCell.h"
#import "EHIRentalsUpcomingRentalCell.h"
#import "EHIRentalsFooterCell.h"
#import "EHIRentalsFallbackCell.h"
#import "EHIRentalsPagingCell.h"
#import "EHIRentalsUnauthenticatedViewController.h"
#import "EHIListCollectionView.h"
#import "EHIActivityIndicatorCell.h"
#import "EHISegmentedControl.h"
#import "EHIRestorableConstraint.h"
#import "EHIActivityIndicator.h"

@interface EHIRentalsViewController () <EHIRentalsPagingCellActions, EHIListCollectionViewDelegate>
@property (strong, nonatomic) EHIRentalsViewModel *viewModel;
@property (strong, nonatomic) EHIRentalsUnauthenticatedViewController *unauthViewController;
@property (weak  , nonatomic) IBOutlet EHISegmentedControl *segmentedControl;
@property (weak  , nonatomic) IBOutlet EHIActivityIndicator *activityIndicator;
@property (weak  , nonatomic) IBOutlet EHIListCollectionView *collectionView;
@property (weak  , nonatomic) IBOutlet UIView *unauthContainer;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *segmentedControlHeight;
@end

@implementation EHIRentalsViewController

- (id)initWithCoder:(NSCoder *)decoder
{
    if(self = [super initWithCoder:decoder]) {
        self.viewModel = [EHIRentalsViewModel new];
    }
    
    return self;
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    [super prepareForSegue:segue sender:sender];
    
    if([segue.identifier isEqualToString:@"EHISegueEmbedUnauthenticatedRentalsViewController"]) {
        self.unauthViewController = segue.destinationViewController;
    }
}

- (void)updateWithAttributes:(NAVAttributes *)attributes
{
    [super updateWithAttributes:attributes];
   
    // try and refresh rentals upon any navigation
    [self.viewModel refreshRentals];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
  
    // build the rest of the content sections
    [self.collectionView.sections construct:@{
        @(EHIRentalsSectionFallback)       : EHIRentalsFallbackCell.class,
        @(EHIRentalsSectionPastRental)     : EHIRentalsPastRentalCell.class,
        @(EHIRentalsSectionUpcomingRental) : EHIRentalsUpcomingRentalCell.class,
        @(EHIRentalsSectionPaging)         : EHIRentalsPagingCell.class,
        @(EHIRentalsSectionFooter)         : EHIRentalsFooterCell.class,
        @(EHIRentalsSectionLoading)        : EHIActivityIndicatorCell.class,
    }];
   
    // dynamically size the appropriate sections
    for(EHIListDataSourceSection *section in self.collectionView.sections) {
        section.isDynamicallySized = [self sectionIsDynamicallySized:section.index];
    }
 
    // update the fixed height of the activity indicator
    EHIListDataSourceSection *indicator = self.collectionView.sections[EHIRentalsSectionLoading];
    indicator.metrics = [EHIActivityIndicatorCell.defaultMetrics copy];
    indicator.metrics.fixedSize = (CGSize){
        .width = EHILayoutValueNil, .height = 180.0f
    };
}

- (BOOL)sectionIsDynamicallySized:(EHIRentalsSection)section
{
    return section != EHIRentalsSectionLoading
        && section != EHIRentalsSectionPaging;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIRentalsViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(updateUnauthContainerVisibility:)];
    [MTRReactor autorun:self action:@selector(updateSegmentedControlTitles:)];
    [MTRReactor autorun:self action:@selector(invalidateLoadingAnimation:)];
    
    EHIListDataSourceSection *fallback = self.collectionView.sections[EHIRentalsSectionFallback];
    EHIListDataSourceSection *pastRentals = self.collectionView.sections[EHIRentalsSectionPastRental];
    EHIListDataSourceSection *upcomingRentals = self.collectionView.sections[EHIRentalsSectionUpcomingRental];
    EHIListDataSourceSection *paging = self.collectionView.sections[EHIRentalsSectionPaging];
    EHIListDataSourceSection *footer = self.collectionView.sections[EHIRentalsSectionFooter];
    
    model.bind.map(@{
        source(model.title)                 : dest(self, .title),
        source(model.fallbackViewModel)     : dest(fallback, .model),
        source(model.pastRentals)           : dest(pastRentals, .models),
        source(model.upcomingRentals)       : dest(upcomingRentals, .models),
        source(model.pagingModel)           : dest(paging, .model),
        source(model.footerViewModel)       : dest(footer, .model),
    });
}

- (void)updateUnauthContainerVisibility:(MTRComputation *)computation
{
    // check if we should display unauth
    BOOL isVisible = !self.viewModel.shouldHideUnauth;
   
    // disable refresh control when unauthed
    self.collectionView.ehiRefreshControl.isDisabled = isVisible;
    
    // hide the container if necessary
    UIView.animate(!computation.isFirstRun).duration(0.25).transform(^{
        self.unauthContainer.alpha = isVisible ? 1.0 : 0.0f;
    }).start(nil);
   
    // if this is an update, then re-run the analytics "on load" event too
    if(!computation.isFirstRun) {
        [self invalidateAnalyticsContext];
    }
}

- (void)updateSegmentedControlTitles:(MTRComputation *)computation
{
    self.viewModel.segmentedControlItems.each(^(NSString *title, int index) {
        [self.segmentedControl setTitle:title forSegmentAtIndex:index];
    });
}

- (void)invalidateLoadingAnimation:(MTRComputation *)computation
{
    BOOL isLoading = self.viewModel.isLoading;
    
    self.activityIndicator.isAnimating = isLoading;

    self.collectionView.ehiRefreshControl.isRefreshing = isLoading;
    self.collectionView.alwaysBounceVertical = !isLoading;
    
    [self.collectionView performAnimated:!computation.isFirstRun batchUpdates:^{
        self.collectionView.sections[EHIRentalsSectionLoading].model = isLoading ? [EHIModel placeholder] : nil;
    } completion:nil];
}

# pragma mark - Actions

- (IBAction)segmentedControlValueChanged:(UISegmentedControl *)segmentedControl
{
    [self.viewModel switchToMode:segmentedControl.selectedSegmentIndex];
    [self invalidateAnalyticsContext];
}

# pragma mark -  UICollectionViewDelegate

- (BOOL)collectionView:(UICollectionView *)collectionView shouldSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    return [self.viewModel shouldSelectRentalAtIndexPath:indexPath];
}

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    [self.viewModel selectRentalAtIndexPath:indexPath];
}

# pragma mark - EHIRentalsPagingCellActions

- (void)pagingCellDidLoadMoreRentals:(EHIRentalsPagingCell *)cell
{
    [self.viewModel invalidateVisibleRentals];
}

# pragma mark - EHIViewController

- (BOOL)showsPhoneButton
{
    return YES;
}

# pragma mark - Analytics

- (void)prepareToUpdateAnalyticsContext
{
    [EHIAnalytics changeScreen:EHIScreenRentals];
}

- (void)updateAnalyticsContext:(EHIAnalyticsContext *)context
{
    [super updateAnalyticsContext:context];
    
    context.state = [self currentState];
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:nil];
}

//
// Helpers
//

- (NSString *)currentState
{
    if(!self.viewModel.shouldHideUnauth) {
        return EHIAnalyticsRentalsStateUnauth;
    } else if(self.viewModel.mode == EHIRentalsModeUpcoming) {
        return EHIAnalyticsRentalsStateUpcoming;
    } else {
        return EHIAnalyticsRentalsStatePast;
    }
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenRentals;
}

@end
