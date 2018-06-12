//
//  EHIInfoModalModelable.m
//  Enterprise
//
//  Created by Ty Cobb on 7/1/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIInfoModalModelable.h"

@implementation EHICarClassExtra (InfoModal)

- (NSString *)infoTitle
{
    return self.name;
}

- (NSString *)infoDetails
{
    return self.longDetails;
}

- (NSString *)infoId
{
    return self.code;
}

@end

@implementation EHILocationPolicy (InfoModal)

- (NSString *)infoTitle
{
    return self.name;
}

- (NSString *)infoDetails
{
    return self.text;
}

- (NSString *)infoId
{
    return self.codeText;
}

@end

@implementation EHIPromotionContract (InfoModal)

- (NSString *)infoTitle
{
    return EHILocalizedString(@"weekend_special_educational_dialog_title", @"Weekend Special now in the app!", @"");
}

- (NSString *)infoDetails
{
    return EHILocalizedString(@"weekend_special_educational_dialog_subtitle", @"Book from Friday to Monday and enjoy a special rate.", @"");
}

- (NSString *)infoId
{
    return self.uid;
}

@end

@implementation EHIUserPaymentMethod (InfoModal)

- (NSString *)infoTitle
{
    return [self paymentTitle];
}

- (NSString *)infoDetails
{
    return [self paymentDetails];
}

- (NSString *)infoId
{
    return self.uid;
}

//
// Helpers
//

- (BOOL)isBilling
{
    return self.paymentType == EHIUserPaymentTypeBilling;
}

- (NSString *)paymentTitle
{
    NSString *title = [self isBilling] ? EHILocalizedString(@"profile_payment_options_delete_billing_title", @"Billing Code:", @"") : EHILocalizedString(@"profile_payment_options_delete_credit_card_title", @"Credit Card:", @"");

    NSString *name = self.customDisplayName;
    return [NSString stringWithFormat:@"%@ %@", title, name];
}

- (NSString *)paymentDetails
{
    return [self isBilling] ? EHILocalizedString(@"profile_payment_options_delete_billing_message", @"Would you like to delete this billing code? You will no longer to select it when renting.", @"") : EHILocalizedString(@"profile_payment_options_delete_credit_card_message", @"Would you like to delete this credit card?", @"");
}

@end
