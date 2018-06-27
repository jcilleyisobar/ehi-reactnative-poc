//
//  EHIProfilePaymentDeleteViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 10/4/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIInfoModalViewModel.h"
#import "EHIUserPaymentMethod.h"

@interface EHIProfilePaymentDeleteViewModel : EHIInfoModalViewModel
+ (instancetype)initWithPaymentMethod:(EHIUserPaymentMethod *)paymentMethod;
@end
