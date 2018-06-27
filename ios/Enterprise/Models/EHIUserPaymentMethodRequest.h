//
//  EHIUserPaymentMethodRequest.h
//  Enterprise
//
//  Created by Rafael Ramos on 10/5/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIModel.h"

@interface EHIUserPaymentMethodRequest : EHIModel <EHINetworkEncodable>
+ (instancetype)requestWithPaymentMethod:(EHIUserPaymentMethod *)paymentMethod;
@end
