//
//  EHIClassSelectFilterViewController.m
//  Enterprise
//
//  Created by mplace on 4/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIClassSelectFilterViewController.h"
#import "EHIClassSelectFilterViewModel.h"
#import "EHIBarButtonItem.h"
#import "EHIListCollectionView.h"
#import "EHIActionButton.h"
#import "EHIClassSelectFilterPickerCell.h"
#import "EHISectionHeader.h"
#import "EHIFilterToggleCell.h"

@interface EHIClassSelectFilterViewController () <EHIClassSelectFilterPickerCellActions, UICollectionViewDelegate>
@property (strong, nonatomic) EHIClassSelectFilterViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIListCollectionView *collectionView;
@property (weak  , nonatomic) IBOutlet EHIActionButton *applyFiltersButton;
@end

@implementation EHIClassSelectFilterViewController

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if (self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIClassSelectFilterViewModel new];
    }
    
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    // set up the collection view sections
    [self initializeCollectionViewSections];
}

- (void)initializeCollectionViewSections
{
    // vehicle feature filter section
    EHIListDataSourceSection *vehicleFeatures = self.collectionView.sections[EHIClassSelectFilterSectionVehicleFeatures];
    vehicleFeatures.klass = EHIClassSelectFilterPickerCell.class;
    
    // vehicle type filter section
    EHIListDataSourceSection *vehicleType = self.collectionView.sections[EHIClassSelectFilterSectionVehicleType];
    vehicleType.klass = EHIFilterToggleCell.class;
    vehicleType.header.klass = EHISectionHeader.class;
}

- (BOOL)needsBottomLine
{
    return YES;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIClassSelectFilterViewModel *)model
{
    [super registerReactions:model];
    
    EHIListDataSourceSection *vehicleFeatures = self.collectionView.sections[EHIClassSelectFilterSectionVehicleFeatures];
    EHIListDataSourceSection *vehicleType = self.collectionView.sections[EHIClassSelectFilterSectionVehicleType];
    
    model.bind.map(@{
        source(model.title)                          : dest(self, .title),
        source(model.applyFiltersButtonTitle)        : dest(self, .applyFiltersButton.ehi_attributedTitle),
        source(model.shouldEnableApplyFiltersButton) : dest(self, .applyFiltersButton.enabled),
        source(model.vehicleFeaturesModels)          : dest(vehicleFeatures, .models),
        source(model.vehicleTypeModels)              : dest(vehicleType, .models),
        source(model.toggleFilterSectionHeader)      : dest(vehicleType, .header.model)
    });
}

# pragma mark - Actions

- (void)didTapResetButton:(id)sender
{
    // ask the view model to reset the filters
    [self.viewModel resetFilters];
}

- (void)didTapCancelButton:(id)sender
{
    // ask the view model to cancel filtering
    [self.viewModel cancelFilters];
}

- (IBAction)didTapApplyFiltersButton:(id)sender
{
    // ask the view model to apply filters
    [self.viewModel applyFilters];
}

# pragma mark - UICollectionViewDelegate

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    // Only select the filter if its a vehicle type toggle filter
    if(indexPath.section != EHIClassSelectFilterSectionVehicleType) {
        return;
    }
    
    // grab the cell for the indexPath
    EHIFilterToggleCell *cell = (EHIFilterToggleCell *)[collectionView cellForItemAtIndexPath:indexPath];
    // update cell state
    [cell toggleFilterSelection];
    // ask the view model to select the appropriate filter
    [self.viewModel selectFilterAtIndexPath:indexPath];
}

# pragma mark - EHIClassSelectFilterPickerCellActions

- (void)didDismissPickerForCell:(EHIClassSelectFilterPickerCell *)cell
{
    // grab the index path for the cell
    NSIndexPath *indexPath = [self.collectionView indexPathForCell:cell];
    // ask the view model to select the appropriate filter
    [self.viewModel selectFilterAtIndexPath:indexPath];
}

# pragma mark - Keyboard

- (UIScrollView *)keyboardSupportedScrollView
{
    return self.collectionView;
}

# pragma mark - EHIViewController

- (void)updateNavigationItem:(UINavigationItem *)item
{
    [super updateNavigationItem:item];
    
    item.rightBarButtonItem = [EHIBarButtonItem buttonWithType:EHIButtonTypeReset target:self action:@selector(didTapResetButton:)];
    item.leftBarButtonItems = [EHIBarButtonItem backButtonWithTarget:self action:@selector(didTapCancelButton:)];
}

# pragma mark - Analytics

- (void)prepareToUpdateAnalyticsContext
{
    [EHIAnalytics changeScreen:EHIScreenReservationClassSelect state:EHIScreenReservationClassSelectFilter];
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:nil];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenReservationClassSelectFilter;
}

@end
