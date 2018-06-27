//
//  EHIServices+Payment.m
//  Enterprise
//
//  Created by Rafael Machado on 27/09/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIServices+Payment.h"
#import "EHIServices_Private.h"
#import "EHISettings.h"
#import "EHIPaymentGateway.h"

@implementation EHIServices (Payment)

- (id<EHINetworkCancelable>)fetchCardSubmissionTokenForReservationId:(NSString *)resId handler:(void (^)(EHICreditCardSubmissionToken *, EHIServicesError *))handler
{
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeGBORental get:@"reservations/%@/%@/%@/cardSubmissionKey", kEHIServicesBrandPathKey, kEHIServicesChannelPathKey, resId];
    
    return [self startRequest:request handler:^(id response, EHIServicesError *error) {
        EHICreditCardSubmissionToken *token = [EHICreditCardSubmissionToken modelWithDictionary:response];
        ehi_call(handler)(token, error);
    }];
}

- (id<EHINetworkCancelable>)check3dsDataForReservation:(EHIReservation *)reservation handler:(void (^)(EHI3DSData *, EHIServicesError *))handler
{
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeGBORental get:@"reservations/%@/%@/%@/3dsData", kEHIServicesBrandPathKey, kEHIServicesChannelPathKey, reservation.uid];
    
    return [self startRequest:request handler:^(id response, EHIServicesError *error) {
        EHI3DSData *data = [EHI3DSData modelWithDictionary:response[@"prepay3_dsdata"]];
        ehi_call(handler)(data, error);
    }];
}

- (id<EHINetworkCancelable>)submit:(EHINetworkRequest *)request handler:(void (^)(id, EHIServicesError *))handler
{
    return [self startRequest:request handler:handler];
}

@end
