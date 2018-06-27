//
//  EHIRectGeometry.h
//  Enterprise
//
//  Created by Ty Cobb on 1/5/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHISharedGeometry.h"

CG_INLINE CGRect CGRectByCenteringSizeInRect(CGRect rect, CGSize size)
{
    return (CGRect) {
        .size = size,
        .origin = (CGPoint){
            .x = rect.origin.x + (rect.size.width  - size.width) / 2.0f,
            .y = rect.origin.y + (rect.size.height - size.height) / 2.0f
        }
    };
}

CG_INLINE CGPoint CGRectGetCenter(CGRect rect)
{
    return (CGPoint) {
        .x = rect.origin.x + rect.size.width  / 2.0f,
        .y = rect.origin.y + rect.size.height / 2.0f,
    };
}

CG_INLINE CGRect CGRectWithCenterAndSize(CGPoint center, CGSize size)
{
    return (CGRect) {
        .origin = (CGPoint) {
            .x = center.x - size.width  / 2.0f,
            .y = center.y - size.height / 2.0f
        },
        .size = size
    };
}

CG_INLINE CGPoint CGRectInterpolate(CGRect rect, CGFloat xFactor, CGFloat yFactor)
{
    return (CGPoint) {
        .x = rect.origin.x + rect.size.width  * xFactor,
        .y = rect.origin.y + rect.size.height * yFactor
    };
}

CG_INLINE CGRect CGRectInsetWithOffset(CGRect rect, UIOffset offset)
{
    rect.origin.x += offset.horizontal;
    rect.origin.y += offset.vertical;
    
    rect.size.width  -= offset.horizontal * 2.0f;
    rect.size.height -= offset.vertical * 2.0f;
    
    return rect;
}

CG_INLINE CGRect CGRectApplyInsets(CGRect rect, UIEdgeInsets insets)
{
    rect.origin.x += insets.left;
    rect.origin.y += insets.top;
    
    rect.size.width  -= insets.left + insets.right;
    rect.size.height -= insets.top + insets.bottom;
    
    return rect;
}

CG_INLINE CGPoint CGRectGetTopLeft(CGRect rect)
{
    return rect.origin;
}

CG_INLINE CGPoint CGRectGetTopRight(CGRect rect)
{
    return (CGPoint) {
        .x = CGRectGetMaxX(rect),
        .y = rect.origin.y,
    };
}

CG_INLINE CGPoint CGRectGetBottomLeft(CGRect rect)
{
    return (CGPoint) {
        .x = rect.origin.x,
        .y = CGRectGetMaxY(rect)
    };
}

CG_INLINE CGPoint CGRectGetBottomRight(CGRect rect)
{
    return (CGPoint) {
        .x = CGRectGetMaxX(rect),
        .y = CGRectGetMaxY(rect)
    };
}
