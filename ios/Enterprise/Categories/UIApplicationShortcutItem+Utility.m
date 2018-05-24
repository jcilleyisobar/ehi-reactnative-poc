//
//  UIApplicationShortcutItem+Utility.m
//  Enterprise
//
//  Created by Alex Koller on 10/1/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "UIApplicationShortcutItem+Utility.h"

NSString * const EHIShortcutVersionUserInfoKey = @"ShortcutVersionUserInfoKey";

@implementation UIApplicationShortcutItem (Utility)

+ (instancetype)itemForLocation:(EHILocation *)location
{
    NSString *type     = EHIShortcutTypeLocationDetailsKey;
    NSString *title    = location.displayName;
    NSString *subtitle = EHILocalizedString(@"application_shortcut_location_details_subtitle", @"View Details", @"");
    UIApplicationShortcutIcon *icon = [UIApplicationShortcutIcon iconWithType:UIApplicationShortcutIconTypeLocation];
    NSDictionary *userInfo = [self userInfoWithDictionary:@{
        @key(location.uid) : location.uid ?: @"",
    }];
    
    return [[UIApplicationShortcutItem alloc] initWithType:type localizedTitle:title localizedSubtitle:subtitle icon:icon userInfo:userInfo];
}

+ (instancetype)itemForRental:(EHIUserRental *)rental
{
    NSString *type     = EHIShortcutTypeRentalDetailsKey;
    NSString *title    = EHILocalizedString(@"application_shortcut_upcoming_rental_title", @"Upcoming Rental", @"");
    NSString *subtitle = EHILocalizedString(@"application_shortcut_upcoming_rental_subtitle", @"View Details", @"");
    UIApplicationShortcutIcon *icon = [UIApplicationShortcutIcon iconWithType:UIApplicationShortcutIconTypeCompose];
    NSDictionary *userInfo = [self userInfoWithDictionary:@{
        // provide a uid for identifying shortcut items
        @key(rental.uid)                : rental.confirmationNumber ?: @"",
        @key(rental.confirmationNumber) : rental.confirmationNumber ?: @"",
        @key(rental.lastName)           : rental.lastName ?: @"",
    }];
    
    return [[UIApplicationShortcutItem alloc] initWithType:type localizedTitle:title localizedSubtitle:subtitle icon:icon userInfo:userInfo];
}

//
// Helpers
//

+ (NSDictionary *)userInfoWithDictionary:(NSDictionary *)dictionary
{
    NSMutableDictionary *userInfo           = [dictionary mutableCopy];
    userInfo[EHIShortcutVersionUserInfoKey] = [NSBundle versionShort];
    
    return [userInfo copy];
}

@end
