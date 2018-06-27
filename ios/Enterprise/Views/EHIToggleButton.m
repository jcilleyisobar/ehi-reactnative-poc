//
//  EHIToggleButton.m
//  Enterprise
//
//  Created by mplace on 2/27/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIToggleButton.h"

@interface EHIToggleButton ()
@property (strong, nonatomic) UIColor *deselectedColor;
@property (strong, nonatomic) UIColor *borderColor;
@property (assign, nonatomic) CGFloat borderWidth;
@end

@implementation EHIToggleButton

- (instancetype)initWithFrame:(CGRect)frame
{
    if(self = [super initWithFrame:frame]) {
        [self applyDefaults];
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    [self applyDefaults];
}

- (void)applyDefaults
{
    // ignore tint color
    self.tintColor = [UIColor clearColor];
    
    self.backgroundColor   = self.deselectedColor;
    self.layer.borderWidth = self.borderWidth;
    self.layer.borderColor = self.borderColor.CGColor;
}

# pragma mark - Selection

- (void)setSelected:(BOOL)selected
{
    [super setSelected:selected];
    
    // update background color
    self.backgroundColor = selected ? [UIColor ehi_greenColor] : self.deselectedColor;
    
    // update checkmark image
    UIImage *image = selected ? [UIImage imageNamed:@"icon_checkmarknew"] : nil;
    [self setImage:image forState:UIControlStateNormal];
}

# pragma mark - Accessors

- (void)setStyle:(EHIToggleButtonStyle)style
{
    _style = style;
    
    [self applyDefaults];
}

- (UIColor *)deselectedColor
{
    switch (self.style) {
        case EHIToggleButtonStyleDefault:
            return [UIColor ehi_grayColor1];
        case EHIToggleButtonStyleWhite:
            return [UIColor whiteColor];
    }
}

- (UIColor *)borderColor
{
    switch (self.style) {
        case EHIToggleButtonStyleDefault:
            return [UIColor ehi_grayColor1];
        case EHIToggleButtonStyleWhite:
            return [UIColor ehi_grayColor3];
    }
}
    
- (CGFloat)borderWidth
{
    switch (self.style) {
        case EHIToggleButtonStyleDefault:
            return 3.0f;
        case EHIToggleButtonStyleWhite:
            return 1.0f;
    }
}

@end
