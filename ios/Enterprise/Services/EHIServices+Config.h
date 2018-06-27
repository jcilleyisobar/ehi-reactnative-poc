//
//  EHIServices+Config.h
//  Enterprise
//
//  Created by Ty Cobb on 6/15/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIServices.h"
#import "EHIConfiguration.h"
#import "EHIWebContent.h"

@interface EHIServices (Config)

- (id<EHINetworkCancelable>)fetchConfigurationSupport:(EHIConfiguration *)configuration handler:(void(^)(id result, EHIServicesError *error))handler;
- (id<EHINetworkCancelable>)fetchContentForType:(EHIWebContentType)type handler:(void(^)(EHIWebContent *, EHIServicesError *))handler;

@end
