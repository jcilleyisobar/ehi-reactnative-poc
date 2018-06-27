//
//  EHIServices_Private.h
//  Enterprise
//
//  Created by Ty Cobb on 1/14/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIServices.h"
#import "EHINetworkClient.h"
#import "EHIUser.h"
#import "EHISettings.h"

@interface EHIServices () <EHINetworkClientDelegate>
/** Client for interfacing with the underlying networking implementation. */
@property (nonatomic, readonly) EHINetworkClient *client;
@end

@interface EHIServices (Parsing)

/** Block which transforms response data from one format to another */
typedef id(^EHIServicesParser)(id responseData);

/**
 @brief Services helper that fetches a request
 
 The request is not parsed in any way, and the data from the client is passed to the handler.
 See @c -startRequest:parseAsynchronously:withBlock:handler for more information.
*/

- (id<EHINetworkCancelable>)startRequest:(EHINetworkRequest *)request handler:(EHIServicesHandler)handler;

/**
 @brief Services helper that fetches a response for a specified model type
 
 The network response is transformed automatically by calling @c +modelWithDictioanry: on 
 the passed type. See @c -startRequest:parseAsynchronously:withBlock:handler for more 
 information.
 
 @param klass The model klass to generate
*/

- (id<EHINetworkCancelable>)startRequest:(EHINetworkRequest *)request parseModel:(Class<EHIModel>)klass asynchronously:(BOOL)isAsynchronous handler:(EHIServicesHandler)handler;

/**
 @brief Services helper that fetches a response for a specified model type
 
 The network response is automatically applied to the paramaterized @c model as an update
 using @c -updateWithDictionary:. See @c -startRequest:parseAsynchronously:withBlock:handler for 
 more information.
 
 @param klass The model klass to generate
*/

- (id<EHINetworkCancelable>)startRequest:(EHINetworkRequest *)request updateModel:(EHIModel *)model asynchronously:(BOOL)isAsynchronous handler:(EHIServicesHandler)handler;

- (id<EHINetworkCancelable>)startRequest:(EHINetworkRequest *)request updateModel:(EHIModel *)model forceDeletions:(BOOL)forceDeletions asynchronously:(BOOL)isAsynchronous handler:(EHIServicesHandler)handler;

/**
 @brief Services helper that fetches a response for a specified model collection
 
 The network response is transformed automatically by calling @c +modelsWithDictionaries: on
 the passed type. See @c -startRequest:parseAsynchronously:withBlock:handler for more 
 information.
 
 @param The model klass to generate a collection of
*/

- (id<EHINetworkCancelable>)startRequest:(EHINetworkRequest *)request parseCollection:(Class<EHIModel>)klass asynchronously:(BOOL)isAsynchronous handler:(EHIServicesHandler)handler;

/**
 @brief Services helper that provides parsing support
 
 The request will be handed to the service's internal @c client. When the reponse comes back, it is first 
 passed through the @c parser, and if @c isAsynchronous is @c YES it will also be parsed on a worker queue.
 
 @param request        The network request to fetch
 @param isAsynchronous @c YES is the parsing should happen on a worker queue
 @param parser         The block to process the response
 @param handler        The handler called back when parsing completes
 
 @return An object that can be used to cancel the request
*/

- (id<EHINetworkCancelable>)startRequest:(EHINetworkRequest *)request parseAsynchronously:(BOOL)isAsynchronous withBlock:(EHIServicesParser)parser handler:(EHIServicesHandler)handler;

@end
