//
//  EHIWebContent.m
//  Enterprise
//
//  Created by Ty Cobb on 6/25/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel_Subclass.h"
#import "EHIWebContent.h"

@implementation EHIWebContent

# pragma mark - Mappings

+ (NSDictionary *)mappings:(EHIWebContent *)model
{
    return @{
        // key facts
        @"key_facts"                    : @key(model.body),
        // privacy policy
        @"privacy_policy"               : @key(model.body),
        // terms of use
        @"terms_of_use"                 : @key(model.body),
        // taxes
        @"taxes_fees_and_surcharges"    : @key(model.body),
        // terms and conditions
        @"terms_and_conditions"         : @key(model.body),
        @"terms_and_conditions_version" : @key(model.version),
        // prepay
        @"prepay_terms_and_conditions"  : @key(model.body),

    };
}

+ (instancetype)webContentWithBody:(NSString *)body
{
    EHIWebContent *model;
    return [self modelWithDictionary:@{
        @key(model.body) : body
    }];
}

@end
