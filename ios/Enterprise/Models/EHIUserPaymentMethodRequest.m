//
//  EHIUserPaymentMethodRequest.m
//  Enterprise
//
//  Created by Rafael Ramos on 10/5/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIUserPaymentMethodRequest.h"
#import "EHIUserPaymentMethod.h"

@interface EHIUserPaymentMethodRequest ()
@property (strong, nonatomic) EHIUserPaymentMethod *paymentMethod;
@end

@implementation EHIUserPaymentMethodRequest

+ (instancetype)requestWithPaymentMethod:(EHIUserPaymentMethod *)paymentMethod
{
    EHIUserPaymentMethodRequest *request = [EHIUserPaymentMethodRequest new];
    request.paymentMethod = paymentMethod;
    
    return request;
}

# pragma mark - Network

- (void)encodeWithRequest:(EHINetworkRequest *)request
{
    [super encodeWithRequest:request];
    
    request[@"payment_service_context_reference_identifier"] = self.paymentMethod.paymentReferenceId ?: @"";
    request[@"payment_reference_id"] = self.paymentMethod.paymentReferenceId ?: @"";
    request[@"expiration_date"] = self.paymentMethod.expirationDate.description ?: [[NSDate ehi_today] ehi_stringWithFormat:@"MMyyyy"];
    request[@"payment_type"]    = [self decodePaymentType];
    request[@"preferred"]       = EHIStringifyFlag(self.paymentMethod.isPreferred);
    request[@"card_type"]       = [self decodeCardType];
    request[@"first_six"]       = self.paymentMethod.firstSix ?: @"";
    request[@"last_four"]       = self.paymentMethod.lastFour ?: @"";
    request[@"alias"]           = self.paymentMethod.alias ?: @"";
}

- (NSString *)decodePaymentType
{
    switch (self.paymentMethod.paymentType) {
        case EHIUserPaymentTypeBilling: return @"BUSINESS_ACCOUNT_APPLICANT";
        case EHIUserPaymentTypeCard: return @"CREDIT_CARD";
        case EHIUserPaymentTypeUnknown: return @"";
    }
}

- (NSString *)decodeCardType
{
    switch (self.paymentMethod.cardType) {
        case EHICreditCardTypeAmericanExpress: return @"AMERICAN_EXPRESS";
        case EHICreditCardTypeMastercard: return @"MASTERCARD";
        case EHICreditCardTypeVisa: return @"VISA";
        default: return @"";
    }
}

@end
