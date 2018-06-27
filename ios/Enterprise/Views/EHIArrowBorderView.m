//
//  EHIArrowBorderView.m
//  Enterprise
//
//  Created by Marcelo Rodrigues on 22/06/2018.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIArrowBorderView.h"

@implementation EHIArrowBorderView

# pragma mark - Accessors

- (void)setSide:(EHIArrowBorderLayerSide)side
{
    self.borderLayer.side = side;
}

- (void)setFillColor:(UIColor *)fillColor
{
    self.borderLayer.fillColor = fillColor.CGColor;
}

- (void)setStrokeColor:(UIColor *)strokeColor
{
    self.borderLayer.strokeColor = strokeColor.CGColor;
}

- (EHIArrowBorderLayer *)borderLayer
{
    return (EHIArrowBorderLayer *)self.layer;
}

# pragma mark - Layer

+ (Class)layerClass
{
    return [EHIArrowBorderLayer class];
}

@end
