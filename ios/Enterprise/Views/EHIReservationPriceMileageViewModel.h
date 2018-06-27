//
//  EHIReservationPriceMileageViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 7/14/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@class EHICarClassMileage;
@interface EHIReservationPriceMileageViewModel : EHIViewModel <MTRReactive>
@property (copy  , nonatomic, readonly) NSString *title;
@property (copy  , nonatomic, readonly) NSString *subtitle;
@end
