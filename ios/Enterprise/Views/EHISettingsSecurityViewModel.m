//
//  EHISettingsSecurityViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 1/21/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHISettingsSecurityViewModel.h"
#import "EHISecurityManager.h"

@implementation EHISettingsSecurityViewModel

+ (NSArray *)viewModels
{
    EHISettingsSecurityViewModel *touchId = [EHISettingsSecurityViewModel new];
    touchId.row           = EHISettingsSecurityRowTouchId;
    touchId.title         = EHILocalizedString(@"settings_security_row_touch_id_title", @"Touch ID Unlock", @"");
    touchId.details       = [self detailsForRow:EHISettingsSecurityRowTouchId];
    touchId.settingsKey   = NSStringFromProperty(useTouchId);
    touchId.toggleModifer = ^BOOL(BOOL enabled) {
        return enabled && [EHISecurityManager sharedInstance].canEvaluateBiometrics;
    };
    
    return @[touchId];
}

//
// Helpers
//

+ (NSString *)detailsForRow:(EHISettingsSecurityRow)row
{
    switch(row) {
        case EHISettingsSecurityRowTouchId:
            return EHILocalizedString(@"settings_security_row_touch_id_detail_title", @"You will be able to use touch id to reauthenticate yourself when trying to edit your profile.", @"");
    }
}

@end
