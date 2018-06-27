//
//  EHISegmentedControl.m
//  EHIius
//
//  Created by mplace on 9/6/15.
//  Copyright (c) 2014 Isobar. All rights reserved.
//

#import "EHISegmentedControl.h"

@implementation EHISegmentedControl

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        _fontSize = 14.0;
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    [self initializeBackgroundViews];
    [self invalidateFontAttributes];
}

- (void)initializeBackgroundViews
{
    UIImage *selectedBackground = [self imageWithView:[self viewWithColor:[UIColor ehi_greenColor]]];
    UIImage *deselectedBackground = [self imageWithView:[self viewWithColor:[UIColor clearColor]]];
    UIImage *highlightedBackground = [self imageWithView:[self viewWithColor:[[UIColor ehi_greenColor] colorWithAlphaComponent:0.3f]]];
    
    // Background images: selected state
    [self setBackgroundImage:deselectedBackground
                    forState:UIControlStateNormal
                  barMetrics:UIBarMetricsDefault];
    [self setBackgroundImage:selectedBackground
                    forState:UIControlStateSelected
                  barMetrics:UIBarMetricsDefault];
    [self setBackgroundImage:highlightedBackground
                    forState:UIControlStateSelectedHighlighted
                  barMetrics:UIBarMetricsDefault];
    [self setBackgroundImage:highlightedBackground
                    forState:UIControlStateHighlighted
                  barMetrics:UIBarMetricsDefault];
    
}

//
// Helpers
//

- (UIView *)viewWithColor:(UIColor *)color
{
    NSInteger backgroundWidth = UIDeviceIsTablet ? 130 : 80;
    
    UIView *view = [[UIView alloc] initWithFrame:CGRectMake(0, 0, backgroundWidth, self.frame.size.height)];
    UIView *subview = [[UIView alloc] initWithFrame:CGRectMake(0, 0, backgroundWidth, self.frame.size.height)];
    
    [subview setBackgroundColor:color];
    [subview.layer setBorderWidth:0.5f];
    [subview.layer setBorderColor:[UIColor ehi_greenColor].CGColor];
    [view addSubview:subview];
    
    return view;
}

- (UIImage *)imageWithView:(UIView *)view
{
    UIGraphicsBeginImageContextWithOptions(view.frame.size, NO, 0.0);
    [view.layer renderInContext:UIGraphicsGetCurrentContext()];
    
    UIImage *image = UIGraphicsGetImageFromCurrentImageContext();
    
    UIGraphicsEndImageContext();
    
    return image;
}

# pragma mark - Setters

- (void)setFontSize:(CGFloat)fontSize
{
    if(_fontSize == fontSize) {
        return;
    }
    
    _fontSize = fontSize;
    
    [self invalidateFontAttributes];
}

//
// Helpers
//

- (void)invalidateFontAttributes
{
    UIFont *font = [UIFont ehi_fontWithStyle:EHIFontStyleBold size:self.fontSize];
    NSMutableDictionary *attributes = [NSMutableDictionary dictionary];
    [attributes setObject:font forKey:NSFontAttributeName];
    [attributes setObject:[UIColor ehi_greenColor] forKey:NSForegroundColorAttributeName];
    
    UIFont *selectedFont = [UIFont ehi_fontWithStyle:EHIFontStyleBold size:self.fontSize];
    NSMutableDictionary *selectedAttributes = [NSMutableDictionary dictionary];
    [selectedAttributes setObject:selectedFont forKey:NSFontAttributeName];
    [selectedAttributes setObject:[UIColor whiteColor] forKey:NSForegroundColorAttributeName];
    
    [self setTitleTextAttributes:[attributes copy]
                        forState:UIControlStateNormal];
    
    [self setTitleTextAttributes:[selectedAttributes copy]
                        forState:UIControlStateSelected];
}

@end
