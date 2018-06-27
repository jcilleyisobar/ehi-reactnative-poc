//
//  EHIActionButton.m
//  Enterprise
//
//  Created by Ty Cobb on 2/4/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIActionButton.h"

@implementation EHIActionButton

- (instancetype)initWithFrame:(CGRect)frame
{
    // apply default sizing if necessary
    if(!frame.size.height) {
        frame.size.height = 46.0f;
    }
    
    return [super initWithFrame:frame];
}

# pragma mark - EHIButton

- (void)applyDefaults
{
    [super applyDefaults];
   
    self.titleLabel.font = [UIFont ehi_fontWithStyle:EHIFontStyleBold size:18.0f];
    
    // icon tint color
    self.imageView.tintColor = [UIColor whiteColor];
    
    // configure title colors
    [self setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self setTitleColor:[UIColor ehi_grayColor2] forState:UIControlStateDisabled];
  
    // configure background colors
    [self setBackgroundColor:[UIColor ehi_greenColor] forState:UIControlStateNormal];
    [self setBackgroundColor:[UIColor ehi_grayColor4] forState:UIControlStateDisabled];
}

@end
