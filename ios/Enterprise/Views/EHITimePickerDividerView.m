//
//  EHITimePickerDividerView.m
//  Enterprise
//
//  Created by Michael Place on 3/29/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHITimePickerDividerView.h"

@interface EHITimePickerDividerView ()
@property (strong, nonatomic) CAGradientLayer *gradientLayer;
@property (strong, nonatomic) CALayer *solidLayer;
@end

@implementation EHITimePickerDividerView

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if (self = [super initWithCoder:aDecoder]) {
        // set the default type
        _type = EHITimePickerDividerTypeSolid;
    }
    
    return self;
}

# pragma mark - Layout

- (void)layoutSubviews
{
    [super layoutSubviews];
    
    [self invalidateLayerBounds];
}

- (void)invalidateLayerBounds
{
   self.solidLayer.frame = self.bounds;
   self.gradientLayer.frame = self.bounds;
}

# pragma mark - Layers

- (void)invalidateLayers
{
    // remove sublayers so we can start fresh
    [self removeSublayers];
    
    switch (self.type) {
        case EHITimePickerDividerTypeSolid:
            [self configureSolidDivider];
        case EHITimePickerDividerTypeTapered:
            [self configureTaperedDivider];
    }
}

- (void)configureSolidDivider
{
    if (!_solidLayer) {
        _solidLayer = [CALayer layer];
        _solidLayer.backgroundColor = [UIColor ehi_grayColor3].CGColor;
        _solidLayer.frame = self.bounds;
    }
    
    [self.layer insertSublayer:self.solidLayer atIndex:0];
}

- (void)configureTaperedDivider
{
    if(!_gradientLayer) {
        _gradientLayer = [CAGradientLayer layer];
        _gradientLayer.frame = self.bounds;
    }
    
    // initialize colors
    CGColorRef outerColor = [UIColor clearColor].CGColor;
    CGColorRef innerColor = [UIColor grayColor].CGColor;
    
    // set up colors and locations
    self.gradientLayer.colors = @[(__bridge id)outerColor, (__bridge id)innerColor, (__bridge id)outerColor];
    self.gradientLayer.locations = @[@0.0f, @0.6f, @1.0f];
    
    // ensures that the gradient is horizontal
    self.gradientLayer.startPoint  = CGPointMake(0.0, 0.5);
    self.gradientLayer.endPoint    = CGPointMake(1.0, 0.5);
    
    [self.layer insertSublayer:self.gradientLayer atIndex:0];
}

//
// Helper
//

- (void)removeSublayers
{
    [self.solidLayer removeFromSuperlayer];
    [self.gradientLayer removeFromSuperlayer];
}

# pragma mark - Setter

- (void)setType:(EHITimePickerDividerType)type
{
    _type = type;
    
    [self invalidateLayers];
}

@end
