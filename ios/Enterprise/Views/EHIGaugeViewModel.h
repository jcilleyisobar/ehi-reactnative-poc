//
//  EHIGaugeViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 1/6/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIGaugeStructure.h"
#import "EHILoyaltyTierDataProvider.h"

typedef NS_ENUM(NSInteger, EHIGaugeType) {
    EHIGaugeTypePercent,
    EHIGaugeTypeSegmented
};

@class EHIUserLoyalty;
@interface EHIGaugeViewModel : EHIViewModel <MTRReactive>
/**
 @param loyalty that should be rendered (will use it's color as the fillColor)
 @param fill value (in %)
 @return Allocated object
*/
- (instancetype)initWithLoyalty:(EHIUserLoyalty *)loyalty fill:(CGFloat)fill;

@property (assign, nonatomic, readonly) CGFloat fill;
@property (assign, nonatomic, readonly) EHIArcSegmentData arcData;
@property (assign, nonatomic, readonly) EHIMeterData meterData;
@property (assign, nonatomic, readonly) EHIMeterData innerMeterData;

@end
