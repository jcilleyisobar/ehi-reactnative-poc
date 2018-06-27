//
//  EHIServices+Payment.h
//  Enterprise
//
//  Created by Rafael Machado on 27/09/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIServices.h"
#import "EHIReservation.h"
#import "EHICreditCard.h"
#import "EHI3DSData.h"
#import "EHICreditCardSubmissionToken.h"

@interface EHIServices (Payment)

- (id<EHINetworkCancelable>)fetchCardSubmissionTokenForReservationId:(NSString *)resId handler:(void (^)(EHICreditCardSubmissionToken *, EHIServicesError *))handler;
- (id<EHINetworkCancelable>)check3dsDataForReservation:(EHIReservation *)reservation handler:(void (^)(EHI3DSData *, EHIServicesError *))handler;

/** Proxy to payment gateways */
- (id<EHINetworkCancelable>)submit:(EHINetworkRequest *)request handler:(void (^)(id, EHIServicesError *))handler;
@end
