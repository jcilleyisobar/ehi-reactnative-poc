//
//  EHIFilters.m
//  Enterprise
//
//  Created by mplace on 4/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIFilters.h"
#import "EHICarClassFeature.h"

@implementation EHIFilter

+ (instancetype)wildcardFilter
{
    EHIFilter *filter = [EHIFilter new];
    filter.title      = EHILocalizedString(@"class_select_default_filter_title", @"All", @"title for the default filter option: All");
    filter.type       = EHIFilterTypeWildcard;
    
    return filter;
}

@end

@implementation EHIFilters

# pragma mark - Getter

- (BOOL)isActive
{
    switch (self.style) {
        case EHIFilterStyleToggle:
            return _isActive;
        case EHIFilterStylePicker:
            return (![self.currentFilter.title isEqualToString:self.defaultFilter.title]);
        case EHIFilterStyleLink:
            return NO;
    }
}

- (NSString *)displayTitle
{
    switch (self.style) {
        case EHIFilterStyleToggle:
            return self.currentFilter.title;
        case EHIFilterStylePicker:
            return [NSString stringWithFormat:@"%@ %@", self.currentFilter.title, self.title];
        case EHIFilterStyleLink:
            return self.currentFilter.title;
    }
}

- (EHIFilter *)defaultFilter
{
    if (!_defaultFilter) {
        _defaultFilter = [EHIFilter wildcardFilter];
    }
    
    return _defaultFilter;
}

# pragma mark - Generation

+ (NSArray *)vehicleTypeFiltersForCarClassFilters:(NSArray *)filters
{
    // use car class type filter
    EHICarClassFilters *carClassFilters = filters.find(^(EHICarClassFilters *filters) {
        return filters.type == EHIFilterTypeClass;
    });
    
    // generate EHFilters toggles from filter options
    return (carClassFilters.filterValues ?: @[]).map(^(EHICarClassFilter *carClassFilter) {
        EHIFilter *currentFilter = [EHIFilter new];
        currentFilter.title      = carClassFilter.title;
        currentFilter.value      = carClassFilter.code;

        // transfer car class filter type to current filter
        currentFilter.type       = carClassFilters.type;
        
        // create toggle filter
        EHIFilters *filters   = [EHIFilters new];
        filters.style         = EHIFilterStyleToggle;
        filters.currentFilter = currentFilter;
        
        return filters;
    });
}

+ (NSArray *)vehicleFeatureFiltersForCarClassFilters:(NSArray *)filters
{
    // grab applicable services filters
    NSArray *featureFilters = filters.select(^(EHICarClassFilters *filters) {
        return filters.type == EHIFilterTypeTransmission || filters.type == EHIFilterTypePassengerCapacity;
    }).sortBy(^(EHICarClassFilters *filters) {
        return filters.type;
    });
    
    // generate EHIFilters models
    featureFilters = featureFilters.map(^(EHICarClassFilters *carClassFilters) {
        EHIFilters *filters   = [EHIFilters new];
        filters.style         = EHIFilterStylePicker;
        filters.title         = carClassFilters.title;
        filters.currentFilter = [EHIFilter wildcardFilter];
        
        // map EHICarClassFilter to EHIFilter
        NSArray *possibleFilters = carClassFilters.filterValues.map(^(EHICarClassFilter *carClassFilter) {
            EHIFilter *filter = [EHIFilter new];
            filter.title      = carClassFilter.title;
            filter.value      = carClassFilter.code;
            
            // transfer car class filter type to this filter option
            filter.type       = carClassFilters.type;
            
            return filter;
        });
        
        // sort passenger options by value
        if(carClassFilters.type == EHIFilterTypePassengerCapacity) {
            possibleFilters = possibleFilters.sortBy(^(EHIFilter *filter) { return filter.value; });
        }
        
        // prepend all option and set
        possibleFilters = @[[EHIFilter wildcardFilter]].concat(possibleFilters);
        filters.possibleFilters = (NSArray<EHIFilter> *)possibleFilters;
        
        return filters;
    });
    
    return featureFilters;
}

+ (NSArray *)locationHoursFilters
{
    EHIFilters *model;
    EHIFilter *filter;
    
    return [EHIFilters modelsWithDictionaries:@[@{
        @key(model.style) : @(EHIFilterStyleToggle),
        @key(model.currentFilter) : @{
            @key(filter.title) : EHILocalizedString(@"location_filter_open_sunday_title", @"Open Sunday", @"title for location filter: Open Sunday"),
            @key(filter.type)  : @(EHIFilterTypeLocationHours),
            @key(filter.key) : @"openSundays"
    }
    }]];
}

+ (NSArray *)locationTypeFilters
{
    EHIFilters *model;
    EHIFilter *filter;

    return [EHIFilters modelsWithDictionaries:@[@{
        @key(model.style) : @(EHIFilterStyleToggle),
        @key(model.iconImageName) : @"icon_airport_gray",
        @key(model.currentFilter) : @{
            @key(filter.title) : EHILocalizedString(@"location_filter_airport_title", @"Airport", @"title for location filter: Airport"),
            @key(filter.type)  : @(EHIFilterTypeLocationType),
            @key(filter.key)   : @"AIRPORT",
        }
    }, @{
        @key(model.style) : @(EHIFilterStyleToggle),
        @key(model.iconImageName) : @"icon_portofcall_01",
        @key(model.currentFilter) : @{
            @key(filter.title) : EHILocalizedString(@"location_filter_port_of_call_title", @"Port", @"title for location filter: Port of call"),
            @key(filter.type)  : @(EHIFilterTypeLocationType),
            @key(filter.key)   : @"PORT_OF_CALL",
        }
    }, @{
        @key(model.style) : @(EHIFilterStyleToggle),
        @key(model.iconImageName) : @"icon_train_01",
        @key(model.currentFilter) : @{
            @key(filter.title) : EHILocalizedString(@"location_filter_rail_station_title", @"Rail", @"title for location filter: Rail"),
            @key(filter.type)  : @(EHIFilterTypeLocationType),
            @key(filter.key)   : @"RAIL",
        }
    }, ]];
}

+ (NSArray *)locationMiscellaneousFilters
{
    return @[];
}

@end
