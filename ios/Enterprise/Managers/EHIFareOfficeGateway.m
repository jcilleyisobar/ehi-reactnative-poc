//
//  EHIFareOfficeGateway.m
//  Enterprise
//
//  Created by Rafael Ramos on 9/28/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIFareOfficeGateway.h"
#import "EHISettings.h"

@implementation EHIFareOfficeGateway

- (id<EHINetworkCancelable>)submitCreditCard:(EHICreditCard *)card withToken:(EHICreditCardSubmissionToken *)token handler:(EHIPaymentGatewayHandler)handler
{
    NSString *url = [token.context.url stringByReplacingOccurrencesOfString:@"{cardSubmissionKey}"
                                                                 withString:token.cardSubmissionKey];
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeNone post:url];
    [request body:^(EHINetworkRequest *request) {
        request[@"card"] = card;
    }];

    return [[EHIServices sharedInstance] submit:request handler:^(id response, EHIServicesError *error) {
        ehi_call(handler)(response, error);
    }];
}

@end
