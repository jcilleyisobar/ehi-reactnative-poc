//
//  EHIMeterLayer.h
//  Enterprise
//
//  Created by Alex Koller on 5/29/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIGaugeStructure.h"

NS_ASSUME_NONNULL_BEGIN

@interface EHIMeterLayer : CALayer
@property (assign, nonatomic) EHIMeterData meterData;
- (void)setFillPercent:(CGFloat)fillPercent animated:(BOOL)animated;
@end

NS_ASSUME_NONNULL_END
