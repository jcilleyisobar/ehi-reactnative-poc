//
//  EHIReservationPaymentMethod.m
//  Enterprise
//
//  Created by Rafael Ramos on 2/25/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIReservationPaymentMethod.h"

@implementation EHIReservationPaymentMethod

# pragma mark - Mapping

+ (NSDictionary *)mappings:(EHIReservationPaymentMethod *)model
{
    return @{
       @"amount"           : @key(model.amount),
       @"transaction_type" : @key(model.transactionType),
       @"card_details"     : @key(model.creditCard)
    };
}

@end
