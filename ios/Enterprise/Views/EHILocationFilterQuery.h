//
//  EHILocationFilterQuery.h
//  Enterprise
//
//  Created by mplace on 4/17/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIFilters.h"
#import "EHISearchRegion.h"
#import "EHIAnalyticsEncodable.h"
#import "EHILocationFilterDateQuery.h"
#import "EHIDateTimeProviderProtocol.h"

@import CoreLocation;

typedef NS_ENUM(NSInteger, EHILocationFilterQueryLocationType) {
    EHILocationFilterQueryLocationTypeRoundTrip,
    EHILocationFilterQueryLocationTypePickupOneWay,
    EHILocationFilterQueryLocationTypeDropoffOneWay
};

@interface EHILocationFilterQuery : EHIModel <EHIAnalyticsEncodable, EHIDateTimeProviderProtocol>

@property (assign, nonatomic) EHILocationFilterQueryLocationType locationType;
/** Region at which to perform all location searches */
@property (assign, nonatomic) EHISearchRegion region;
/** Array of locations to pre-populate with */
@property (copy  , nonatomic) NSArray *locations;
/** Models to populate the location type section with */
@property (copy  , nonatomic) NSArray *locationTypeFilters;
/** Models to populate the miscellaneous section with */
@property (copy  , nonatomic) NSArray *miscellaneousFilters;

@property (strong, nonatomic) EHILocationFilterDateQuery *datesFilter;

@property (assign, nonatomic) BOOL oneWayNeedsPickupData;
@property (assign, nonatomic) BOOL oneWayNeedsDropoffData;

/** returns an array of the active filters */
- (NSArray *)activeFilters;

- (BOOL)isFilteringByDates;
- (BOOL)isEmpty;

@end
