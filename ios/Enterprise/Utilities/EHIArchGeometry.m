//
//  EHIArchGeometry.m
//  Enterprise
//
//  Created by Rafael Ramos on 1/6/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIArchGeometry.h"
#import "NSCollections+Utility.h"

@interface EHIArchGeometry ()
@property (assign, nonatomic) CGPoint aCenter;
@property (assign, nonatomic) CGFloat aRadius;
@property (assign, nonatomic) CGFloat theSegments;
@end

@implementation EHIArchGeometry

- (EHIArchGeometry *(^)(CGPoint))center
{
    return ^(CGPoint aCenter) {
        self.aCenter = aCenter;
        return self;
    };
}

- (EHIArchGeometry *(^)(CGFloat))radius
{
    return ^(CGFloat aRadius) {
        self.aRadius = aRadius;
        return self;
    };
}

- (EHIArchGeometry *(^)(CGFloat))segments
{
    return ^(CGFloat theSegments) {
        self.theSegments = theSegments;
        return self;
    };
}

- (NSArray *)points
{
    // short-circuit when there's no segments
    if(self.theSegments == 0) {
        return @[];
    }
    
    CGFloat segments = self.theSegments;
    CGFloat radius   = self.aRadius;
    CGPoint center   = self.aCenter;
    
    //       _,..._
    //    ,-'      `-.
    //   /            \
    //  |           ↓  \  sum one to draw the last segment
    //  |_______.______|
    //
    return [NSArray ehi_arrayWithCapacity:segments + 1].map(^(id elem, NSInteger segmentStep){
        CGFloat angle = [self angleForSegments:segments atStep:segmentStep];
        
        CGFloat oppositeLength = [self adjacentTo:angle withHypotenuse:radius];
        CGFloat adjacentLegnth = [self oppositeTo:angle withHypotenuse:radius];
        
        CGPoint adjacentPoint = CGPointMake(center.x + adjacentLegnth, center.y);
        CGPoint oppositePoint = CGPointMake(center.x, center.y - oppositeLength);
        
        CGPoint targetPoint   = CGPointMake(adjacentPoint.x, oppositePoint.y);
        
        // boxing CGPoint since it's a struct and collections on Objective-C only handles objects
        return [NSValue valueWithCGPoint:targetPoint];
    });
}

//
// Helpers
//

- (CGFloat)angleForSegments:(CGFloat)segments atStep:(CGFloat)step
{
    // 180 is half of the circle, and dividing it by the number of segments gives us the angle (in °)
    CGFloat angle = (180.0f/segments);
    
    //
    //          _,..._                              _,..._
    //       ,-'   |  `-.                        ,-'   |  `-.
    //      /      |  ,' \                      / `.   |   ' \
    //     |       |<'    |                    |    \  | ,'   |
    //     |       |------|  shift the angle   |     `.|`-----|
    //     |        `.    |      -90°          |              |
    //      \         \  ,'                     \            ,'
    //       `.        `,                        `.        _,
    //         `-....-'                            `-....-'
    //
    return (angle * step) - 90.0f;
}

- (CGFloat)adjacentTo:(CGFloat)angle withHypotenuse:(CGFloat)hypotenuse
{
    CGFloat cos = __cospi(angle/180.0f);
    return cos * hypotenuse;
}

- (CGFloat)oppositeTo:(CGFloat)angle withHypotenuse:(CGFloat)hypotenuse
{
    CGFloat sin = __sinpi(angle/180.0);
    return sin * hypotenuse;
}

@end
