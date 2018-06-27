//
//  EHIServices+Deals.h
//  Enterprise
//
//  Created by Rafael Ramos on 18/06/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIServices.h"
#import "EHIDealsConfiguration.h"

@interface EHIServices (Deals)
- (id<EHINetworkCancelable>)fetchConfiguration:(EHIDealsConfiguration *)configuration handler:(void (^)(EHIDealsConfiguration *, EHIServicesError *))handler;
- (id<EHINetworkCancelable>)fetchDeals:(EHIDealsConfiguration *)configuration handler:(void (^)(EHIDealsConfiguration *, EHIServicesError *))handler;
@end
