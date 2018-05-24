//
//  EHIDashboardNotificationsViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 12/28/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHIDashboardNewFeatureViewModel.h"

@implementation EHIDashboardNewFeatureViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _title       = EHILocalizedString(@"notification_prompt_title", @"ENHANCE YOUR RENTAL WITH NEW APP FEATURES", @"");
        _subtitle    = EHILocalizedString(@"notification_prompt_content", @"Turn on notifications to get custom alerts and enable location access for real-time information from the Enterprise Rental Assistant.", @"");
        _acceptTitle = EHILocalizedString(@"notification_prompt_accept", @"ENABLE", @"");
        _denyTitle   = EHILocalizedString(@"notification_prompt_deny", @"NOT NOW", @"");
    }

    return self;
}

@end
