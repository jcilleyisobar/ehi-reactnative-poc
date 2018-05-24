//
//  UNNotificationRequest+Utility.h
//  Enterprise
//
//  Created by Alex Koller on 11/30/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHINotificationInterfaces.h"
#import "EHIGeonotification.h"

NS_ASSUME_NONNULL_BEGIN

extern NSString * const EHINotificationRentalConfirmationNumberKey;
extern NSString * const EHINotificationRentalPickupLocationIdKey;
extern NSString * const EHINotificationRentalPickupLocationNameKey;
extern NSString * const EHINotificationRentalPickupLocationPhoneKey;
extern NSString * const EHINotificationRentalPickupLocationLatitudeKey;
extern NSString * const EHINotificationRentalPickupLocationLongitudeKey;
extern NSString * const EHINotificationRentalReturnLocationIdKey;

@interface UNNotificationRequest (Utility)

/** Creates a @c UNNotificationRequest with the given current rental */
+ (nullable instancetype)notificationForCurrentRental:(EHIUserRental *)rental;
/** Creates a @c UNNotificationRequest with the given upcoming rental */
+ (nullable instancetype)notificationForUpcomingRental:(EHIUserRental *)rental;

/** only for test notifications, notifications get fired even when pickup or return date is in the past */
+ (nullable instancetype)notificationForUpcomingRental:(EHIUserRental *)rental debug:(BOOL)debug;
+ (nullable instancetype)notificationForCurrentRental:(EHIUserRental *)rental debug:(BOOL)debug;

/** Creates a @c UNNotificationRequest from a geonotification */
+ (instancetype)notificationForGeonotification:(EHIGeonotification *)geonotification;

/** The common user info for rental notifications */
+ (NSDictionary *)userInfoForRental:(EHIUserRental *)rental;

@end

NS_ASSUME_NONNULL_END
