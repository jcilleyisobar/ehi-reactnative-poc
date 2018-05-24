//
//  EHINetworkClient.h
//  Enterprise
//
//  Created by Ty Cobb on 1/13/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHINetworkRequest.h"
#import "EHINetworkCancelable.h"
#import "EHINetworkError.h"

@protocol EHINetworkClientDelegate;

@interface EHINetworkClient : NSObject

/** Receives network request lifecycle events */
@property (weak, nonatomic) id<EHINetworkClientDelegate> delegate;

/** Shared HTTP headers for all network requests */
@property (strong, nonatomic) NSDictionary *headers;

/**
 @brief Fetches the data for the specified network request
 
 When the request data comes back, or an error occurs, the client will call back the handler
 with either the data or an error.

 @param request The request to kick out to the network
 @param handler The callback when the request completes
 
 @return An object that can be used to cancel the request
*/

- (id<EHINetworkCancelable>)fetchRequest:(EHINetworkRequest *)request handler:(EHINetworkResponseHandler)handler;

/**
 @brief Updates the shared HTTP headers
 
 Headers may be updated inside this method using key-value subscripting, and setting the value of 
 a header to @c nil will clear it.
 
 The value of the client's @c headers property will be the headers before updating, and changes
 won't be reflected until after the call to @c -updateHeaders: completes.

 @param block The block to update the headers
*/

- (void)updateHeaders:(void(^)(EHINetworkClient *client))block;

/**
 @brief Subscripting for updating shared HTTP headers

 This method throws an exception if used outside an @c -updateHeaders: block.
 
 @param value The updated header value
 @param key   The header name
*/

- (void)setObject:(NSString *)value forKeyedSubscript:(NSString *)key;

@end

@protocol EHINetworkClientDelegate <NSObject> @optional

/**
 @brief Marks a response as an error condition
 
 This method is only called for requests which do not error normally. Provides the client the
 opportunity to inspect the reponse and determine if it should be treated as an error.
 
 @param client   The requesting network client
 @param request  The potentially offending request
 @param response The de-serialized response
 
 @return @c YES if the response response should be treated as an error
*/

- (BOOL)client:(EHINetworkClient *)client request:(EHINetworkRequest *)request shouldFailWithResponse:(id)response;

/**
 @brief Parses the service error / response
 
 Provides the delegate an opportunity to customize the error, such as in the event that errors
 display information is returned in the response and not the standard error object.
 
 @param client   The requesting network client
 @param request  The offending request
 @param response The de-serialized response

 @return A new, customized error or @c nil if no customization is required
*/

- (id<EHINetworkError>)client:(EHINetworkClient *)client parseError:(NSError *)error response:(id)response;

/**
 @brief Allows the delegate to preempt the standard error handling path
 
 The delegate @em must call the completion once it finishes handling (or not handling) the error so
 the normal service flow can resume.
 
 The path will use the @c error (or @c nil) passed to the completion block for the rest of the flow.
 
 @param client          The requesting network client
 @param error           The error to optionally handle
 @param completion A block to call after finishing work on the error
*/

- (void)client:(EHINetworkClient *)client preemptivelyHandleError:(id<EHINetworkError>)error completion:(void(^)(id<EHINetworkError> error))completion;

/**
 @brief Asks delegate if request should be retried
 
 Called after all internal serialization has completed and before the passed in request handler
 is called. Allows delegate to make changes and determine if client should retry the request.
 
 @param client          The requesting network client
 @param error           The error associated with the request
 @param completion      A block to call after determining if request should be retried
 
 @return Whether the handler and subsequent error handling callbacks should be invoked
 */

- (void)client:(EHINetworkClient *)client shouldRetryRequest:(EHINetworkRequest *)request error:(id<EHINetworkError>)error completion:(void (^)(BOOL retry))completion;

/**
 @brief Notifies the delegate of a failure
 
 This step happens after the rest of the error delegate chain, and is called for any request
 that finished with an error.
 
 @param client  The requesting network client
 @param request The offending request
 @param error   The resultant error
*/

- (void)client:(EHINetworkClient *)client request:(EHINetworkRequest *)request failedWithError:(id<EHINetworkError>)error;

@end
