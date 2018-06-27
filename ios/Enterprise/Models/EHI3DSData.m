//
//  EHI3DSData.m
//  Enterprise
//
//  Created by cgross on 1/25/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHI3DSData.h"

@interface EHI3DSData ()
@property (copy, nonatomic, readonly) NSString *accessControlSystemUrl;
@property (copy, nonatomic, readonly) NSString *authenticationRequest;
@end

@implementation EHI3DSData

+ (NSDictionary *)mappings:(EHI3DSData *)model
{
    return @{
        @"perform3_ds" : @key(model.isSupported),
        @"acs_url"     : @key(model.accessControlSystemUrl),
        @"pa_req"      : @key(model.authenticationRequest),
    };
}

- (NSURL *)url
{
    return [NSURL URLWithString:self.accessControlSystemUrl];
}

- (NSString *)body
{
    // percent encode the values
    NSString *pareq = [self.authenticationRequest stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet alphanumericCharacterSet]];
    NSString *termUrl = [EHI3dsTermUrl stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet URLHostAllowedCharacterSet]];
    
    return [NSString stringWithFormat:@"PaReq=%@&TermUrl=%@", pareq, termUrl];
}

@end
