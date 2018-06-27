//
//  UNNotificationCategory+Utility.h
//  Enterprise
//
//  Created by Alex Koller on 11/30/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

// categories
extern NSString * const EHINotificationCategoryCurrent;
extern NSString * const EHINotificationCategoryUpcoming;
extern NSString * const EHINotificationCategoryWayfinding;
extern NSString * const EHINotificationCategoryAfterHours;

// actions
extern NSString * const EHINotificationActionLocation;
extern NSString * const EHINotificationActionCallBranch;
extern NSString * const EHINotificationActionGasStations;
extern NSString * const EHINotificationActionWayfinding;
extern NSString * const EHINotificationActionAfterHours;

#import "EHINotificationInterfaces.h"

@interface UNNotificationCategory (Utility)

/** Notification category used when showing current rentals */
+ (instancetype)currentRentalCategory;
/** Notification category used when showing upcoming rentals */
+ (instancetype)upcomingRentalCategory;
/** Notification category used when approaching airport pickup location */
+ (instancetype)wayfindingCategory;
/** Notification category used when approaching after hours return location */
+ (instancetype)afterHoursCategory;
/** Set containing each custom category for the app */
+ (NSSet *)allCategories;

/** The registered @c UIUserNotificationCategory for a given identifier */
+ (UNNotificationCategory *)categoryForIdentifier:(NSString *)identifier;
/** All currently registered notification categories for the app */
+ (NSArray<UNNotificationCategory *> *)notificationCategories;

@end
