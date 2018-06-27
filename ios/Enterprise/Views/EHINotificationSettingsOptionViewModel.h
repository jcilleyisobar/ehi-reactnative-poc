//
//  EHINotificationSettingsOptionViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 11/30/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHINotificationSettingsOptionViewModel : EHIViewModel <MTRReactive>

/** The description for this option */
@property (copy  , nonatomic) NSString *title;
/** @c if this option is selected */
@property (assign, nonatomic) BOOL selected;

@end
