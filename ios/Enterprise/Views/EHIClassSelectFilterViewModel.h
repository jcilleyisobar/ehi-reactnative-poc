//
//  EHIClassSelectFilterViewModel.h
//  Enterprise
//
//  Created by mplace on 4/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHISectionHeaderModel.h"

typedef NS_ENUM(NSUInteger, EHIClassSelectFilterSection) {
    EHIClassSelectFilterSectionVehicleFeatures,
    EHIClassSelectFilterSectionVehicleType,
};

@interface EHIClassSelectFilterViewModel : EHIViewModel <MTRReactive>

/** Title for the screen */
@property (copy  , nonatomic) NSString *title;
/** Title for the apply filters button */
@property (copy  , nonatomic) NSAttributedString *applyFiltersButtonTitle;
/** Models to populate the vehicle type section */
@property (copy  , nonatomic) NSArray *vehicleTypeModels;
/** Models to populate the vehicle features section */
@property (copy  , nonatomic) NSArray *vehicleFeaturesModels;
/** Header model for the toggle filters */
@property (strong, nonatomic) EHISectionHeaderModel *toggleFilterSectionHeader;
/** YES if the apply filters button should be enabled */
@property (assign, nonatomic) BOOL shouldEnableApplyFiltersButton;

/** Discards filter changes and navigates back */
- (void)cancelFilters;
/** Sets all active filters to inactive */
- (void)resetFilters;
/** Applys the filters and navigates back */
- (void)applyFilters;
/** Selects the filter at the parameterized indexPath */
- (void)selectFilterAtIndexPath:(NSIndexPath *)indexPath;

@end
