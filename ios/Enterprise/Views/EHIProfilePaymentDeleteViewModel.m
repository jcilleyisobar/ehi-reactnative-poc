//
//  EHIProfilePaymentDeleteViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 10/4/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIProfilePaymentDeleteViewModel.h"
#import "EHIViewModel_Subclass.h"
#import "EHIToastManager.h"
#import "EHIReservationBuilder.h"

@interface EHIProfilePaymentDeleteViewModel ()
@property (strong, nonatomic) EHIUserPaymentMethod *paymentMethod;
@end

@implementation EHIProfilePaymentDeleteViewModel

+ (instancetype)initWithPaymentMethod:(EHIUserPaymentMethod *)paymentMethod
{
    EHIProfilePaymentDeleteViewModel *model = [[EHIProfilePaymentDeleteViewModel alloc] initWithModel:paymentMethod];
    model.paymentMethod = paymentMethod;
    
    return model;
}

- (void)didBecomeActive
{
    [EHIAnalytics changeScreen:EHIScreenRemoveBillingModal state:EHIScreenRemoveBillingModal];
    [EHIAnalytics trackState:nil];
}

- (void)present:(EHIInfoModalAction)action
{
    [super present:^BOOL(NSInteger index, BOOL canceled) {
        if(index == 0 && !canceled) {
            [EHIAnalytics trackAction:EHIAnalyticsDeleteBillingModalActionDelete handler:self.encodeReservation];
        } else {
            [EHIAnalytics trackAction:EHIAnalyticsDeleteBillingModalActionClose handler:self.encodeReservation];
        }
        return action(index, canceled);
    }];
}

- (EHIInfoModalButtonLayout)buttonLayout
{
    return EHIInfoModalButtonLayoutSecondaryDismiss;
}

- (NSString *)firstButtonTitle
{
    return EHILocalizedString(@"profile_payment_options_delete_action_text", @"Delete", @"");
}

- (NSString *)secondButtonTitle
{
    return EHILocalizedString(@"standard_cancel_button_title", @"Cancel", @"");
}

- (void)showToast
{
    BOOL isBilling = self.paymentMethod.paymentType == EHIUserPaymentTypeBilling;
    NSString *message = isBilling ? EHILocalizedString(@"profile_payment_options_delete_billing_success", @"Billing code deleted.", @"") : EHILocalizedString(@"profile_payment_options_delete_credit_card_success", @"Credit card deleted.", @"");
    
    [EHIToastManager showMessage:message];
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
