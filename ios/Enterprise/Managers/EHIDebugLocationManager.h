//
//  EHIDebugLocationManager.h
//  Enterprise
//
//  Created by Alex Koller on 12/17/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

typedef NS_ENUM(NSUInteger, EHIDebugRouteType) {
    EHIDebugRouteTypeChicago,
    EHIDebugRouteTypeLondon,
    EHIDebugRouteTypeActive,
    EHIDebugRouteTypeUpcoming,
};

@protocol EHIDebugLocationManagerListener;

@interface EHIDebugLocationManager : NSObject

/** The route to follow when updating locations */
@property (assign, nonatomic) EHIDebugRouteType routeType;
/** The time it takes to navigation between waypoints on a route. Defaults to 10.0. */
@property (assign, nonatomic) NSTimeInterval routeIntervalDuration;
/** The route that will be mocked when location updates occur */
@property (copy  , nonatomic, readonly) NSArray<CLLocation *> *currentRoute;
/** @c YES if location updates are being mocked */
@property (assign, nonatomic, readonly) BOOL isUpdatingLocation;

+ (EHIDebugLocationManager *)sharedInstance;

- (void)startUpdatingLocation;
- (void)stopUpdatingLocation;

- (void)addListener:(id<EHIDebugLocationManagerListener>)listener;

@end

@protocol EHIDebugLocationManagerListener <NSObject> @optional

- (void)debugLocationManager:(EHIDebugLocationManager *)manager didUpdateLocation:(CLLocation *)location;
- (void)debugLocationManager:(EHIDebugLocationManager *)manager didEnterRegion:(CLRegion *)region;

@end