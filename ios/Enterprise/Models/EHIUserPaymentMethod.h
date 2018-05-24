//
//  EHIUserPaymentMethod.h
//  Enterprise
//
//  Created by fhu on 5/19/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"
#import "EHICreditCardType.h"

#define EHIUserPaymentMethodMaxNumberOfCreditCardsAllowed 4

typedef NS_ENUM(NSUInteger, EHIUserPaymentType) {
    EHIUserPaymentTypeBilling,
    EHIUserPaymentTypeCard,
    EHIUserPaymentTypeUnknown,
};

typedef NS_ENUM(NSUInteger, EHIUserPaymentUseType) {
    EHIUserPaymentUseTypeBusiness,
    EHIUserPaymentUseTypeUnknown,
};

@interface EHIUserPaymentMethod : EHIModel <EHINetworkEncodable>

@property (assign, nonatomic, readonly) EHIUserPaymentType paymentType;
@property (assign, nonatomic, readonly) EHIUserPaymentUseType useType;
@property (assign, nonatomic, readonly) EHICreditCardType cardType;
@property (copy  , nonatomic, readonly) NSString *cardTypeDisplay;
@property (assign, nonatomic, readonly) BOOL isPreferred;
@property (assign, nonatomic, readonly) BOOL isExpired;
@property (assign, nonatomic, readonly) BOOL isNearExpiration;
@property (assign, nonatomic, readonly) NSInteger sequence;
@property (strong, nonatomic, readonly) NSDate *expirationDate;
@property (copy  , nonatomic, readonly) NSString *paymentReferenceId;
@property (copy  , nonatomic, readonly) NSString *alias;
@property (copy  , nonatomic, readonly) NSString *firstSix;
@property (copy  , nonatomic, readonly) NSString *lastFour;
@property (copy  , nonatomic, readonly) NSString *billingNumber;
@property (copy  , nonatomic, readonly) NSString *maskedBillingNumber;
@property (copy  , nonatomic, readonly) NSString *paymentDescription;

/** YES if the user has entered the billing code manually */
@property (assign, nonatomic) BOOL isCustom;
/** YES if the user has opted to use the billing code attached to their corporate account */
@property (assign, nonatomic) BOOL isExisting;

// Computed
@property (copy  , nonatomic, readonly) NSString *customDisplayName;

/** Returns a payment method that the user can modify the referenceId of */
+ (EHIUserPaymentMethod *)customBillingMethod;
/** Returns a payment method signifying the use of the billing account attached to their corporate account */
+ (EHIUserPaymentMethod *)existingBillingMethod;
/** Returns a payment method signifying the use of a credit card not attached to the profile */
+ (EHIUserPaymentMethod *)otherPaymentMethod;
/** Returns a payment method signifying the use of a credit card for one-time payment */
+ (EHIUserPaymentMethod *)oneTimePaymentMethod:(NSString *)panguiId;
/** Returns a payment method with a credit card type */
+ (EHIUserPaymentMethod *)creditCardPaymentMethod;
/** Returns an empty method */
+ (EHIUserPaymentMethod *)emptyPaymentMethod;

+ (NSArray *)skipPreferredSorting:(NSArray *)payments;
+ (NSArray *)allButPreferred:(NSArray *)payments;
+ (NSArray *)sortPayments:(NSArray *)payments;

@end

EHIAnnotatable(EHIUserPaymentMethod);
