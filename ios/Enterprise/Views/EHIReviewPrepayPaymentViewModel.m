//
//  EHIReviewPrepayPaymentViewModel.m
//  Enterprise
//
//  Created by cgross on 1/18/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIReviewPrepayPaymentViewModel.h"
#import "EHIReservationBuilder.h"

@implementation EHIReviewPrepayPaymentViewModel

- (instancetype)initWithModel:(id)model
{
    if (self = [super initWithModel:model]) {
        _prepayTitle = EHILocalizedString(@"reservation_review_prepay_payment_section_title", @"PAYMENT INFO", @"title for the review prepay payment method cell.");
        _termsTitle  = EHILocalizedString(@"review_prepay_policies", @"Prepayment Policy Terms & Conditions", @"");
        _removeCreditCardButton = EHILocalizedString(@"review_prepay_tap_to_change", @"REMOVE", @"");
        _creditCardButtonTitle  = [self creditCardAddedString];
    }
    return self;
}

- (NSString *)creditCardAddedString
{
    return self.builder.creditCardAdded
        ? EHILocalizedString(@"review_prepay_creditcard_added", @"CREDIT CARD ADDED", @"")
        : EHILocalizedString(@"review_prepay_add_payment_method", @"ADD PAYMENT METHOD", @"");
}

# pragma mark - Passthrough

- (EHIReservationBuilder *)builder
{
    return [EHIReservationBuilder sharedInstance];
}

@end
