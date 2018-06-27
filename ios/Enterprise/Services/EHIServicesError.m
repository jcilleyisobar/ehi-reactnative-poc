//
//  EHIServicesError.m
//  Enterprise
//
//  Created by Ty Cobb on 6/2/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIServicesError.h"
#import "EHIMapTransformer.h"
#import "EHIErrors.h"
#import "EHISettings.h"

#define EHIServicesErrorDisplayTransformerName  @"EHIServicesErrorDisplayTransformer"
#define EHIServicesErrorCodeTransformerName     @"EHIServicesErrorCodeTransformer"
#define EHIServicesErrorPriorityTransformerName @"EHIServicesErrorPriorityTransformer"

#define EHIServicesResponseErrorsKey    @"errors"
#define EHIServicesResponseMessagesKey  @"messages"
#define EHIServicesResponseMessageKey   @"message"
#define EHIServicesResponseCodeKey      @"code"
#define EHIServicesResponsePriorityKey  @"priority"
#define EHIServicesResponseDisplayAsKey @"display_as"

NS_ASSUME_NONNULL_BEGIN

@interface EHIServicesError ()
@property (strong, nonatomic, nullable) NSError *internalError;
@property (strong, nonatomic) NSArray *errorCodes;
@property (assign, nonatomic) EHIServicesErrorDisplay displayMode;
@property (assign, nonatomic) EHIServicesErrorPriority priority;
@property (assign, nonatomic) BOOL hasFailed;
@end

@implementation EHIServicesError

+ (BOOL)shouldErrorForResponse:(NSDictionary *)response
{
    if([response isKindOfClass:[NSDictionary class]]) {
        return [self errorsFromResponse:response].count != 0;
    }
    
    return NO;
}

# pragma mark - Generation

- (instancetype)init
{
    if(self = [super init]) {
        // default to alert display type
        _displayMode = EHIServicesErrorDisplayAlert;
        // default to error priority
        _priority    = EHIServicesErrorPriorityError;
    }
    
    return self;
}

+ (instancetype)servicesErrorFailure
{
    EHIServicesError *error = [EHIServicesError new];
    error.hasFailed = YES;
    
    return error;
}

+ (nullable instancetype)servicesErrorFromError:(NSError *)error response:(id)response
{
    EHIServicesError *result = nil;
    NSArray *errors          = nil;
    NSString *message        = nil;

    if([response isKindOfClass:[NSDictionary class]]) {
        // extract the errors from the response
        errors  = [self errorsFromResponse:response];
        // parse out message from the services structure if possible
        message = [self messageFromErrors:errors];
    }

    // generate an error from the services structure if we were able to generate a message
    if(message) {
        result = [self errorFromSystemError:error serviceErrors:errors message:message];
    }
    // otherwise, if we couldn't parse a message but have a non-cancelation error attempt to generate
    // a fallback
    else if(error && error.code != NSURLErrorCancelled) {
        result = [self errorFromSystemError:error response:response];
    }
    // fareoffice call might return NSURLErrorCancelled and we have to handle it
    else if (error && [error.userInfo[NSURLErrorFailingURLStringErrorKey] containsString:[EHISettings environment].farepaymentUrl]) {
        result = [self errorFromSystemError:error response:response];
    }
    
    BOOL panguiError = [result hasErrorCode:EHIServicesErrorCodePanguiError] || [result hasErrorCode:EHIServicesErrorCodeDebitCardError];
    
    // an error is only a failure if there was a network error and if this is a service
    result.hasFailed = (error != nil && result.priority >= EHIServicesErrorPriorityError) || panguiError;
  
    return result;
}

//
// Helpers
//

+ (EHIServicesError *)errorFromSystemError:(NSError *)error serviceErrors:(NSArray *)errors message:(NSString *)message
{
    EHIServicesError *result = [EHIServicesError new];
    
    // apply display type if necessary
    NSNumber *displayType = [self displayTypeFromErrors:errors];
    if(displayType) {
        result.displayMode = displayType.integerValue;
    }
    
    // apply priority if necessary
    NSNumber *priority = [self priorityFromErrors:errors];
    if(priority) {
        result.priority = priority.integerValue;
    }
    
    // store the error codes / base error
    result.errorCodes  = [self errorCodesFromErrors:errors] ?: @[];
    
    NSMutableDictionary *userInfo = [NSMutableDictionary dictionaryWithDictionary:error.userInfo];
    userInfo[NSLocalizedDescriptionKey]   = message;
    userInfo[EHIAnalyticsErrorCodeKey]    = errors.firstObject[EHIServicesResponseCodeKey];
    
    result.internalError = [NSError errorWithDomain:EHIErrorDomainServices code:error.code userInfo:userInfo];
    
    return result;
}

+ (EHIServicesError *)errorFromSystemError:(NSError *)error response:(NSDictionary *)response
{
    EHIServicesError *result = nil;
    
    // it's possible services are completely down and not returning any response, so
    // generate a "unavailable" error in this case
    if(!response) {
        result = [EHIServicesError new];

        NSMutableDictionary *userInfo = [NSMutableDictionary dictionaryWithDictionary:error.userInfo];
        userInfo[EHIAnalyticsErrorCodeKey]    = @"UNAVAILABLE";
        userInfo[NSLocalizedDescriptionKey]   = EHILocalizedString(@"error_service_unavailable", @"We are unable to complete your request.  Please call us.", @"error message when server is unavailable and returns 503 code");
        
        result.internalError = [NSError errorWithDomain:error.domain code:error.code userInfo:userInfo];
        
        EHIDomainError(EHILogDomainNetwork, @"request  - wrapping error: %@", error.localizedDescription);
    }
    // if we failed to parse the error for some reason, but have a services error then
    // wrap it blindly
    else {
        result = [EHIServicesError new];
        // just embed the error
        result.internalError = error;
    }
    
    return result;
}

+ (NSArray *)errorsFromResponse:(NSDictionary *)response
{
    // TODO: clean up this mess :)
    NSDictionary *panguiErrorResponse = [self parsePanguiError:response];
    NSArray *panguiError = panguiErrorResponse ? @[panguiErrorResponse] : nil;
    return response[EHIServicesResponseMessagesKey]
        ?: response[EHIServicesResponseErrorsKey]
        ?: panguiError
        ?: @[];
}

+ (NSString *)messageFromErrors:(NSArray *)errors
{
#if defined(DEBUG) || defined(UAT)
    NSArray *messages = errors.map(^(NSDictionary *error) {
        NSString *message = error[EHIServicesResponseMessageKey];
        return message.length == 0 ? nil : [NSString stringWithFormat:@"%@: %@", error[EHIServicesResponseCodeKey], message];
    });
#else
    // get the list of messages from the errors
    NSArray *messages = errors.pluck(EHIServicesResponseMessageKey).select(^(id message) {
        return message != [NSNull null];
    });
#endif
    
    // only generate an error message if we have any error strings
    return messages.count ? messages.join(@"\n\n") : nil;
}

+ (NSArray *)errorCodesFromErrors:(NSArray *)errors
{
    return errors.pluck(EHIServicesResponseCodeKey).map(^(NSString *key) {
        return [self.errorCodeTransformer transformedValue:key];
    });
}

+ (NSNumber *)displayTypeFromErrors:(NSArray *)errors
{
    // parse the display types into our enumeration
    return errors.pluck(EHIServicesResponseDisplayAsKey).map(^(NSString *key) {
        return [self.errorDisplayTransformer transformedValue:key];
    })
    // and get the highest order display type (or nil, if none exist)
    .inject(nil, ^(NSNumber *memo, NSNumber *display) {
        return @(MAX(memo.integerValue, display.integerValue));
    });
}

+ (NSNumber *)priorityFromErrors:(NSArray *)errors
{
    // parse the display types into our enumeration
    return errors.pluck(EHIServicesResponsePriorityKey).map(^(NSString *key) {
        return [self.errorPriorityTransformer transformedValue:key];
    })
    // and get the highest order display type (or nil, if none exist)
    .inject(nil, ^(NSNumber *memo, NSNumber *display) {
        return @(MAX(memo.integerValue, display.integerValue));
    });
}

# pragma mark - Pangui error mapping

+ (nullable NSDictionary *)parsePanguiError:(NSDictionary *)response
{
    BOOL hasError = [self hasPanguiError:response];
    if(hasError) {
        NSString *panguiMessage   = [self panguiLocalizedMessage:response];
        NSString *panguiErrorCode = [self panguiErrorCode:EHIServicesErrorCodePanguiError];
        return @{
            EHIServicesResponseMessageKey : panguiMessage,
            EHIServicesResponseCodeKey    : panguiErrorCode
        };
    }
    
    BOOL hasDebitCardError = [self hasDebitCardError:response];
    if(hasDebitCardError) {
        NSString *panguiMessage   = [self panguiLocalizedMessage:response];
        NSString *panguiErrorCode = [self panguiErrorCode:EHIServicesErrorCodeDebitCardError];
        return @{
            EHIServicesResponseMessageKey : panguiMessage,
            EHIServicesResponseCodeKey    : panguiErrorCode
        };
    }
    
    return nil;
}

+ (BOOL)hasPanguiError:(NSDictionary *)response
{
    NSInteger statusCode = [response[@"ProcessPaymentMediaIdentificationRS"][@"CommonResponse"][@"StatusCode"] integerValue];
    return statusCode == EHIServicesErrorPanguiStatusBusinessError ||
           statusCode == EHIServicesErrorPanguiStatusTechicalError;
}

+ (BOOL)hasDebitCardError:(NSDictionary *)response
{
    return (BOOL)[response[@"ProcessPaymentMediaIdentificationRS"][@"PartialPrimaryAccount"][@"DebitCardIndicator"] boolValue];
}

+ (NSString *)panguiLocalizedMessage:(NSDictionary *)response
{
    return response[@"ProcessPaymentMediaIdentificationRS"][@"CommonResponse"][@"BusinessMessage"][@"LocalizedMessage"] ?: @"PANGUI_ERROR";
}

+ (NSString *)panguiErrorCode:(EHIServicesErrorCode)errorCode
{
    return [self.errorCodeTransformer reverseTransformedValue:@(errorCode)];
}

# pragma mark - Consumption

- (void)consume
{
    self.internalError = nil;
}

- (BOOL)hasBeenConsumed
{
    return !self.internalError;
}

- (BOOL)hasErrorCode:(EHIServicesErrorCode)code
{
    return self.errorCodes ? self.errorCodes.has(@(code)) : NO;
}

# pragma mark - Accessors

- (NSInteger)code
{
    return self.internalError.code;
}

- (nullable NSString *)domain
{
    return self.internalError.domain;
}

- (nullable NSString *)message
{
    return self.internalError.localizedDescription;
}

- (BOOL)isCancelation
{
    return [self.domain isEqualToString:NSURLErrorDomain] && self.code == NSURLErrorCancelled;
}

# pragma mark - Transformers

+ (NSValueTransformer *)errorDisplayTransformer
{
    NSValueTransformer *transformer = [NSValueTransformer valueTransformerForName:EHIServicesErrorDisplayTransformerName];
    if(transformer) {
        return transformer;
    }
    
    transformer = [[EHIMapTransformer alloc] initWithMap:@{
        @"silent"      : @(EHIServicesErrorDisplaySilent),
        @"alert"       : @(EHIServicesErrorDisplayAlert),
        @"callus"      : @(EHIServicesErrorDisplayCallUs),
        @"callus_cont" : @(EHIServicesErrorDisplayCallUsAndContinue),
    }];
    
    [NSValueTransformer setValueTransformer:transformer forName:EHIServicesErrorDisplayTransformerName];
    
    return transformer;
}

+ (NSValueTransformer *)errorPriorityTransformer
{
    EHIMapTransformer *transformer = (EHIMapTransformer *)[NSValueTransformer valueTransformerForName:EHIServicesErrorPriorityTransformerName];
    if(transformer) {
        return transformer;
    }
    
    transformer = [[EHIMapTransformer alloc] initWithMap:@{
        @"INFO"  : @(EHIServicesErrorPriorityInfo),
        @"WARN"  : @(EHIServicesErrorPriorityWarn),
        @"ERROR" : @(EHIServicesErrorPriorityError),
    }];
   
    transformer.defaultValue = @(EHIServicesErrorPriorityError);
    
    [NSValueTransformer setValueTransformer:transformer forName:EHIServicesErrorPriorityTransformerName];
    
    return transformer;
}

+ (NSValueTransformer *)errorCodeTransformer
{
    EHIMapTransformer *transformer = (EHIMapTransformer *)[NSValueTransformer valueTransformerForName:EHIServicesErrorCodeTransformerName];
    if(transformer) {
        return transformer;
    }
    
    transformer = [[EHIMapTransformer alloc] initWithMap:@{
        @"PRICING_16401"                     : @(EHIServicesErrorCodeNoValidProducts),
        @"CROS_TRAVEL_PURPOSE_NOT_SPECIFIED" : @(EHIServicesErrorCodeTravelPurposeNotSpecified),
        @"CROS_LOGIN_SYSTEM_ERROR"           : @(EHIServicesErrorCodeLoginSystemError),
        @"GBO_PROFILE_PASSWORD_REQUIRED"     : @(EHIServicesErrorCodeLoginSystemError),
        @"CROS_CONTRACT_PIN_REQUIRED"        : @(EHIServicesErrorCodePinRequired),
        @"CROS_CONTRACT_PIN_INVALID"         : @(EHIServicesErrorCodePinInvalid),
        @"INVALID_API_KEY"                   : @(EHIServicesErrorCodeInvalidAPIKey),
        @"CROS_INVALID_AUTH_TOKEN"           : @(EHIServicesErrorCodeInvalidAuthToken),
        @"CROS_CONTRACT_NOT_ON_PROFILE"      : @(EHIServicesErrorCodeContractNotOnProfile),
        @"CROS_BUSINESS_LEISURE_CONTRACT_NOT_ON_PROFILE"           : @(EHIServicesErrorCodeBusinessLeisureNotOnProfileError),
        @"CROS_LOGIN_TERMS_AND_CONDITIONS_ACCEPT_VERSION_MISMATCH" : @(EHIServicesErrorCodeTermsAndConditions),
        @"CROS_REDEMPTION_RES_LOOKUP_LOGIN_REQUIRED"               : @(EHIServicesErrorCodeReservationLookupLoginRequired),
        @"CROS_LOGIN_WEAK_PASSWORD_ERROR"                          : @(EHIServicesErrorCodePasswordIsOutdated),
        @"CROS_RES_PRE_RATE_ADDITIONAL_FIELD_REQUIRED"             : @(EHIServicesErrorCodeAdditionalInfoRequired),
        @"CROS_RES_INVALID_ADDITIONAL_FIELD"                       : @(EHIServicesErrorCodeAdditionalInfoInvalid),
        @"PANGUI_ERROR"                                            : @(EHIServicesErrorCodePanguiError),
        @"PANGUI_DEBIT_CARD_ERROR"                                 : @(EHIServicesErrorCodeDebitCardError)
    }];
   
    // default to the "none" error code
    transformer.defaultValue = @(EHIServicesErrorCodeNone);
    
    [NSValueTransformer setValueTransformer:transformer forName:EHIServicesErrorCodeTransformerName];
    
    return transformer;
}

@end

NS_ASSUME_NONNULL_END
