//
//  UNNotificationRequest+Utility.m
//  Enterprise
//
//  Created by Alex Koller on 11/30/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "UNNotificationRequest+Utility.h"
#import "EHIUserRental.h"
#import "EHISettings.h"

NS_ASSUME_NONNULL_BEGIN

NSString * const EHINotificationRentalConfirmationNumberKey       = @"com.ehi.EHINotificationRentalConfirmationNumberKey";
NSString * const EHINotificationRentalPickupLocationIdKey         = @"com.ehi.EHINotificationRentalPickupLocationIdKey";
NSString * const EHINotificationRentalPickupLocationNameKey       = @"com.ehi.EHINotificationRentalPickupLocationNameKey";
NSString * const EHINotificationRentalPickupLocationPhoneKey      = @"com.ehi.EHINotificationRentalPickupLocationPhoneKey";
NSString * const EHINotificationRentalPickupLocationLatitudeKey   = @"com.ehi.EHINotificationRentalPickupLocationLatitudeKey";
NSString * const EHINotificationRentalPickupLocationLongitudeKey  = @"com.ehi.EHINotificationRentalPickupLocationLongitudeKey";
NSString * const EHINotificationRentalReturnLocationIdKey         = @"com.ehi.EHINotificationRentalReturnLocationIdKey";

NSString * const EHINotificationActionViewKey                     = @"com.ehi.EHINotificationActionViewKey";

@implementation UNNotificationRequest (Utility)

# pragma mark - Rental

+ (nullable instancetype)notificationForCurrentRental:(EHIUserRental *)rental
{
    return [self notificationForCurrentRental:rental debug:NO];
}

+ (nullable instancetype)notificationForCurrentRental:(EHIUserRental *)rental debug:(BOOL)debug
{
    EHIRentalReminderTime reminderTime = [EHISettings sharedInstance].currentRentalReminderTime;
    NSDate *fireDate = [self fireDateForLocalDate:rental.returnDate inTimeZone:rental.returnLocation.timeZoneId withReminderTime:reminderTime];
    
    // no notification when fire date is already past
    if([fireDate ehi_isPast] && !debug) {
        return nil;
    }
    
    UNMutableNotificationContent *content = [UNMutableNotificationContent new];
    content.categoryIdentifier = EHINotificationCategoryCurrent;
    content.sound    = UNNotificationSound.defaultSound;
    content.body     = [self alertBodyWithName:rental.firstName location:rental.returnLocation displayDate:rental.returnTimeDisplay current:YES];
    content.userInfo = [self userInfoForRental:rental];
    content.title    = EHILocalizedString(@"notifications_alert_current_title", @"Current Trip", @"");
    
    UNCalendarNotificationTrigger *trigger = [UNCalendarNotificationTrigger triggerWithDateMatchingComponents:[fireDate ehi_components:NSCalendarUnitTime | NSCalendarDayGranularity] repeats:NO];

    return [self requestWithIdentifier:EHINotificationCategoryCurrent content:content trigger:trigger];
}

+ (nullable instancetype)notificationForUpcomingRental:(EHIUserRental *)rental
{
    return [self notificationForUpcomingRental:rental debug:NO];
}

+ (nullable instancetype)notificationForUpcomingRental:(EHIUserRental *)rental debug:(BOOL)debug
{
    EHIRentalReminderTime reminderTime = [EHISettings sharedInstance].upcomingRentalReminderTime;
    NSDate *fireDate = [self fireDateForLocalDate:rental.pickupDate inTimeZone:rental.pickupLocation.timeZoneId withReminderTime:reminderTime];
    
    // no notification when fire date is already past
    if([fireDate ehi_isPast] && !debug) {
        return nil;
    }
    
    UNMutableNotificationContent *content = [UNMutableNotificationContent new];
    content.categoryIdentifier = EHINotificationCategoryUpcoming;
    content.sound    = UNNotificationSound.defaultSound;
    content.body     = [self alertBodyWithName:rental.firstName location:rental.pickupLocation displayDate:rental.pickupTimeDisplay current:NO];
    content.userInfo = [self userInfoForRental:rental];
    content.title    = EHILocalizedString(@"notifications_alert_upcoming_title", @"Upcoming Trip", @"");
    
    UNCalendarNotificationTrigger *trigger = [UNCalendarNotificationTrigger triggerWithDateMatchingComponents:[fireDate ehi_components:NSCalendarUnitTime | NSCalendarDayGranularity] repeats:NO];
    
    return [self requestWithIdentifier:EHINotificationCategoryCurrent content:content trigger:trigger];
}

//
// Helpers
//

+ (NSDate *)fireDateForLocalDate:(NSDate *)date inTimeZone:(NSString *)timeZone withReminderTime:(EHIRentalReminderTime)time
{
    // convert local date to UTC
    NSDateComponents *components = [[NSCalendar currentCalendar] components:NSCalendarMinuteGranularity fromDate:date];
    components.timeZone          = [NSTimeZone timeZoneWithName:timeZone];
    NSDate *rentalDate           = [[NSCalendar currentCalendar] dateFromComponents:components];

    // adjust depending on preferences
    return [rentalDate dateByAddingTimeInterval:[self timeOffsetForRentalReminderTime:time]];
}

+ (NSTimeInterval)timeOffsetForRentalReminderTime:(EHIRentalReminderTime)time
{
    switch(time) {
        case EHIRentalReminderTimeThirtyMinutes:
            return -1 * 60 * 30;
        case EHIRentalReminderTimeTwoHours:
            return -1 * 60 * 60 * 2;
        case EHIRentalReminderTimeOneDay:
            return -1 * 60 * 60 * 24;
        default:
            return -1 * NSTimeIntervalSince1970;
    }
}

+ (NSString *)alertBodyWithName:(NSString *)name location:(EHILocation *)location displayDate:(NSString *)date current:(BOOL)current
{
    NSString *alertBody = current
        ? EHILocalizedString(@"notifications_current_rental_alert_message", @"Hey #{name}, your reservation is almost over. Be sure to drop off your vehicle at #{location-name} in #{time}", @"")
        : EHILocalizedString(@"notifications_upcoming_rental_alert_message", @"Hey #{name}, see you soon! Your vehicle will be ready for pickup at #{location-name} in #{time}", @"");
    
    alertBody = [alertBody ehi_applyReplacementMap:@{
        @"name"          : name.capitalizedString ?: @"",
        @"location-name" : location.displayName ?: @"",
        @"time"          : date ?: @""
    }];
    
    return alertBody;
}

# pragma mark - Geonotifications

+ (instancetype)notificationForGeonotification:(EHIGeonotification *)geonotification
{
    UNMutableNotificationContent *content = [UNMutableNotificationContent new];
    content.categoryIdentifier = [self notificationCategoryForGeonotificationType:geonotification.type];
    content.sound    = UNNotificationSound.defaultSound;
    content.body     = geonotification.message;
    content.userInfo = geonotification.userInfo;
    content.title    = EHILocalizedString(@"notifications_rental_assistant_title", @"Enterprise Rental Assistance", @"");
    
    UNTimeIntervalNotificationTrigger *trigger = [UNTimeIntervalNotificationTrigger triggerWithTimeInterval:1 repeats:NO];
    
    return [self requestWithIdentifier:content.categoryIdentifier content:content trigger:trigger];
}

//
// Helpers
//

+ (nullable NSString *)notificationCategoryForGeonotificationType:(EHIGeonotificationType)type
{
    switch(type) {
        case EHIGeonotificationTypeAfterHours:
            return EHINotificationCategoryAfterHours;
        case EHIGeonotificationTypeWayfinding:
            return EHINotificationCategoryWayfinding;
        default:
            return nil;
    }
}

# pragma mark - User Info

+ (NSDictionary *)userInfoForRental:(EHIUserRental *)rental
{
    return @{
        EHINotificationRentalConfirmationNumberKey        : rental.confirmationNumber           ?: @"",
        EHINotificationRentalPickupLocationNameKey        : rental.pickupLocation.localizedName ?: @"",
        EHINotificationRentalPickupLocationPhoneKey       : rental.pickupLocation.phoneNumber   ?: @"",
        EHINotificationRentalPickupLocationIdKey          : rental.pickupLocation.uid           ?: @"",
        EHINotificationRentalPickupLocationLatitudeKey    : @(rental.pickupLocation.position.latitude),
        EHINotificationRentalPickupLocationLongitudeKey   : @(rental.pickupLocation.position.longitude),
        EHINotificationRentalReturnLocationIdKey          : rental.returnLocation.uid           ?: @"",
    };
}

@end

NS_ASSUME_NONNULL_END
