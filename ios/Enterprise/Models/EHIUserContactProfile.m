//
//  EHIUserContactProfile.m
//  Enterprise
//
//  Created by mplace on 2/24/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIUserContactProfile.h"

@implementation EHIUserContactProfile

+ (NSDictionary *)mappings:(EHIUserContactProfile *)model
{
    return @{
        @"email"      : @key(model.email),
        @"mask_email" : @key(model.maskedEmail),
    };
}

# pragma mark - EHINetworkEncodable

- (void)encodeWithRequest:(EHINetworkRequest *)request
{
    [super encodeWithRequest:request];
    
    request[@"email"] = self.email;
    request[@"phones"] = self.phones;
}

@end
