//
//  EHIArrowLayer.h
//  Enterprise
//
//  Created by Ty Cobb on 3/24/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIAnimatedShapeLayer.h"

typedef NS_ENUM(NSUInteger, EHIArrowDirection) {
    EHIArrowDirectionLeft,
    EHIArrowDirectionRight,
    EHIArrowDirectionUp,
    EHIArrowDirectionDown,
};

@interface EHIArrowLayer : EHIAnimatedShapeLayer
/** The direction this arrow points */
@property (assign, nonatomic) EHIArrowDirection direction;
@end
