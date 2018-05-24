//
//  EHISelectPaymentFooterViewModel.m
//  Enterprise
//
//  Created by Stu Buchbinder on 10/27/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHISelectPaymentFooterViewModel.h"
#import "EHIWebViewModel.h"
#import "EHIViewModel_Subclass.h"

@interface EHISelectPaymentFooterViewModel ()
@property (assign, nonatomic) BOOL showTerms;
@property (assign, nonatomic) BOOL continueButtonDisabled;
@end

@implementation EHISelectPaymentFooterViewModel

- (NSAttributedString *)terms
{
    NSString *policiesText = EHILocalizedString(@"review_prepay_policies_read", @"I have read the #{policies}", @"");
    NSString *policiesName = EHILocalizedString(@"general_prepay_policies", @"Prepayment Policy Terms & Conditions", @"");
    
    NSAttributedString *attributedPoliciesName = [NSAttributedString attributedStringWithString:policiesName font:[UIFont ehi_fontWithStyle:EHIFontStyleRegular size:14.0f] color:[UIColor ehi_lightGreenColor] tapHandler:^{
             [[[EHIWebViewModel alloc] initWithType:EHIWebContentTypePrepayTermsAndConditions] push];
    }];

    EHIAttributedStringBuilder *policiesBuilder = EHIAttributedStringBuilder.new
    .text(policiesText).fontStyle(EHIFontStyleLight, 15.0f).replace(@"#{policies}", attributedPoliciesName);

    policiesBuilder.attributes(@{NSBaselineOffsetAttributeName: @0});
    
    return policiesBuilder.string;
}

- (NSString *)continueTitle
{
    return EHILocalizedString(@"reservation_itinerary_action_button", @"CONTINUE", @"");
}

#pragma mark - Actions

- (void)didTapContinue
{
    [EHIAnalytics trackAction:EHIAnalyticsResActionContinue handler:nil];
}

- (void)toggleTermsRead
{
    [EHIAnalytics trackAction:EHIAnalyticsResActionCreditCardTC handler:nil];

    self.termsRead = !self.termsRead;
    
    [self computeContinueButtonState];
}

- (void)setCurrentPaymentMethod:(EHIUserPaymentMethod *)currentPaymentMethod
{
    _currentPaymentMethod = currentPaymentMethod;
    
    self.showTerms = currentPaymentMethod != nil;
    [self computeContinueButtonState];
}

- (void)computeContinueButtonState
{
    self.continueButtonDisabled = !(self.currentPaymentMethod != nil && self.termsRead);
}

@end
