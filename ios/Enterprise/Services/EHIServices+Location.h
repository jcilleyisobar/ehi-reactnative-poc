//
//  EHIServices+Location.h
//  Enterprise
//
//  Created by Ty Cobb on 1/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@import CoreLocation;

#import "EHIServices.h"
#import "EHILocations.h"
#import "EHISpatialLocations.h"
#import "EHILocationSearchQuery.h"
#import "EHILocationFilterQuery.h"
#import "EHISearchRegion.h"

@interface EHIServices (Location)
- (id<EHINetworkCancelable>)fetchLocationsForQuery:(EHILocationSearchQuery *)query handler:(void (^)(EHILocations *, EHIServicesError *))handler;
- (id<EHINetworkCancelable>)fetchLocationsForRegion:(EHISearchRegion)region filters:(EHILocationFilterQuery *)filterQuery handler:(void (^)(EHISpatialLocations *, EHIServicesError *))handler;
- (id<EHINetworkCancelable>)fetchHoursForLocation:(EHILocation *)location date:(NSDate *)date handler:(void (^)(EHILocationDay *, EHIServicesError *))handler;
- (id<EHINetworkCancelable>)updateHoursForLocation:(EHILocation *)location fromDate:(NSDate *)fromDate toDate:(NSDate *)toDate handler:(void (^)(EHILocation *, EHIServicesError *))handler;
- (id<EHINetworkCancelable>)updateHoursForLocation:(EHILocation *)location handler:(void(^)(EHILocation *, EHIServicesError *))handler;
- (id<EHINetworkCancelable>)updateDetailsForLocation:(EHILocation *)location handler:(void (^)(EHILocation *, EHIServicesError *))handler;
- (id<EHINetworkCancelable>)updateAgeOptionsForLocation:(EHILocation *)location handler:(void (^)(EHILocation *, EHIServicesError *))handler;
@end
