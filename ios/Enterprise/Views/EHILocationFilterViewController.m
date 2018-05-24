//
//  EHILocationFilterViewController.m
//  Enterprise
//
//  Created by mplace on 2/25/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILocationFilterViewController.h"
#import "EHILocationFilterViewModel.h"
#import "EHIListCollectionView.h"
#import "EHIBarButtonItem.h"
#import "EHIFilterToggleCell.h"
#import "EHICollectionTitleView.h"
#import "EHILocationFilterMiscCell.h"
#import "EHISectionHeader.h"
#import "EHIActionButton.h"
#import "EHIDateTimeComponentFilterCell.h"

@interface EHILocationFilterViewController () <UICollectionViewDelegate, EHIDateTimeComponentFilterCellActions>
@property (strong, nonatomic) EHILocationFilterViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIListCollectionView *collectionView;
@property (weak  , nonatomic) IBOutlet EHIActionButton *applyFiltersButton;
@end

@implementation EHILocationFilterViewController

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHILocationFilterViewModel new];
    }
    
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [self initializeCollectionViewSections];
}

- (void)initializeCollectionViewSections
{
    EHIListDataSourceSection *open = self.collectionView.sections[EHILocationFilterSectionOpenDuringTravel];
    open.klass = EHIDateTimeComponentFilterCell.class;
    open.header.klass = EHISectionHeader.class;
    open.header.model = [self.viewModel headerModelForSection:EHILocationFilterSectionOpenDuringTravel];
    open.isDynamicallySized = YES;
    
    EHIListDataSourceSection *locationType = self.collectionView.sections[EHILocationFilterSectionLocationType];
    locationType.klass = EHIFilterToggleCell.class;
    locationType.header.klass = EHISectionHeader.class;
    locationType.header.model = [self.viewModel headerModelForSection:EHILocationFilterSectionLocationType];

    EHIListDataSourceSection *miscellaneous = self.collectionView.sections[EHILocationFilterSectionMiscellaneous];
    miscellaneous.klass = EHIFilterToggleCell.class;
    miscellaneous.header.klass = EHISectionHeader.class;
    miscellaneous.header.model = [self.viewModel headerModelForSection:EHILocationFilterSectionMiscellaneous];
}

- (BOOL)needsBottomLine
{
    return YES;
}

# pragma mark - Reactions

- (void)registerReactions:(EHILocationFilterViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(refreshCollectionView:)];
    
    EHIListDataSourceSection *open  = self.collectionView.sections[EHILocationFilterSectionOpenDuringTravel];
    EHIListDataSourceSection *locationType  = self.collectionView.sections[EHILocationFilterSectionLocationType];
    EHIListDataSourceSection *miscellaneous = self.collectionView.sections[EHILocationFilterSectionMiscellaneous];
    
    model.bind.map(@{
        source(model.title)                  : dest(self, .title),
        source(model.applyFilterButtonTitle) : dest(self, .applyFiltersButton.ehi_title),
        source(model.dateTimeFilter)         : dest(open, .model),
        source(model.locationTypeFilters)    : dest(locationType, .models),
        source(model.miscellaneousFilters)   : dest(miscellaneous, .models),
    });
}

- (void)refreshCollectionView:(MTRComputation *)computation
{
    __unused BOOL refresh = self.viewModel.shouldRefreshContent;

    [self.collectionView ehi_invalidateLayoutAnimated:!computation.isFirstRun];
}

# pragma mark - View Actions

- (void)didTapResetButton:(id)sender
{
    // clear filters
    [self.viewModel clearFilters];
}

- (void)didTapCancelButton:(id)sender
{
    // discard any changes and pop back
    [self.viewModel cancelFiltering];
}

- (IBAction)didTapApplyFiltersButton:(id)sender
{
    // apply changes and pop back
    [self.viewModel applyFilters];
}

# pragma mark -  UICollectionViewDelegate

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    // Only select the filter if its a toggle filter
    NSInteger section = indexPath.section;
    if(section == EHILocationFilterSectionMiscellaneous || section == EHILocationFilterSectionOpenDuringTravel) {
        return;
    }
    
    // grab the cell for the indexPath
    EHIFilterToggleCell *cell = (EHIFilterToggleCell *)[collectionView cellForItemAtIndexPath:indexPath];
    // update the cell state
    [cell toggleFilterSelection];
    // let the view model handle the selection
    [self.viewModel selectFilterAtIndexPath:indexPath];
}

# pragma mark - EHIDateTimeComponentFilterCellActions

- (void)dateTimeComponentDidTapOnSection:(NSNumber *)section
{
    [self.viewModel didTapOnSection:[section integerValue]];
}

- (void)dateTimeComponentDidTapOnCleanSection:(NSNumber *)section
{
    [self.viewModel didTapOnCleanSection:[section integerValue]];
}

# pragma mark - Analytics

- (void)updateAnalyticsContext:(EHIAnalyticsContext *)context
{
    context.routerState = EHIScreenLocationFilter;
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:nil];
}

# pragma mark - EHIViewController

- (void)updateNavigationItem:(UINavigationItem *)item
{
    [super updateNavigationItem:item];
    
    item.rightBarButtonItem = [EHIBarButtonItem buttonWithType:EHIButtonTypeReset target:self action:@selector(didTapResetButton:)];
    item.leftBarButtonItem  = [EHIBarButtonItem backButtonWithTarget:self action:@selector(didTapCancelButton:)];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenLocationFilter;
}

@end
