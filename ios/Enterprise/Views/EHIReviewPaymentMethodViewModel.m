//
//  EHIReviewPaymentMethodViewModel.m
//  Enterprise
//
//  Created by Stu Buchbinder on 11/3/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIReviewPaymentMethodViewModel.h"
#import "EHIUserPaymentMethod.h"
#import "EHICreditCardFormatter.h"
#import "EHIWebViewModel.h"
#import "EHIAnalytics.h"

@interface EHIReviewPaymentMethodViewModel ()
@property (strong, nonatomic) EHIUserPaymentMethod *paymentMethod;
@property (assign, nonatomic) BOOL isModify;
@end

@implementation EHIReviewPaymentMethodViewModel

- (instancetype)initWithModel:(EHIUserPaymentMethod *)model forCorporateFlow:(BOOL)isCorporateFlow inModify:(BOOL)modify
{
    if ([super initWithModel:model]) {
        self.isCorporateFlow = isCorporateFlow;
        self.isModify = modify;
    }

    return self;
}

- (void)updateWithModel:(EHIUserPaymentMethod *)model
{
    [super updateWithModel:model];
    
    self.paymentMethod = model;
}

#pragma mark - Accessors

- (NSString *)terms
{
    return EHILocalizedString(@"general_prepay_policies", @"Prepayment Policy Terms & Conditions", @"");
}

- (BOOL)showTermsToggle
{
    return !self.isCorporateFlow && _showTermsToggle;
}

- (BOOL)shouldHandleTouches
{
    return !self.isModify && !self.isCorporateFlow;
}

- (NSString *)title
{
    if (self.isCorporateFlow) {
        BOOL isBilling = self.paymentMethod.paymentType == EHIUserPaymentTypeBilling;
        if (isBilling) {
            return EHILocalizedString(@"reservation_confirmation_payment_billing_title", @"Corporate Code", @"title for the confirmation payment options cell when a corporate code was used for payment");
        } else {
            return EHILocalizedString(@"reservation_confirmation_payment_pick_up_title", @"Pay At Pick-Up Counter", @"title for the confirmation payment options cell when the user has chosen to pay at the counter");
        }
    } else {
        return self.paymentMethod.alias ?: self.paymentMethod.cardTypeDisplay;
    }
}

- (NSString *)paymentTitle
{
    return self.paymentMethod.customDisplayName;
}

- (NSString *)subtitle
{
    if (self.isCorporateFlow) {
        BOOL isBilling = self.paymentMethod.paymentType == EHIUserPaymentTypeBilling;
        if (isBilling) {
            return self.paymentMethod.maskedBillingNumber;
        } else {
            return EHILocalizedString(@"review_payment_options_payment_subtitle", @"Your credit card will not be charged. This just saves you time at the counter.", @"payment subtitle for the review payment options cell.");
        }
    } else {
        return [self expiration:self.paymentMethod];
    }
}

- (NSString *)cardImage
{
    return [EHICreditCardFormatter cardIconForCardType:self.paymentMethod.cardType];
}

- (NSAttributedString *)readTermsTitle
{
    NSString *policiesText = EHILocalizedString(@"review_prepay_policies_read", @"I have read the #{policies}", @"");
    NSString *policiesName = EHILocalizedString(@"general_prepay_policies", @"Prepayment Policy Terms & Conditions", @"");
    
    __weak __typeof(self) welf = self;
    NSAttributedString *attributedPoliciesName =
    [NSAttributedString attributedStringWithString:policiesName
                                              font:[UIFont ehi_fontWithStyle:EHIFontStyleRegular size:14.0f]
                                             color:[UIColor ehi_lightGreenColor]
                                        tapHandler:^{
                                            [welf showTerms];
                                        }];
    
    EHIAttributedStringBuilder *policiesBuilder = EHIAttributedStringBuilder.new
    .text(policiesText).fontStyle(EHIFontStyleRegular, 14.0f).replace(@"#{policies}", attributedPoliciesName);
    
    policiesBuilder.attributes(@{NSBaselineOffsetAttributeName: @1});
    
    return policiesBuilder.string;
}

#pragma mark - Helpers

- (NSString *)expiration:(EHIUserPaymentMethod *)paymentMethod
{
    if(paymentMethod.paymentType != EHIUserPaymentTypeBilling) {
        NSString *expirationTitle = (paymentMethod.isExpired)
            ? EHILocalizedString(@"profile_payment_options_expired_text", @"Expired: #{date}", @"")
            : EHILocalizedString(@"profile_payment_options_expires_text", @"Expires: #{date}", @"");

        NSString *expireDate      = [paymentMethod.expirationDate ehi_stringWithFormat:@"MM/YY"] ?: @"";

        return [expirationTitle ehi_applyReplacementMap: @{ @"date" : expireDate }];
    }
    
    return nil;
}

#pragma mark - Actions

- (void)showTerms
{
    [EHIAnalytics trackAction:EHIAnalyticsResActionPrepayPolicy handler:nil];
    
    [[[EHIWebViewModel alloc] initWithType:EHIWebContentTypePrepayTermsAndConditions] push];
}

@end
