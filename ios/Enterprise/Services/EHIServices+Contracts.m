//
//  EHIServices+Contracts.m
//  Enterprise
//
//  Created by Rafael Ramos on 3/29/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIServices+Contracts.h"
#import "EHIServices_Private.h"

@implementation EHIServices (Contracts)
- (id<EHINetworkCancelable>)fetchPromotion:(EHIPromotionContract *)promotion handler:(void (^)(EHIPromotionContract *, EHIServicesError *))handler
{
    return [self fetchContract:promotion.code handler:^(id response, EHIServicesError * error) {
        [promotion updateWithDictionary:response[@"contract_details"]];
        ehi_call(handler)(promotion, error);
    }];
}

- (id<EHINetworkCancelable>)fetchContractNumber:(NSString *)contractNumber handler:(void (^)(EHIContractDetails *, EHIServicesError *))handler
{
    return [self fetchContract:contractNumber handler:^(id response, EHIServicesError * error) {
        EHIContractDetails *contractDetails = [EHIContractDetails modelWithDictionary:response[@"contract_details"]];
        ehi_call(handler)(contractDetails, error);
    }];
}

//
// Helpers
//

- (id<EHINetworkCancelable>)fetchContract:(NSString *)contractNumber handler:(void (^)(id, EHIServicesError *))handler
{
#if EHIPromotionMock
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeNone post:@"mock://promotion.json"];
#else
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeGBORental get:@"/accounts/%@/%@?contract=%@", kEHIServicesBrandPathKey, kEHIServicesChannelPathKey, contractNumber];
#endif
    return [self startRequest:request handler:handler];
}

@end
