//
//  EHIPathGeometry.h
//  Enterprise
//
//  Created by Ty Cobb on 1/26/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHISharedGeometry.h"

CG_INLINE void CGPathAdvanceToPoint(CGMutablePathRef path, CGPoint point, BOOL shouldDraw)
{
    if(shouldDraw) {
        CGPathAddLineToPoint(path, NULL, point.x, point.y);
    } else {
        CGPathMoveToPoint(path, NULL, point.x, point.y);
    }
}

CG_INLINE void CGPathAddLineWithOffset(CGMutablePathRef path, CGFloat offsetX, CGFloat offsetY)
{
    CGPoint currentPoint = CGPathGetCurrentPoint(path);
    CGFloat newX = currentPoint.x + offsetX;
    CGFloat newY = currentPoint.y + offsetY;
    CGPathAddLineToPoint(path, NULL, newX, newY);
}

CG_INLINE void CGPathAddLineWithXOffset(CGMutablePathRef path, CGFloat offset)
{
    CGPathAddLineWithOffset(path, offset, 0.0);
}

CG_INLINE void CGPathAddLineWithYOffset(CGMutablePathRef path, CGFloat offset)
{
    CGPathAddLineWithOffset(path, 0.0, offset);
}

CG_INLINE CGPathRef CGPathCreateBorder(CGRect rect, UIRectEdge edges, CGFloat inset)
{
    CGMutablePathRef path = CGPathCreateMutable();
   
    CGPoint topLeft     = CGPointOffset(CGRectGetTopLeft(rect), inset, inset);
    CGPoint topRight    = CGPointOffset(CGRectGetTopRight(rect), -inset, inset);
    CGPoint bottomLeft  = CGPointOffset(CGRectGetBottomLeft(rect), inset, -inset);
    CGPoint bottomRight = CGPointOffset(CGRectGetBottomRight(rect), -inset, -inset);
    
    CGPathAdvanceToPoint(path, topLeft,     NO);
    CGPathAdvanceToPoint(path, topRight,    edges & UIRectEdgeTop);
    CGPathAdvanceToPoint(path, bottomRight, edges & UIRectEdgeRight);
    CGPathAdvanceToPoint(path, bottomLeft,  edges & UIRectEdgeBottom);
    CGPathAdvanceToPoint(path, topLeft,     edges & UIRectEdgeLeft);
    
    return path; 
}

CG_INLINE NSString * NSStringFromCGPath(CGPathRef path)
{
    return [[UIBezierPath bezierPathWithCGPath:path] description];
}
