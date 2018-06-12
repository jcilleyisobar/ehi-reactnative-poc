//
//  EHISettingsNotificationViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 11/30/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHISettingsNotificationViewModel.h"

NS_ASSUME_NONNULL_BEGIN

@implementation EHISettingsNotificationViewModel

+ (NSArray *)viewModels
{
    EHISettingsNotificationViewModel *pickupViewModel = [EHISettingsNotificationViewModel new];
    pickupViewModel.row             = EHISettingsNotificationRowPickup;
    pickupViewModel.title           = EHILocalizedString(@"settings_item_text_pickup_notification", @"Remind me to pick up my vehicle", @"");
    pickupViewModel.subtitle        = [EHISettings stringForRentalReminderTime:[EHISettings sharedInstance].upcomingRentalReminderTime];
    pickupViewModel.hidesDetailIcon = YES;
    
    EHISettingsNotificationViewModel *returnViewModel = [EHISettingsNotificationViewModel new];
    returnViewModel.row             = EHISettingsNotificationRowReturn;
    returnViewModel.title           = EHILocalizedString(@"settings_item_text_return_notification", @"Remind me to return my vehicle", @"");
    returnViewModel.subtitle        = [EHISettings stringForRentalReminderTime:[EHISettings sharedInstance].currentRentalReminderTime];
    returnViewModel.hidesDetailIcon = YES;
    
    EHISettingsNotificationViewModel *rentalAssistantViewModel  = [EHISettingsNotificationViewModel new];
    rentalAssistantViewModel.row         = EHISettingsNotificationRowRentalAssistant;
    rentalAssistantViewModel.title       = EHILocalizedString(@"notification_settings_rental_assistant_title", @"Enterprise Rental Assistant", @"");
    rentalAssistantViewModel.details     = [self detailsForRow:EHISettingsNotificationRowRentalAssistant];
    rentalAssistantViewModel.settingsKey = NSStringFromProperty(useRentalAssistant);
    
    return @[pickupViewModel, returnViewModel, rentalAssistantViewModel];
}

//
// Helpers
//

+ (nullable NSString *)detailsForRow:(EHISettingsNotificationRow)row
{
    switch(row) {
        case EHISettingsNotificationRowRentalAssistant:
            return EHILocalizedString(@"notification_settings_rental_assistant_details", @"This feature can recognize when you are near an Enterprise location for your reservation and provide you with helpful information so that you can have an even better rental experience.", @"");
        default:
            return nil;
    }
}

@end

NS_ASSUME_NONNULL_END
