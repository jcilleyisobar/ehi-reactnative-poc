//
//  EHIArrowBorderLayer.h
//  Enterprise
//
//  Created by Alex Koller on 11/19/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

typedef NS_ENUM(NSUInteger, EHIArrowBorderLayerSide) {
    EHIArrowBorderLayerSideTop,
    EHIArrowBorderLayerSideRight,
    EHIArrowBorderLayerSideBottom,
    EHIArrowBorderLayerSideLeft,
};

@interface EHIArrowBorderLayer : CAShapeLayer
/** The side on which the arrow border is drawn */
@property (assign, nonatomic) EHIArrowBorderLayerSide side;
@end
