//
//  EHINetworkRequest.m
//  Enterprise
//
//  Created by Ty Cobb on 1/13/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHINetworkRequest.h"
#import "EHINetworkEncodable.h"
#import "EHISettings.h"
#import "EHIUser.h"

#define EHINetworkRequestFormatPath(_path) \
    va_list args; va_start(args, _path); _path = [[NSString alloc] initWithFormat:path arguments:args]; va_end(args);

@interface EHINetworkRequest ()
/** A mutable container for constructing the URL */
@property (strong, nonatomic) NSURLComponents *components;
/** A container for encoding various attributes dictionaries */
@property (strong, nonatomic) NSMutableDictionary *attributes;
@property (assign, nonatomic) EHIServicesEnvironmentType serviceType;
@end

@implementation EHINetworkRequest

+ (instancetype)service:(EHIServicesEnvironmentType)serviceType get:(NSString *)path, ...
{
    EHINetworkRequestFormatPath(path);
    return [[self alloc] initWithPath:path serviceType:serviceType method:EHINetworkRequestMethodGet];
}

+ (instancetype)service:(EHIServicesEnvironmentType)serviceType post:(NSString *)path, ...
{
    EHINetworkRequestFormatPath(path);
    return [[self alloc] initWithPath:path serviceType:serviceType method:EHINetworkRequestMethodPost];
}

+ (instancetype)service:(EHIServicesEnvironmentType)serviceType put:(NSString *)path, ...
{
    EHINetworkRequestFormatPath(path);
    return [[self alloc] initWithPath:path serviceType:serviceType method:EHINetworkRequestMethodPut];
}

+ (instancetype)service:(EHIServicesEnvironmentType)serviceType update:(NSString *)path, ...
{
    EHINetworkRequestFormatPath(path);
    EHINetworkRequest *request = [[self alloc] initWithPath:path serviceType:serviceType method:EHINetworkRequestMethodDelete];
    request.serviceType = serviceType;

    return request;
}

- (instancetype)initWithPath:(NSString *)path serviceType:(EHIServicesEnvironmentType)serviceType method:(EHINetworkRequestMethod)method
{
    NSParameterAssert(path);
    
    if(self = [super init]) {
        _serviceType = serviceType;
        _method      = method;
        _components  = [self componentsByResolvingPath:path];
        
        [self headers:^(EHINetworkRequest *request) {
            request[EHIRequestHeaderAcceptLanguageKey] = [NSLocale ehi_identifier];
        }];
        
        [self addSharedHeadersForService:serviceType];
    }
	
    return self;
}

- (void)addSharedHeadersForService:(EHIServicesEnvironmentType)serviceType
{
    if(serviceType == EHIServicesEnvironmentTypeAEM) {
        return;
    }
    
    // add shared headers for requests relative to GBO
    NSString *service = [EHISettings.environment serviceWithType:serviceType];
    if(service && [self.url.absoluteString hasPrefix:service]) {
        [self headers:^(EHINetworkRequest *request) {
            NSString *apiKey = [EHISettings.environment servicesApiKeyWithType:serviceType];
#if defined(DEBUG) || defined(UAT)
            if(EHISettings.sharedInstance.forceWrongApiKey) {
                apiKey = EHIWrongApiKey;
            }
#endif
            request[EHIRequestHeaderApiKeyKey]        = apiKey;
            request[EHIRequestHeaderAuthTokenKey]     = EHIUser.currentUser.authorizationToken;
            request[EHIRequestHeaderCorKey]           = [NSLocale ehi_region];
            request[EHIRequestHeaderCorrelationIdKey] = [NSUUID.UUID UUIDString];
        }];
    }
    
    // add shared headers/params for requests relative to solr
    if([self.url.absoluteString hasPrefix:EHISettings.environment.search]) {
        [[self headers:^(EHINetworkRequest *request) {
            request[EHIRequestHeaderSearchApiKey] = EHISettings.environment.searchApiKey;
        }] parameters:^(EHINetworkRequest *request) {
            request[EHIRequestParamFallbackKey]  = @"en_GB";
        }];
    }
}

# pragma mark - URL Resolution

- (NSURLComponents *)componentsByResolvingPath:(NSString *)path
{
    if([path hasPrefix:@"mock"]) {
        return [[NSURLComponents alloc] initWithURL:[self filesystemUrlFromPath:path] resolvingAgainstBaseURL:NO];
    }
  
    return [[NSURLComponents alloc] initWithString:[self absolutePathByResolvingPath:path]];
}

- (NSString *)absolutePathByResolvingPath:(NSString *)path
{
    // this is a relative path
    if(![path hasPrefix:@"http"]) {
        // ensure relative paths don't have a leading slash
        if([path hasPrefix:@"/"]) {
            path = [path substringFromIndex:1];
        }

        // now we should have an absolute path
        path = [[EHISettings.environment serviceWithType:self.serviceType] stringByAppendingString:path];
    }

    // url-encode the path before we try and make a URL out of it
    path = [path stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet URLQueryAllowedCharacterSet]];
    
    return path;
}

- (NSURL *)filesystemUrlFromPath:(NSString *)path
{
    NSParameterAssert([path matchesRegex:@"mock://.+\\..+"]);
    
    NSArray *components = [[path substringFromIndex:@"mock://".length] componentsSeparatedByString:@"."];
    path = [[NSBundle mainBundle] pathForResource:[components firstObject] ofType:[components lastObject]];
    
    return [NSURL fileURLWithPath:path];
}

# pragma mark - Builders

- (EHINetworkRequest *)headers:(void (^)(EHINetworkRequest *))block
{
    return [self buildAttributes:self.headers block:block handler:^(NSDictionary *attributes) {
        _headers = attributes;
    }];
}

- (EHINetworkRequest *)parameters:(void (^)(EHINetworkRequest *))block
{
    return [self buildAttributes:self.parameters block:block handler:^(NSDictionary *attributes) {
        _parameters = attributes;
    }];
}

- (EHINetworkRequest *)body:(void (^)(EHINetworkRequest *))block
{
    return [self buildAttributes:self.body block:block handler:^(NSDictionary *attributes) {
        _body = attributes;
    }];
}

- (EHINetworkRequest *)cookieProperties:(void (^)(EHINetworkRequest *request))block
{
    return [self buildAttributes:self.cookies block:block handler:^(NSDictionary *attributes) {
        _cookies = attributes;
    }];
}

//
// Helpers
//

- (EHINetworkRequest *)buildAttributes:(NSDictionary *)attributes block:(void(^)(EHINetworkRequest *request))builder handler:(void(^)(NSDictionary *attributes))handler
{
    self.attributes = [[NSMutableDictionary alloc] initWithDictionary:attributes];
    
    ehi_call(builder)(self);
    handler(self.attributes.copy);
    
    self.attributes = nil;
    
    return self;
}

# pragma mark - Subscripting

- (id)objectForKeyedSubscript:(NSString *)key;
{
    return [self.attributes objectForKey:key];
}

- (void)setObject:(id)value forKeyedSubscript:(NSString *)key
{
    NSParameterAssert(key);
   
    id encodedValue = [self encodeValue:value];
    // update the attributes, nil'ing the value out if the encode value is nil
    [self.attributes setValue:encodedValue forKey:key];
}

- (id)encodeValue:(id)value
{
    if(!value) {
        return nil;
    }
    else if([value isKindOfClass:[NSArray class]]) {
        return [self encodeArray:value];
    }
    else if([value isKindOfClass:[NSNull class]]) {
        return [NSNull null];
    }
    else if([value respondsToSelector:@selector(encodeWithRequest:)]) {
        return [self encodeNestedEncodable:value];
    }
    
    NSString *string = [value description]; // this is a plain ol' value, just stringify it
    return string.length ? string : nil;    // if this is 0 length string, we won't even bother
}

//
// Helpers
//

- (NSArray *)encodeArray:(NSArray *)array
{
    return array.map(^(id value) {
        return [self encodeValue:value];
    });
}

- (NSDictionary *)encodeNestedEncodable:(id<EHINetworkEncodable>)value
{
    // if this value is encodable, create a new context (dictionary) to encode into and store the current context.
    // when this context finishes encoding, return to the previous context and return our encoded data
    
    id context = self.attributes;
    
    NSMutableDictionary *attributes = [NSMutableDictionary new];
    self.attributes = attributes;
    [value encodeWithRequest:self];
    
    self.attributes = context;
    
    return attributes;
}

# pragma mark - Accessors

- (NSURL *)url
{
    return self.components.URL;
}

- (NSString *)correlationId
{
    return self.headers[EHIRequestHeaderCorrelationIdKey];
}

- (NSString *)httpMethod
{
    switch(self.method) {
        case EHINetworkRequestMethodGet:
            return @"GET";
        case EHINetworkRequestMethodPost:
            return @"POST";
        case EHINetworkRequestMethodPut:
            return @"PUT";
        case EHINetworkRequestMethodDelete:
            return @"DELETE";
    }
}

# pragma mark - Debugging

- (NSString *)description
{
    return [[super description] stringByAppendingFormat:@" %@", self.relativePath];
}

- (NSString *)relativePath
{
    NSString *path = self.url.absoluteString;
   
    // find the base path for this url, if any
    NSString *base = (@[
        [EHISettings.environment serviceWithType:self.serviceType],
        EHISettings.environment.search,
    ]).find(^(NSString *root) {
        return [path hasPrefix:root];
    });
   
    // clear out the base path if we found one
    if(base) {
        path = [path stringByReplacingCharactersInRange:(NSRange){ .length = base.length } withString:@""];
    }
    // otherwise, if this is a file url prettify it for logging
    else if([path hasPrefix:@"file:///"]) {
        path = [NSString stringWithFormat:@"mock/%@", path.lastPathComponent];
    }
    
    return path;
}

@end
