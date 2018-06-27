//
//  EHIServices+Contracts.h
//  Enterprise
//
//  Created by Rafael Ramos on 3/29/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIServices.h"
#import "EHIPromotionContract.h"
#import "EHIContractDetails.h"

@interface EHIServices (Contracts)

- (id<EHINetworkCancelable>)fetchPromotion:(EHIPromotionContract *)promotion handler:(void (^)(EHIPromotionContract *, EHIServicesError *))handler;
- (id<EHINetworkCancelable>)fetchContractNumber:(NSString *)contractNumber handler:(void (^)(EHIContractDetails *, EHIServicesError *))handler;

@end
