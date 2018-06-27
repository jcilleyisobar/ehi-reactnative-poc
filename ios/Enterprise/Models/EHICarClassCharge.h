//
//  EHICarClassCharge.h
//  Enterprise
//
//  Created by mplace on 2/19/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIPrice.h"
#import "EHIPriceContext.h"
#import "EHICarClassChargeRate.h"
#import "EHICarClassChargeType.h"

@interface EHICarClassCharge : EHIModel <EHIPriceContext>
@property (assign, nonatomic, readonly) EHICarClassChargeType type;
@property (strong, nonatomic, readonly) EHIPrice *viewTotal;
@property (strong, nonatomic, readonly) EHIPrice *paymentTotal;
@property (copy  , nonatomic, readonly) NSArray<EHICarClassChargeRate> *rates;
@end

EHIAnnotatable(EHICarClassCharge)