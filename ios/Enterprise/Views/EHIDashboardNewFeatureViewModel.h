//
//  EHIDashboardNotificationsViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 12/28/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIDashboardNewFeatureViewModel : EHIViewModel

@property (copy, nonatomic, readonly) NSString *title;
@property (copy, nonatomic, readonly) NSString *subtitle;
@property (copy, nonatomic, readonly) NSString *acceptTitle;
@property (copy, nonatomic, readonly) NSString *denyTitle;

- (void)didInteract;

@end
