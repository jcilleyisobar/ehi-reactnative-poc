//
//  EHICreditCardPanguiResponse.h
//  Enterprise
//
//  Created by Rafael Ramos on 10/25/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIModel.h"

@interface EHICreditCardPanguiResponse : EHIModel
@property (copy  , nonatomic, readonly) NSString *paymentId;
@property (assign, nonatomic, readonly) BOOL isDebitCard;
@end
