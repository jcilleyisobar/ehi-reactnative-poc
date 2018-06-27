//
//  EHINetworkConstants.h
//  Enterprise
//
//  Created by Ty Cobb on 1/14/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHINetworkError.h"

#define EHIRequestHeaderApiKeyKey         @"Ehi-API-Key"
#define EHIRequestHeaderAuthTokenKey      @"Ehi-Auth-Token"
#define EHIRequestHeaderSearchApiKey      @"SEARCH-TOKEN"
#define EHIRequestHeaderCorrelationIdKey  @"CORRELATION_ID"
#define EHIRequestHeaderAcceptLanguageKey @"Accept-Language"
#define EHIRequestHeaderContentType       @"Content-Type"
#define EHIRequestHeaderCorKey            @"Country-Of-Residence-Code"
#define EHIRequestParamJSONCharsetUTF8    @"application/json;charset=UTF-8"
#define EHIRequestParamFallbackKey        @"fallback"
#define EHIWrongApiKey                    @"FORCE_WRONG_KEY_TEST_EAPP"

typedef NS_ENUM(NSInteger, EHINetworkRequestMethod) {
    EHINetworkRequestMethodGet,
    EHINetworkRequestMethodPost,
    EHINetworkRequestMethodPut,
    EHINetworkRequestMethodDelete,
};

typedef NS_ENUM(NSInteger, EHINetworkRequestContentType) {
    EHINetworkRequestContentTypeDefault,
    EHINetworkRequestContentTypeFormData,
    EHINetworkRequestContentTypeFormEncoded,
};

typedef void(^EHINetworkResponseHandler)(NSHTTPURLResponse *urlResponse, id response, id<EHINetworkError> error);
