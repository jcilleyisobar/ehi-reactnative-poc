//
//  EHICarClassChargeRate.m
//  Enterprise
//
//  Created by mplace on 2/19/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICarClassChargeRate.h"
#import "EHIModel_Subclass.h"

@implementation EHICarClassChargeRate

# pragma mark - Mappings

+ (NSDictionary *)mappings:(EHICarClassChargeRate *)model
{
    return @{
        @"unit_amount_view"        : @key(model.price),
        @"unit_rate_type"          : @key(model.type),
        @"unit_rate_type_quantity" : @key(model.quantity),
    };
}

+ (void)registerTransformers:(EHICarClassChargeRate *)model
{
    [self key:@key(model.type) registerTransformer:EHIPriceRateTypeTransformer()];
}

@end
