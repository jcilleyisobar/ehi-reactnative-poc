//
//  EHIConfirmationCancelModalViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 2/16/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIConfirmationCancelModalViewModel.h"
#import "EHIViewModel_Subclass.h"
#import "EHIPriceFormatter.h"
#import "EHIWebViewModel.h"
#import "EHIReservationBuilder.h"

@implementation EHIConfirmationCancelModalViewModel

- (instancetype)initWithPrice:(EHIPrice *)originalPrice
					cancelFee:(EHIPrice *)cancelFee
			  cancellationFee:(EHICancellationFee *)cancellationFee
					   refund:(EHIPrice *)refundAmount
{
    if(self = [super init]) {        
        _subtitle           = [self subtitleWithCancellationFee:cancellationFee];
        _originalAmount     = [self priceToString:originalPrice];
        _cancellationFee    = [self cancelPriceToString:cancelFee];
        _refundedAmount     = [self attributedTitleForPrice:refundAmount];
        
        if([cancellationFee eligibleForCurrencyConvertion]) {
            _conversionSubtitle = [self conversionSubtitleWithCancellationFee:cancellationFee];
            _convertedRefund    = [self convertedRefundWithCancellationFee:cancellationFee];
        }
    }
    
    return self;
}

- (void)didBecomeActive
{
    [EHIAnalytics trackState:nil];
    [EHIAnalytics changeScreen:EHIScreenConfirmationCancelReservation state:EHIScreenConfirmationCancelReservation];
}

- (void)present:(EHIInfoModalAction)action
{
    [super present:^BOOL(NSInteger index, BOOL canceled) {
        if(!canceled && index == 0) {
            [EHIAnalytics trackAction:EHIAnalyticsResCancelActionYes handler:self.encodeReservation];
        } else {
            [EHIAnalytics trackAction:EHIAnalyticsResCancelActionNo handler:self.encodeReservation];
        }
        
        return action(index, canceled);
    }];
}

# pragma mark - Accessors

- (NSString *)originalAmountTile
{
    return EHILocalizedString(@"reservation_cancel_original_amount", @"Original Amount", @"");
}

- (NSString *)cancellationFeeTitle
{
    return EHILocalizedString(@"reservation_cancel_cancelation_fee", @"Cancelation Fee", @"");
}

- (NSString *)refundedAmountTitle
{
    return EHILocalizedString(@"reservation_cancel_refunded_amount", @"Refunded Amount", @"");
}

# pragma mark - Info Modal

- (NSString *)detailsNibName
{
    return @"EHIConfirmationCancelModalView";
}

- (NSString *)firstButtonTitle
{
    return EHILocalizedString(@"reservation_cancel_confirmation_message", @"YES, CANCEL RESERVATION", @"");
}

- (NSString *)secondButtonTitle
{
    return EHILocalizedString(@"reservation_cancel_dismiss_message", @"NO, KEEP CURRENT RESERVATION",@"");
}

- (NSString *)title
{
    return EHILocalizedString(@"reservation_cancel_message_title", @"Are you sure you want to cancel?", @"");
}

#pragma mark - Actions

- (void)displayTermsAndConditions
{
    self.router.transition.dismiss.start(^{
        [[[EHIWebViewModel alloc] initWithType:EHIWebContentTypePrepayTermsAndConditions] present];
    });
}

#pragma mark - Helpers

- (NSAttributedString *)subtitleWithCancellationFee:(EHICancellationFee *)cancellationFee
{
    NSString *message = EHILocalizedString(@"reservation_cancel_message_details", @"By tapping \"Yes, Cancel Reservation\", your reservation will be canceled and you will receive a refund applied to the original form of payment. This action can't be undone. - #{terms}", @"");
    
    if(cancellationFee) {
        NSString *cancel = EHILocalizedString(@"confirmation_cancel_reservation_message", @"Are you sure you want to cancel this reservation? You will have a cancellation fee of #{amount}. - #{terms}", @"");
        
        NSString *amount = [self priceToString:cancellationFee.feeView];
        message = [cancel ehi_applyReplacementMap:@{
            @"amount" : amount
        }];
    }
    
    return [self generalPrepayPolicies:message];
}

- (NSAttributedString *)generalPrepayPolicies:(NSString *)string
{
    NSString *policies = [EHILocalizedString(@"general_prepay_policies", @"Prepayment Policy Terms & Conditions", @"")  stringByAppendingString:@"\n"];
    
    __weak __typeof(self) welf = self;
    NSAttributedString *attributedString =
    [NSAttributedString attributedStringWithString:policies
                                              font:[UIFont ehi_fontWithStyle:EHIFontStyleLight size:14.0f]
                                             color:[UIColor ehi_lightGreenColor]
                                        tapHandler:^{
                                            [welf displayTermsAndConditions];
                                        }
    ];
    
    return EHIAttributedStringBuilder.new
        .text(string)
        .fontStyle(EHIFontStyleRegular, 14.0f)
        .replace(@"#{terms}", attributedString)
        .string;
}

- (NSAttributedString *)attributedTitleForPrice:(EHIPrice *)price
{
    EHIPriceFormatter *priceFormatter = [self format:price];
    // decreasing the font size if the price exceeds 7 characters in an effort to squeeze it into the fixed button width
    priceFormatter.size(priceFormatter.string.length > 7 ? EHIPriceFontSizeMedium * .9f : EHIPriceFontSizeMedium);

    EHIAttributedStringBuilder *builder = [EHIAttributedStringBuilder new]
    .paragraph(2, NSTextAlignmentRight).append(priceFormatter.attributedString);
    
    return builder.string;
}

- (NSString *)priceToString:(EHIPrice *)price
{
    return [self format:price].string;
}

- (NSString *)cancelPriceToString:(EHIPrice *)price
{
    return [self format:price].neg(price.amount > 0).string;
}

- (NSString *)conversionSubtitleWithCancellationFee:(EHICancellationFee *)cancellationFee
{
    NSString *conversion = EHILocalizedString(@"reservation_currency_conversion_title", @"All charges and refunds are processed in your destination’s currency (#{currency_code}). Conversations are shown here for your convenience.", @"");
    return [conversion ehi_applyReplacementMap:@{
        @"currency_code" : cancellationFee.paymentPrice.code ?: @""
    }];
}

- (NSString *)convertedRefundWithCancellationFee:(EHICancellationFee *)cancellationFee
{
    NSString *refund = EHILocalizedString(@"reservation_cancel_currency_refund", @"refunded as: #{refund}", @"");
    NSString *price  = [self format:cancellationFee.refundAmountPayment].string;
    
    return [refund ehi_applyReplacementMap:@{
       @"refund" : price ?: @""
    }];
}

- (EHIPriceFormatter *)format:(EHIPrice *)price
{
    return [EHIPriceFormatter format:price];
}

# pragma mark - Analytics

- (void (^)(EHIAnalyticsContext *))encodeReservation
{
    return ^(EHIAnalyticsContext *context) {
        [[EHIReservationBuilder sharedInstance] updateAnalyticsContext:context];
    };
}

@end
