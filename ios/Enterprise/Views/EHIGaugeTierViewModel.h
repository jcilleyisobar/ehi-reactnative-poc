//
//  EHIGaugeTierViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 1/11/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIGaugeViewModel.h"

typedef NS_ENUM(NSInteger, EHIGaugeTierTotalType) {
    EHIGaugeTierTotalTypeDays,
    EHIGaugeTierTotalTypeRentals,
};

@class EHIUserLoyalty;
@interface EHIGaugeTierViewModel : EHIViewModel <MTRReactive>

- (instancetype)initWithUserLoyalty:(EHIUserLoyalty *)loyalty total:(EHIGaugeTierTotalType)totalType;

@property (strong, nonatomic, readonly) EHIGaugeViewModel *gaugeModel;
@property (copy  , nonatomic, readonly) NSAttributedString *currentAmount;
@property (copy  , nonatomic, readonly) NSString *unitTitle;
@property (copy  , nonatomic, readonly) NSString *total;
@end
