//
//  EHIDashboardLocationPromptViewModel.m
//  Enterprise
//
//  Created by Marcelo Rodrigues on 21/06/2018.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIDashboardLocationPromptViewModel.h"
#import "EHINotificationManager.h"
#import "EHILocationManager.h"
#import "EHISettings.h"
#import "EHIUser.h"


@implementation EHIDashboardLocationPromptViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]){
        _title       = [EHILocalizedString(@"location_prompt_title", @"Get Smart Updates about Your Rental Based on Your Location", "") uppercaseString];
        _mainText = EHILocalizedString(@"location_prompt_description", @"Enable this feature and select \"Always Allow\" to receive smart updates about your rental such as return instructions as you near the drop-off location.", @"");

        _acceptTitle = EHILocalizedString(@"notification_prompt_accept", @"ENABLE", @"");
        _denyTitle   = EHILocalizedString(@"notification_prompt_deny", @"NOT NOW", @"");
    }

    return self;
}

# pragma mark - Actions

- (void)acceptLocation
{
    [self didInteract];

    [[EHILocationManager sharedInstance] locationsAvailableWithHandler:^(BOOL locationsAvailable, NSError *error) {
        BOOL notRegisteredForNotifications = ![[EHINotificationManager sharedInstance] isRegisteredForNotifications];
        BOOL isLogged = [EHIUser currentUser] != nil;

        if(locationsAvailable && notRegisteredForNotifications && isLogged){
            [[EHINotificationManager sharedInstance] registerForNotificationsWithDefaults:nil];
        }
    }];
}

- (void)denyLocation
{
    [self didInteract];
}

- (void)didInteract
{
    [EHISettings didPromptForNotifications];
    [EHISettings didPromptForLocationInUpcomingRental];
}

@end
