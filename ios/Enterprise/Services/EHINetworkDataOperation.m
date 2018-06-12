//
//  EHINetworkDataOperation.m
//  Enterprise
//
//  Created by Ty Cobb on 1/14/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHINetworkDataOperation.h"
#import "EHINetworkOperation_Subclass.h"
#import "EHICrashManager.h"

@interface EHINetworkDataOperation ()
/** The session manager for creating network tasks */
@property (strong, nonatomic) AFHTTPSessionManager *sessionManager;
/** The session task running the actual network call */
@property (strong, nonatomic) NSURLSessionTask *task;
/** The @c CACurrentMediaTime() of when the operation was started */
@property (assign, nonatomic) CGFloat startTime;
/** Computed property returning the manager's response serializer */
@property (nonatomic, readonly) AFJSONResponseSerializer *responseSerializer;
@end

@implementation EHINetworkDataOperation

- (instancetype)initWithRequest:(EHINetworkRequest *)request sessionManager:(AFHTTPSessionManager *)manager
{
    if(self = [super initWithRequest:request sessionManager:manager]) {
        _sessionManager = manager;
    }
    
    return self;
}

- (void)start:(EHINetworkResponseHandler)handler
{
    // serialize the network request, may be asynchronous
    [self serializeRequestFromNetworkRequest:self.request handler:^(NSURLRequest *request) {
        // if we were canceled during serialization, quit here
        if(self.isCanceled) {
            return;
        }
        
        // note start time to determine latency
        self.startTime = CACurrentMediaTime();
       
        // otherwise kick off the session task
        self.task = [self startSessionTaskForRequest:request handler:^(NSURLResponse *urlResponse, id response, NSError *error) {
            [self logRequest:request response:(NSHTTPURLResponse *)urlResponse error:error];
            
            error = [self errorFromAFNetworkingError:error response:nil];
            ehi_call(handler)((NSHTTPURLResponse *)urlResponse, response, error);
        }];
    }];
}

- (BOOL)cancel
{
    [self.task cancel];
    return [super cancel];
}

# pragma mark - Tasking

- (NSURLSessionTask *)startSessionTaskForRequest:(NSURLRequest *)request handler:(void(^)(NSURLResponse *, id, NSError *))handler
{
    // log this request before we start it
    if(self.request.correlationId) {
        EHIDomainInfo(EHILogDomainNetwork, @"request  - %@ - %@", self.request.correlationId, [self relativePathForRequest:request]);
    } else {
        EHIDomainInfo(EHILogDomainNetwork, @"request  - %@", [self relativePathForRequest:request]);
    }
    
    // create the correct type of task
    NSURLSessionTask *task = request.HTTPBody
        ? [self.sessionManager uploadTaskWithRequest:request fromData:request.HTTPBody progress:nil completionHandler:handler]
        : [self.sessionManager dataTaskWithRequest:request completionHandler:handler];

    // and kick if off
    [task resume];
    
    return task;
}

- (NSString *)relativePathForRequest:(NSURLRequest *)request
{
    NSString *path  = self.request.relativePath;
    NSString *query = request.URL.query;

    return query.length ? [path stringByAppendingFormat:@"?%@", query] : path;
}

# pragma mark - Request Translation

- (void)serializeRequestFromNetworkRequest:(EHINetworkRequest *)request handler:(void(^)(NSURLRequest *request))handler
{
    handler([self basicUrlRequestFromNetworkRequest:request]);
}

- (NSURLRequest *)basicUrlRequestFromNetworkRequest:(EHINetworkRequest *)request
{
    // add per-request fields to the serializer
    for(NSString *field in request.headers) {
        [self.sessionManager.requestSerializer setValue:request.headers[field] forHTTPHeaderField:field];
    }
    
    NSMutableURLRequest *urlRequest =
        [self.sessionManager.requestSerializer requestWithMethod:request.httpMethod URLString:request.url.absoluteString
                                                      parameters:request.parameters ?: request.body error:nil];

    // destroy all the per-request fields
    for(NSString *field in request.headers) {
        [self.sessionManager.requestSerializer setValue:nil forHTTPHeaderField:field];
    }

    NSArray *cookies = (request.cookies ?: @{}).map(^(NSString *key, NSDictionary *properties){
        return [NSHTTPCookie cookieWithProperties:properties];
    });

    if(cookies.firstObject != nil) {
        urlRequest.allHTTPHeaderFields = [NSHTTPCookie requestHeaderFieldsWithCookies:cookies];
    }
    
    return urlRequest;
}

# pragma mark - Errors

- (NSError *)errorFromAFNetworkingError:(NSError *)error response:(NSArray *)response
{
    if(!error)
        return nil;
    
    NSHTTPURLResponse *failingResponse = error.userInfo[AFNetworkingOperationFailingURLResponseErrorKey];
    
    // don't know what to do with non-HTTP responses
    if(![failingResponse isKindOfClass:[NSHTTPURLResponse class]]) {
        return error;
    }
    
    // we want to return the status code and description that we actually care about
    NSString *description = [response componentsJoinedByString:@"; "] ?: error.userInfo[NSLocalizedDescriptionKey];
    
    return [NSError errorWithDomain:error.domain code:failingResponse.statusCode userInfo:@{
        NSLocalizedDescriptionKey : description ?: @(failingResponse.statusCode).description
    }];
}

# pragma mark - Accessors

- (AFJSONResponseSerializer *)responseSerializer
{
    return (AFJSONResponseSerializer *)self.sessionManager.responseSerializer;
}

# pragma mark - Debug

- (void)logRequest:(NSURLRequest *)request response:(NSHTTPURLResponse *)response error:(NSError *)error
{
    NSTimeInterval latency = CACurrentMediaTime() - self.startTime;
    NSUInteger bytesRead   = response.expectedContentLength == NSURLResponseUnknownLength ? 0 : (NSUInteger)response.expectedContentLength;
    
    EHICrashNetworkOperation *operation = [[EHICrashNetworkOperation alloc] initWithMethod:request.HTTPMethod
                                                                                   url:request.URL
                                                                               latency:latency
                                                                             bytesRead:bytesRead
                                                                             bytesSent:request.HTTPBody.length
                                                                          responseCode:response.statusCode
                                                                                 error:error];
    
    [EHICrashManager logNetworkOperation:operation];
}

@end
