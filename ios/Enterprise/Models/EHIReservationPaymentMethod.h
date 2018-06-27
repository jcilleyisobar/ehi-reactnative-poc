//
//  EHIReservationPaymentMethod.h
//  Enterprise
//
//  Created by Rafael Ramos on 2/25/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIModel.h"
#import "EHIPrice.h"
#import "EHICreditCard.h"

@interface EHIReservationPaymentMethod : EHIModel

@property (strong, nonatomic, readonly) EHIPrice *amount;
@property (copy  , nonatomic, readonly) NSString *transactionType;
@property (strong, nonatomic, readonly) EHICreditCard *creditCard;

@end

EHIAnnotatable(EHIReservationPaymentMethod)