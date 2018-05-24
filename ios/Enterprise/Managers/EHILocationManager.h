//
//  EHILocationManager.h
//  Enterprise
//
//  Created by George Stuart on 2/4/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIUserLocation.h"

@interface EHILocationManager : NSObject

/** The user's current location; only one instance ever created */
@property (nonatomic, readonly) EHIUserLocation *userLocation;
/** @c YES if the user has granted access to location information */
@property (nonatomic, readonly) BOOL locationsAvailable;

/** The shared instance for consumers to access the manager */
+ (EHILocationManager *)sharedInstance;

/** Runs through the permission flow to gain access to the user's location */
- (void)locationsAvailableWithHandler:(void(^)(BOOL locationsAvailable, NSError *error))handler;
/** Runs through the permission flow, and returns the current location if successful */
- (void)currentLocationWithHandler:(void(^)(CLLocation *location, NSError *error))handler;

@end
