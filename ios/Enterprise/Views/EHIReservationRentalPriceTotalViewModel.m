//
//  EHIReservationRentalPriceTotalViewModel.m
//  Enterprise
//
//  Created by mplace on 3/26/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservationRentalPriceTotalViewModel.h"
#import "EHIViewModel_Subclass.h"
#import "EHIReservationBuilder.h"
#import "EHIPriceFormatter.h"

@interface EHIReservationRentalPriceTotalViewModel ()
@property (strong, nonatomic) EHICarClass *carClass;
@property (strong, nonatomic) id<EHIPriceContext> priceContext;
@property (strong, nonatomic) EHIPrice *price;
@property (strong, nonatomic) EHIPrice *paidAmount;
@property (assign, nonatomic) BOOL isSecretRate;
@property (assign, nonatomic) BOOL prepaySelected;
@property (assign, nonatomic) BOOL hasRefundAmount;
@end

@implementation EHIReservationRentalPriceTotalViewModel

- (instancetype)initWithModel:(id)model
               prepaySelected:(BOOL)prepaySelected
                   paidAmount:(EHIPrice *)paidAmount
                 actualAmount:(EHIPrice *)actualAmount
              showOtherOption:(BOOL)showOption
                       layout:(EHIReservationRentalPriceTotalLayout)layout
                 isSecretRate:(BOOL)isSecretRate
{
    if(self = [super initWithModel:model]) {
        _layout = layout;
        _showOtherPaymentOption = showOption;
        _prepaySelected = prepaySelected;
        _paidAmount   = paidAmount;
        _actualAmount = [self actualAmountWithPrice:actualAmount];
        _isSecretRate = isSecretRate;
        
        if([model isKindOfClass:[EHICarClass class]]) {
            _carClass = model;
            _priceContext    = [_carClass priceContextForPrepay:prepaySelected];
            _hasRefundAmount = [_carClass hasRefundAmount];
        }
        
        if([model isKindOfClass:[EHIPrice class]]) {
            _price = model;
        }
    }
    return self;
}

- (BOOL)showTopDivider
{
    return self.layout == EHIReservationRentalPriceTotalLayoutInvoice;
}

- (NSString *)totalTitle
{
    switch (self.layout) {
        case EHIReservationRentalPriceTotalLayoutReview:
            return self.prepaySelected
                ? EHILocalizedString(@"reservation_review_prepay_total_title", @"Total", @"title for an estimated total cell")
                : EHILocalizedString(@"reservation_review_estimated_total_title", @"Estimated Total", @"title for an estimated total cell");
        case EHIReservationRentalPriceTotalLayoutInvoice:
            return EHILocalizedString(@"trip_summary_final_total", @"Final Total", @"");
        case EHIReservationRentalPriceTotalLayoutUnpaidRefund:
            return self.hasRefundAmount
                ? EHILocalizedString(@"review_payment_refunded_at_end_title", @"Refunded at end of rental", @"Title for total amount cell in modify")
                : EHILocalizedString(@"review_payment_unpaid_amount_title", @"Unpaid Amount", @"Title for total amount cell in modify");
    }
}

- (NSString *)updatedTotalTitle
{
    return EHILocalizedString(@"review_payment_updated_total_title", @"Updated Total", @"Title for updated total label in modify");
}

- (NSAttributedString *)updatedTotalLabel
{
    return [self formatDifferencePrice:[self.priceContext viewPrice] isNegative:NO];
}

- (NSString *)paidAmountTitle
{
    return EHILocalizedString(@"review_payment_paid_amount_title", @"Paid Amount", @"Title for paid amount label in modify");
}

- (NSAttributedString *)paidAmountLabel
{
    if(self.paidAmount) {
		BOOL isNegative = self.layout == EHIReservationRentalPriceTotalLayoutUnpaidRefund;
		return [self formatDifferencePrice:self.paidAmount isNegative:isNegative];
    }

	return nil;
}

- (NSAttributedString *)formatDifferencePrice:(EHIPrice *)price isNegative:(BOOL)isNegative
{
    BOOL negative = price.amount < 0 || isNegative;
    return [EHIPriceFormatter format:price].size(EHIPriceFontSizeSmall).scalesChange(NO).fontStyle(EHIFontStyleLight).neg(negative).attributedString;
}

- (NSString *)originalTotal
{
    return EHILocalizedString(@"review_payment_original_total_title", @"Original Total", @"Title for original total label in modify");
}

- (NSString *)endOfRental
{
    if(self.layout == EHIReservationRentalPriceTotalLayoutUnpaidRefund) {
        return self.hasRefundAmount
            ? EHILocalizedString(@"review_payment_refunded_at_end_title", @"Refund at end of rental", @"Title for refund label in modify")
            : EHILocalizedString(@"review_payment_unpaid_at_end_title", @"Due at end of rental", @"Title for refund label in modify");
    }
    
    return nil;
}

- (NSString *)actualAmountWithPrice:(EHIPrice *)amountPrice
{
    BOOL isUnpaidRefund = self.layout == EHIReservationRentalPriceTotalLayoutUnpaidRefund;
    if(amountPrice && isUnpaidRefund) {
        NSString *actualAmount = EHILocalizedString(@"reservation_currency_refund", @"Actual amount: #{refund}", @"");
        NSString *amount = [EHIPriceFormatter format:amountPrice].string;
        return [actualAmount ehi_applyReplacementMap:@{
            @"refund": amount ?: @""
        }];
    }
    
    return nil;
}

- (BOOL)showsTransparency
{
    return self.priceContext.viewCurrencyDiffersFromSourceCurrency;
}

- (NSAttributedString *)total
{
    if (self.isSecretRate) {
        NSString* title = EHILocalizedString(@"reservation_price_unavailable", @"No Pricing Available", @"Reservation price button fallback text when price exists");
        return [EHIAttributedStringBuilder new].text(title).size(16.0).string;
    }
    
    BOOL scalesChange = YES;
    EHIPrice *price;
    switch(self.layout) {
        case EHIReservationRentalPriceTotalLayoutReview:
            price = self.priceContext.viewPrice;
            break;
        case EHIReservationRentalPriceTotalLayoutInvoice:
            scalesChange = NO;
            price = self.price;
            break;
        case EHIReservationRentalPriceTotalLayoutUnpaidRefund: {
            EHICarClassPriceDifference *priceDifference = self.carClass.unpaidRefundDifference;
            price = priceDifference.viewPrice;
            break;
        }
    }
    
    return [EHIPriceFormatter format:price].size(EHIPriceFontSizeMedium).scalesChange(scalesChange).attributedString;
}

- (NSString *)transparencyTitle
{
    NSString *format = nil;
    if([self.priceContext eligibleForCurrencyConvertion]) {
        if(self.layout == EHIReservationRentalPriceTotalLayoutUnpaidRefund) {
            format = EHILocalizedString(@"reservation_currency_conversion_title", @"All charges and refunds are processed in your destination's currency (#{currency_code}). Conversations are shown here for your convenience.", @"");
        } else {
            format = EHILocalizedString(@"car_class_details_transparency_total_na", @"You will be charged in your destination\'s currency (#{currency_code}). Your bank's foreign transaction fees may apply.", @"title for a transparency total cell");
        }
    } else {
        format = EHILocalizedString(@"car_class_details_transparency_total", @"You will pay in your destination's currency (#{currency_code})", @"title for a transparency total cell");
    }
    
    return [format ehi_applyReplacementMap:@{
        @"currency_code" : self.priceContext.paymentPrice.code ?: @""
    }];
}

- (NSAttributedString *)transparency
{
    if (self.isSecretRate) {
        NSString* title = EHILocalizedString(@"reservation_price_unavailable", @"No Pricing Available", @"Reservation price button fallback text when price exists");
        return [EHIAttributedStringBuilder new].text(title).size(16.0).string;
    } else {
        return [EHIPriceFormatter format:self.priceContext.paymentPrice]
        .scalesChange(NO).fontStyle(EHIFontStyleItalic).size(EHIPriceFontSizeSmall).attributedString;
    }
}

- (NSString *)otherPaymentOptionTotal
{
    if(self.showOtherPaymentOption) {
        EHIPrice *otherPrice = self.prepaySelected ? [self.carClass vehicleRateForPrepay:!self.prepaySelected].priceSummary.viewPrice : self.carClass.prepayDifference.viewPrice;
        
        NSString *otherPaymentText = self.prepaySelected
        ? EHILocalizedString(@"reservation_review_pay_later_na", @"or pay later for #{amount}", @"pay later option on review screen")
        : EHILocalizedString(@"reservation_review_pay_now_na", @"or save #{amount} if you pay now", @"pay now option on review screen");
        
        return [otherPaymentText ehi_applyReplacementMap:@{
            @"amount" : [EHIPriceFormatter format:otherPrice].abs(!self.prepaySelected).string
        }].lowercaseString;
    }
    
    return nil;
}

# pragma mark - Analytics

- (void)didTapChangePayment
{
    NSString *action = self.prepaySelected ? EHIAnalyticsResChangeBodyPayLater : EHIAnalyticsResChangeBodyPayNow;
    NSString *macro  = self.prepaySelected ? EHIAnalyticsMacroEventPayLaterSelected : EHIAnalyticsMacroEventPayNowSelected;
    
    [EHIAnalytics trackAction:action handler:^(EHIAnalyticsContext *context) {
        context.macroEvent = macro;
        
        [[EHIReservationBuilder sharedInstance] updateAnalyticsContext:context];
    }];
}

@end
