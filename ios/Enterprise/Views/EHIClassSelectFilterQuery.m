//
//  EHIClassSelectFilterQuery.m
//  Enterprise
//
//  Created by mplace on 4/23/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIClassSelectFilterQuery.h"

@implementation EHIClassSelectFilterQuery

- (NSArray *)activeFilters
{
    // combine and select the active filters
    NSArray *activeFilters = @[]
        .concat(self.vehicleFeatureFilters)
        .concat(self.vehicleTypeFilters)
        .select(^(EHIFilters *filters) {
            return filters.isActive;
        });
    
    // return nil if we don't have filters
    return activeFilters.count ? activeFilters : nil;
}

# pragma mark - EHIAnalyticsEncodable

+ (void)encodeWithContext:(EHIAnalyticsContext *)context instance:(nullable EHIClassSelectFilterQuery *)instance
{
    context[EHIAnalyticsFilterTypeKey] = instance ? @"Class" : nil;
    context[EHIAnalyticsFilterListKey] = [instance analyticsFilterList];
}

- (NSArray *)analyticsFilterList
{
    NSArray *activeFilters = [self activeFilters];
    return !activeFilters ? nil : activeFilters.map(^(EHIFilters *filters) {
        return filters.displayTitle;
    });
}

@end
