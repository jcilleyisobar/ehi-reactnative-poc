//
//  EHIPageControlDot.m
//  Enterprise
//
//  Created by Ty Cobb on 3/6/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIPageControlDot.h"

@implementation EHIPageControlDot

- (instancetype)init
{
    // calculate a hardcoded size for now
    CGSize size = (CGSize){ .width = 20.0f, .height = 2.0f };
    
    if(self = [super initWithFrame:(CGRect){ .size = size }]) {
        [self invalidateIsHighlighted];
        [self mas_makeConstraints:^(MASConstraintMaker *make) {
            make.size.mas_equalTo(self.bounds.size);
        }];
    }
    
    return self;
}

# pragma mark - Highlighting

- (void)setIsHighlighted:(BOOL)isHighlighted
{
    if(_isHighlighted != isHighlighted) {
        _isHighlighted = isHighlighted;
        [self invalidateIsHighlighted];
    }
}

- (void)invalidateIsHighlighted
{
    self.backgroundColor = self.isHighlighted ? [UIColor ehi_greenColor] : [UIColor ehi_grayColor1];
}

@end
