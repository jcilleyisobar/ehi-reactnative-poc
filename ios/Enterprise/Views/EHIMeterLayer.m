//
//  EHIMeterLayer.m
//  Enterprise
//
//  Created by Alex Koller on 5/29/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIMeterLayer.h"

#define EHIMeterLayerStrokeWidth (11.0)
#define EHIMeterLayerOutlineStrokeWidth (1.0)

NS_ASSUME_NONNULL_BEGIN

@interface EHIMeterLayer ()
@property (strong, nonatomic) CAShapeLayer *backgroundLayer;
@property (strong, nonatomic) CAShapeLayer *fillLayer;
@property (strong, nonatomic) CAShapeLayer *lineLayer;
@property (assign, nonatomic) CGFloat fillPercent;
@end

@implementation EHIMeterLayer

- (instancetype)init
{
    if(self = [super init]) {
        [self configure];
    }
    
    return self;
}

- (nullable instancetype)initWithCoder:(NSCoder *)decoder
{
    if(self = [super initWithCoder:decoder]) {
        [self configure];
    }
    
    return self;
}

- (void)setMeterData:(EHIMeterData)meterData
{
    _meterData = meterData;
    
    [self configure];
    
    [self setNeedsDisplay];
}

//
// Helpers
//

- (void)configure
{
    self.backgroundColor = [UIColor clearColor].CGColor;
    
    CGColorRef backgroundColor = self.meterData.backgroundColor ?: [UIColor ehi_grayColor1].CGColor;
    self.backgroundLayer = [self shapeLayerWithStrokeColor:backgroundColor];
    [self addSublayer:self.backgroundLayer];
    
    CGColorRef fillColor = self.meterData.fillColor ?: [UIColor ehi_greenColor].CGColor;
    self.fillLayer = [self shapeLayerWithStrokeColor:fillColor];
    self.fillLayer.strokeEnd = 0.0f;
    [self addSublayer:self.fillLayer];
    
    if(self.needsOutline) {
        CGColorRef outlineColor = self.meterData.outlineColor;
        self.lineLayer = [self shapeLayerWithStrokeColor:outlineColor];
        self.lineLayer.lineWidth = EHIMeterLayerOutlineStrokeWidth;
        [self addSublayer:self.lineLayer];
    }
}

- (CAShapeLayer *)shapeLayerWithStrokeColor:(CGColorRef)strokeColor
{
    CAShapeLayer *shape = [CAShapeLayer layer];
    shape.fillColor = [UIColor clearColor].CGColor;
    shape.lineWidth = EHIMeterLayerStrokeWidth;
    shape.strokeColor = strokeColor;
    // it's an arc, so draw only half of it
    shape.strokeStart = 0.5f;
    
    return shape;
}

# pragma mark - Layout

- (void)layoutSublayers
{
    [super layoutSublayers];
   
    // generate the rect for the arc
    CGRect rect = self.bounds;
    rect.size.height *= 2.0f;
   
    // create the arc path and update our shape layers
    CGPathRef arcPath = EHICreateArcPathWithRect(rect); {
        self.backgroundLayer.path = arcPath;
        self.fillLayer.path       = arcPath;
    } CGPathRelease(arcPath);
    
    if(self.needsOutline) {
        CGFloat inset      = -EHIMeterLayerStrokeWidth / (EHIMeterLayerOutlineStrokeWidth * 2);
        CGRect lineBounds  = CGRectInset(rect, inset, inset);
        CGPathRef linePath = EHICreateArcPathWithRect(lineBounds); {
            self.lineLayer.path = linePath;
        } CGPathRelease(linePath);
    }
}

//
// Helpers
//

CGPathRef EHICreateArcPathWithRect(CGRect rect)
{
    // draw clipped oval
    rect = CGRectInset(rect, EHIMeterLayerStrokeWidth / 2, EHIMeterLayerStrokeWidth / 2);
    CGPathRef ovalPath = CGPathCreateWithEllipseInRect(rect, NULL);
    
    return ovalPath;   
}

- (BOOL)needsOutline
{
    return self.meterData.outlineColor != nil;
}

# pragma mark - Setters

- (void)setFillPercent:(CGFloat)fillPercent
{
    [self setFillPercent:fillPercent animated:NO];
}

- (void)setFillPercent:(CGFloat)fillPercent animated:(BOOL)animated
{
    CGFloat strokeEnd = [self strategyFillWithValue:fillPercent];
    
    [CALayer ehi_animate:animated duration:EHIMeterLayerAnimationDuration transform:^{
        self.fillLayer.strokeEnd = strokeEnd;
    }];
}

//
// Helpers
//

- (CGFloat)strategyFillWithValue:(CGFloat)value
{
    return self.meterData.fillStrategy == EHIMeterFillStrategyTypeStep ? [self stepFillForValue:value] : [self percentFillForValue:value];
}

- (CGFloat)percentFillForValue:(CGFloat)value
{
    CGFloat strokeStart = self.fillLayer.strokeStart;
    return (1 - strokeStart) * (value / 100.f) + strokeStart;
}

- (CGFloat)stepFillForValue:(CGFloat)value
{
    NSInteger segments = self.meterData.segments;
    // calculate the circunference length
    CGFloat circleLength = M_PI * CGRectGetWidth(self.bounds);
    // since we are working with arcs, but all the calcs are with full circles, let multiply the segments by 2
    CGFloat segmentLength = circleLength / (segments * 2.0);
    
    // (partitions + targetPartition) will move to the correct partition of the arc
    // multiply by the segment length to get the length that should be drawn
    // divide by the full length of the circle, because stroke property on CAShapeLayer goes from 0 to 1.
    CGFloat fill = ((segments + value) * segmentLength) / circleLength;
    
    return fill;
}

@end

NS_ASSUME_NONNULL_END
