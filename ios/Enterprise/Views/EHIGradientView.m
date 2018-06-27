//
//  EHIGradientView.m
//  Enterprise
//
//  Created by Rafael Ramos on 06/06/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIGradientView.h"

@implementation EHIGradientView

- (void)layoutSubviews
{
    [super layoutSubviews];
    
    self.gradientLayer.colors = (self.colors ?: @[]).map(^(UIColor *color){
        return color.CGColor;
    });
}

- (CAGradientLayer *)gradientLayer
{
    return (CAGradientLayer *)self.layer;
}

+ (Class)layerClass
{
    return [CAGradientLayer class];
}

@end
