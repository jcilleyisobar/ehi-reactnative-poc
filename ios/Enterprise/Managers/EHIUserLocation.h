//
//  EHIUserLocation.h
//  Enterprise
//
//  Created by Ty Cobb on 2/24/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@import CoreLocation;

#import "EHIModel.h"
#import "EHIAnalyticsEncodable.h"

@interface EHIUserLocation : NSObject <EHIAnalyticsEncodable>

/** The user's current location; may be @c nil */
@property (strong, nonatomic) CLLocation *currentLocation;
/** @c YES if the current location is available; application may change this flag depending on state */
@property (assign, nonatomic) BOOL isAvailable;

/** Returns the shared user location instance */
+ (instancetype)location;

/** Uses the cached location if possible; otherwise, passes through to @c -syncronizeCurrentLocationWithHandler: */
- (void)currentLocationWithHandler:(void(^)(CLLocation *, NSError *))handler;
/** Fetches and caches the user's current location, passing it to the block when complete */
- (void)synchronizeCurrentLocationWithHandler:(void(^)(CLLocation *, NSError *))handler;

/** Calculates the distance in meters between the user's location and the coordinate */
- (CLLocationDistance)distanceToCoordinate:(CLLocationCoordinate2D)coordinate;
/** Returns the distance as a localized string between the user's location and the coordinate */
- (NSString *)localizedDistanceToCoordinate:(CLLocationCoordinate2D)coordinate;

@end
