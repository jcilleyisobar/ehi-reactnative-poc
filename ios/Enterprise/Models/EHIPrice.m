//
//  EHIPrice.m
//  Enterprise
//
//  Created by Ty Cobb on 4/3/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIPrice.h"

@implementation EHIPrice

+ (NSDictionary *)mappings:(EHIPrice *)model
{
    return @{
         @"total_charged"   : @key(model.amount),
         @"amount_charged"  : @key(model.amount),
         @"currency_code"   : @key(model.code),
         @"currency_symbol" : @key(model.symbol)
    };
}

@end
