//
//  EHILocationHours.m
//  Enterprise
//
//  Created by Ty Cobb on 1/20/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILocationWeek.h"
#import "EHIModel_Subclass.h"

@implementation EHILocationWeek

# pragma mark - EHIModel

+ (void)registerTransformers:(EHILocationWeek *)model
{
    [self key:@key(model.type) registerMap:@{
        @"STANDARD" : @(EHILocationHoursTypeStandard),
        @"DROP"     : @(EHILocationHoursTypeDrop),
    } defaultValue:@(EHILocationHoursTypeUnknown)];
}

+ (NSDictionary *)mappings:(EHILocationWeek *)model
{
    return @{
        @"location-hours" : @key(model.days),
        @"hours" : @key(model.days)
    };
}

@end
