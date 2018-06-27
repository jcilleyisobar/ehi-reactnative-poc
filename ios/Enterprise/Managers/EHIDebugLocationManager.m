//
//  EHIDebugLocationManager.m
//  Enterprise
//
//  Created by Alex Koller on 12/17/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHIDebugLocationManager.h"
#import "EHIGeofenceManager.h"
#import "EHIUserManager.h"
#import "EHIMapping.h"

#define EHIChicagoLocation  [[CLLocation alloc] initWithLatitude:41.8631225 longitude:-87.7675238]
#define EHIOhareLocation    [[CLLocation alloc] initWithLatitude:41.995592 longitude:-87.885729]
#define EHIMidwayLocation   [[CLLocation alloc] initWithLatitude:41.793059 longitude:-87.751574]
#define EHIHeathrowLocation [[CLLocation alloc] initWithLatitude:51.4775 longitude:-0.461389]
#define EHILondonLocation   [[CLLocation alloc] initWithLatitude:51.5287718 longitude:-0.2416796]

@interface EHIDebugLocationManager () <EHIUserListener>
@property (strong, nonatomic) NSHashTable *listeners;
@property (copy  , nonatomic) NSArray *routes;
@property (copy  , nonatomic) NSSet *previousRegions;
// location mocking
@property (strong, nonatomic) NSTimer *locationTimer;
@property (assign, nonatomic) NSUInteger currentRouteWaypoint;
@property (assign, nonatomic) UIBackgroundTaskIdentifier backgroundTask;
@property (assign, nonatomic, readonly) NSUInteger nextRouteWaypoint;
@end

@implementation EHIDebugLocationManager

+ (EHIDebugLocationManager *)sharedInstance
{
    static EHIDebugLocationManager *sharedInstance;
    static dispatch_once_t onceToken;
    
    dispatch_once(&onceToken, ^{
        sharedInstance = [EHIDebugLocationManager new];
    });
    
    return sharedInstance;
}

- (instancetype)init
{
    if(self = [super init]) {
        _listeners = [NSHashTable weakObjectsHashTable];
        _routeIntervalDuration = 10.0;
        
        [self invalidateRoutes];
    }
    
    return self;
}

//
// Helpers
//

- (void)invalidateRoutes
{
    NSMutableArray *routes = @[
        @[EHIChicagoLocation, EHIOhareLocation, EHIMidwayLocation],
        @[EHILondonLocation, EHIHeathrowLocation],
    ].mutableCopy;
    
    EHIUser *user = [EHIUser currentUser];
    EHIUserRental *currentRental  = user.currentRentals.all.firstObject;
    EHIUserRental *upcomingRental = user.upcomingRentals.all.firstObject;

    [self insertRouteForLocation:currentRental.returnLocation radius:EHIGeofencingReturnRadius routes:routes];
    [self insertRouteForLocation:upcomingRental.pickupLocation radius:EHIGeofencingPickupRadius routes:routes];
    
    self.routes = routes;
}

- (void)insertRouteForLocation:(EHILocation *)location radius:(NSInteger)geofenceRadius routes:(NSMutableArray *)routes
{
    if(!location) {
        [routes addObject:@[]];
        return;
    }
    
    CLLocationDegrees outsideLongitude = location.position.longitude + (3 * geofenceRadius / CLLocationDistanceDegreesToMeters);
    
    CLLocation *rentalLocation  = [[CLLocation alloc] initWithLatitude:location.position.latitude longitude:location.position.longitude];
    CLLocation *outsideLocation = [[CLLocation alloc] initWithLatitude:location.position.latitude longitude:outsideLongitude];
    
    [routes addObject:@[rentalLocation, outsideLocation]];
}

# pragma mark - Accessors

- (NSArray *)currentRoute
{
    return self.routes[self.routeType];
}

- (NSUInteger)nextRouteWaypoint
{
    return self.currentRouteWaypoint == self.currentRoute.count - 1 ? 0 : self.currentRouteWaypoint + 1;
}

- (BOOL)isUpdatingLocation
{
    return self.locationTimer.isValid;
}

# pragma mark - Setters

- (void)setRouteType:(EHIDebugRouteType)routeType
{
    if(_routeType == routeType) {
        return;
    }
    
    _routeType = routeType;
    
    self.previousRegions      = nil;
    self.currentRouteWaypoint = 0;
    
    [self stopUpdatingLocation];
    [self invalidateRoutes];
}

# pragma mark - Updates

- (void)startUpdatingLocation
{
    [self startLocationTimer];
}

- (void)stopUpdatingLocation
{
    [self.locationTimer invalidate];
    
    self.previousRegions      = nil;
    self.currentRouteWaypoint = 0;
}

# pragma mark - Timer

- (void)startLocationTimer
{
    [self.locationTimer invalidate];
    
    // start next waypoint timer with background option
    UIBackgroundTaskIdentifier newTask = [[UIApplication sharedApplication] beginBackgroundTaskWithExpirationHandler:^{
        [self endBackgroundLocationUpdates];
    }];
    
    // end old task and save new id
    [self endBackgroundLocationUpdates];
    [self setBackgroundTask:newTask];
    
    self.locationTimer = [NSTimer timerWithTimeInterval:1/15.0 target:self selector:@selector(updateUserLocation:) userInfo:[NSDate date] repeats:YES];
    [[NSRunLoop mainRunLoop] addTimer:self.locationTimer forMode:NSRunLoopCommonModes];
}

- (void)updateUserLocation:(NSTimer *)timer
{
    NSTimeInterval difference = [[NSDate date] timeIntervalSinceDate:timer.userInfo];
    CGFloat progress = difference / self.routeIntervalDuration;
    
    if(progress >= 1.0) {
        [self userReachedWaypoint];
        return;
    }
    
    CLLocationCoordinate2D startCoordinate = self.currentRoute[self.currentRouteWaypoint].coordinate;
    CLLocationCoordinate2D endCoordinate = self.currentRoute[self.nextRouteWaypoint].coordinate;
    
    CLLocationDegrees latDelta = progress * (endCoordinate.latitude - startCoordinate.latitude);
    CLLocationDegrees lonDelta = progress * (endCoordinate.longitude - startCoordinate.longitude);

    CLLocationCoordinate2D userCoordinate = CLLocationCoordinate2DOffset(startCoordinate, latDelta, lonDelta);
    CLLocation *userLocation = [[CLLocation alloc] initWithLatitude:userCoordinate.latitude longitude:userCoordinate.longitude];
    
    // notify listeners
    [self notifyListeners:@selector(debugLocationManager:didUpdateLocation:) withObject:userLocation];
    [self updateRegionsForUserCoordinate:userCoordinate];
}

- (void)userReachedWaypoint
{
    // move to next waypoint and restart
    [self setCurrentRouteWaypoint:self.nextRouteWaypoint];
    [self startLocationTimer];
}

//
// Helpers
//

- (void)endBackgroundLocationUpdates
{
    [[UIApplication sharedApplication] endBackgroundTask:self.backgroundTask];
    self.backgroundTask = UIBackgroundTaskInvalid;
}

# pragma mark - Regions

- (void)updateRegionsForUserCoordinate:(CLLocationCoordinate2D)coordinate
{
    NSArray *currentRegions = [EHIGeofenceManager sharedInstance].monitoredRegions.select(^(CLCircularRegion *region) {
        return [region containsCoordinate:coordinate];
    });
    
    // determine which regions are new
    NSMutableSet *newRegions = [NSMutableSet setWithArray:currentRegions];
    [newRegions minusSet:self.previousRegions];
    
    // update current regions
    self.previousRegions = [NSSet setWithArray:currentRegions];
    
    // notify listeners
    for(CLRegion *region in newRegions) {
        [self notifyListeners:@selector(debugLocationManager:didEnterRegion:) withObject:region];
    }
}

# pragma mark - Listeners

- (void)addListener:(id<EHIDebugLocationManagerListener>)listener
{
    [self.listeners addObject:listener];
}

- (void)notifyListeners:(SEL)selector withObject:(id)object
{
    for(id<EHIDebugLocationManagerListener> listener in self.listeners) {
        [self notifyListener:listener selector:selector withObject:object];
    }
}

- (void)notifyListener:(id<EHIDebugLocationManagerListener>)listener selector:(SEL)selector withObject:(id)object
{
    if([listener respondsToSelector:selector]) {
        IGNORE_PERFORM_SELECTOR_WARNING(
            [listener performSelector:selector withObject:self withObject:object];
        );
    }
}

@end
