//
//  EHIDeals.m
//  Enterprise
//
//  Created by Rafael Ramos on 18/06/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIModel_Subclass.h"
#import "EHIDeals.h"

@implementation EHIDeals

+ (NSDictionary *)mappings:(EHIDeals *)model
{
    return @{
        @"displayName" : @key(model.displayName),
        @"dealsType"   : @key(model.type),
        @"deals"       : @key(model.deals),
    };
}

+ (void)registerTransformers:(EHIDeals *)model
{
    [super registerTransformers:model];
    
    [self key:@key(model.type) registerMap:@{
        @"local"         : @(EHIDealsTypeLocal),
        @"international" : @(EHIDealsTypeInternacional),
    } defaultValue:@(EHIDealsTypeUnknown)];
}

@end
