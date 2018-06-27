//
//  EHIDashboardNotificationsPromptViewModel.m
//  Enterprise
//
//  Created by Marcelo Rodrigues on 08/06/2018.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIDashboardNotificationsPromptViewModel.h"
#import "EHINotificationManager.h"
#import "EHISettings.h"

@implementation EHIDashboardNotificationsPromptViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]){
        _title       = [EHILocalizedString(@"notification_promdspt_title", @"Get Notification About Your Rental and Special Offers", "") uppercaseString];

        NSString *bulletOne = EHILocalizedString(@"notification_prompt_bullet_point_one", @"Stay up to date on your rental", @"");
        NSString *bulletTwo = EHILocalizedString(@"notification_prompt_bullet_point_two", @"Get the special offers for your rentals", @"");
        NSString *bulletThree = EHILocalizedString(@"notification_prompt_bullet_point_three", @"We promise not to spam you with notifications", @"");

        _bullet = EHIAttributedStringBuilder.new
            .appendText(@"•").space.appendText(bulletOne).newline
            .appendText(@"•").space.appendText(bulletTwo).newline
            .appendText(@"•").space.appendText(bulletThree).string.string;

        _acceptTitle = EHILocalizedString(@"notification_prompt_accept", @"ENABLE", @"");
        _denyTitle   = EHILocalizedString(@"notification_prompt_deny", @"NOT NOW", @"");
    }

    return self;
}

# pragma mark - Actions

- (void)acceptNotifications
{
    [self didInteract];

    [[EHINotificationManager sharedInstance] registerForNotificationsWithDefaults:nil];
}

- (void)denyNotifications
{
    [self didInteract];
}

- (void)didInteract
{
    [EHISettings didPromptForNotifications];
}

@end
