//
//  EHINetworkOperation_Subclass.h
//  Enterprise
//
//  Created by Ty Cobb on 1/15/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHINetworkOperation.h"

@interface EHINetworkOperation ()
/** @c YES if the operation has already been canceled */
@property (assign, nonatomic) BOOL isCanceled;
@end

@interface EHINetworkOperation (SubclassingHooks)

/**
 @brief Initializes the network operation
 
 This method is called by the factory method, @c +operationWithRequest:sessionManager, after
 it determines the correct operation class to instantiate.
 
 @note The session manager is not stored by the base @c EHINetworkOperation class.
 
 @param request The network request to make
 @param manager The session manager for making request
 
 @return A new network operation
*/

- (instancetype)initWithRequest:(EHINetworkRequest *)request sessionManager:(AFHTTPSessionManager *)manager;

@end
