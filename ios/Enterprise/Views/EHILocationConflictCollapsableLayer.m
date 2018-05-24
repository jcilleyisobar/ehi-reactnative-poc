//
//  EHILocationConflictCollapsableLayer.m
//  Enterprise
//
//  Created by Rafael Ramos on 6/19/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHILocationConflictCollapsableLayer.h"

@implementation EHILocationConflictCollapsableLayer

//
//                                      B  /\  C
//                        A               /  \        D
//                                       /    \
//      .''''''''''''''''''''''''''''''''      ''''''''''''''.
//      |                                                    |
//      |                                                    |
//      |                                                    |
//      |                                                    |
//   G  |                                                    |  E
//      |                                                    |
//      |                                                    |
//      |                                                    |
//      |                                                    |
//      |....................................................|
//
//                               F
//

- (UIBezierPath *)arrowPath
{
    UIBezierPath *path = [UIBezierPath new];
    
    // adjust arrow center
    CGFloat padding = self.padding + self.arrowHeight;
    
    [path moveToPoint:CGPointMake(self.lineWidth/2, self.arrowHeight)];
    
    // draw A
    [path addLineToPoint:CGPointMake(CGRectGetWidth(self.bounds) - padding, self.arrowHeight)];
    // draw B
    [path addLineToPoint:CGPointMake(CGRectGetWidth(self.bounds) - padding + self.arrowHeight, self.lineWidth/2)];
    // draw C
    [path addLineToPoint:CGPointMake(CGRectGetWidth(self.bounds) - padding + (self.arrowHeight * 2), self.arrowHeight)];
    // draw D
    [path addLineToPoint:CGPointMake(CGRectGetWidth(self.bounds) - (self.lineWidth/2), self.arrowHeight)];
    // draw E
    [path addLineToPoint:CGPointMake(CGRectGetWidth(self.bounds) - (self.lineWidth/2), CGRectGetHeight(self.bounds) - (self.lineWidth/2))];
    // draw F
    [path addLineToPoint:CGPointMake(self.lineWidth/2, CGRectGetHeight(self.bounds) - (self.lineWidth/2))];
    // draw G
    [path closePath];
    
    return path;
}

# pragma mark - Layout

- (void)layoutSublayers
{
    [super layoutSublayers];
    
    [CALayer ehi_performUnanimated:^{
        self.path = self.arrowPath.CGPath;
    }];
}

@end
