//
//  EHICreditCardFormatter.h
//  Enterprise
//
//  Created by Alex Koller on 1/15/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHICreditCardType.h"

@interface EHICreditCardFormatter : NSObject

/**
 @brief Formats the card number into 4 digit chunks
 
 The provided card number is stripped of all non-decimal characters
 and split into 4 digit chunks. Numbers that are multiples of 4 will
 @b not have trailing spaces. Input greater than 16 characters after
 stripping will be truncated to 16 characters.
 */

+ (NSString *)formatCardNumber:(NSString *)cardNumber;

/**
 @brief Returns the credit card type from a card number
 
 The card number is stripped before processing. The card must have at
 least 1 digit to possibly return a card type. Card numbers can change
 type until at least 4 digits have been entered.
 */

+ (EHICreditCardType)typeForCardNumber:(NSString *)cardNumber;

+ (NSString *)cardIconForCardType:(EHICreditCardType)type;


/**
 Masks a given credit card number

 @param cardNumber The credit card number as a string
 @return a masked number with "*"
 */
+ (NSString *)maskCardNumber:(NSString *)cardNumber;

@end
