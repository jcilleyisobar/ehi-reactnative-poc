//
//  EHICreditCardPanguiResponse.m
//  Enterprise
//
//  Created by Rafael Ramos on 10/25/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIModel_Subclass.h"
#import "EHICreditCardPanguiResponse.h"

@implementation EHICreditCardPanguiResponse

- (void)updateWithDictionary:(NSDictionary *)dictionary
{
    _paymentId   = dictionary[@"PaymentMediaReferenceIdentifier"];
    _isDebitCard = (BOOL)[dictionary[@"PartialPrimaryAccount"][@"DebitCardIndicator"] boolValue];
}

@end
