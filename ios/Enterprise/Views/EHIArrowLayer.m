//
//  EHIArrowLayer.m
//  Enterprise
//
//  Created by Ty Cobb on 3/24/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIArrowLayer.h"

#define EHIArrowViewVertexCount 3
#define EHIArrowDirectionIsHorizontal(_direction) ((_direction) == EHIArrowDirectionLeft || (_direction) == EHIArrowDirectionRight)

@implementation EHIArrowLayer

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
    self.backgroundColor = [UIColor clearColor].CGColor;
    self.direction = self.direction ?: EHIArrowDirectionRight;
}

# pragma mark - Layout

- (void)layoutSublayers
{
    [super layoutSublayers];
    
    CGFloat majorSide = EHIArrowDirectionIsHorizontal(self.direction) ? self.bounds.size.height : self.bounds.size.width;
    CGFloat minorSide = EHIArrowDirectionIsHorizontal(self.direction) ? self.bounds.size.width  : self.bounds.size.height;
    
    CGPoint arrowVertices[EHIArrowViewVertexCount];
    arrowVertices[0] = CGPointMake(0.0f, 0.0f);
    arrowVertices[1] = CGPointMake(minorSide, majorSide / 2.0f);
    arrowVertices[2] = CGPointMake(0.0f, majorSide);
    
    UIBezierPath *bezierPath = [UIBezierPath bezierPath];
    [bezierPath moveToPoint:arrowVertices[0]];
    [bezierPath addLineToPoint:arrowVertices[1]];
    [bezierPath addLineToPoint:arrowVertices[2]];
    [bezierPath closePath];
    
    CGAffineTransform transform = [self transformForDirection:self.direction majorSide:majorSide minorSide:minorSide];
    [bezierPath applyTransform:transform];
   
    [CALayer ehi_performUnanimated:^{
        self.path = bezierPath.CGPath;
    }];
}

//
// Helpers
//

- (CGAffineTransform)transformForDirection:(EHIArrowDirection)direction majorSide:(CGFloat)majorSide minorSide:(CGFloat)minorSide
{
    CGAffineTransform transform = CGAffineTransformIdentity;
    
    switch(direction) {
        case EHIArrowDirectionLeft:
            transform = CGAffineTransformTranslate(transform, minorSide, majorSide);
            transform = CGAffineTransformRotate(transform, M_PI);
            break;
        case EHIArrowDirectionUp:
            transform = CGAffineTransformTranslate(transform, 0.0f, minorSide);
            transform = CGAffineTransformRotate(transform, -M_PI_2);
            break;
        case EHIArrowDirectionDown:
            transform = CGAffineTransformTranslate(transform, majorSide, 0.0f);
            transform = CGAffineTransformRotate(transform, M_PI_2);
            break;
        default: break;
    }
    
    return transform;
}

# pragma mark - Setters

- (void)setDirection:(EHIArrowDirection)direction
{
    if(_direction != direction) {
        _direction = direction;
        [self setNeedsLayout];
    }
}

@end
