//
//  EHINetworkEncodable.m
//  Enterprise
//
//  Created by Ty Cobb on 1/14/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHINetworkEncodable.h"

@implementation NSDictionary (Encodable)

- (void)encodeWithRequest:(EHINetworkRequest *)request
{
    for(NSString *key in self) {
        request[key] = self[key];
    }
}

@end
