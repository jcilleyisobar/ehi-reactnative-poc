//
//  EHIReviewPaymentOptionsViewModel.h
//  Enterprise
//
//  Created by mplace on 6/5/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

typedef NS_ENUM(NSUInteger, EHIReviewPaymentOption) {
    EHIReviewPaymentOptionBilling,
    EHIReviewPaymentOptionPayment
};

@interface EHIReviewPaymentOptionsViewModel : EHIViewModel <MTRReactive>
@property (copy, nonatomic, readonly) NSString *title;
/** Title for the billing option */
@property (copy, nonatomic, readonly) NSString *billingTitle;
/** Title for the counter payment option */
@property (copy, nonatomic, readonly) NSString *paymentTitle;
/** Subtitle for the counter payment option */
@property (copy, nonatomic, readonly) NSAttributedString *paymentSubtitle;
/** Hint text for the billing code entry text field */
@property (copy, nonatomic, readonly) NSString *billingEntryHintTitle;
/** Subtitle for the billing option */
@property (copy, nonatomic) NSAttributedString *billingSubtitle;
/** Billing number title*/
@property (copy, nonatomic) NSString *billingNumberTitle;
/** Custom billing code */
@property (copy, nonatomic) NSString *customBillingCode;
/** Account name for the billing account */
@property (copy, nonatomic) NSAttributedString *billingAccountTitle;
/** Account name for the payment account */
@property (copy, nonatomic) NSAttributedString *paymentAccountTitle;
/** The current payment option */
@property (assign, nonatomic) EHIReviewPaymentOption currentPaymentOption;

/** @YES if the billing account entry field should be hidden */
@property (assign, nonatomic) BOOL shouldHideBillingEntry;
/** @YES if the billing account picker text field should be hidden */
@property (assign, nonatomic) BOOL shouldHideBillingPicker;
/** @YES if the payment account picker text field should be hidden */
@property (assign, nonatomic) BOOL shouldHidePaymentPicker;

/** Returns the number of billing accounts to display */
- (NSInteger)numberOfPaymentMethods;
/** Returns the title of the billing account at a given index */
- (NSString *)titleForPaymentMethodAtIndex:(NSInteger)index;

/** Selects the billing account at a given index */
- (void)selectPaymentMethodAtIndex:(NSInteger)index;
/** Updates the custom billing payment method with the parameterized code */
- (void)updateCustomBillingCode:(NSString *)code;

@end
