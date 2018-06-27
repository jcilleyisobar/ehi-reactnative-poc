//
//  EHIRegion.m
//  Enterprise
//
//  Created by Alex Koller on 5/12/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIRegion.h"
#import "EHIModel_Subclass.h"

@implementation EHIRegion

# pragma mark - Mappings

+ (NSDictionary *)mappings:(EHIRegion *)model
{
    return @{
        @"country_subdivision_name" : @key(model.name),
        @"country_subdivision_code" : @key(model.code)
    };
}

- (NSComparisonResult)compare:(EHIRegion *)target
{
    return [self.name compare:target.name];
}

@end
