//
//  EHILocationDay.m
//  Enterprise
//
//  Created by Ty Cobb on 5/8/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel_Subclass.h"
#import "EHILocationDay.h"

@implementation EHILocationDay

# pragma mark - Mappings

+ (NSDictionary *)mappings:(EHILocationDay *)model
{
    return @{
        @"DROP"        : @key(model.dropTimes),
        @"STANDARD"    : @key(model.standardTimes),
        @"AFTER_HOURS" : @key(model.afterHoursTimes),
    };
}

@end
