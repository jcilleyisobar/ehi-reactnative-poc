//
//  EHIPaymentOptionCellViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 1/28/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIPaymentOptionCellViewModel.h"
#import "EHIPriceFormatter.h"
#import "EHIUser.h"

@interface EHIPaymentOptionCellViewModel ()
@property (strong, nonatomic) EHICarClass *carClass;
@end

@implementation EHIPaymentOptionCellViewModel

# pragma mark - Model configuration

- (void)configureWithPaymentOption:(EHIReservationPaymentOption)paymentOption carClass:(EHICarClass *)carClass
{
    _paymentOption = paymentOption;
    _carClass      = carClass;
    
    switch (paymentOption) {
        case EHIReservationPaymentOptionPayNow:
            [self configurePayNow]; break;
        case EHIReservationPaymentOptionPayLater:
            [self configurePayLater]; break;
        case EHIReservationPaymentOptionRedeemPoints:
            [self configureRedemptionPoints]; break;
        default: break;
    }
}

- (void)configurePayNow
{
    _title      = EHILocalizedString(@"choose_your_rate_pay_now_title", @"Pay Now", @"");
    _layoutType = self.prepayEnabled ? EHIPaymentOptionLayoutEnabled : EHIPaymentOptionLayoutDisabled;
    
    if(_layoutType == EHIPaymentOptionLayoutEnabled) {
        _price = [self attributedPrice];
    } else {
        NSString *unavailable = EHILocalizedString(@"choose_your_rate_prepay_unavailable", @"UNAVAILABLE", @"");
        _price = EHIAttributedStringBuilder.new.appendText(unavailable).fontStyle(EHIFontStyleLight, 13.0f).string;
    }
}

- (BOOL)prepayEnabled
{
    EHICarClassCharge *charge = [self.carClass chargeForPrepay:YES];
    return self.carClass.supportsPrepay && charge.rates.firstObject != nil;
}

- (void)configurePayLater
{
    _title      = EHILocalizedString(@"choose_your_rate_pay_later_title", @"Pay Later", @"");
    _layoutType = self.payLaterEnabled ? EHIPaymentOptionLayoutEnabled : EHIPaymentOptionLayoutDisabled;
    
    if(_layoutType == EHIPaymentOptionLayoutEnabled) {
        _price      = [self attributedPrice];
    } else {
        NSString *unavailable = EHILocalizedString(@"choose_your_rate_prepay_unavailable", @"UNAVAILABLE", @"");
        _price = EHIAttributedStringBuilder.new.appendText(unavailable).fontStyle(EHIFontStyleLight, 13.0f).string;
    }
}

- (BOOL)payLaterEnabled
{
    EHICarClassCharge *charge = [self.carClass chargeForPrepay:NO];
    return charge.rates.firstObject != nil;
}

- (void)configureRedemptionPoints
{
    _title = EHILocalizedString(@"choose_your_rate_redeem_points_title", @"Redeem Points", @"");

    BOOL canRedeemPoints = [EHIUser currentUser].points >= self.carClass.redemptionPoints;
    _layoutType          = canRedeemPoints ? EHIPaymentOptionLayoutEnabled : EHIPaymentOptionLayoutDisabled;
    
    BOOL payLaterEnabled     = self.payLaterEnabled;
    BOOL isRedemptionAllowed = self.carClass.isRedemptionAllowed;
    if(!(isRedemptionAllowed && payLaterEnabled)) {
        NSString *unavailable = EHILocalizedString(@"choose_your_rate_prepay_unavailable", @"UNAVAILABLE", @"");
        _price = EHIAttributedStringBuilder.new.appendText(unavailable).fontStyle(EHIFontStyleLight, 13.0f).string;
    } else {
        NSString *redemptionAmount = @(self.carClass.redemptionPoints).ehi_localizedDecimalString;
        NSString *priceSubtitle    = [self priceSubtitle];
        _price = [self attributedTitle:redemptionAmount subtitle:priceSubtitle];
    }
}

# pragma mark - Accessors

- (NSAttributedString *)subtitle
{
    EHIAttributedStringBuilder *builder = EHIAttributedStringBuilder.new;
    CGFloat fontSize = 14.0f;
    switch (self.paymentOption) {
        case EHIReservationPaymentOptionPayNow: {
            NSString *subtitle = EHILocalizedString(@"reservation_pay_now_cancel_message", @"Cancel with fee", @"");
            return builder.appendText(subtitle).fontStyle(EHIFontStyleLight, fontSize).string;
        }
        case EHIReservationPaymentOptionPayLater: {
            NSString *subtitle = EHILocalizedString(@"reservation_pay_later_cancel_message", @"Cancel anytime", @"");
            return builder.appendText(subtitle).fontStyle(EHIFontStyleLight, fontSize).string;
        }
        case EHIReservationPaymentOptionRedeemPoints: {
            NSString *totalPoints = [EHIUser currentUser].loyaltyPoints;
            NSString *subtitle    = [EHILocalizedString(@"choose_your_rate_redeem_points_unit", @"Your Points", @"") ehi_appendComponent:@": "];
            return builder.appendText(subtitle).fontStyle(EHIFontStyleLight, fontSize).appendText(totalPoints).fontStyle(EHIFontStyleBold, fontSize).string;
        }
        default: return nil;
    }
}

- (NSString *)discount
{
    BOOL isPrepay  = self.paymentOption == EHIReservationPaymentOptionPayNow;
    BOOL isEnabled = self.layoutType == EHIPaymentOptionLayoutEnabled;
    EHIPrice *priceDifference = self.carClass.prepayDifference.viewDifference;
    
    if(isPrepay && priceDifference && isEnabled) {
        NSString *title = EHILocalizedString(@"reservation_pay_now_savings", @"Save #{amount}", @"");
        NSString *price = [EHIPriceFormatter format:priceDifference].abs(YES).string;
        return [title ehi_applyReplacementMap:@{
            @"amount" : price ?: @""
        }];
    }
    
    return nil;
}

//
// Helpers
//


- (NSAttributedString *)attributedPrice
{
    BOOL isPrepay         = self.paymentOption == EHIReservationPaymentOptionPayNow;
    NSString *subtitle    = [self priceSubtitle];
    EHIPrice *price       = [[self.carClass chargeForPrepay:isPrepay] viewPrice];
    NSString *priceString = [EHIPriceFormatter format:price].string;
    
    return [self attributedTitle:priceString subtitle:subtitle];
}

- (NSString *)priceSubtitle
{
    switch (self.paymentOption) {
        case EHIReservationPaymentOptionPayNow:
        case EHIReservationPaymentOptionPayLater:
            return EHILocalizedString(@"reservation_price_subtitle_total_cost", @"TOTAL COST", @"");
        case EHIReservationPaymentOptionRedeemPoints:
            return EHILocalizedString(@"choose_your_rate_points_per_day", @"POINTS/DAY", @"");
        default:
            return nil;
    }
}

- (NSAttributedString *)attributedTitle:(NSString *)title subtitle:(NSString *)subtitle
{
    EHIAttributedStringBuilder *builder = [EHIAttributedStringBuilder new]
    .paragraph(0, NSTextAlignmentRight).appendText(title).fontStyle(EHIFontStyleBold, 18.0f);
    
    // append a new line with subtitle, if any
    if(subtitle) {
        builder.newline.appendText(subtitle).size(14.f);
    }
    
    return builder.string;
}

@end
