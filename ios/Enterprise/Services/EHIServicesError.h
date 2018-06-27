//
//  EHIServicesError.h
//  Enterprise
//
//  Created by Ty Cobb on 6/2/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHINetworkError.h"

typedef NS_ENUM(NSInteger, EHIServicesErrorDisplay) {
    EHIServicesErrorDisplaySilent,
    EHIServicesErrorDisplayAlert,
    EHIServicesErrorDisplayCallUs,
    EHIServicesErrorDisplayCallUsAndContinue
};

typedef NS_ENUM(NSInteger, EHIServicesErrorResult) {
    EHIServicesErrorResultAction,
    EHIServicesErrorResultCallUs,
};

typedef NS_ENUM(NSUInteger, EHIServicesErrorCode) {
    EHIServicesErrorCodeNone,
    EHIServicesErrorCodeNoValidProducts,
    EHIServicesErrorCodeLoginSystemError,
    EHIServicesErrorCodePinRequired,
    EHIServicesErrorCodePinInvalid,
    EHIServicesErrorCodeContractNotOnProfile,
    EHIServicesErrorCodeBusinessLeisureNotOnProfileError,
    EHIServicesErrorCodeTravelPurposeNotSpecified,
    EHIServicesErrorCodeTermsAndConditions,
    EHIServicesErrorCodeInvalidAPIKey,
    EHIServicesErrorCodeInvalidAuthToken,
    EHIServicesErrorCodeReservationLookupLoginRequired,
    EHIServicesErrorCodePasswordIsOutdated,
    EHIServicesErrorCodeAdditionalInfoRequired,
    EHIServicesErrorCodeAdditionalInfoInvalid,
    EHIServicesErrorCodePanguiError,
    EHIServicesErrorCodeDebitCardError
};

typedef NS_ENUM(NSUInteger, EHIServicesErrorPriority) {
    EHIServicesErrorPriorityNone,
    EHIServicesErrorPriorityInfo,
    EHIServicesErrorPriorityWarn,
    EHIServicesErrorPriorityError
};

typedef NS_ENUM(NSUInteger, EHIServicesErrorPanguiStatus) {
    EHIServicesErrorPanguiStatusSuccess,
    EHIServicesErrorPanguiStatusBusinessError,
    EHIServicesErrorPanguiStatusTechicalError
};

NS_ASSUME_NONNULL_BEGIN

@interface EHIServicesError : NSObject <EHINetworkError>

/** The HTTP status code for the error */
@property (assign, nonatomic, readonly) NSInteger code;
/** The domain for this error */
@property (copy  , nonatomic, nullable, readonly) NSString *domain;
/** The localized description for this error */
@property (copy  , nonatomic, nullable, readonly) NSString *message;

/** The mechanism for displaying this error */
@property (assign, nonatomic, readonly) EHIServicesErrorDisplay displayMode;
/** The priority for this error */
@property (assign, nonatomic, readonly) EHIServicesErrorPriority priority;
/** @c YES if this error has already been consumed */
@property (assign, nonatomic, readonly) BOOL hasBeenConsumed;
/** @c YES if this error indicates a server failure */
@property (assign, nonatomic, readonly) BOOL hasFailed;
/** @c YES if this error was from a request cancelation */
@property (assign, nonatomic, readonly) BOOL isCancelation;

/** The foundation error backing this service error, or @c nil */
@property (strong, nonatomic, nullable, readonly) NSError *internalError;

/** @c YES if an error should be generated from the response */
+ (BOOL)shouldErrorForResponse:(NSDictionary *)response;
/** Constructs a a service error from the given error and response dictionary */
+ (nullable instancetype)servicesErrorFromError:(NSError *)error response:(id)response;
/** Creates an empty error with hasFailed set to @YES */
+ (instancetype)servicesErrorFailure;

/** Consumes this error, so that it won't be automatically handled elsewhere */
- (void)consume;
/** Check if this error has the specified error code **/
- (BOOL)hasErrorCode:(EHIServicesErrorCode)code;

@end

NS_ASSUME_NONNULL_END
