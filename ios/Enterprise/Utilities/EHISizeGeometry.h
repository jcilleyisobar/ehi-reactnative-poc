//
//  EHISizeGeometry.h
//  Enterprise
//
//  Created by Ty Cobb on 1/29/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

CG_INLINE CGSize CGSizeSubtractSize(CGSize minuend, CGSize subtrahend)
{
    return (CGSize) {
        .width  = minuend.width  - subtrahend.width,
        .height = minuend.height - subtrahend.height
    };
}

NS_INLINE CGSize CGSizeDivideSize(CGSize dividend, CGSize divisor)
{
    return (CGSize) {
        .width  = dividend.width  / divisor.width,
        .height = dividend.height / divisor.height
    };
}

NS_INLINE CGSize CGSizeAddSize(CGSize term1, CGSize term2)
{
    return (CGSize) {
        .width  = term1.width + term2.width,
        .height = term1.width + term2.height,
    };
}

NS_INLINE CGSize CGSizeScaleIndependently(CGSize size, CGFloat widthScale, CGFloat heightScale)
{
    return (CGSize) {
        .width  = size.width  * widthScale,
        .height = size.height * heightScale
    };
}
