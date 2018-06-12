//
//  UNNotificationCategory+Utility.m
//  Enterprise
//
//  Created by Alex Koller on 11/30/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "UNNotificationCategory+Utility.h"

// category
NSString * const EHINotificationCategoryCurrent    = @"currentCategory";
NSString * const EHINotificationCategoryUpcoming   = @"upcomingCategory";
NSString * const EHINotificationCategoryWayfinding = @"wayfindingCategory";
NSString * const EHINotificationCategoryAfterHours = @"afterHoursCategory";

// actions
NSString * const EHINotificationActionLocation    = @"locationAction";
NSString * const EHINotificationActionCallBranch  = @"callAction";
NSString * const EHINotificationActionGasStations = @"gasStationsAction";
NSString * const EHINotificationActionWayfinding  = @"wayfindingAction";
NSString * const EHINotificationActionAfterHours  = @"afterHoursActions";

@implementation UNNotificationCategory (Utility)

+ (instancetype)currentRentalCategory
{
    UNNotificationAction *getDirections = [self getDirectionsAction];
    UNNotificationAction *gasStations   = [self gasStationsAction];

    return [UNNotificationCategory categoryWithIdentifier:EHINotificationCategoryCurrent
                                                  actions:@[getDirections, gasStations]
                                        intentIdentifiers:@[]
                                                  options:UNNotificationCategoryOptionNone];
}

+ (instancetype)upcomingRentalCategory
{
    UNNotificationAction *callBranch    = [self callBranchAction];
    UNNotificationAction *getDirections = [self getDirectionsAction];

    return [UNNotificationCategory categoryWithIdentifier:EHINotificationCategoryUpcoming
                                                  actions:@[callBranch, getDirections]
                                        intentIdentifiers:@[]
                                                  options:UNNotificationCategoryOptionNone];
}

+ (instancetype)wayfindingCategory
{
    UNNotificationAction *wayfinding = [self wayfindingAction];

    return [UNNotificationCategory categoryWithIdentifier:EHINotificationCategoryWayfinding
                                                  actions:@[wayfinding]
                                        intentIdentifiers:@[]
                                                  options:UNNotificationCategoryOptionNone];
}

+ (instancetype)afterHoursCategory
{
    UNNotificationAction *afterHours = [self afterHoursAction];

    return [UNNotificationCategory categoryWithIdentifier:EHINotificationCategoryAfterHours
                                                  actions:@[afterHours]
                                        intentIdentifiers:@[]
                                                  options:UNNotificationCategoryOptionNone];
}

+ (NSSet *)allCategories
{
    return [NSSet setWithArray:@[
        [self currentRentalCategory],
        [self upcomingRentalCategory],
        [self wayfindingCategory],
        [self afterHoursCategory],
    ]];
}

//
// Helpers
//

+ (UNNotificationAction *)gasStationsAction
{
    return [UNNotificationAction actionWithIdentifier:EHINotificationActionGasStations
                                                title:EHILocalizedString(@"notification_gas_station_button", @"Gas Stations", @"")
                                              options:UNNotificationActionOptionNone];
}

+ (UNNotificationAction *)getDirectionsAction
{
    return [UNNotificationAction actionWithIdentifier:EHINotificationActionLocation
                                                title:EHILocalizedString(@"notification_get_directions_button", @"Get Directions", @"")
                                              options:UNNotificationActionOptionNone];
}

+ (UNNotificationAction *)callBranchAction
{
    return [UNNotificationAction actionWithIdentifier:EHINotificationActionCallBranch
                                                title:EHILocalizedString(@"notifications_call_branch_button", @"Call Branch", @"")
                                              options:UNNotificationActionOptionNone];
}

+ (UNNotificationAction *)wayfindingAction
{
    return [UNNotificationAction actionWithIdentifier:EHINotificationActionWayfinding
                                                title:EHILocalizedString(@"notifications_wayfinding_button", @"Terminal Directions", @"")
                                              options:UNNotificationActionOptionNone];
}

+ (UNNotificationAction *)afterHoursAction
{
    return [UNNotificationAction actionWithIdentifier:EHINotificationActionAfterHours
                                                title:EHILocalizedString(@"notifications_after_hours_button", @"Return Instructions", @"")
                                              options:UNNotificationActionOptionNone];
}

# pragma mark - Helpers

+ (UNNotificationCategory *)categoryForIdentifier:(NSString *)identifier;
{
    return (self.notificationCategories ?: @[]).find(^(UNNotificationCategory *category) {
        return [category.identifier isEqualToString:identifier];
    });
}

+ (NSArray<UNNotificationCategory *> *)notificationCategories
{
    __block NSSet *allCategories = [NSSet set];
    
    dispatch_group_t group = dispatch_group_create();
    dispatch_group_enter(group);
    [UNUserNotificationCenter.currentNotificationCenter getNotificationCategoriesWithCompletionHandler:^(NSSet<UNNotificationCategory *> * _Nonnull categories) {
        allCategories = categories;
        dispatch_group_leave(group);
    }];
    
    dispatch_group_wait(group, DISPATCH_TIME_FOREVER);
    
    return allCategories.allObjects;
}

@end
