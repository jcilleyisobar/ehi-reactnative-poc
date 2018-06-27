//
//  EHINetworkOperation.h
//  Enterprise
//
//  Created by Ty Cobb on 1/14/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import <AFNetworking/AFNetworking.h>
#import "EHINetworkCancelable.h"
#import "EHINetworkRequest.h"

@interface EHINetworkOperation : NSObject <EHINetworkCancelable>

/** The network request (or file request) to make */
@property (strong, nonatomic) EHINetworkRequest *request;

/**
 @brief Factory for generating network operations from requests
 
 @param request The network request to execute
 @param manager The session manager for creating network tasks
*/

+ (instancetype)operationForRequest:(EHINetworkRequest *)request sessionManager:(AFHTTPSessionManager *)manager;

/**
 @brief Kicks off the network operation
 
 The operation is cancelable at any time by called the @c -cancel method provided
 by @c EHINetworkCancelable.
 
 @param handler The handler to call when the request completes
*/

- (void)start:(EHINetworkResponseHandler)handler;

@end

