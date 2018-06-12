//
//  EHICarClassVehicleRate.m
//  Enterprise
//
//  Created by Michael Place on 5/29/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICarClassVehicleRate.h"
#import "EHIModel_Subclass.h"

@implementation EHICarClassVehicleRate

# pragma mark - Mapping

+ (NSDictionary *)mappings:(EHICarClassVehicleRate *)model
{
    return @{
        @"charge_type"   : @key(model.type),
        @"price_summary" : @key(model.priceSummary),
        @"extras"        : @key(model.extras)
    };
}

+ (void)registerTransformers:(EHICarClassVehicleRate *)model
{
    [super registerTransformers:model];
    
    [self key:@key(model.type) registerTransformer:EHICarClassChargeTypeTransformer()];
}

@end
