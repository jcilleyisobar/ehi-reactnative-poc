//
//  EHICreditCard.m
//  Enterprise
//
//  Created by Alex Koller on 1/15/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHICreditCard.h"
#import "EHIModel_Subclass.h"

@implementation EHICreditCard

- (void)updateWithDictionary:(NSDictionary *)dictionary forceDeletions:(BOOL)forceDeletions
{
    [super updateWithDictionary:dictionary forceDeletions:forceDeletions];
    
    NSInteger type = [dictionary[@key(self.type)] integerValue];
    if(type) {
        _type = type;
    }
}

+ (NSDictionary *)mappings:(EHICreditCard *)model
{
    return @{
        @"card_type"        : @key(model.type),
        @"expiration_month" : @key(model.expirationMonth),
        @"expiration_year"  : @key(model.expirationYear),
        @"number"           : @key(model.cardNumber),
        @"cvv"              : @key(model.cvvNumber),
    };
}

+ (void)registerTransformers:(EHICreditCard *)model
{
    [super registerTransformers:model];
    
    [self key:@key(model.type) registerMap:@{
        @"AMEX"       : @(EHICreditCardTypeAmericanExpress),
        @"DINERS"     : @(EHICreditCardTypeDinersClub),
        @"DISCOVER"   : @(EHICreditCardTypeDiscover),
        @"JCB"        : @(EHICreditCardTypeJcb),
        @"MASTERCARD" : @(EHICreditCardTypeMastercard),
        @"VISA"       : @(EHICreditCardTypeVisa),
    } defaultValue:@(EHICreditCardTypeUnknown)];
}

- (NSString *)cardTypeName
{
    return [[self.class transformerForKey:@key(self.type)] reverseTransformedValue:@(self.type)];
}

- (NSString *)firstSix
{
    return [self.cardNumber substringToIndex:6];
}

- (NSString *)lastFour
{
    NSInteger length = self.cardNumber.length;
    return [self.cardNumber substringFromIndex:length - 4];
}

# pragma mark - EHINetworkEncodable

- (void)encodeWithRequest:(EHINetworkRequest *)request
{
    [super encodeWithRequest:request];
    
    request[@"type"]             = [[self.class transformerForKey:@key(self.type)] reverseTransformedValue:@(self.type)];
    request[@"expirationMonth"]  = @(self.expirationMonth).description;
    request[@"expirationYear"]   = @(self.expirationYear).description;
    request[@"number"]           = self.cardNumber;
    request[@"cvc"]              = self.cvvNumber;
}

@end
