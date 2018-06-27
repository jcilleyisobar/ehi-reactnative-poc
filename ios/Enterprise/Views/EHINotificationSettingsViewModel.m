//
//  EHINotificationSettingsViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 11/30/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHINotificationSettingsViewModel.h"
#import "EHINotificationSettingsOptionViewModel.h"
#import "EHINotificationManager.h"
#import "EHISettings.h"

NS_ASSUME_NONNULL_BEGIN

@interface EHINotificationSettingsViewModel ()
@property (assign, nonatomic) EHINotificationSettingsType type;
@end

@implementation EHINotificationSettingsViewModel

- (instancetype)initWithType:(EHINotificationSettingsType)type;
{
    if(self = [super init]) {
        _type    = type;
        _options = [self constructOptions];
    }
    
    return self;
}

//
// Helpers
//

- (NSArray *)constructOptions
{
    return @(0).upTo(EHIRentalReminderTimeCount - 1).map(^(NSNumber *typeNumber) {
        EHIRentalReminderTime type = [typeNumber unsignedIntegerValue];
        NSString *option = [EHISettings stringForRentalReminderTime:type];
        
        EHINotificationSettingsOptionViewModel *viewModel = [EHINotificationSettingsOptionViewModel new];
        viewModel.title    = option;
        viewModel.selected = type == self.currentNotificationType;
        
        return viewModel;
    });
}

- (EHIRentalReminderTime)currentNotificationType
{
    if(self.type == EHINotificationSettingsTypePickup) {
        return [EHISettings sharedInstance].upcomingRentalReminderTime;
    } else {
        return [EHISettings sharedInstance].currentRentalReminderTime;
    }
}

# pragma mark - Accessors

- (NSString *)title
{
    switch(self.type) {
        case EHINotificationSettingsTypePickup:
            return EHILocalizedString(@"notification_setting_title_pickup", @"Pick Up Reminder", @"");
        case EHINotificationSettingsTypeReturn:
            return EHILocalizedString(@"notification_setting_title_return", @"Return Reminder", @"");
    }
}

# pragma mark - Actions

- (void)selectOptionAtIndex:(NSUInteger)index
{
    [[EHINotificationManager sharedInstance] promptRegistrationIfNeeded:^(BOOL shouldNotify) {
        if(shouldNotify) {
            [self saveRentalReminderTime:(EHIRentalReminderTime)index];
        }
    }];
}

//
// Helpers
//

- (void)saveRentalReminderTime:(EHIRentalReminderTime)time
{
    // change settings
    if(self.type == EHINotificationSettingsTypePickup) {
        [EHISettings sharedInstance].upcomingRentalReminderTime = time;
    } else {
        [EHISettings sharedInstance].currentRentalReminderTime = time;
    }
    
    // back to settings after selection
    self.router.transition
        .pop(1).start(nil);
}

@end

NS_ASSUME_NONNULL_END
