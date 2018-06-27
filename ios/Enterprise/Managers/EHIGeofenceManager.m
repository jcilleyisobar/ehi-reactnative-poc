//
//  EHIGeofenceManager.m
//  Enterprise
//
//  Created by Alex Koller on 12/10/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHIGeofenceManager.h"
#import "EHINotificationManager.h"
#import "EHIUser.h"
#import "EHIGeonotification.h"
#import "EHICacheLocation.h"
#import "EHIDataStore.h"
#import "EHIServices+Location.h"
#import "EHISettings.h"
#import "EHIDebugLocationManager.h"
#import "EHIAnalytics.h"

@interface EHIGeofenceManager () <CLLocationManagerDelegate, EHIDebugLocationManagerListener>
@property (strong, nonatomic) CLLocationManager *locationManager;
@property (assign, nonatomic, readonly) BOOL supportsGeofencing;
@property (copy  , nonatomic) EHIGeofenceEnableHandler completion;
@end

@implementation EHIGeofenceManager

+ (EHIGeofenceManager *)sharedInstance
{
    static EHIGeofenceManager *_sharedInstance;
    static dispatch_once_t onceToken;
    dispatch_once_on_main_thread(&onceToken, ^{
        _sharedInstance = [self new];
    });
    
    return _sharedInstance;
}

- (instancetype)init
{
    if(self = [super init]) {
        _locationManager = [CLLocationManager new];
        _locationManager.delegate = self;
    }
    
    return self;
}

+ (void)prepareToLaunch
{
    // initialize our CLLocationManager for handling region delegate callbacks
    [self sharedInstance];
    
#if defined(DEBUG) || defined(UAT)
    [[EHIDebugLocationManager sharedInstance] addListener:[self sharedInstance]];
#endif
}

# pragma mark - Accessors

- (BOOL)supportsGeofencing
{
    return [CLLocationManager isMonitoringAvailableForClass:[CLCircularRegion class]];
}

- (BOOL)hasAlwaysAuthorization
{
    return [CLLocationManager authorizationStatus] == kCLAuthorizationStatusAuthorizedAlways;
}

- (NSArray *)monitoredRegions
{
    return self.locationManager.monitoredRegions.allObjects;
}

# pragma mark - Registration

- (void)requestAlwaysAuthorization
{
    // let system handle permission request
    if([CLLocationManager authorizationStatus] == kCLAuthorizationStatusNotDetermined) {
        [self.locationManager requestAlwaysAuthorization];
    }
    // otherwise, reset internal setting and direct user to location settings
    else {
        [self updateUseRentalAssistant:NO];
        [self promptAlwaysAuthorizationSettings];
    }
}

//
// Helpers
//

- (void)promptAlwaysAuthorizationSettings
{
    // show an alert for this error
    EHIAlertViewBuilder *alert = EHIAlertViewBuilder.new
        .title(EHILocalizedString(@"locations_unvailable_error_title", @"Locations Services Unavailable", @"Title for location services error"))
        .message(EHILocalizedString(@"location_services_always_denied_error", @"Geofencing requires your location access settings for Enterprise to be set to 'Always'.", @""))
        .cancelButton(EHILocalizedString(@"alert_okay_title", @"Okay", @"Title for alert 'okay' button"))
        .button(EHILocalizedString(@"alert_settings_button", @"Settings", @"Title for location alert settings button"));
    
    alert.show(^(NSInteger index, BOOL canceled) {
        if(!canceled) {
            [UIApplication ehi_openURL:[NSURL URLWithString:UIApplicationOpenSettingsURLString]];
        }
    });
}

- (void)updateUseRentalAssistant:(BOOL)enable
{
    [[EHISettings sharedInstance] setUseRentalAssistant:enable];

    if(!enable) {
        [self disableGeofencing];
    }
}

# pragma mark - CLLocationManagerDelegate

- (void)locationManager:(CLLocationManager *)manager didChangeAuthorizationStatus:(CLAuthorizationStatus)status
{
    BOOL authorized = status == kCLAuthorizationStatusAuthorizedAlways;
    if(authorized) {
        [self monitorRentalGeofencesForUser:[EHIUser currentUser]];
    } else {
        // reset setting
        [self updateUseRentalAssistant:NO];
    }

    BOOL allowed = status == kCLAuthorizationStatusAuthorizedAlways || status == kCLAuthorizationStatusAuthorizedWhenInUse;
    [self completeWithAllowed:allowed];
}

- (void)locationManager:(CLLocationManager *)manager didEnterRegion:(CLRegion *)region
{
    // process all geonotifications for this region
    [EHIDataStore find:[EHIGeonotification class] handler:^(NSArray *models) {
        models.each(^(EHIGeonotification *notification) {
            if([notification.region.identifier isEqualToString:region.identifier]) {
                [self handleGeonotification:notification];
            }
        });
    }];
}

- (void)locationManager:(CLLocationManager *)manager monitoringDidFailForRegion:(CLRegion *)region withError:(NSError *)error
{
    [self renderRegionError:error];
}

//
// Helpers
//

- (void)handleGeonotification:(EHIGeonotification *)geonotification
{
    // check to see if notification is time relevant (shortly before rental start/end)
    if(!geonotification.shouldFire) {
        return;
    }

    UNNotificationRequest *notificaton = [UNNotificationRequest notificationForGeonotification:geonotification];
    
    // background
    if([UIApplication sharedApplication].applicationState != UIApplicationStateActive) {
        [[EHINotificationManager sharedInstance] requestLocalNotification:notificaton];
    }
    // foreground
    else {
        [EHIAlertViewBuilder showWithNotification:notificaton.content];
    }
}

# pragma mark - Creation

- (void)monitorRentalGeofencesForUser:(EHIUser *)user
{
    [self clearRentalGeofences];
    
    // skip if no user, turned off, or not allowed
    if(!user || ![EHISettings sharedInstance].useRentalAssistant || !self.supportsGeofencing) {
        return;
    }
    
    [self insertHoursForLocations:user.rentalLocations handler:^{
        [self monitorRegionsForCurrentRentals:user.currentRentals.all upcomingRentals:user.upcomingRentals.all];
    }];
}

- (void)monitorRegionsForCurrentRentals:(NSArray *)currentRentals upcomingRentals:(NSArray *)upcomingRentals
{
    // schedule return help for after hours
    (currentRentals ?: @[]).each(^(EHIUserRental *rental) {
        if(rental.isReturningAfterHours) {
            [self monitorRentalForAfterHours:rental];
        }
    });
    
    // schedule pickup assistance at airports
    (upcomingRentals ?: @[]).each(^(EHIUserRental *rental) {
        if(rental.pickupLocation.type == EHILocationTypeAirport) {
            [self monitorRentalForWayfinding:rental];
        }
    });
}

//
// Helpers
//

- (void)insertHoursForLocations:(NSArray *)locations handler:(void (^)(void))handler
{
    [EHIDataStore find:[EHICacheLocation class] handler:^(NSArray *models){
        // map our cached locations
        NSDictionary *cache = models.map(^(EHICacheLocation *model) {
            return model.locationId ? @[model.locationId, model] : nil;
        }).dict;
        
        // update time zones we have while marking locations for which we're missing time zone info
        NSMutableArray *locationsWithoutHours = [NSMutableArray new];
        locations.each(^(EHILocation *location) {
            EHICacheLocation *cacheLocation = cache[location.uid];
            
            if(cacheLocation.hours) {
                location.hours = cacheLocation.hours;
            } else {
                [locationsWithoutHours addObject:location];
            }
        });
        
        if(locationsWithoutHours.count == 0) {
            ehi_call(handler)();
        } else {
            [self fetchHoursForLocations:locationsWithoutHours updateCache:cache handler:handler];
        }
    }];
}

- (void)fetchHoursForLocations:(NSArray *)locations updateCache:(NSDictionary *)cache handler:(void (^)(void))handler
{
    dispatch_group_t group = dispatch_group_create();
    
    // combined common objects by id, @"location.uid" : @[EHILocation, EHILocation, ...]
    NSDictionary *locationsMap = locations.groupBy(^(EHILocation *location) { return location.uid; });
    
    // fetch details for each unique id
    locationsMap.allKeys.each(^(NSString *locationId) {
        dispatch_group_enter(group);
        
        EHILocation *location = [EHILocation modelWithDictionary:@{ @key(location.uid) : locationId }];
        [[EHIServices sharedInstance] updateHoursForLocation:location handler:^(EHILocation *detailedLocation, EHIServicesError *error) {
            if(!error.hasFailed) {
                // get or create our cache record
                EHICacheLocation *cacheLocation = cache[locationId] ?: [EHICacheLocation new];
                
                // update and save the cache record
                [cacheLocation updateWithLocation:detailedLocation];
                [EHIDataStore save:cacheLocation handler:nil];
                
                // update our locations
                NSArray *locations = locationsMap[locationId];
                locations.each(^(EHILocation *location) {
                    location.hours = cacheLocation.hours;
                });
            }
            
            dispatch_group_leave(group);
        }];
    });
    
    dispatch_group_notify(group, dispatch_get_main_queue(), ^{
        ehi_call(handler)();
    });
}

# pragma mark - Removal

- (void)clearRentalGeofences
{
    // stop monitoring
    self.monitoredRegions.each(^(CLRegion *region) {
        [self.locationManager stopMonitoringForRegion:region];
    });
    
    // purge our notes for the regions
    [EHIDataStore purge:[EHIGeonotification class] handler:nil];
}

//
// Helpers
//

- (void)monitorRentalForAfterHours:(EHIUserRental *)rental
{
    EHIGeonotification *notification = [EHIGeonotification geonotificationForAfterHoursRental:rental];
    [EHIDataStore save:notification handler:nil];
    
    [self.locationManager startMonitoringForRegion:notification.region];
}

- (void)monitorRentalForWayfinding:(EHIUserRental *)rental
{
    EHIGeonotification *notification = [EHIGeonotification geonotificationForWayfindingRental:rental];
    [EHIDataStore save:notification handler:nil];
    
    [self.locationManager startMonitoringForRegion:notification.region];
}

- (void)completeWithAllowed:(BOOL)allowed
{
    ehi_call(self.completion)(allowed);
    self.completion = nil;
}

# pragma mark - Activation

- (void)enableGeofencingWithCompletion:(EHIGeofenceEnableHandler)completion
{
    self.completion = [completion copy];

    // update geofences or request authorization
    if(self.hasAlwaysAuthorization) {
        [self completeWithAllowed:YES];
        [self monitorRentalGeofencesForUser:[EHIUser currentUser]];
    } else {
        [self requestAlwaysAuthorization];
    }
}

- (void)disableGeofencing
{
    [self completeWithAllowed:NO];
    [self clearRentalGeofences];
}

# pragma mark - Errors

- (void)renderRegionError:(NSError *)error
{
    // fail silently
}

# pragma mark - EHIDebugLocationManagerListener

- (void)debugLocationManager:(EHIDebugLocationManager *)manager didEnterRegion:(CLRegion *)region
{
    [self locationManager:self.locationManager didEnterRegion:region];
}

@end
