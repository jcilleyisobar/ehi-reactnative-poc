//
//  EHINotificationManager+Private.m
//  Enterprise
//
//  Created by Rafael Ramos on 20/12/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHINotificationManager+Private.h"

@implementation EHINotificationManager (Private)

- (UNUserNotificationCenter *)center
{
    return UNUserNotificationCenter.currentNotificationCenter;
}

- (NSArray<UNNotificationRequest *> *)pendingNotifications
{
    __block NSArray *pendingNotifications = [NSArray array];
    
    ehi_dispatch_sync(^(EHIDispatchSyncCompletionBlock completion) {
        [self.center getPendingNotificationRequestsWithCompletionHandler:^(NSArray<UNNotificationRequest *> * _Nonnull requests) {
            pendingNotifications = requests;
            ehi_call(completion)();
        }];
    });
    
    return [pendingNotifications copy];
}

- (void)removePendingNotificationRequestsMatching:(BOOL (^)(UNNotificationRequest *notification))criteria
{
    NSArray *identifiers = (self.pendingNotifications ?: @[]).select(criteria).map(^(UNNotificationRequest *notification){
        return notification.identifier;
    });
    
    [self.center removePendingNotificationRequestsWithIdentifiers:identifiers];
}

@end
