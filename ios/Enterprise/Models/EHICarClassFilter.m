//
//  EHICarClassFilter.m
//  Enterprise
//
//  Created by mplace on 6/15/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICarClassFilter.h"
#import "EHIModel_Subclass.h"

@implementation EHICarClassFilter

+ (NSDictionary *)mappings:(EHICarClassFilter *)model
{
    return @{
        @"description" : @key(model.title),
        @"filter_name" : @key(model.type),
        @"filter_code" : @key(model.code),
    };
}

+ (void)registerTransformers:(EHICarClassFilter *)model
{
    [super registerTransformers:model];
    
    [self key:@key(model.type) registerMap:@{
        @"TRANSMISSION" : @(EHIFilterTypeTransmission),
        @"PASSENGERS"   : @(EHIFilterTypePassengerCapacity),
        @"FUEL"         : @(EHIFilterTypeFuel),
        @"CLASS"        : @(EHIFilterTypeClass),
    } defaultValue:@(EHIFilterTypeUnknown)];
}

@end

@implementation EHICarClassFilters

+ (NSDictionary *)mappings:(EHICarClassFilters *)model
{
    return @{
        @"filter_description" : @key(model.title),
        @"filter_code"        : @key(model.type),
        @"filter_values"      : @key(model.filterValues),
    };
}

+ (void)registerTransformers:(EHICarClassFilters *)model
{
    [super registerTransformers:model];
    
    [self key:@key(model.type) registerMap:@{
        @"TRANSMISSION" : @(EHIFilterTypeTransmission),
        @"PASSENGERS"   : @(EHIFilterTypePassengerCapacity),
        @"FUEL"         : @(EHIFilterTypeFuel),
        @"CLASS"        : @(EHIFilterTypeClass),
    } defaultValue:@(EHIFilterTypeUnknown)];
}

@end