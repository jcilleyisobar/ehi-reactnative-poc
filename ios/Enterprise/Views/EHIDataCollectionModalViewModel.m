//
//  EHIDataCollectionModalViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 21/05/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIDataCollectionModalViewModel.h"
#import "EHIUser.h"

@implementation EHIDataCollectionModalViewModel

 - (NSString *)title
{
    return EHILocalizedString(@"modal_analytics_reminder_title", @"Data Collection Reminder", @"");
}

- (NSString *)details
{
    return EHILocalizedString(@"modal_analytics_reminder_details", @"In order to improve our services to you we...", @"");
}

 - (NSString *)firstButtonTitle
{
    return EHILocalizedString(@"modal_analytics_reminder_continue_button_title", @"CONTINUE", @"");
}

- (NSString *)secondButtonTitle
{
    return EHILocalizedString(@"modal_analytics_reminder_change_settings_button", @"CHANGE PRIVACY SETTINGS", @"");
}

- (void)present:(EHIInfoModalAction)action
{
    NSString *state = [EHIUser currentUser] != nil ? EHIAnalyticsDashStateNone : EHIAnalyticsDashStateUnauth;

    void (^handler)(EHIAnalyticsContext *context) = ^(EHIAnalyticsContext *context) {
        [context setRouterScreen:EHIScreenDashboard];
        context.state  = state;
    };
    
    [super present:^BOOL(NSInteger index, BOOL canceled) {
        if(!canceled && index == 1) {
            [EHIAnalytics trackAction:EHIAnalyticsDashActionDataCollectionChange handler:handler];
        } else {
            [EHIAnalytics trackAction:EHIAnalyticsDashActionDataCollectionContinue handler:handler];
        }
        
        return action(index, canceled);
    }];
}

@end
