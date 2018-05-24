//
//  EHICarClassChargeRate.h
//  Enterprise
//
//  Created by mplace on 2/19/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIPrice.h"
#import "EHIPriceRateType.h"

@interface EHICarClassChargeRate : EHIModel
@property (assign, nonatomic, readonly) EHIPriceRateType type;
@property (strong, nonatomic, readonly) EHIPrice *price;
@property (assign, nonatomic, readonly) NSInteger quantity;
@end

EHIAnnotatable(EHICarClassChargeRate);