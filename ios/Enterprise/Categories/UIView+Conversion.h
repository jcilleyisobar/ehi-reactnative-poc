//
//  UIView+Conversion.h
//  Enterprise
//
//  Created by Ty Cobb on 4/23/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@interface UIView (Conversion)

/** Calculates the receiver's frame in the given @c view; @c view should not be @c nil */
- (CGRect)ehi_frameInView:(UIView *)view;

@end
