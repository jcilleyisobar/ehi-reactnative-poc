//
//  EHINetworkFileOperation.m
//  Enterprise
//
//  Created by Ty Cobb on 1/14/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHINetworkFileOperation.h"

@implementation EHINetworkFileOperation

- (void)start:(EHINetworkResponseHandler)handler
{
    // log this request
    EHIDomainInfo(EHILogDomainNetwork, @"mock - %@", self.request.relativePath);
    
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT , 0), ^{
        NSError *error;
        id response = [NSData dataWithContentsOfURL:self.request.url options:0 error:&error];
        response = [NSJSONSerialization JSONObjectWithData:response options:0 error:&error];
        ehi_call(handler)(nil, response, error);
    });
}

@end
