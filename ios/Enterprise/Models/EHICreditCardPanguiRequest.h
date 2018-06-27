//
//  EHICreditCardPanguiRequest.h
//  Enterprise
//
//  Created by Rafael Ramos on 9/28/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIModel.h"
#import "EHICreditCard.h"
#import "EHICreditCardSubmissionToken.h"

@interface EHICreditCardPanguiRequest : EHIModel <EHINetworkEncodable>
+ (instancetype)requestForCreditCard:(EHICreditCard *)creditCard token:(EHICreditCardSubmissionToken *)token;
@end
