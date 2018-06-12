//
//  EHIPaymentInputViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 1/14/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHICreditCard.h"

@interface EHIPaymentInputViewModel : EHIViewModel <MTRReactive>

@property (copy  , nonatomic, readonly) NSString *nameTitle;
@property (copy  , nonatomic, readonly) NSString *namePlaceholder;
@property (copy  , nonatomic, readonly) NSString *cardNumberTitle;
@property (copy  , nonatomic, readonly) NSString *cardNumberPlaceholder;
@property (copy  , nonatomic, readonly) NSString *expirationTitle;
@property (copy  , nonatomic, readonly) NSString *expirationMonthPlaceholder;
@property (copy  , nonatomic, readonly) NSString *expirationYearPlaceholder;
@property (copy  , nonatomic, readonly) NSString *cvvTitle;
@property (copy  , nonatomic, readonly) NSString *cvvPlaceholder;

@property (copy  , nonatomic) NSString *name;
@property (copy  , nonatomic) NSString *cardNumber;
@property (copy  , nonatomic) NSString *expirationMonth;
@property (copy  , nonatomic) NSString *expirationYear;
@property (copy  , nonatomic) NSString *cvv;

@property (assign, nonatomic) BOOL showNameError;
@property (assign, nonatomic) BOOL showCardNumberError;
@property (assign, nonatomic) BOOL showExpirationMonthError;
@property (assign, nonatomic) BOOL showExpirationYearError;
@property (assign, nonatomic) BOOL showCvvError;

@property (assign, nonatomic) BOOL hideTerms;
@property (assign, nonatomic) BOOL hideSave;

/** state of the terms & conditions checkbox */
@property (assign, nonatomic) BOOL policiesRead;
@property (copy  , nonatomic) NSAttributedString *termsTitle;

/** state of the save checkbox */
@property (assign, nonatomic) BOOL saveCard;
@property (copy  , nonatomic) NSString *saveTitle;

// computed
@property (copy  , nonatomic, readonly) NSString *cardImageName;
@property (assign, nonatomic, readonly) BOOL invalidCreditCard;

/** Wraps up credit card fields while exposing errors. @c nil if errors found. */
- (EHICreditCard *)createCreditCard;

@end
