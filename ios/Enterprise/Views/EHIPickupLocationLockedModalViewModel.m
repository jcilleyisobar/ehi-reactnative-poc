//
//  EHIPickupLocationLockedModalViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 11/16/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIPickupLocationLockedModalViewModel.h"
#import "EHIViewModel_Subclass.h"
#import "EHIConfiguration.h"
#import "EHIReservationBuilder.h"

@implementation EHIPickupLocationLockedModalViewModel

- (void)didBecomeActive
{
    [EHIAnalytics changeScreen:EHIScreenLocationLockedModal state:EHIScreenLocationLockedModal];
    [EHIAnalytics trackState:nil];
}

- (NSString *)title
{
    return EHILocalizedString(@"modify_location_cant_modify_title", @"", @"");
}

- (NSString *)details
{
    return EHILocalizedString(@"modify_location_cant_modify_message", @"", @"");
}

- (NSString *)firstButtonTitle
{
    return EHILocalizedString(@"standard_button_call", @"CALL US", @"");
}

- (NSString *)secondButtonTitle
{
    return EHILocalizedString(@"standard_close_button", @"CLOSE", @"");
}

- (BOOL)hidesCloseButton
{
    return YES;
}

- (EHIInfoModalButtonLayout)buttonLayout
{
    return EHIInfoModalButtonLayoutSecondaryDismiss;
}

- (void)present
{
    [self present:^BOOL(NSInteger index, BOOL canceled) {
        BOOL shouldCall = index == 0 && !canceled;
        
        if(shouldCall) {
            [EHIAnalytics trackAction:EHIAnalyticsLocationLockedModalActionCallUs handler:self.encodeReservation];
            
            NSString *number = [EHIConfiguration configuration].primarySupportPhone.number;
            [UIApplication ehi_promptPhoneCall:number];
        }
        
        [EHIAnalytics trackAction:EHIAnalyticsLocationLockedModalActionClose handler:self.encodeReservation];
        
        return YES;
    }];
}

//
// Helpers
//

- (void (^)(EHIAnalyticsContext *))encodeReservation
{
    return ^(EHIAnalyticsContext *context) {
        [[EHIReservationBuilder sharedInstance] updateAnalyticsContext:context];
    };
}

@end
