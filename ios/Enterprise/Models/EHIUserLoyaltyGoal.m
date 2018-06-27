//
//  EHIUserLoyaltyGoal.m
//  Enterprise
//
//  Created by mplace on 2/23/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIUserLoyaltyGoal.h"
#import "EHIModel_Subclass.h"

@implementation EHIUserLoyaltyGoal

+ (NSDictionary *)mappings:(EHIUserLoyaltyGoal *)model
{
    return @{
        @"remaining_rental_count" : @key(model.remainingRentals),
        @"remaining_rental_days"  : @key(model.remainingRentalDays),
        @"next_tier_rental_count" : @key(model.nextTierRentals),
        @"next_tier_rental_days"  : @key(model.nextTierRentalDays),
        @"loyalty_tier"           : @key(model.tier),
    };
}

+ (void)registerTransformers:(EHIUserLoyaltyGoal *)model
{
    [self key:@key(model.tier) registerTransformer:EHILoyaltyTierTypeTransform()];
}

@end
