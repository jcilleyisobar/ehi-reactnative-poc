//
//  EHIConfirmationPaymentOptionViewModel.m
//  Enterprise
//
//  Created by Michael Place on 7/7/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIConfirmationPaymentOptionViewModel.h"
#import "EHIUserPaymentMethod.h"
#import "EHIReservationPaymentMethod.h"
#import "EHICreditCardFormatter.h"
#import "EHIPriceFormatter.h"
#import "EHIWebViewModel.h"
#import "EHIAnalytics.h"

@interface EHIConfirmationPaymentOptionViewModel ()
@property (copy  , nonatomic) NSString *title;
@property (copy  , nonatomic) NSString *paymentTitle;
@property (copy  , nonatomic) NSString *value;
@property (copy  , nonatomic) NSString *policies;
@property (copy  , nonatomic) NSString *cardImage;
@property (assign, nonatomic) BOOL hidePoliciesLink;
@end

@implementation EHIConfirmationPaymentOptionViewModel

- (void)updateWithModel:(EHIModel *)model
{
    [super updateWithModel:model];
    
    if([model isKindOfClass:[EHIUserPaymentMethod class]]) {
        self.paymentMethod = (EHIUserPaymentMethod *)model;
    } else if([model isKindOfClass:[EHIReservationPaymentMethod class]]) {
        self.reservationMethod = (EHIReservationPaymentMethod *)model;
    }
}

- (void)setPaymentMethod:(EHIUserPaymentMethod *)paymentMethod
{
    BOOL isBilling    = paymentMethod.paymentType == EHIUserPaymentTypeBilling;
    self.hidePoliciesLink = YES;
    self.paymentTitle = isBilling
        ? EHILocalizedString(@"reservation_confirmation_payment_billing_title", @"Corporate Code", @"title for the confirmation payment options cell when a corporate code was used for payment")
        : EHILocalizedString(@"reservation_confirmation_payment_pick_up_title", @"Pay At Pick-Up Counter", @"title for the confirmation payment options cell when the user has chosen to pay at the counter");
    
    self.value = isBilling
        ? paymentMethod.customDisplayName
        : EHILocalizedString(@"review_payment_options_payment_subtitle", @"Your credit card will not be charged. This just saves you time at the counter.", @"payment subtitle for the review payment options cell.");
}

- (void)setReservationMethod:(EHIReservationPaymentMethod *)reservationMethod
{
    EHICreditCard *creditCard = reservationMethod.creditCard;
    
    self.value            = [EHICreditCardFormatter maskCardNumber:creditCard.cardNumber];
    self.cardImage        = [EHICreditCardFormatter cardIconForCardType:creditCard.type];
    self.paymentTitle     = EHILocalizedString(@"reservation_confirmation_prepay_payment_title", @"Credit Card Charged", @"");
    self.policies         = EHILocalizedString(@"general_prepay_policies", @"Prepayment Policy Terms & Conditions", @"");
    self.hidePoliciesLink = NO;
}

- (void)showPolicies
{
    [EHIAnalytics trackAction:EHIAnalyticsResActionPrepayPolicy handler:nil];

    [[[EHIWebViewModel alloc] initWithType:EHIWebContentTypePrepayTermsAndConditions] push];
}

# pragma mark - Accessors

- (NSString *)title
{
    return EHILocalizedString(@"reservation_confirmation_payment_method_section_title", @"PAYMENT METHOD", @"");
}

@end
