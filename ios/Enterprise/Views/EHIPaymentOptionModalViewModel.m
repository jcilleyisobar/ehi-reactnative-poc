//
//  EHIPaymentOptionModalViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 2/18/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIPaymentOptionModalViewModel.h"
#import "EHIViewModel_Subclass.h"
#import "NSString+Formatting.h"
#import "EHIWebViewModel.h"

@implementation EHIPaymentOptionModalViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _prepayTitle      = [EHILocalizedString(@"payment_options_modal_info_prepay_title", @"Pay Now", @"") ehi_appendComponent:@":"];
        _prepayDetails    = EHILocalizedString(@"payment_options_modal_info_prepay_description", @"Your credit card will be charged at the time of booking. Cancel for a full refund up to 3 days before the rental starts", @"");
        
        _payLaterTitle    = [EHILocalizedString(@"payment_options_modal_info_pay_later_title", @"Pay Later", @"") ehi_appendComponent:@":"];
        _payLaterDetails  = EHILocalizedString(@"payment_options_modal_info_pay_later_description", @"You will be charged at the counter when you pick up your rental. Cancel anytime.", @"");
    }
    
    return self;
}

- (NSString *)title
{
    return EHILocalizedString(@"choose_your_rate_modal_title", @"Payment Options", @"");
}

- (BOOL)hidesCloseButton
{
    return YES;
}

- (NSString *)secondButtonTitle
{
    return EHILocalizedString(@"standard_close_button", @"CLOSE", @"");
}

- (NSString *)detailsNibName
{
    return @"EHIPaymentOptionModalView";
}

- (NSAttributedString *)prepayPolicy
{
    NSString *policiesString = EHILocalizedString(@"general_prepay_policies", @"PAY NOW TERMS & CONDITIONS", @"");
    
    __weak __typeof(self) welf = self;
    return [NSAttributedString
            attributedStringWithString:policiesString
            font:[UIFont ehi_fontWithStyle:EHIFontStyleLight size:18.0f]
            color:[UIColor ehi_lightGreenColor] tapHandler:^{
                welf.router.transition.dismiss.start(^{
                    [[[EHIWebViewModel alloc] initWithType:EHIWebContentTypePrepayTermsAndConditions] present];
                });
    }];
}

@end
