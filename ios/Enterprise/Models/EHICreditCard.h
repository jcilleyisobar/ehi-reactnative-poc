//
//  EHICreditCard.h
//  Enterprise
//
//  Created by Alex Koller on 1/15/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIModel.h"
#import "EHICreditCardType.h"

@interface EHICreditCard : EHIModel <EHINetworkEncodable>

@property (assign, nonatomic, readonly) EHICreditCardType type;
@property (assign, nonatomic, readonly) NSInteger expirationMonth;
@property (assign, nonatomic, readonly) NSInteger expirationYear;
@property (copy  , nonatomic, readonly) NSString *cardNumber;
@property (copy  , nonatomic, readonly) NSString *cvvNumber;
@property (copy  , nonatomic) NSString *holderName;
@property (assign, nonatomic) BOOL save;

// computed
- (NSString *)cardTypeName;
- (NSString *)firstSix;
- (NSString *)lastFour;
@end
