//
//  EHIUserPaymentMethod.m
//  Enterprise
//
//  Created by fhu on 5/19/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIUserPaymentMethod.h"
#import "EHIModel_Subclass.h"
#import "EHICreditCardFormatter.h"

@interface EHIUserPaymentMethod ()
@property (copy  , nonatomic) NSString *alias;
@property (assign, nonatomic) EHIUserPaymentType paymentType;
@property (assign, nonatomic) CGFloat amountCharged;
@property (copy  , nonatomic) NSString *currencyCode;
@property (copy  , nonatomic) NSString *expiryDate;
@end

@implementation EHIUserPaymentMethod

+ (EHIUserPaymentMethod *)customBillingMethod
{
    EHIUserPaymentMethod *paymentMethod = [EHIUserPaymentMethod new];
    
    paymentMethod.isCustom = YES;
    paymentMethod.alias    = EHILocalizedString(@"review_payment_options_billing_entry_dropdown_title", @"+ Add Billing Code", @"title for the add billing code option in the billing account picker.");

    return paymentMethod;
}

+ (EHIUserPaymentMethod *)existingBillingMethod
{
    EHIUserPaymentMethod *paymentMethod = [EHIUserPaymentMethod new];
    
    paymentMethod.isExisting = YES;
    
    return paymentMethod;
}

+ (EHIUserPaymentMethod *)otherPaymentMethod
{
    EHIUserPaymentMethod *paymentMethod = [EHIUserPaymentMethod new];
    
    paymentMethod.alias = EHILocalizedString(@"reservation_credit_card_option_other", @"Other", @"title for the other credit card option in the credit card picker."),
    paymentMethod.paymentType = EHIUserPaymentTypeCard;
    
    return paymentMethod;
}

+ (EHIUserPaymentMethod *)oneTimePaymentMethod:(NSString *)panguiId
{
    EHIUserPaymentMethod * paymentMethod = [self creditCardPaymentMethod];
    
    [paymentMethod updateWithDictionary:@{
        @key(paymentMethod.paymentReferenceId) : panguiId ?: @""
    }];

    return paymentMethod;
}

+ (EHIUserPaymentMethod *)creditCardPaymentMethod
{
    EHIUserPaymentMethod *paymentMethod = [EHIUserPaymentMethod new];
    paymentMethod.paymentType = EHIUserPaymentTypeCard;
    
    return paymentMethod;
}

+ (EHIUserPaymentMethod *)emptyPaymentMethod
{
    EHIUserPaymentMethod *paymentMethod = [EHIUserPaymentMethod new];
    paymentMethod.paymentType = EHIUserPaymentTypeUnknown;
    
    return paymentMethod;
}

# pragma mark - Computed

- (NSString *)customDisplayName
{
    BOOL isBilling = self.paymentType == EHIUserPaymentTypeBilling;
    return isBilling ? self.customBillingName : self.customCardName;
}

- (NSString *)customCardName
{
    NSString *alias  = self.alias ?: @"";
    NSString *masked = [[@"*" ehi_repeat:12] ehi_appendComponent:self.lastFour];
    return [NSString stringWithFormat:@"%@ %@", alias, masked].ehi_trim;
}

- (NSString *)customBillingName
{
    NSString *alias  = self.alias;
    NSString *masked = self.maskedBillingNumber;

    if(alias && masked) {
        return [NSString stringWithFormat:@"%@ (%@)", alias, masked];
    }

    return [NSString stringWithFormat:@"%@", masked ?: alias];
}

//Helpers

- (NSString *)cardTypeDisplay
{
    switch (self.cardType) {
        case EHICreditCardTypeVisa:
            return EHILocalizedString(@"user_payment_card_type_visa", @"VISA", @"");
        case EHICreditCardTypeAmericanExpress:
            return EHILocalizedString(@"user_payment_card_type_amex", @"AMEX", @"");
        case EHICreditCardTypeMastercard:
            return EHILocalizedString(@"user_payment_card_type_mastercard", @"MASTERCARD", @"");
        case EHICreditCardTypeCarteBlanche:
        case EHICreditCardTypeDinersClub:
        case EHICreditCardTypeDiscover:
             return EHILocalizedString(@"user_payment_card_type_discover", @"DISCOVER", @"");
        case EHICreditCardTypeEnRoute:
        case EHICreditCardTypeJcb:
        case EHICreditCardTypeUnknown:
            return nil;
    }
}

# pragma mark - Mapping

+ (NSDictionary *)mappings:(EHIUserPaymentMethod *)model
{
    return @{
        @"payment_type"                : @key(model.paymentType),
        @"card_type"                   : @key(model.cardType),
        @"payment_method"              : @key(model.cardType),
        @"use_type"                    : @key(model.useType),
        @"expiration_date"             : @key(model.expiryDate),
        @"payment_reference_id"        : @key(model.paymentReferenceId),
        @"amount_charged"              : @key(model.amountCharged),
        @"currency_code"               : @key(model.currencyCode),
        @"first_six"                   : @key(model.firstSix),
        @"last_four"                   : @key(model.lastFour),
        @"billing_number"              : @key(model.billingNumber),
        @"mask_billing_account_number" : @key(model.maskedBillingNumber),
        @"mask_billing_number"         : @key(model.maskedBillingNumber),
        @"description"                 : @key(model.paymentDescription),
        @"preferred"                   : @key(model.isPreferred),
        @"credit_card_expired"         : @key(model.isExpired),
        @"credit_card_near_expiration" : @key(model.isNearExpiration),
    };
}

+ (void)registerTransformers:(EHIUserPaymentMethod *)model
{
    [self key:@key(model.paymentType) registerMap:@{
        @"CREDIT_CARD"                : @(EHIUserPaymentTypeCard),
        @"CREDIT CARD"                : @(EHIUserPaymentTypeCard),
        @"CC"                         : @(EHIUserPaymentTypeCard),
        @"BUSINESS_ACCOUNT_APPLICANT" : @(EHIUserPaymentTypeBilling),
    } defaultValue:@(EHIUserPaymentTypeBilling)];
    
    [self key:@key(model.cardType) registerMap:@{
        @"VISA"         : @(EHICreditCardTypeVisa),
        @"MASTERCARD"   : @(EHICreditCardTypeMastercard),
        @"AMEX"         : @(EHICreditCardTypeAmericanExpress),
        @"AMERICAN_EXPRESS" : @(EHICreditCardTypeAmericanExpress),
    } defaultValue:@(EHICreditCardTypeUnknown)];
    
    [self key:@key(model.useType) registerMap:@{
        @"Business" : @(EHIUserPaymentUseTypeBusiness)
    } defaultValue:@(EHIUserPaymentUseTypeUnknown)];
}

- (void)setExpiryDate:(NSString *)expiryDate
{
    _expirationDate = [expiryDate ehi_dateWithFormat:@"yyyy-MM"];
}

# pragma mark - EHINetworkEncodable

- (void)encodeWithRequest:(EHINetworkRequest *)request
{
    [super encodeWithRequest:request];

    request[@"alias"]     = self.alias;
    request[@"preferred"] = EHIStringifyFlag(self.isPreferred);
    request[@"payment_reference_id"] = self.paymentReferenceId;
    request[@"expiration_date"]      = [self.expirationDate ehi_stringWithFormat:@"yyyy-MM"];
}

# pragma mark - Sorting

+ (NSArray *)skipPreferredSorting:(NSArray *)payments
{
    return [self sortPayments:[self allButPreferred:payments]];
}

+ (NSArray *)allButPreferred:(NSArray *)payments
{
    return (payments ?: @[]).reject(^(EHIUserPaymentMethod *method) {
        return method.isPreferred;
    });
}

+ (NSArray *)sortPayments:(NSArray *)payments
{
    return (payments ?: @[]).sortBy(^(EHIUserPaymentMethod *method) {
        return method.alias ?: method.cardTypeDisplay;
    });
}

@end
