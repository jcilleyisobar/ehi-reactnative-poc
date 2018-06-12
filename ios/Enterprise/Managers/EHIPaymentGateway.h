//
//  EHIPaymentGateway.h
//  Enterprise
//
//  Created by Rafael Ramos on 9/28/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIPaymentGatewayProtocol.h"
#import "EHICreditCard.h"
#import "EHIPaymentGateways.h"

@interface EHIPaymentGateway : NSObject
- (id<EHINetworkCancelable>)submitCreditCard:(EHICreditCard *)card token:(NSString *)token handler:(EHIPaymentGatewayHandler)handler;
@end
