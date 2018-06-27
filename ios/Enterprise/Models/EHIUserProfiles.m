//
//  EHIUserProfiles.m
//  Enterprise
//
//  Created by mplace on 2/23/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIUserProfiles.h"
#import "EHIModel_Subclass.h"

@implementation EHIUserProfiles

# pragma mark - Mappings

+ (NSDictionary *)mappings:(EHIUserProfiles *)model
{
    return @{
        @"basic_profile"    : @key(model.basic),
        @"individual_id"    : @key(model.individualId),
        @"customer_details" : @key(model.corporateContract)
    };
}

@end
