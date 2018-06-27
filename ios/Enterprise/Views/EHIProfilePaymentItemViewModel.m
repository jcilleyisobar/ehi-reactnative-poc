//
//  EHIProfilePaymentItemViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 9/22/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIProfilePaymentItemViewModel.h"
#import "EHIUserPaymentMethod.h"
#import "EHICreditCardFormatter.h"

@interface EHIProfilePaymentItemViewModel ()
@property (strong, nonatomic) EHIUserPaymentMethod *paymentMethod;
@end

@implementation EHIProfilePaymentItemViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        if([model isKindOfClass:[EHIUserPaymentMethod class]]) {
            self.paymentMethod = model;
        }
    }
    
    return self;
}

- (NSString *)title
{
    NSString *billingTitle    = EHILocalizedString(@"profile_payment_options_billing_numbers_title", @"BILLING NUMBERS", @"");
    NSString *creditCardTitle = EHILocalizedString(@"profile_payment_options_credit_cards_title", @"CREDIT CARDS", @"");
    
    return self.isBilling ? billingTitle : creditCardTitle;
}

- (NSString *)paymentTitle
{
    return self.paymentMethod.customDisplayName;
}

- (NSString *)preferredTitle
{
    return EHILocalizedString(@"profile_preferred_label", @"Preferred", @"");
}

- (NSAttributedString *)paymentSubtitle
{
    if(!self.isBilling) {
        NSString *expirationTitle = self.expirationTitle;
        NSString *expireDate      = [self.paymentMethod.expirationDate ehi_stringWithFormat:@"MM/YY"] ?: @"";
        expirationTitle = [expirationTitle ehi_applyReplacementMap:@{
            @"date" : expireDate
        }];
        
        EHIFontStyle fontStyle = self.isExpired ? EHIFontStyleBold : EHIFontStyleLight;
        EHIAttributedStringBuilder *builder = EHIAttributedStringBuilder.new.appendText(expirationTitle).fontStyle(fontStyle, 16.0f);
        
        return builder.string;
    }
    
    return nil;
}

- (NSString *)cardImage {
    return [EHICreditCardFormatter cardIconForCardType:self.paymentMethod.cardType];
}

- (NSString *)expirationTitle
{
    return self.isExpired ? EHILocalizedString(@"profile_payment_options_expired_text", @"Expired #{date}", @"") : EHILocalizedString(@"profile_payment_options_expires_text", @"Expires #{date}", @"");
}

- (BOOL)isPreferred
{
    return self.paymentMethod.isPreferred;
}

- (BOOL)isExpired
{
    return self.paymentMethod.isExpired;
}

//
// Helpers

- (BOOL)isBilling
{
    return self.paymentMethod.paymentType == EHIUserPaymentTypeBilling;
}

@end
