//
//  EHIPreferencesProfile.m
//  Enterprise
//
//  Created by mplace on 2/24/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIUserPreferencesProfile.h"
#import "EHIModel_Subclass.h"

@implementation EHIUserPreferencesProfile

+ (NSDictionary *)mappings:(EHIUserPreferencesProfile *)model
{
    return @{
        @"email_preference" : @key(model.email),
        @"source_code"      : @key(model.sourceCode),
    };
}

# pragma mark - EHINetworkEncodable

- (void)encodeWithRequest:(EHINetworkRequest *)request
{
    request[@"email_preference"] = self.email;
}

@end
