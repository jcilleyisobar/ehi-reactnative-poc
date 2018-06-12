//
//  EHIUserPaymentProfile.m
//  Enterprise
//
//  Created by mplace on 2/24/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIUserPaymentProfile.h"
#import "EHIModel_Subclass.h"

@implementation EHIUserPaymentProfile

+ (NSDictionary *)mappings:(EHIUserPaymentProfile *)model
{
    return @{
        @"payment_methods" : @key(model.paymentMethods),
    };
}

@end
