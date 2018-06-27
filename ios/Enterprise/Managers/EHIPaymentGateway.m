//
//  EHICreditCarManager.m
//  Enterprise
//
//  Created by Rafael Ramos on 9/28/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIPaymentGateway.h"
#import "EHIFareOfficeGateway.h"
#import "EHIPanguiGateway.h"

@implementation EHIPaymentGateway

- (id<EHINetworkCancelable>)submitCreditCard:(EHICreditCard *)card token:(NSString *)token handler:(EHIPaymentGatewayHandler)handler {

    void (^handlerBlock)(EHICreditCardSubmissionToken *, EHIServicesError *) = ^(EHICreditCardSubmissionToken *response, EHIServicesError *error){
        if (!error.hasFailed) {
            id<EHIPaymentGatewayProtocol> processor = [self gatewayForToken:response];
            [processor submitCreditCard:card withToken:response handler:handler];
        } else {
            ehi_call(handler)(nil, error);
        }
    };
    
    EHIServices *instance = [EHIServices sharedInstance];
    if(token) {
        return [instance fetchCardSubmissionTokenForReservationId:token handler:handlerBlock];
    } else {
        return [instance fetchCardSubmissionTokenWithHandler:handlerBlock];
    }
}

//
// Helpers
//

- (id<EHIPaymentGatewayProtocol>)gatewayForToken:(EHICreditCardSubmissionToken *)token
{
    switch (token.context.gateway) {
        case EHIPaymentGatewayProcessorPangui:
            return [EHIPanguiGateway new];
        case EHIPaymentGatewayProcessorFareOffice:
            return [EHIFareOfficeGateway new];
        default:
            return nil;
    }
}

@end
