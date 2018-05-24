//
//  EHIPaymentGatewayProtocol.h
//  Enterprise
//
//  Created by Rafael Ramos on 9/28/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIServices+Payment.h"
#import "EHIServices+User.h"
#import "EHICreditCard.h"
#import "EHICreditCardSubmissionToken.h"

typedef void (^EHIPaymentGatewayHandler)(id response, EHIServicesError *);

@protocol EHIPaymentGatewayProtocol <NSObject>
- (id<EHINetworkCancelable>)submitCreditCard:(EHICreditCard *)card withToken:(EHICreditCardSubmissionToken *)token handler:(EHIPaymentGatewayHandler)handler;
@optional

@end
