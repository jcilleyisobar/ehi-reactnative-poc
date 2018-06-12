//
//  EHINotificationManager+Private.h
//  Enterprise
//
//  Created by Rafael Ramos on 20/12/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHINotificationManager.h"

@interface EHINotificationManager (Private)

@property (weak, nonatomic, readonly) UNUserNotificationCenter *center;
@property (copy, nonatomic, readonly) NSArray<UNNotificationRequest *> *pendingNotifications;

- (void)removePendingNotificationRequestsMatching:(BOOL (^)(UNNotificationRequest *notification))criteria;

@end
