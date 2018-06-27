//
//  EHITagLayer.m
//  Enterprise
//
//  Created by Rafael Ramos on 1/16/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHITagLayer.h"

@implementation EHITagLayer

- (void)layoutSublayers
{
    [super layoutSublayers];
    
    self.path = self.tagPath.CGPath;
}

// CGPointZero     A
//     ........................
//     |                        `.
//     |                          `.  B
//     |                            `.
//  E  |                              `,
//     |                             ,'
//     |                           ,'
//     |                         ,'   C
//     `........................-
//                 D

- (UIBezierPath *)tagPath
{
    UIBezierPath *path = [UIBezierPath new];
    
    CGFloat width  = CGRectGetWidth(self.bounds);
    CGFloat height = CGRectGetHeight(self.bounds);
    // origin
    [path moveToPoint:CGPointZero];
    // draw A
    [path addLineToPoint:CGPointMake(width/self.factor, 0.0f)];
    // draw B
    [path addLineToPoint:CGPointMake(width, height/2)];
    // draw C
    [path addLineToPoint:CGPointMake(width/self.factor, height)];
    // draw D
    [path addLineToPoint:CGPointMake(0.0f, height)];
    // draw E
    [path closePath];
    
    return path;
}

// Based on comps
- (CGFloat)factor
{
    return 1.19713;
}

@end
