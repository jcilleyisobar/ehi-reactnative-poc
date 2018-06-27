//
//  EHIPointGeometry.h
//  Enterprise
//
//  Created by Ty Cobb on 1/26/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHISharedGeometry.h"

CG_INLINE CGPoint CGPointOffset(CGPoint point, CGFloat x, CGFloat y)
{
    return (CGPoint) {
        .x = point.x + x,
        .y = point.y + y
    };
}

CG_INLINE CGPoint CGPointAddPoint(CGPoint point1, CGPoint point2)
{
    return (CGPoint) {
        .x = point1.x + point2.x,
        .y = point1.y + point2.y
    };
}

CG_INLINE CGPoint CGPointSubtractPoint(CGPoint point1, CGPoint point2)
{
    return (CGPoint) {
        .x = point1.x - point2.x,
        .y = point1.y - point2.y
    };
}

CG_INLINE CGPoint CGPointDisplacementFromRect(CGPoint point, CGRect rect)
{
    return (CGPoint){
        MIN(0.0f, point.x - CGRectGetMinX(rect)) + MAX(0.0f, point.x - CGRectGetMaxX(rect)),
        MIN(0.0f, point.y - CGRectGetMinY(rect)) + MAX(0.0f, point.y - CGRectGetMaxY(rect))
    };
}
