//
//  EHICarClassMileage.h
//  Enterprise
//
//  Created by fhu on 7/14/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"
#import "EHIPrice.h"
#import "EHIPriceRateType.h"

@interface EHICarClassMileage : EHIModel

@property (assign, nonatomic, readonly) int totalFreeMiles;
@property (strong, nonatomic, readonly) EHIPrice *excessMileageRate;
@property (assign, nonatomic, readonly) BOOL unlimitedMileage;
@property (copy, nonatomic, readonly) NSString *distanceUnit;

//Computed properties
@property (copy, nonatomic, readonly) NSString *title;
@property (copy, nonatomic, readonly) NSString *subtitle;

@end
