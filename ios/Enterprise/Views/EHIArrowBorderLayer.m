//
//  EHIArrowBorderLayer.m
//  Enterprise
//
//  Created by Alex Koller on 11/19/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHIArrowBorderLayer.h"

#define EHIArrowBorderArrowWidth  (14.0)
#define EHIArrowBorderArrowHeight (7.0)
#define EHIArrowBorderSideIsVertical(_side) ((_side) == EHIArrowBorderLayerSideLeft || (_side) == EHIArrowBorderLayerSideRight)

@implementation EHIArrowBorderLayer

- (instancetype)init
{
    if(self = [super init]) {
        [self configure];
    }
    
    return self;
}

- (id)initWithCoder:(NSCoder *)decoder
{
    if(self = [super initWithCoder:decoder]) {
        [self configure];
    }
    
    return self;
}

//
// Helpers
//

- (void)configure
{
    self.lineWidth   = 1.0;
    self.strokeColor = [UIColor blackColor].CGColor;
    self.fillColor   = [UIColor whiteColor].CGColor;
}

# pragma mark - Layout

- (void)layoutSublayers
{
    [super layoutSublayers];
    
    // drawing varibles
    CGSize size  = self.bounds.size;
    
    // drawing assumes arrow is on bottom or top, must invert here
    if(EHIArrowBorderSideIsVertical(self.side)) {
        size = (CGSize){ .width = size.height, .height = size.width };
    }
    
    CGSize arrowSize = CGSizeMake(EHIArrowBorderArrowWidth, EHIArrowBorderArrowHeight);
    CGFloat longEdge = (size.width - arrowSize.width) / 2;
    
    CGMutablePathRef path = CGPathCreateMutable();
    
    // when verticle, bottom left corner of border will not match view, must adjust starting point here
    if(EHIArrowBorderSideIsVertical(self.side)) {
        CGPathMoveToPoint(path, NULL, (size.height - size.width) / 2, size.width + ((size.height - size.width) / 2));
    } else {
        CGPathMoveToPoint(path, NULL, 0, size.height);
    }
    
    CGPathAddLineWithXOffset(path, longEdge);
    CGPathAddLineWithOffset(path, arrowSize.width / 2, arrowSize.height);
    CGPathAddLineWithOffset(path, arrowSize.width / 2, -arrowSize.height);
    CGPathAddLineWithXOffset(path, longEdge);
    CGPathAddLineWithYOffset(path, -size.height);
    CGPathAddLineWithXOffset(path, -size.width);
    CGPathCloseSubpath(path);

    CGAffineTransform transform = [self transformForSide:self.side];
    CGPathRef transformedPath   = CGPathCreateCopyByTransformingPath(path, &transform);
    
    // draw path with arrow edge stroked
    self.strokeEnd = [self strokeLengthForSide:self.side];
    self.path      = transformedPath;
    
    // release variables
    CGPathRelease(path);
    CGPathRelease(transformedPath);
}

//
// Helpers
//

- (CGAffineTransform)transformForSide:(EHIArrowBorderLayerSide)side
{
    // translate to center before rotating
    CGPoint center = CGRectGetCenter(self.bounds);
    CGAffineTransform transform = CGAffineTransformIdentity;
    transform = CGAffineTransformTranslate(transform, center.x, center.y);
    
    // rotate around center
    switch(side) {
        case EHIArrowBorderLayerSideLeft:
            transform = CGAffineTransformRotate(transform, M_PI_2); break;
        case EHIArrowBorderLayerSideTop:
            transform = CGAffineTransformRotate(transform, M_PI); break;
        case EHIArrowBorderLayerSideRight:
            transform = CGAffineTransformRotate(transform, -M_PI_2); break;
        default: break;
    }
    
    // reset
    transform = CGAffineTransformTranslate(transform, -center.x, -center.y);
    
    return transform;
}

- (CGFloat)strokeLengthForSide:(EHIArrowBorderLayerSide)side
{
    // find length of each side
    CGFloat arrowSideLength  = EHIArrowBorderSideIsVertical(self.side) ? self.bounds.size.height : self.bounds.size.width;
    CGFloat normalSideLength = EHIArrowBorderSideIsVertical(self.side) ? self.bounds.size.width : self.bounds.size.height;
    CGFloat borderSideLength = arrowSideLength - EHIArrowBorderArrowWidth + (EHIArrowBorderArrowHeight * 2 * sqrt(2));

    // total length
    CGFloat perimeter = normalSideLength * 2 + arrowSideLength + borderSideLength;
    
    return borderSideLength / perimeter;
}

# pragma mark - Setters

- (void)setSide:(EHIArrowBorderLayerSide)side
{
    if(_side != side) {
        _side = side;
        [self setNeedsLayout];
    }
}

@end
