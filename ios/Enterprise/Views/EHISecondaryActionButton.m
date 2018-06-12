//
//  EHISecondaryActionButton.m
//  Enterprise
//
//  Created by Ty Cobb on 2/25/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHISecondaryActionButton.h"

@implementation EHISecondaryActionButton

- (void)applyDefaults
{
    [super applyDefaults];
   
    [self setIndicatorType:EHIActivityIndicatorTypeGreen];
    [self invalidateColors];
}

# pragma mark - Colors

- (void)setInvertsColors:(BOOL)invertsColors
{
    _invertsColors = invertsColors;
    
    [self invalidateColors];
    [self synchronize];
}

- (void)setIsOnDarkBackground:(BOOL)isOnDarkBackground
{
    _isOnDarkBackground = isOnDarkBackground;
    
    [self invalidateColors];
    [self synchronize];
}

- (void)invalidateColors
{
    UIColor *primary   = self.isOnDarkBackground ? [UIColor ehi_lightGreenColor] : [UIColor ehi_greenColor];
    UIColor *highlight = self.isOnDarkBackground ? [UIColor ehi_lightGreenColor] : [UIColor ehi_darkGreenColor];
    
    UIColor *background = [UIColor clearColor];
    UIColor *backgroundHighlight = [UIColor clearColor];
    
    if(self.invertsColors) {
        // swap the normal colors
        background = primary;
        // primary (title) uses white instead of clear in this case)
        primary = [UIColor whiteColor];
       
        // swap the highlight colors
        UIColor *temp = highlight;
        highlight = backgroundHighlight;
        backgroundHighlight = temp;
    }
    
    // override superclass title colors
    [self.imageView setTintColor:primary];
    
    // override superclass title colors
    [self setTitleColor:primary forState:UIControlStateNormal];
    [self setTitleColor:highlight forState:UIControlStateHighlighted];
    
    // override superclass background colors
    [self setBackgroundColor:background forState:UIControlStateNormal];
    [self setBackgroundColor:backgroundHighlight forState:UIControlStateHighlighted];
    
    self.layer.borderWidth = 1.0f;
    self.layer.borderColor = primary.CGColor;
}

@end
