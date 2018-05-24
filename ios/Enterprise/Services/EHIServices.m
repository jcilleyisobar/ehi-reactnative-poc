//
//  EHIServices.m
//  Enterprise
//
//  Created by Ty Cobb on 1/5/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIServices_Private.h"
#import "EHIUserManager.h"
#import "EHIConfiguration.h"
#import "EHIErrors.h"
#import "EHIAnalytics.h"
#import "EHICrashManager.h"
#import "EHIServices+URLMasking.h"

NSString * const kEHIServicesParameterSourceCodeKey              = @"EMOBILEAPP";
NSString * const kEHIServicesParameterEnrollSourceCodeKey        = @"EMBLAPPMEM";
NSString * const kEHIServicesBrandPathKey                        = @"ENTERPRISE";
NSString * const kEHIServicesChannelPathKey                      = @"mobile";
NSString * const kEHIServicesGBORegionCookieKey                  = @"gbo_region";

@interface EHIServices()
@property (copy, nonatomic) NSString *lastCookieValue;
@end

@implementation EHIServices

+ (instancetype)sharedInstance
{
    static EHIServices *sharedInstance;
    
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedInstance = [self new];
    });
    
    return sharedInstance;
}

- (instancetype)init
{
    if(self = [super init]) {
        _client = [EHINetworkClient new];
        _client.delegate = self;
    }
    
    return self;
}

# pragma mark - EHINetworkClientDelegate

- (BOOL)client:(EHINetworkClient *)client request:(EHINetworkRequest *)request shouldFailWithResponse:(id)response
{
    return [EHIServicesError shouldErrorForResponse:response];
}

- (id<EHINetworkError>)client:(EHINetworkClient *)client parseError:(NSError *)error response:(id)response
{
    // generate a services error from the base error
    return [EHIServicesError servicesErrorFromError:error response:response];
}

- (void)client:(EHINetworkClient *)client preemptivelyHandleError:(EHIServicesError *)error completion:(void (^)(id<EHINetworkError>))completion
{
    // we're only going to pre-empt "Call Us & Continue" errors
    if(error.displayMode != EHIServicesErrorDisplayCallUsAndContinue) {
        completion(error);
        return;
    }
   
    [self showAlertForCallUsError:error allowsContinue:YES completion:^(NSInteger index, BOOL canceled) {
        // if the user selects continue (EHIServicesErrorResultAction) then clear out the error
        EHIServicesError *resultingError = index == EHIServicesErrorResultAction ? nil : error;
        // allow services to continue
        completion(resultingError);
    }];
}

- (void)client:(EHINetworkClient *)client shouldRetryRequest:(EHINetworkRequest *)request error:(EHIServicesError *)error completion:(void (^)(BOOL retry))completion
{
    // only retry requests with expired credentials
    if(![error hasErrorCode:EHIServicesErrorCodeInvalidAuthToken]) {
        ehi_call(completion)(NO);
        return;
    }
    
    // don't surface reauth errors
    [error consume];
    
    // attempt to refresh encrypted credentials and refire request if we refresh successfully
    [[EHIUserManager sharedInstance] refreshCredentialsWithHandler:^(EHIUser *user, EHIServicesError *error) {
        // update with new auth token
        if(!error.hasFailed) {
            [request headers:^(EHINetworkRequest *request) {
                request[EHIRequestHeaderAuthTokenKey] = user.authorizationToken ?: @"";
            }];
        }
        
        completion(!error.hasFailed);
    }];
}

- (void)client:(EHINetworkClient *)client request:(EHINetworkRequest *)request failedWithError:(EHIServicesError *)error
{
    // log errors to crittercism
    [self logNetworkFailure:request error:error];
    
    // show alert when app is using an invalid API key
    if([error hasErrorCode:EHIServicesErrorCodeInvalidAPIKey]) {
        [self showAlertForInvalidAPIKeyError:error];
        return;
    }
    
    // supress automatic error display if the error was already handled, or if it wasn't a real failure
    if(error.hasBeenConsumed || error.isCancelation || !error.hasFailed) {
        return;
    }
    
    NSString *url  = [self maskURL:request.url];
    // track the error action no matter what
    [EHIAnalytics trackAction:EHIAnalyticsActionPassthrough type:EHIAnalyticsActionTypeError handler:^(EHIAnalyticsContext *context) {
        context.macroEvent = EHIAnalyticsMacroEventError;
        if(url) {
            context[EHIAnalyticsErrorEndpoint]          = url;
        }
        
        context[EHIAnalyticsActionNameKey]              = error.internalError.localizedDescription;
        context[EHIAnalyticsErrorMessageKey]            = error.internalError.localizedDescription;
        
        context[EHIAnalyticsErrorCodeKey]               = error.internalError.userInfo[EHIAnalyticsErrorCodeKey] ?: @"UNKNOWN";
        context[EHIAnalyticsErrorHTTPCodeKey]           = @(error.code);
        
        context[EHIAnalyticsServiceCorrelationIdKey]    = request.correlationId ?: @"UNKNOWN";
        
        context[EHIAnalyticsErrorDataKey]               = @[context[EHIAnalyticsServiceCorrelationIdKey],
                                                            context[EHIAnalyticsErrorCodeKey],
                                                            context.previousPath ?: context.screen ?: @"UNKNOWN"].join(@":");
    }];
    
    // otherwise, display the error based on display type
    switch(error.displayMode) {
        case EHIServicesErrorDisplayAlert:
            [self showAlertForError:error]; break;
        case EHIServicesErrorDisplayCallUs:
            [self showAlertForCallUsError:error allowsContinue:NO completion:nil];
        default: break;
    }
}

- (void)logNetworkFailure:(EHINetworkRequest *)request error:(EHIServicesError *)error
{
    if(IS_DEVICE && !error.hasBeenConsumed) {
        NSMutableString *mutableString = [[NSMutableString alloc] initWithString:@"URL = "];
        [mutableString appendString:request.url.absoluteString ?: @""];
        [mutableString appendString:@" & Message = "];
        [mutableString appendString:error.message];
        [mutableString appendFormat:@" & Code = %ld", (long) error.code];
        
        NSException *nsException = [[NSException alloc] initWithName:@"NetworkError" reason:mutableString userInfo:nil];
        [EHICrashManager logHandledException:nsException];
    }
}

//
// Helpers
//

- (void)showAlertForError:(EHIServicesError *)error
{
    EHIAlertViewBuilder *alert = [self alertForError:error]
        .button(EHILocalizedString(@"alert_service_error_okay", @"Okay", @"Title for the service error alert confirmation button"));
    alert.show(nil);
}

- (void)showAlertForCallUsError:(EHIServicesError *)error allowsContinue:(BOOL)allowsContinue completion:(void(^)(NSInteger, BOOL))completion
{
    EHIAlertViewBuilder *alert = [self alertForError:error];
   
    // add the appropriate action buttons
    if(allowsContinue) {
        alert.button(EHILocalizedString(@"alert_service_error_continue", @"Continue", @"Title for the service error alert 'Continue' button"));
    } else {
        alert.cancelButton(EHILocalizedString(@"alert_service_error_cancel", @"Cancel", @"Title for the service error alert 'Cancel' button"));
    }
   
    alert.button(EHILocalizedString(@"alert_service_error_callus", @"Call Us", @"Title for the service error alert 'Call Us' button"));
    
    // show the alert, handling the result appropriately
    alert.show(^(NSInteger index, BOOL canceled) {
        // if this is a call us action, then call the support number
        if(index == EHIServicesErrorResultCallUs) {
            [UIApplication ehi_promptPhoneCall:[EHIConfiguration configuration].primarySupportPhone.number];
        }
        
        ehi_call(completion)(index, canceled);
    });
}

- (void)showAlertForInvalidAPIKeyError:(EHIServicesError *)error
{
    EHIAlertViewBuilder.new
        .title(@"")
        .message(EHILocalizedString(@"alert_service_error_invalid_api_key_message", @"An update is required for this app. You will be redirected to the store to install it.", @"Error message when app uses an invalid api key"))
        .button(EHILocalizedString(@"alert_service_error_update", @"Update", @"Title for the service error alert update button"))
        // show the alert, take user to iTunes to update the app
        .show(^(NSInteger index, BOOL canceled) {
            [UIApplication ehi_promptUrl:[EHISettings environment].iTunesLink];
        });
}

- (EHIAlertViewBuilder *)alertForError:(EHIServicesError *)error
{
    EHIAlertViewBuilder *alert = EHIAlertViewBuilder.new
        .title(EHILocalizedString(@"alert_service_error_title", @"Error", @"Title for service error alert"))
        .message(error.message);
            
    return alert;
}

@end

@implementation EHIServices (Convenience)

+ (NSURLRequest *)URLRequestForPath:(NSString *)path
{
    return [NSMutableURLRequest requestWithURL:[NSURL URLWithString:path]];
}

@end

@implementation EHIServices (Parsing)

- (id<EHINetworkCancelable>)startRequest:(EHINetworkRequest *)request handler:(EHIServicesHandler)handler
{
    return [self startRequest:request parseAsynchronously:NO withBlock:nil handler:handler];
}

- (id<EHINetworkCancelable>)startRequest:(EHINetworkRequest *)request parseModel:(Class<EHIModel>)klass asynchronously:(BOOL)isAsynchronous handler:(EHIServicesHandler)handler
{
    return [self startRequest:request parseAsynchronously:isAsynchronous withBlock:^(NSDictionary *response) {
        return [klass modelWithDictionary:response];
    } handler:handler];
}

- (id<EHINetworkCancelable>)startRequest:(EHINetworkRequest *)request updateModel:(EHIModel *)model asynchronously:(BOOL)isAsynchronous handler:(EHIServicesHandler)handler
{
    return [self startRequest:request updateModel:model forceDeletions:NO asynchronously:isAsynchronous handler:handler];
}

- (id<EHINetworkCancelable>)startRequest:(EHINetworkRequest *)request updateModel:(EHIModel *)model forceDeletions:(BOOL)forceDeletions asynchronously:(BOOL)isAsynchronous handler:(EHIServicesHandler)handler
{
    return [self startRequest:request parseAsynchronously:isAsynchronous withBlock:^(NSDictionary *response) {
        [model updateWithDictionary:response forceDeletions:forceDeletions];
        return model;
    } handler:^(id response, EHIServicesError *error) {
        ehi_call(handler)(model, error);
    }];
}

- (id<EHINetworkCancelable>)startRequest:(EHINetworkRequest *)request parseCollection:(Class<EHIModel>)klass asynchronously:(BOOL)isAsynchronous handler:(EHIServicesHandler)handler
{
    return [self startRequest:request parseAsynchronously:isAsynchronous withBlock:^(id<EHIMappable> response) {
        return [klass modelsWithDictionaries:response];
    } handler:handler];
}

- (id<EHINetworkCancelable>)startRequest:(EHINetworkRequest *)request parseAsynchronously:(BOOL)isAsynchronous withBlock:(EHIServicesParser)parser handler:(EHIServicesHandler)handler
{
    [request cookieProperties:^(EHINetworkRequest *request) {
        request[kEHIServicesGBORegionCookieKey] = [self cookiePropertiesWithURL:request.url];
    }];

    __weak typeof(self) welf = self;
    return [self.client fetchRequest:request handler:^(NSHTTPURLResponse *urlResponse, id response, EHIServicesError *error) {
        welf.lastCookieValue = urlResponse.allHeaderFields[kEHIServicesGBORegionCookieKey];

        // only async the parsing if we didn't error
        BOOL shouldAsync = isAsynchronous && !error.hasFailed;
        
        // parsing may or may-not happen on the main thread
        optionally_dispatch_async(shouldAsync, dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
            // parse the result
            EHIDomainVerbose(EHILogDomainNetwork, @"parsing  - %@", request.relativePath);
            id result = parser && error.priority != EHIServicesErrorPriorityError ? parser(response) : response;
            
            // call back the handler with the parsed result
            dispatch_async(dispatch_get_main_queue(), ^{
                EHIDomainVerbose(EHILogDomainNetwork, @"parsed   - %@", request.relativePath);
                
                EHIServicesError *surfacedError = error.priority == EHIServicesErrorPriorityWarn ? nil : error;
                ehi_call(handler)(result, surfacedError);
            });
        });
    }];
}

- (NSDictionary *)cookiePropertiesWithURL:(NSURL *)url
{
    NSString *lastCookieValue = self.lastCookieValue;
    
    if(!(lastCookieValue && url)) {
        return nil;
    }
    
    return @{
        NSHTTPCookieName      : kEHIServicesGBORegionCookieKey,
        NSHTTPCookieValue     : lastCookieValue,
        NSHTTPCookieOriginURL : url,
        NSHTTPCookiePath      : @"\\",
    };
}

@end
