//
//  EHITransformGeometry.h
//  Enterprise
//
//  Created by Ty Cobb on 4/1/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

CG_INLINE CGAffineTransform CGAffineTransformSetAnchorPoint(CGAffineTransform transform, CGPoint anchor)
{
    return CGAffineTransformTranslate(transform, anchor.x, anchor.y);
}

CG_INLINE CGAffineTransform CGAffineTransformUnsetAnchorPoint(CGAffineTransform transform, CGPoint anchor)
{
    return CGAffineTransformTranslate(transform, -anchor.x, -anchor.y);
}
