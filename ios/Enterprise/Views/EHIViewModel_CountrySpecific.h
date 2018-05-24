//
//  EHIViewModel_CountrySpecific.h
//  Enterprise
//
//  Created by Rafael Ramos on 10/13/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//
//

#import "EHIViewModel.h"

@interface EHIViewModel (CountrySpecific)
/** Returns the default payment type, based on the current COR */
- (EHICarClassChargeType)defaultPayment;
@end
