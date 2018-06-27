//
//  EHINotificationManager.h
//  Enterprise
//
//  Created by fhu on 11/20/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHINotificationInterfaces.h"

@class EHIUser;
typedef void (^EHINotificationEnableHandler)(BOOL);
@interface EHINotificationManager : NSObject

+ (instancetype)sharedInstance;

/** Performs any initial setup and consumes launch notifications */
+ (void)prepareToLaunch;
/** Consume a local notification received while the app is running */
- (void)handleLocalNotification:(UNNotificationContent *)notification;
/** Consume a local notification where a user selected a specific action */
- (void)handleLocalNotification:(UNNotificationContent *)notification withActionIdentifier:(NSString *)identifier handler:(void (^)())handler;
/** Add a notification request **/
- (void)requestLocalNotification:(UNNotificationRequest *)notification;

/** Prompts the user to receive notifications. Calls handler with result of prompt. */
- (void)promptRegistrationIfNeeded:(void (^)(BOOL shouldNotify))handler;
/** Tell the system we want to register for notifications */
- (void)registerForNotifications;
/** Tell the system we want to register for notifications and set all notification settings to the defaults */
- (void)registerForNotificationsWithDefaults:(EHINotificationEnableHandler)completion;
/** @c YES if the user is currently receiving notifications */
- (BOOL)isRegisteredForNotifications;

/** Schedules current and upcoming rental notifications based on settings */
- (void)scheduleRentalNotificationsForUser:(EHIUser *)user;
/** Clears all rental related notifications */
- (void)clearRentalNotifications;

@end
