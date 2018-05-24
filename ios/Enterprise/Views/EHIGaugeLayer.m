//
//  EHIGaugeLayer.m
//  Enterprise
//
//  Created by Rafael Ramos on 1/9/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIGaugeLayer.h"
#import "EHIArchGeometry.h"

@implementation EHIGaugeLayer

# pragma mark - Accessors

- (void)setArcData:(EHIArcSegmentData)arcData
{
    _arcData = arcData;
    
    self.strokeColor = arcData.segmentColor;
    self.lineWidth   = arcData.lineWidth;
    
    self.path = self.segmentsPath;
    
    [self setNeedsDisplay];
}

- (NSString *)lineCap
{
    return kCALineCapButt;
}

# pragma mark - Segments draw

- (CGPathRef)segmentsPath
{
    CGPoint center   = self.center;
    CGFloat radius   = self.radius;
    CGFloat offset   = radius - self.arcData.offset;
    CGFloat segments = self.arcData.segments;
    
    // in this view we don't need the full segment line, so we create points with a padding as radius and use it as the center
    NSArray *offsetCenter = EHIArchGeometry.new.center(center).radius(offset).segments(segments).points;
    NSArray *segmentsPoints = EHIArchGeometry.new.center(center).radius(radius).segments(segments).points;
    
    __block UIBezierPath *bezier = [UIBezierPath new];
    segmentsPoints.each(^(NSValue *boxedPoint, int step){
        NSValue *boxedCenter = [offsetCenter ehi_safelyAccess:step];
        CGPoint segmentCenter = boxedCenter ? boxedCenter.CGPointValue : center;
        
        CGPoint linePoint = boxedPoint ? boxedPoint.CGPointValue : CGPointZero;
        
        [bezier moveToPoint:segmentCenter];
        [bezier addLineToPoint:linePoint];
    });
    
    return bezier.CGPath;
}

//
// Helpers
//

- (CGPoint)center
{
    CGFloat x = CGRectGetMidX(self.bounds);
    CGFloat y = CGRectGetMaxY(self.bounds);
    
    return CGPointMake(x, y);
}

- (CGFloat)radius
{
    return CGRectGetWidth(self.bounds) / 2;
}

@end
