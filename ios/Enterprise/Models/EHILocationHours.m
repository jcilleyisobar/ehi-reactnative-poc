//
//  EHILocationHours.m
//  Enterprise
//
//  Created by Ty Cobb on 5/8/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel_Subclass.h"
#import "EHILocationHours.h"

@implementation EHILocationHours

# pragma mark - Mappings

+ (NSDictionary *)mappings:(EHILocationHours *)model
{
    return @{
        @"data" : @key(model.days),
    };
}

@end

@implementation EHILocationHours (Subscripting)

- (EHILocationDay *)objectForKeyedSubscript:(id)key
{
    // stringify the the date if necessary
    if([key isKindOfClass:[NSDate class]]) {
        key = [(NSDate *)key ehi_string];
    }
    
    return self.days[key];
}

@end
