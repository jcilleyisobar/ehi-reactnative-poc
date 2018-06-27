//
//  UIView+Conversion.m
//  Enterprise
//
//  Created by Ty Cobb on 4/23/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "UIView+Conversion.h"

@implementation UIView (Conversion)

- (CGRect)ehi_frameInView:(UIView *)view
{
    NSParameterAssert(view);
    return [self convertRect:self.bounds toView:view];
}

@end
