//
//  EHIReservationBookStateBuilder.m
//  Enterprise
//
//  Created by Rafael Ramos on 22/03/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIReservationBookStateBuilder.h"
#import "EHIPriceFormatter.h"

typedef NS_ENUM(NSInteger, EHIReservationBookStateSubtitleFlow) {
    EHIReservationBookStateSubtitleFlowNone,
    EHIReservationBookStateSubtitleFlowCurrencyConversion,
    EHIReservationBookStateSubtitleFlowBilling,
    EHIReservationBookStateSubtitleFlowPriceDifference,
    EHIReservationBookStateSubtitleFlowProfilePaymentCreditCard,
    EHIReservationBookStateSubtitleFlowPrepayCreditCard,
    EHIReservationBookStateSubtitleFlowPayLater
};

@interface EHIReservationBookStateBuilder ()
@property (assign, nonatomic, readonly) EHIReservationBookStateSubtitleFlow subtitleState;
@property (assign, nonatomic) BOOL isModify;
@property (assign, nonatomic) BOOL isPrepay;
@property (assign, nonatomic) BOOL isCollectingNewCreditCard;
@property (assign, nonatomic) BOOL isBusinessTrip;
@property (assign, nonatomic) BOOL didAddCreditCard;
@property (copy  , nonatomic) NSString *discountAlias;
@property (copy  , nonatomic) id<EHIPriceContext> aCurrencyConversion;
@property (copy  , nonatomic) EHICarClassPriceDifference *aPriceDifference;
@property (copy  , nonatomic) EHIUserPaymentMethod *aPaymentMethod;
@end

@implementation EHIReservationBookStateBuilder

- (EHIReservationBookStateBuilder *(^)(BOOL))modify
{
    return ^(BOOL modify) {
        self.isModify = modify;
        return self;
    };
}

- (EHIReservationBookStateBuilder *(^)(BOOL))prepay
{
    return ^(BOOL prepay) {
        self.isPrepay = prepay;
        return self;
    };
}

- (EHIReservationBookStateBuilder *(^)(BOOL))collectsNewCreditCard
{
    return ^(BOOL collectsNewCreditCard) {
        self.isCollectingNewCreditCard = collectsNewCreditCard;
        return self;
    };
}

- (EHIReservationBookStateBuilder *(^)(BOOL))businessTrip
{
    return ^(BOOL businessTrip) {
        self.isBusinessTrip = businessTrip;
        return self;
    };
}

- (EHIReservationBookStateBuilder *(^)(BOOL))addedCreditCard
{
    return ^(BOOL addedCreditCard) {
        self.didAddCreditCard = addedCreditCard;
        return self;
    };
}

- (EHIReservationBookStateBuilder *(^)(NSString *))discount
{
    return ^(NSString *discount) {
        self.discountAlias = discount;
        return self;
    };
}

- (EHIReservationBookStateBuilder *(^)(id<EHIPriceContext>))currencyConversion;
{
    return ^(id<EHIPriceContext> currencyConversion) {
        self.aCurrencyConversion = currencyConversion;
        return self;
    };
}

- (EHIReservationBookStateBuilder *(^)(EHICarClassPriceDifference *))priceDifference
{
    return ^(EHICarClassPriceDifference *priceDifference) {
        self.aPriceDifference = priceDifference;
        return self;
    };
}

- (EHIReservationBookStateBuilder *(^)(EHIUserPaymentMethod *))paymentMethod
{
    return ^(EHIUserPaymentMethod *paymentMethod) {
        self.aPaymentMethod = paymentMethod;
        return self;
    };
}

# pragma mark - State

- (EHIReservationBookStateTitleFlow)titleState
{
    if(self.isAddPaymentFlow) {
        return EHIReservationBookStateTitleFlowAddPayment;
    }
    
    return self.isModify ? EHIReservationBookStateTitleFlowModify : EHIReservationBookStateTitleFlowDefault;
}

- (EHIReservationBookStateSubtitleFlow)subtitleState
{
    BOOL isBilling = self.isBilling;
    if(isBilling) {
        return EHIReservationBookStateSubtitleFlowBilling;
    }
    
    BOOL isBillingInBusinessTrip = self.isBillingInBusinessTrip;
    if(isBillingInBusinessTrip) {
        return EHIReservationBookStateSubtitleFlowBilling;
    }

    BOOL isPaymentFromProfile = self.isPaymentFromProfile;
    if(isPaymentFromProfile) {
        BOOL hasPriceDifference = self.aPriceDifference != nil;
        if(hasPriceDifference) {
            return EHIReservationBookStateSubtitleFlowPriceDifference;
        }

        BOOL isPaymentUsingCreditCard = self.isPaymentUsingCreditCard && self.didAddCreditCard;
        if(isPaymentUsingCreditCard) {
            return EHIReservationBookStateSubtitleFlowProfilePaymentCreditCard;
        }

        return EHIReservationBookStateSubtitleFlowBilling;
    }

    BOOL isPrepayWithCreditCard = self.isPrepayWithCreditCard;
    if(isPrepayWithCreditCard) {
        return EHIReservationBookStateSubtitleFlowPrepayCreditCard;
    }

    BOOL hasPriceDifference = self.aPriceDifference != nil;
    if(hasPriceDifference) {
        return EHIReservationBookStateSubtitleFlowPriceDifference;
    }
    
    BOOL eligibleForCurrencyConversion = self.isEligibleForCurrencyConversion;
    if(eligibleForCurrencyConversion) {
        return EHIReservationBookStateSubtitleFlowCurrencyConversion;
    }
    
    BOOL isPrepayWithoutCreditCard = self.isPrepayWithoutCreditCard;
    if(isPrepayWithoutCreditCard ) {
        return EHIReservationBookStateSubtitleFlowNone;
    }

    return EHIReservationBookStateSubtitleFlowPayLater;
}

# pragma mark - Title & subtitle computation

- (NSString *)title
{
    switch (self.titleState) {
        case EHIReservationBookStateTitleFlowAddPayment:
            return EHILocalizedString(@"reservations_review_add_payment_button_title", @"ADD PAYMENT METHOD", @"");
        case EHIReservationBookStateTitleFlowDefault:
            return EHILocalizedString(@"reservations_review_book_button_title", @"BOOK RENTAL", @"");
        case EHIReservationBookStateTitleFlowModify:
            return EHILocalizedString(@"reservations_modify_review_book_button_title", @"MODIFY RENTAL", @"");
    }
}

- (NSString *)subtitle
{
    switch(self.subtitleState){
        case EHIReservationBookStateSubtitleFlowNone:
            return nil;
        case EHIReservationBookStateSubtitleFlowCurrencyConversion:
            return self.currencyConversionSubtitle;
        case EHIReservationBookStateSubtitleFlowBilling:
            return self.billingSubtitle;
        case EHIReservationBookStateSubtitleFlowPriceDifference:
            return self.unpaidRefundSubtitle;
        case EHIReservationBookStateSubtitleFlowProfilePaymentCreditCard:
            return self.creditCardSubtitle;
        case EHIReservationBookStateSubtitleFlowPrepayCreditCard:
            return EHILocalizedString(@"review_prepay_pay_now", @"Pay now", @"");
        case EHIReservationBookStateSubtitleFlowPayLater:
            return EHILocalizedString(@"reservations_review_book_button_subtitle", @"Pay when you pick up", @"");
    }
}

# pragma mark - Helpers

- (BOOL)isPrepayWithCreditCard
{
    return self.isPrepay && self.didAddCreditCard;
}

- (BOOL)isPrepayWithoutCreditCard
{
    return self.isPrepay && !self.didAddCreditCard;
}

- (BOOL)isPaymentUsingCreditCard
{
    return self.aPaymentMethod.paymentType == EHIUserPaymentTypeCard;
}

- (NSString *)creditCardSubtitle
{
    NSString *cardFormat   = EHILocalizedString(@"review_prepay_credit_card_book_button_subtitle", @"Charge to: #{method}", @"");
    NSString *maskedNumber = self.aPaymentMethod.customDisplayName ?: @"";
    
    return [cardFormat ehi_applyReplacementMap:@{
        @"method" : maskedNumber
    }];
}

- (BOOL)isPaymentFromProfile
{
    return self.aPaymentMethod != nil && self.aPaymentMethod.lastFour != nil;
}

- (NSString *)unpaidRefundSubtitle
{
    BOOL hasRefund = self.aPriceDifference.viewDifference.amount < 0;
    NSString *unpaidFormat = hasRefund
        ? EHILocalizedString(@"review_payment_refund_amount_action", @"#{amount} Refund Amount", @"")
        : EHILocalizedString(@"review_payment_unpaid_amount_action", @"#{amount} Unpaid Amount", @"");
    
    EHIPrice *price = self.aPriceDifference.paymentDifference;
    [price updateWithDictionary:@{
        @"amount" : @(fabs(price.amount))
    }];
    
    NSString *unpaidPrice = [EHIPriceFormatter format:price].string;
    
    return [unpaidFormat ehi_applyReplacementMap:@{
        @"amount" : unpaidPrice
    }];
}

- (BOOL)isBilling
{
    return self.aPaymentMethod != nil && self.aPaymentMethod.paymentType == EHIUserPaymentTypeBilling;
}

- (BOOL)isBillingInBusinessTrip
{
    return self.isBusinessTrip && self.aPaymentMethod != nil
		&& (self.aPaymentMethod.isCustom || self.aPaymentMethod.isExisting);
}

- (NSString *)billingSubtitle
{
    NSString *billingFormat = EHILocalizedString(@"reservations_review_book_button_billing_subtitle", @"Bill to #{account}", @"");
    NSString *account       = self.discountAlias ?: self.aPaymentMethod.alias ?: self.aPaymentMethod.maskedBillingNumber;
    
    return [billingFormat ehi_applyReplacementMap:@{
        @"account" : account ? : @""
    }];
}

- (BOOL)isEligibleForCurrencyConversion
{
    return [self.aCurrencyConversion eligibleForCurrencyConvertion];
}

- (NSString *)currencyConversionSubtitle
{
    NSString *title = EHILocalizedString(@"review_prepay_na_book_button_subtitle", @"#{amount} - converted charge", @"");
    NSString *price = [EHIPriceFormatter format:[self.aCurrencyConversion paymentPrice]].string;
    
    return [title ehi_applyReplacementMap:@{
        @"amount" : price ?: @""
    }];
}

- (BOOL)isAddPaymentFlow
{
    BOOL prepayWithoutPaymentMethod = self.isPrepay && self.aPaymentMethod == nil;
    return (prepayWithoutPaymentMethod && !self.isModify)
        || (prepayWithoutPaymentMethod && self.isModify && self.isCollectingNewCreditCard);
}

@end
