//
//  EHIServices.h
//  Enterprise
//
//  Created by Ty Cobb on 1/5/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHINetworkCancelable.h"
#import "EHIServicesError.h"

extern NSString * const kEHIServicesParameterSourceCodeKey;
extern NSString * const kEHIServicesParameterEnrollSourceCodeKey;
extern NSString * const kEHIServicesBrandPathKey;
extern NSString * const kEHIServicesChannelPathKey;

@interface EHIServices : NSObject

/** 
 @brief Singleton accessor 
 
 The services are a common entry point for accessing all of the applications network
 calls. Network traffic should not happen outside this layer.
*/

+ (instancetype)sharedInstance;

@end

@interface EHIServices (Convenience)
+ (NSURLRequest *)URLRequestForPath:(NSString *)path;
@end

/** Handler which returns generic reponse data */
typedef void(^EHIServicesHandler)(id response, EHIServicesError *error);
