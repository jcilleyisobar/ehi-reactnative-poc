//
//  EHICreditCardPaymentContext.h
//  Enterprise
//
//  Created by Rafael Ramos on 9/28/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIModel.h"
#import "EHIPaymentGateways.h"

@interface EHICreditCardPaymentContext : EHIModel
@property (assign, nonatomic, readonly) NSInteger sourceSystemId;
@property (assign, nonatomic, readonly) EHIPaymentGatewayProcessor gateway;
@property (assign, nonatomic, readonly) NSString *callingApplicationName;
@property (copy  , nonatomic, readonly) NSString *url;
@end
