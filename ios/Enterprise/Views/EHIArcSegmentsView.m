//
//  EHIArcSegmentsView.m
//  Enterprise
//
//  Created by Rafael Ramos on 1/9/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIArcSegmentsView.h"
#import "EHIGaugeLayer.h"

@implementation EHIArcSegmentsView

# pragma mark - Accessors

- (void)setArcData:(EHIArcSegmentData)arcData
{
    self.gaugeLayer.arcData = arcData;
    
    [self setNeedsDisplay];
}

# pragma mark - Layer

- (EHIGaugeLayer *)gaugeLayer
{
    return (EHIGaugeLayer *)self.layer;
}

+ (Class)layerClass
{
    return [EHIGaugeLayer class];
}

@end
