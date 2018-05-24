//
//  EHIProfilePaymentAddViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 9/23/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIProfilePaymentAddViewModel.h"

@implementation EHIProfilePaymentAddViewModel

- (NSString *)title
{
    return (self.type == EHIProfilePaymentAddTypeSelect) ?
        EHILocalizedString(@"select_payment_add_credit_card_button", @"ADD NEW CREDIT CARD", @"").uppercaseString :
        EHILocalizedString(@"profile_payment_options_credit_card_action_text", @"ADD CREDIT CARD", @"");
}

@end
