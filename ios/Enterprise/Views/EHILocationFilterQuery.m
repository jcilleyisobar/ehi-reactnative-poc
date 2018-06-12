//
//  EHILocationFilterQuery.m
//  Enterprise
//
//  Created by mplace on 4/17/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILocationFilterQuery.h"

static NSString *const EHILocationFilterDateFormat = @"YYYY-MM-dd";
static NSString *const EHILocationFilterTimeFormat = @"HH:mm";

@implementation EHILocationFilterQuery

- (NSArray *)activeFilters
{
    // append whatever filters we have available
    NSArray *result = @[]
        .concat(self.locationTypeFilters)
        .concat(self.miscellaneousFilters);
   
    // bail out if we don't have any filters
    if(!result.count) {
        return nil;
    }

    // otherwise, select and return the active ones
    return result.select(^(EHIFilters *filters) {
        return filters.isActive;
    });
}

# pragma mark - EHINetworkEncodable

- (void)encodeWithRequest:(EHINetworkRequest *)request
{
	[self encodeFilters:request];
	[self encodeDateQuery:request];
}

- (void)encodeFilters:(EHINetworkRequest *)request
{
	NSArray *activeFilters = [self activeFilters] ?: @[];
	if(activeFilters.count <= 0) {
		return;
	}

	// encode active hours filters
	activeFilters.select(^(EHIFilters *activeFilter) {
        return activeFilter.currentFilter.type == EHIFilterTypeLocationHours;
    }).each(^ (EHIFilters *activeFilter) {
        request[activeFilter.currentFilter.key] = @"true";
    });

	// encode active location type filters
	request[@"locationTypes"] = activeFilters.select(^(EHIFilters *activeFilter) {
        return activeFilter.currentFilter.type == EHIFilterTypeLocationType;
    }).map(^(EHIFilters *activeFilter){
        return activeFilter.currentFilter.key;
    }).join(@",");

	// if we're filtering, don't show any non-enterprise locations
	request[@"brand"] = @"ENTERPRISE";
}

- (void)encodeDateQuery:(EHINetworkRequest *)request
{
    switch (self.locationType) {
        case EHILocationFilterQueryLocationTypeRoundTrip:
			[self encodePickup:request];
			[self encodeDropoff:request];
            break;
        case EHILocationFilterQueryLocationTypePickupOneWay:
			[self encodePickup:request];
            if(self.oneWayNeedsDropoffData) {
				[self encodeDropoff:request];
            }
            break;
        case EHILocationFilterQueryLocationTypeDropoffOneWay:
			[self encodeDropoff:request];
            if(self.oneWayNeedsPickupData) {
				[self encodePickup:request];
			}
            break;
    }
}

- (void)encodePickup:(EHINetworkRequest *)request
{
	if(self.pickupDate) {
		request[@"pickupDate"] = [self.pickupDate ehi_stringWithFormat:EHILocationFilterDateFormat];
	}

    request[@"pickupTime"] = self.pickupTime
        ? [self.pickupTime ehi_stringWithFormat:EHILocationFilterTimeFormat]
        : @"12:00";
}

- (void)encodeDropoff:(EHINetworkRequest *)request
{
	if(self.returnDate) {
		request[@"dropoffDate"] = [self.returnDate ehi_stringWithFormat:EHILocationFilterDateFormat];
	}
    request[@"dropoffTime"] = self.returnTime
        ? [self.returnTime ehi_stringWithFormat:EHILocationFilterTimeFormat]
        : @"12:00";
}

# pragma mark - EHIAnalyticsEncodable

+ (void)encodeWithContext:(EHIAnalyticsContext *)context instance:(nullable EHILocationFilterQuery *)instance
{
    context[EHIAnalyticsFilterTypeKey]   = instance ? @"Location" : nil;
    context[EHIAnalyticsFilterListKey]   = [instance analyticsFilterList];
    context[EHIAnalyticsLocSearchResult] = @(instance.locations.count);
    context[EHIAnalyticsFilterLocationTypeKey] = [instance analyticsFilterDatesList];
	context[EHIAnalyticsLocClosedLocations]	   = @([instance analyticsClosedLocationsCount]);
}

- (NSArray *)analyticsFilterList
{
    return (self.activeFilters ?: @[]).map(^(EHIFilters *filters) {
        return filters.displayTitle;
    });
}

- (NSArray *)analyticsFilterDatesList
{
    NSArray *filters = @{
        EHIAnalyticsFilterPickupDateKey  : self.pickupDate ?: [NSNull null],
        EHIAnalyticsFilterPickupTimeKey  : self.pickupTime ?: [NSNull null],
        EHIAnalyticsFilterDropoffDateKey : self.returnDate ?: [NSNull null],
        EHIAnalyticsFilterDropoffTimeKey : self.returnTime ?: [NSNull null]
    }.map(^(NSString *key, id obj){
        return ![obj isEqual:NSNull.null] ? key : obj;
    }).reject(NSNull.class);
    
    return filters.count > 0 ? filters : @[EHIAnalyticsFilterNoneKey];
}

- (NSInteger)analyticsClosedLocationsCount
{
    return (self.locations ?: @[]).select(^(EHILocation *location){
        return location.isAllDayClosedForPickup || location.isAllDayClosedForDropoff;
    }).count;
}

#pragma mark - EHIDateTimeProviderProtocol

- (NSDate *)pickupDate
{
	return self.datesFilter.pickupDate;
}

- (NSDate *)pickupTime
{
	return self.datesFilter.pickupTime;
}

- (NSDate *)returnDate
{
	return self.datesFilter.returnDate;
}

- (NSDate *)returnTime
{
	return self.datesFilter.returnTime;
}

- (BOOL)isFilteringByDates
{
    return self.pickupDate != nil || self.pickupTime != nil
        || self.returnDate != nil || self.returnTime != nil;
}

- (BOOL)isEmpty
{
    return self.pickupDate == nil && self.pickupTime == nil
        && self.returnDate == nil && self.returnTime == nil
        && self.activeFilters.count == 0;
}

@end
