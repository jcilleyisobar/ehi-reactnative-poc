//
//  EHIAlertViewBuilder+Notifications.h
//  Enterprise
//
//  Created by Alex Koller on 12/15/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHINotificationInterfaces.h"
#import "EHIAlertViewBuilder.h"

@interface EHIAlertViewBuilder (Notifications)

+ (void)showWithNotification:(UNNotificationContent *)notification;

@end
