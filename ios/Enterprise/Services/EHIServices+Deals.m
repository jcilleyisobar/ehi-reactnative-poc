//
//  EHIServices+Deals.m
//  Enterprise
//
//  Created by Rafael Ramos on 18/06/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIServices_Private.h"
#import "EHIServices+Deals.h"

@implementation EHIServices (Deals)

- (id<EHINetworkCancelable>)fetchConfiguration:(EHIDealsConfiguration *)configuration handler:(void (^)(EHIDealsConfiguration *, EHIServicesError *))handler
{
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeAEM get:@"bin/ecom/%@", configuration.countryDeals];
    
    return [self startRequest:request updateModel:configuration asynchronously:YES handler:handler];
}

- (id<EHINetworkCancelable>)fetchDeals:(EHIDealsConfiguration *)configuration handler:(void (^)(EHIDealsConfiguration *, EHIServicesError *))handler
{
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeAEM get:@"bin/ecom/%@", configuration.countryLanguageDeals];
    
    return [self startRequest:request updateModel:configuration asynchronously:YES handler:handler];
}

@end
