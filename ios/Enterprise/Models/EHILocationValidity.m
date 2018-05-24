//
//  EHILocationValidity.m
//  Enterprise
//
//  Created by Rafael Ramos on 5/22/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIModel_Subclass.h"

@implementation EHILocationValidity

+ (NSDictionary *)mappings:(EHILocationValidity *)model
{
    return @{
        @"locationHours" : @key(model.hours),
        @"validityType"  : @key(model.status),
    };
}

+ (void)registerTransformers:(EHILocationValidity *)model
{
    [self key:@key(model.status) registerMap:@{
        @"VALID_STANDARD_HOURS" : @(EHILocationValidityStatusValidStandardHours),
        @"VALID_AFTER_HOURS"    : @(EHILocationValidityStatusValidAfterHours),
        @"INVALID_ALL_DAY" 		: @(EHILocationValidityStatusInvalidAllDay),
        @"INVALID_AT_THAT_TIME" : @(EHILocationValidityStatusInvalidAtThatTime),
    } defaultValue:@(EHILocationValidityStatusUnknown)];
}

@end
