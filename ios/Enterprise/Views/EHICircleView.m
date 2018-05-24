//
//  EHICircleView.m
//  Enterprise
//
//  Created by fhu on 4/24/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICircleView.h"

@implementation EHICircleView

- (void)layoutSubviews
{
    [super layoutSubviews];
    
    CGFloat radius = MAX(CGRectGetHeight(self.bounds), CGRectGetWidth(self.bounds));
    [self.layer setCornerRadius:radius / 2];
}
@end
