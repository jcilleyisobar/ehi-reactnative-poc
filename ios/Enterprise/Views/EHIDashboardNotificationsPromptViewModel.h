//
//  EHIDashboardNotificationsPromptViewModel.h
//  Enterprise
//
//  Created by Marcelo Rodrigues on 08/06/2018.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIDashboardNotificationsPromptViewModel : EHIViewModel <MTRReactive>

@property (copy, nonatomic, readonly) NSString *title;
@property (copy, nonatomic, readonly) NSString *bullet;
@property (copy, nonatomic, readonly) NSString *acceptTitle;
@property (copy, nonatomic, readonly) NSString *denyTitle;

- (void)acceptNotifications;
- (void)denyNotifications;

@end
