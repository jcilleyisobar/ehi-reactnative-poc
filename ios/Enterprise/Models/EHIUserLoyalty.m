//
//  EHIUserLoyalty.m
//  Enterprise
//
//  Created by mplace on 2/23/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIUserLoyalty.h"
#import "EHIModel_Subclass.h"

@implementation EHIUserLoyalty

+ (NSDictionary *)mappings:(EHIUserLoyalty *)model
{
    return @{
        @"loyalty_number"        : @key(model.number),
        @"points_to_date"        : @key(model.pointsToDate),
        @"loyalty_tier"          : @key(model.tier),
        @"loyalty_program_code"  : @key(model.program),
        @"activity_to_next_tier" : @key(model.goal),
    };
}

+ (void)registerTransformers:(EHIUserLoyalty *)model
{
    [self key:@key(model.tier) registerTransformer:EHILoyaltyTierTypeTransform()];
}

@end
