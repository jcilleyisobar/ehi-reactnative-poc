//
//  EHINetworkClient.m
//  Enterprise
//
//  Created by Ty Cobb on 1/13/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import <AFNetworking/AFNetworking.h>
#import <AFNetworking/AFNetworkActivityIndicatorManager.h>
#import "EHINetworkClient.h"
#import "EHINetworkOperation.h"
#import "EHIServicesError.h"

@interface EHINetworkClient ()
@property (strong, nonatomic) AFHTTPSessionManager *sessionManager;
@property (strong, nonatomic) NSMutableDictionary *updatingHeaders;
@end

@implementation EHINetworkClient

+ (void)initialize
{
    [super initialize];
    
    [AFNetworkActivityIndicatorManager sharedManager].enabled = YES;
}

- (instancetype)init
{
    if(self = [super init]) {
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didEnterBackground:) name:UIApplicationDidEnterBackgroundNotification object:nil];
    }
    
    return self;
}

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)didEnterBackground:(NSNotification *)notification
{
    self.sessionManager = nil;
}

# pragma mark - Requests

- (id<EHINetworkCancelable>)fetchRequest:(EHINetworkRequest *)request handler:(EHINetworkResponseHandler)handler
{
    NSAssert(request.url, @"We can't make a request without a URL.");
    
    if(!self.sessionManager) {
        self.sessionManager = [self rebuildSessionManagerWithHeaders:nil];
    }
    
    // create the network request
    EHINetworkOperation *operation = [EHINetworkOperation operationForRequest:request sessionManager:self.sessionManager];

    // kick it off with our modified handler
    [operation start:^(NSHTTPURLResponse *urlResponse, id response, NSError *error) {
        // allow the delegate to customize the error
        id<EHINetworkError> finalError = [self parseErrorForRequest:request response:response error:error];
       
        // dispatch the completion to the main queue
        dispatch_async(dispatch_get_main_queue(), ^{
            [self didFinishRequest:request withHTTPResponse:urlResponse response:response error:finalError handler:handler];
        });
    }];
    
    return operation;
}

- (id<EHINetworkError>)parseErrorForRequest:(EHINetworkRequest *)request response:(id)response error:(NSError *)error
{
    id<EHINetworkError> result = error;
    
    BOOL shouldFail = error != nil;
    
    if(!shouldFail && [self.delegate respondsToSelector:@selector(client:request:shouldFailWithResponse:)]) {
        shouldFail = [self.delegate client:self request:request shouldFailWithResponse:response];
    }
   
    if(shouldFail && [self.delegate respondsToSelector:@selector(client:parseError:response:)]) {
        result = [self.delegate client:self parseError:error response:response];
    }
    
    return result;
}

- (void)didFinishRequest:(EHINetworkRequest *)request withHTTPResponse:(NSHTTPURLResponse *)urlResponse response:(id)response error:(id<EHINetworkError>)error handler:(EHINetworkResponseHandler)handler
{
    if(!error) {
        EHIDomainInfo(EHILogDomainNetwork, @"response - %@", request.relativePath);
        EHIDomainVerbose(EHILogDomainNetwork, @"%@", response);
    } else {
        EHIDomainError(EHILogDomainNetwork, @"response - %@ ERROR: %@", request.relativePath, error.message);
    }
    
    // if the degate doesn't want to preempt, then finish
    if(![self.delegate respondsToSelector:@selector(client:preemptivelyHandleError:completion:)] || !error) {
        [self prepareToHandleRequest:request withHTTPResponse:urlResponse response:response error:error handler:handler];
    }
    // otherwise, allow the delegate to try and handle the error
    else {
        [self.delegate client:self preemptivelyHandleError:error completion:^(id<EHINetworkError> resultingError) {
            // and then complete with whatever error it gives us
            [self prepareToHandleRequest:request withHTTPResponse:urlResponse response:response error:error handler:handler];
        }];
    }
}

- (void)prepareToHandleRequest:(EHINetworkRequest *)request withHTTPResponse:(NSHTTPURLResponse *)urlResponse response:(id)response error:(id<EHINetworkError>)error handler:(EHINetworkResponseHandler)handler
{
    // allow delegate to circumvent completion and perform steps to fix a faulty request
    if([self.delegate respondsToSelector:@selector(client:shouldRetryRequest:error:completion:)]) {
        [self.delegate client:self shouldRetryRequest:request error:error completion:^(BOOL retry) {
            if(retry) {
                [self fetchRequest:request handler:handler];
            } else {
                [self finishHandlingRequest:request withHTTPResponse:urlResponse response:response error:error handler:handler];
            }
        }];
    }
    // otherwise, finish immediately
    else {
        [self finishHandlingRequest:request withHTTPResponse:urlResponse response:response error:error handler:handler];
    }
}

- (void)finishHandlingRequest:(EHINetworkRequest *)request withHTTPResponse:(NSHTTPURLResponse *)urlResponse response:(id)response error:(id<EHINetworkError>)error handler:(EHINetworkResponseHandler)handler
{
    // allow the handler first crack at the response
    ehi_call(handler)(urlResponse, response, error);
    
    // then run our own processing if necessary
    if(error && [self.delegate respondsToSelector:@selector(client:request:failedWithError:)]) {
        // async it so that it's guaranteed to run after the operations in the handler
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.delegate client:self request:request failedWithError:error];
        });
    }
}

# pragma mark - Headers

- (NSDictionary *)headers
{
    return self.sessionManager.session.configuration.HTTPAdditionalHeaders ?: @{ };
}

- (void)setHeaders:(NSDictionary *)headers
{
    self.sessionManager = [self rebuildSessionManagerWithHeaders:headers];
}

- (void)updateHeaders:(void (^)(EHINetworkClient *))block
{
    NSParameterAssert(block);
   
    // create storage for the mutated headers
    self.updatingHeaders = [self.headers mutableCopy];
    
    // update the headers and rebuild the session manager
    block(self);
    self.headers = [self.updatingHeaders copy];
    
    // clear out the mutated headers
    self.updatingHeaders = nil;
}

- (void)setObject:(NSString *)value forKeyedSubscript:(NSString *)key
{
    NSParameterAssert(key);
    NSAssert(self.updatingHeaders, @"setObject:forKeyedSubscript is unavailable outside of an updateHeaders: block");
    [self.updatingHeaders setValue:value forKey:key];
}

- (AFHTTPSessionManager *)rebuildSessionManagerWithHeaders:(NSDictionary *)headers
{
    if(!headers) {
        headers = @{};
    }
    
    NSURLSessionConfiguration *sessionConfiguration = [NSURLSessionConfiguration defaultSessionConfiguration];
    sessionConfiguration.HTTPAdditionalHeaders = headers;
	
    AFHTTPSessionManager *sessionManager = [[AFHTTPSessionManager alloc] initWithSessionConfiguration:sessionConfiguration];
    sessionManager.completionQueue   = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_BACKGROUND, 0);
    sessionManager.requestSerializer = [AFJSONRequestSerializer serializer];

	// If we are not DEBUG, explicitly pin our trusted certs
#if !defined(DEBUG) && !defined(UAT)

	// Pin our SSL certificate(s) directly within the app
	AFSecurityPolicy *securityPolicy = [AFSecurityPolicy policyWithPinningMode:AFSSLPinningModeCertificate];

	NSArray *certPaths = @[

        // Root Certificates
        [[NSBundle mainBundle] pathForResource:@"GeoTrust_Global_CA" ofType:@"cer"], // for QA SOLR and FareOffice
        [[NSBundle mainBundle] pathForResource:@"AddTrust_External_CA_Root" ofType:@"cer"], // for PROD SOLR and all MSI

        // TODO: Do not pin this one when iOS 10.1 (beta) problem will be solved
        // Intermediate Certificates
        [[NSBundle mainBundle] pathForResource:@"COMODO_RSA_Organization_Validation_Secure_Server_CA" ofType:@"cer"], // for PROD SOLR and all MSI
    ];
	
    
    NSArray *certs = certPaths.map(^(NSString *cert){
        return [NSData dataWithContentsOfFile:cert];
    });

	[securityPolicy setPinnedCertificates:[NSSet setWithArray:certs]];
	sessionManager.securityPolicy = securityPolicy;
#endif

    // allow serializing text/plain for non-mobile/location services (payment services)
    AFJSONResponseSerializer *serializer = [AFJSONResponseSerializer serializerWithReadingOptions:NSJSONReadingAllowFragments];
    serializer.acceptableContentTypes = [serializer.acceptableContentTypes setByAddingObject:@"text/plain"];
    sessionManager.responseSerializer = serializer;
    
    return sessionManager;
}

@end
