//
//  EHIRewardsBenefitsTierGaugeViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 1/11/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIGaugeTierViewModel.h"

@interface EHIRewardsBenefitsTierGaugeViewModel : EHIViewModel <MTRReactive>
@property (copy  , nonatomic, readonly) NSAttributedString *progressTitle;
@property (copy  , nonatomic, readonly) NSString *orTitle;
@property (strong, nonatomic, readonly) EHIGaugeTierViewModel *daysGaugeModel;
@property (strong, nonatomic, readonly) EHIGaugeTierViewModel *rentalsGaugeModel;
@property (assign, nonatomic, readonly) BOOL useDoubleGauge;
@end
