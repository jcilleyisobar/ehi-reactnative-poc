//
//  EHIPanguiGateway.m
//  Enterprise
//
//  Created by Rafael Ramos on 9/28/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIPanguiGateway.h"
#import "EHICreditCardPanguiRequest.h"
#import "EHICreditCardPanguiResponse.h"

@implementation EHIPanguiGateway

- (id<EHINetworkCancelable>)submitCreditCard:(EHICreditCard *)card withToken:(EHICreditCardSubmissionToken *)token handler:(EHIPaymentGatewayHandler)handler
{
    BOOL isSaving = card.save;
    if(isSaving) {
        return [self save:card withToken:token handler:handler];
    } else {
        return [self submit:card withToken:token handler:^(EHICreditCardPanguiResponse *response, EHIServicesError *error) {
            BOOL shouldSave = !error.hasFailed && isSaving;
            if(shouldSave) {
                [self addCreditCard:card paymentId:response.paymentId handler:handler];
            } else {
                ehi_call(handler)(response.paymentId, error);
            }
        }];
    }
}

- (id<EHINetworkCancelable>)save:(EHICreditCard *)card withToken:(EHICreditCardSubmissionToken *)token handler:(EHIPaymentGatewayHandler)handler
{
    return [self submit:card withToken:token handler:^(EHICreditCardPanguiResponse *response, EHIServicesError *error) {
        if(!error.hasFailed) {
            [self addCreditCard:card paymentId:response.paymentId handler:handler];
        } else {
            ehi_call(handler)(nil, error);
        }
    }];
}

- (void)save:(EHICreditCard *)creditCard withResponse:(EHICreditCardPanguiResponse *)response handler:(void (^)(EHIUserPaymentMethod *, EHIServicesError *))handler
{
    [[EHIServices sharedInstance] addCreditCard:creditCard withToken:response.paymentId handler:handler];
}

- (id<EHINetworkCancelable>)submit:(EHICreditCard *)card withToken:(EHICreditCardSubmissionToken *)token handler:(EHIPaymentGatewayHandler)handler
{
    NSString *url = token.context.url;
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeNone post:url];

    [request body:^(EHINetworkRequest *request){
        request[@"ProcessPaymentMediaIdentificationRQ"] = [EHICreditCardPanguiRequest requestForCreditCard:card token:token];
    }];

    return [[EHIServices sharedInstance] submit:request handler:^(id response, EHIServicesError *error) {
        if(!error.hasFailed) {
            EHICreditCardPanguiResponse *panguiResponse = [EHICreditCardPanguiResponse modelWithDictionary:response[@"ProcessPaymentMediaIdentificationRS"]];
            ehi_call(handler)(panguiResponse, error);
        } else {
            ehi_call(handler)(nil, error);
        }
    }];
}

//
// Helpers
//

- (void)addCreditCard:(EHICreditCard *)creditCard paymentId:(NSString *)paymentId handler:(void (^)(id response, EHIServicesError *))handler
{
    [[EHIServices sharedInstance] addCreditCard:creditCard withToken:paymentId handler:^(id response, EHIServicesError *error) {
        if(!error.hasFailed) {
            ehi_call(handler)(paymentId, error);
        } else {
            ehi_call(handler)(nil, error);
        }
    }];
}

@end
