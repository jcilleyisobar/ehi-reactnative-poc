//
//  EHIDashboardLoyaltyPromptViewModel.h
//  Enterprise
//
//  Created by mplace on 5/18/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIDashboardLoyaltyPromptViewModel : EHIViewModel <MTRReactive>
@property (copy, nonatomic, readonly) NSString *iconImageName;
@property (copy, nonatomic, readonly) NSString *title;
@property (copy, nonatomic, readonly) NSString *details;
@property (copy, nonatomic, readonly) NSString *actionButtonTitle;

- (void)joinEnterprisePlus;

@end
