//
//  EHICreditCardFormatter.m
//  Enterprise
//
//  Created by Alex Koller on 1/15/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHICreditCardFormatter.h"

@implementation EHICreditCardFormatter

+ (NSString *)formatCardNumber:(NSString *)cardNumber
{
    cardNumber = [cardNumber ehi_stripNonDecimalCharacters];
    
    if(cardNumber.length > 16) {
        cardNumber = [cardNumber substringToIndex:16];
    }
    
    return [cardNumber ehi_split:4 separator:nil];
}

+ (EHICreditCardType)typeForCardNumber:(NSString *)cardNumber
{
    cardNumber = [cardNumber ehi_stripNonDecimalCharacters];
    
    if(cardNumber.length >= 2) {
        NSInteger firstTwo = [cardNumber substringToIndex:2].integerValue;
        
        switch(firstTwo) {
            case 34:
            case 37:
                return EHICreditCardTypeAmericanExpress;
            case 36:
                return EHICreditCardTypeDinersClub;
            case 38:
                return EHICreditCardTypeCarteBlanche;
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
                return EHICreditCardTypeMastercard;
        }
    }
    
    if(cardNumber.length >= 3) {
        NSInteger firstThree = [cardNumber substringToIndex:3].integerValue;
        
        switch(firstThree) {
            case 300:
            case 301:
            case 302:
            case 303:
            case 304:
            case 305:
                return EHICreditCardTypeDinersClub;
        }
    }
    
    if(cardNumber.length >= 1) {
        NSInteger firstOne = [cardNumber substringToIndex:1].integerValue;
        
        switch(firstOne) {
            case 3:
                return EHICreditCardTypeJcb;
            case 4:
                return EHICreditCardTypeVisa;
        }
    }
    
    if(cardNumber.length >= 4) {
        NSInteger firstFour = [cardNumber substringToIndex:4].integerValue;
        
        switch(firstFour) {
            case 2014:
            case 2149:
                return EHICreditCardTypeEnRoute;
            case 2131:
            case 1800:
                return EHICreditCardTypeJcb;
            case 6011:
                return EHICreditCardTypeDiscover;
        }
    }
    
    return EHICreditCardTypeUnknown;
}

+ (NSString *)cardIconForCardType:(EHICreditCardType)type
{
    switch(type) {
        case EHICreditCardTypeVisa:
            return @"creditcard_01_visa";
        case EHICreditCardTypeMastercard:
            return @"creditcard_02_mastercard";
        case EHICreditCardTypeAmericanExpress:
            return @"creditcard_03_amex";
        default:
            return @"icon-card-1";
    };
}

+ (NSString *)maskCardNumber:(NSString *)number
{
    NSString *cardNumber = [number ehi_trim];
    if(cardNumber.length <= 0) {
        return @"";
    }
    
    NSString *mask        = [@"*" ehi_repeat:12];
    NSInteger cardLength  = cardNumber.length;
    NSInteger index       = cardLength - MIN(cardLength, 4);
    NSString *last4Digits = [cardNumber substringFromIndex:index];
    
    return [NSString stringWithFormat:@"%@%@", mask, last4Digits];
}

@end
