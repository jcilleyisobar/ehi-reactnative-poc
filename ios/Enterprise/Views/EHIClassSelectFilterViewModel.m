//
//  EHIClassSelectFilterViewModel.m
//  Enterprise
//
//  Created by mplace on 4/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIClassSelectFilterViewModel.h"
#import "EHIViewModel_Subclass.h"
#import "EHIFilters.h"
#import "EHIClassSelectFilterQuery.h"
#import "EHICarClass.h"
#import "EHIReservationBuilder.h"

@interface EHIClassSelectFilterViewModel ()
@property (nonatomic, readonly) EHIReservationBuilder *builder;
@property (copy, nonatomic) NSArray *carClassMasterList;
@property (copy, nonatomic) NSArray *filteredCarClasses;
@end

@implementation EHIClassSelectFilterViewModel

- (instancetype)initWithModel:(id)model
{
    if (self = [super initWithModel:model]) {
        _title = EHILocalizedString(@"class_select_filter_screen_title", @"Filters", @"title for the class select filter screen");
        _toggleFilterSectionHeader = [EHISectionHeaderModel modelWithTitle:EHILocalizedString(@"class_select_filter_toggle_section_header_title", @"VEHICLE TYPE", @"title for the class select toggle filter section header")];
    }
    
    return self;
}

- (void)updateWithModel:(EHIClassSelectFilterQuery *)model
{
    [super updateWithModel:model];
    
    // set our master list
    self.carClassMasterList = model.carClasses;
    // use any previously applied vehicle feature filters if any, otherwise generate a fresh copy
    self.vehicleFeaturesModels = model.vehicleFeatureFilters ? [[NSArray alloc] initWithArray:model.vehicleFeatureFilters copyItems:YES] : [self defaultVehicleFeatureFilters];
    // use any previously applied vehicle type filters if any, otherwise generate a fresh copy
    self.vehicleTypeModels = model.vehicleTypeFilters ? [[NSArray alloc] initWithArray:model.vehicleTypeFilters copyItems:YES] : [self defaultVehicleTypeFilters];
    
    [self invalidateFilters];
}

- (void)invalidateFilters
{
    // apply the AND filters to the master list first
    NSArray *filteredCarClasses = [self applyAndFiltersToCarClassList:self.carClassMasterList];
    // take the remaining car classes and apply the OR filters
    filteredCarClasses = [self applyOrFiltersToCarClassList:filteredCarClasses];
    
    // update our filtered car classes
    self.filteredCarClasses = filteredCarClasses;
}

- (NSArray *)applyAndFiltersToCarClassList:(NSArray *)carClasses
{
    return self.vehicleFeaturesModels.inject(carClasses.copy, ^(NSArray *filteredClasses, EHIFilters *filter) {
        // if its a wildcard filter, include all filtered classes
        if(filter.currentFilter.type == EHIFilterTypeWildcard) {
            return filteredClasses;
        }
        // otherwise apply filter and return results
        else {
            return (filteredClasses ?: @[]).select(^(EHICarClass *carClass) {
                return [carClass matchesFilter:filter.currentFilter];
            });
        }
    });
}

- (NSArray *)applyOrFiltersToCarClassList:(NSArray *)carClasses
{
    // select the active filters
    NSArray *orFilters = self.vehicleTypeModels.select(^(EHIFilters *filter){
        return filter.isActive;
    });
    
    if(!orFilters.count) {
        return carClasses;
    }
    
    NSArray *result = carClasses.select(^(EHICarClass *carClass) {
        // if any of the filters match the car class, we should keep it
        return orFilters.any(^(EHIFilters *filter) {
            return [carClass matchesFilter:filter.currentFilter];
        });
    });
    
    return [result copy];
}

# pragma mark - Button Title

- (void)invalidateApplyFiltersButton
{
    // localize the prefix
    NSString *prefix = EHILocalizedString(@"class_select_filter_apply_filters_button_title_prefix", @"FILTER", @"button title prefix for a button that allows the user to apply class select filters.");
    // localize the format suffix
    NSString *format = EHILocalizedString(@"class_select_filter_apply_filters_button_title_suffix", @"#{vehicles} Vehicles", @"button title suffix for a button that informs the user of the number of vehicles that the filters result in.");
    // apply the data mapping to the format
    format = [format ehi_applyReplacementMap:@{
        @"vehicles" : @(self.filteredCarClasses.count),
    }];
    
    // build the attributed title
    EHIAttributedStringBuilder *builder = EHIAttributedStringBuilder.new.text(prefix).fontStyle(EHIFontStyleBold, 18.0f).space
        .appendText(format).fontStyle(EHIFontStyleLight, 14.0f);
    
    // set the button title
    self.applyFiltersButtonTitle = builder.string;
    // disable the apply filters button if there are no car classes
    self.shouldEnableApplyFiltersButton = self.filteredCarClasses.count != 0;
}

# pragma mark - Actions

- (void)cancelFilters
{
    // pop back to the class select screen discarding all changes
    self.router.transition
        .pop(1).start(nil);
}

- (void)resetFilters
{
    // reset our models to the initial state
    self.vehicleFeaturesModels = [self defaultVehicleFeatureFilters];
    self.vehicleTypeModels     = [self defaultVehicleTypeFilters];
    
    [self invalidateFilters];
}

- (void)applyFilters
{
    // generate the class select filter query
    EHIClassSelectFilterQuery *query = [self filterQuery];
    
    // fire the tracking action before popping
    [EHIAnalytics trackAction:EHIAnalyticsActionApplyFilter handler:^(EHIAnalyticsContext *context) {
        [context encode:[EHIClassSelectFilterQuery class] encodable:query];
    }];
    
    // pop back to the class select screen discarding all changes
    self.router.transition
        .pop(1).object(query).start(nil);
}

- (void)selectFilterAtIndexPath:(NSIndexPath *)indexPath
{
    // use the appropriate array of filters based on section
    NSArray *sectionFilters = [self sectionFiltersForSection:indexPath.section];
    // grab the filter based on the index
    EHIFilters *filter = sectionFilters[indexPath.row];
    // update the active state
    filter.isActive = !filter.isActive;
    
    // recompute the filtered car classes
    [self invalidateFilters];
}

//
// Helper
//

- (NSArray *)sectionFiltersForSection:(EHIClassSelectFilterSection)section
{
    switch (section) {
        case EHIClassSelectFilterSectionVehicleFeatures:
            return self.vehicleFeaturesModels;
        case EHIClassSelectFilterSectionVehicleType:
            return self.vehicleTypeModels;
    }
}

# pragma mark - Setters

- (void)setFilteredCarClasses:(NSArray *)filteredCarClasses
{
    _filteredCarClasses = filteredCarClasses;
    
    // update the apply filter button title, based on filtered car class count
    [self invalidateApplyFiltersButton];
}

# pragma mark - Getter

- (EHIClassSelectFilterQuery *)filterQuery
{
    EHIClassSelectFilterQuery *query = [EHIClassSelectFilterQuery new];
    query.filteredCarClasses         = self.filteredCarClasses;
    query.carClasses                 = self.carClassMasterList;
    query.vehicleFeatureFilters      = self.vehicleFeaturesModels;
    query.vehicleTypeFilters         = self.vehicleTypeModels;

    return query;
}

- (NSArray *)defaultVehicleFeatureFilters
{
    return [EHIFilters vehicleFeatureFiltersForCarClassFilters:self.builder.carClassesFilters];
}

- (NSArray *)defaultVehicleTypeFilters
{
    return [EHIFilters vehicleTypeFiltersForCarClassFilters:self.builder.carClassesFilters];
}

# pragma mark - Passthrough

- (EHIReservationBuilder *)builder
{
    return [EHIReservationBuilder sharedInstance];
}

@end
