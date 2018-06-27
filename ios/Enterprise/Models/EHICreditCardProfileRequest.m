//
//  EHICreditCardProfileRequest.m
//  Enterprise
//
//  Created by Rafael Ramos on 9/28/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHICreditCardProfileRequest.h"

@interface EHICreditCardProfileRequest ()
@property (strong, nonatomic) EHICreditCard *card;
@property (copy  , nonatomic) NSString *token;
@end

@implementation EHICreditCardProfileRequest

+ (instancetype)requestForCreditCard:(EHICreditCard *)card token:(NSString *)token
{
    EHICreditCardProfileRequest *model = [EHICreditCardProfileRequest new];
    model.card  = card;
    model.token = token;
    
    return model;
}

- (void)encodeWithRequest:(EHINetworkRequest *)request
{
    [super encodeWithRequest:request];
    
    request[@"payment_reference_id"] = self.token ?: @"";
    request[@"use_type"]             = @"Business";
    request[@"payment_type"]         = @"CREDIT_CARD";
    request[@"card_type"]            = [self decodeCardType] ?: @"";
    request[@"preferred"]            = EHIStringifyFlag(NO);
    request[@"expiration_date"]      = [NSString stringWithFormat:@"%ld-%ld", (long)self.card.expirationYear, (long)self.card.expirationMonth];
    request[@"last_four"]            = self.card.lastFour ?: @"";
    request[@"first_six"]            = self.card.firstSix ?: @"";
}

//
// Helpers
//

- (NSString *)decodeCardType
{
    switch (self.card.type) {
        case EHICreditCardTypeAmericanExpress: return @"AMERICAN_EXPRESS";
        default: return self.card.cardTypeName;
    }
}

@end
