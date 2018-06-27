//
//  EHINetworkError.h
//  Enterprise
//
//  Created by Ty Cobb on 6/2/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

typedef NS_ENUM(NSInteger, EHINetworkStatusCode) {
    EHINetworkStatusCodeOk                  = 200,
    EHINetworkStatusCodeCreated             = 201,
    EHINetworkStatusCodeBadRequest          = 400,
    EHINetworkStatusCodeNotFound            = 404,
    EHINetworkStatusCodeConflict            = 409,
    EHINetworkStatusCodeUnprocessableEntity = 422,
    EHINetworkStatusCodeInternalServerError = 500,
    EHINetworkStatusCodeServicesUnavailable = 503,
};

@protocol EHINetworkError <NSObject>
/** The messsage for this error */
@property (copy, nonatomic, readonly) NSString *message;
@end

@interface NSError (Network) <EHINetworkError>

@end
