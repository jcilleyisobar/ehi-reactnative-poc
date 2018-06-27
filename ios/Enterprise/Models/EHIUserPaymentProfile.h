//
//  EHIUserPaymentProfile.h
//  Enterprise
//
//  Created by mplace on 2/24/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"
#import "EHIUserPaymentMethod.h"

@interface EHIUserPaymentProfile : EHIModel
@property (copy, nonatomic) NSArray<EHIUserPaymentMethod> *paymentMethods;
@end
