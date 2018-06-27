//
//  EHIGeofenceManager.h
//  Enterprise
//
//  Created by Alex Koller on 12/10/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

@class EHIUser;

typedef void (^EHIGeofenceEnableHandler)(BOOL);
@interface EHIGeofenceManager : NSObject

/** If user has enabled always on location services that allow geofencing */
@property (assign, nonatomic, readonly) BOOL hasAlwaysAuthorization;
/** List of @c CLRegion objects describing currently monitored regions */
@property (copy  , nonatomic, readonly) NSArray *monitoredRegions;

/** Initializes location manager for responding to location based app launches */
+ (void)prepareToLaunch;
/** The shared instance for consumers to access the manager */
+ (EHIGeofenceManager *)sharedInstance;

/** Prompts user to enable Always authorized location services. Attempts to monitor geofences if successful. */
- (void)requestAlwaysAuthorization;

/** Sets up geofences for user's rental locations. If @c user is @c nil, clears geofences. */
- (void)monitorRentalGeofencesForUser:(EHIUser *)user;
/** Clear all rental related geofences */
- (void)clearRentalGeofences;
- (void)enableGeofencingWithCompletion:(EHIGeofenceEnableHandler)completion;
- (void)disableGeofencing;

@end
