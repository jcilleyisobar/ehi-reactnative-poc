//
//  EHINetworkRequest.h
//  Enterprise
//
//  Created by Ty Cobb on 1/13/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHINetworkConstants.h"
#import "EHIEnvironments.h"

@interface EHINetworkRequest : NSObject

/** The resolved URL for this request */
@property (copy, nonatomic, readonly) NSURL *url;
/** Additional HTTP headers to add to the request */
@property (copy, nonatomic, readonly) NSDictionary *headers;
/** Query parameters to append to the URL */
@property (copy, nonatomic, readonly) NSDictionary *parameters;
/** An object tree to serialize the request body form */
@property (copy, nonatomic, readonly) NSDictionary *body;
/*!
 The container of properties to be used to initialize cookies.
 See the NSHTTPCookie -initWithProperties: method for
 more information on the constraints imposed on the dictionary, and
 for descriptions of the supported keys and values.
 */
@property (copy, nonatomic, readonly) NSDictionary *cookies;
/** The enumerated HTTP verb for this request */
@property (assign, nonatomic, readonly) EHINetworkRequestMethod method;
/** The stringified HTTP verb for this request */
@property (copy, nonatomic, readonly) NSString *httpMethod;
/** A relative path for this request */
@property (copy, nonatomic, readonly) NSString *relativePath;
/** The services correlation id for tracking this request, or @c nil */
@property (copy, nonatomic, readonly) NSString *correlationId;

+ (instancetype)service:(EHIServicesEnvironmentType)serviceType get:(NSString *)path, ...;
+ (instancetype)service:(EHIServicesEnvironmentType)serviceType post:(NSString *)path, ...;
+ (instancetype)service:(EHIServicesEnvironmentType)serviceType put:(NSString *)path, ...;
+ (instancetype)service:(EHIServicesEnvironmentType)serviceType update:(NSString *)path, ...;

- (EHINetworkRequest *)headers:(void(^)(EHINetworkRequest *request))block;
- (EHINetworkRequest *)parameters:(void(^)(EHINetworkRequest *request))block;
- (EHINetworkRequest *)body:(void(^)(EHINetworkRequest *request))block;
- (EHINetworkRequest *)cookieProperties:(void (^)(EHINetworkRequest *request))block;

- (void)setObject:(id)object forKeyedSubscript:(NSString *)key;
- (id)objectForKeyedSubscript:(NSString *)key;

@end
