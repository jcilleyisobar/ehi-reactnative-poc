//
//  EHIAlertViewBuilder+Notifications.m
//  Enterprise
//
//  Created by Alex Koller on 12/15/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHIAlertViewBuilder+Notifications.h"
#import "EHINotificationManager.h"

@implementation EHIAlertViewBuilder (Notifications)

+ (void)showWithNotification:(UNNotificationContent *)notification
{
    EHIAlertViewBuilder *builder = [EHIAlertViewBuilder new];
    
    // text
    builder.title(notification.title).message(notification.body);

    UNNotificationCategory *category = [UNNotificationCategory categoryForIdentifier:notification.categoryIdentifier];
    
    // get notification actions
    NSArray<UNNotificationAction *> *actions = category.actions ?: @[];

    // add buttons with default cancel
    (actions ?: @[]).each(^(UNNotificationAction *action) {
        builder.button(action.title);
    });
    
    builder.cancelButton(nil);

    // show
    builder.show(^(NSInteger index, BOOL canceled) {
        if(!canceled) {
            UNNotificationAction *action = actions[index];

            [[EHINotificationManager sharedInstance] handleLocalNotification:notification withActionIdentifier:action.identifier handler:nil];
        }
    });
}

@end
