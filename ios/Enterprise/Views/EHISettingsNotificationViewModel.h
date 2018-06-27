//
//  EHISettingsNotificationViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 11/30/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHISettingsControlViewModel.h"

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSUInteger, EHISettingsNotificationRow) {
    EHISettingsNotificationRowPickup,
    EHISettingsNotificationRowReturn,
    EHISettingsNotificationRowRentalAssistant,
};

@interface EHISettingsNotificationViewModel : EHISettingsControlViewModel <MTRReactive>

@property (assign, nonatomic) EHISettingsNotificationRow row;

@end

NS_ASSUME_NONNULL_END