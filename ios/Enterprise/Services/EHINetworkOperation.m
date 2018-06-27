//
//  EHINetworkOperation.m
//  Enterprise
//
//  Created by Ty Cobb on 1/14/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHINetworkOperation_Subclass.h"
#import "EHINetworkDataOperation.h"
#import "EHINetworkFileOperation.h"

@implementation EHINetworkOperation

+ (instancetype)operationForRequest:(EHINetworkRequest *)request sessionManager:(AFHTTPSessionManager *)manager
{
    return [[[self subclassForRequest:request] alloc] initWithRequest:request sessionManager:manager];
}

+ (Class)subclassForRequest:(EHINetworkRequest *)request
{
    return request.url.isFileURL ? [EHINetworkFileOperation class] : [EHINetworkDataOperation class];
}

- (instancetype)initWithRequest:(EHINetworkRequest *)request sessionManager:(AFHTTPSessionManager *)manager
{
    if(self = [self init]) {
        _request = request;
    }
    
    return self;
}

- (void)start:(EHINetworkResponseHandler)handler
{
    
}

- (BOOL)cancel
{
    self.isCanceled = YES;
    return YES;
}

@end
