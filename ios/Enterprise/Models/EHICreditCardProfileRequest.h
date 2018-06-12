//
//  EHICreditCardProfileRequest.h
//  Enterprise
//
//  Created by Rafael Ramos on 9/28/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIModel.h"
#import "EHICreditCard.h"

@interface EHICreditCardProfileRequest : EHIModel <EHINetworkEncodable>
+ (instancetype)requestForCreditCard:(EHICreditCard *)card token:(NSString *)token;
@end
