//
//  EHICarClassVehicleRate.h
//  Enterprise
//
//  Created by Michael Place on 5/29/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"
#import "EHICarClassChargeType.h"
#import "EHICarClassExtras.h"
#import "EHICarClassPriceSummary.h"

@interface EHICarClassVehicleRate : EHIModel
@property (assign, nonatomic, readonly) EHICarClassChargeType type;
@property (strong, nonatomic, readonly) EHICarClassExtras *extras;
@property (strong, nonatomic, readonly) EHICarClassPriceSummary *priceSummary;
@end

EHIAnnotatable(EHICarClassVehicleRate)