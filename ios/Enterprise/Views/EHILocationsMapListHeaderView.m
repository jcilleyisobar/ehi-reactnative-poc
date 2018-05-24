//
//  EHILocationsMapListHeaderView.m
//  Enterprise
//
//  Created by Ty Cobb on 7/17/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILocationsMapListHeaderView.h"
#import "EHILocationFilterWidgetView.h"

@interface EHILocationsMapListHeaderView ()
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet UIImageView *iconView;
@property (weak  , nonatomic) IBOutlet UIView *alphaBackground;
@property (weak  , nonatomic) IBOutlet UIView *innerContainerView;
@property (weak  , nonatomic) IBOutlet UIView *containerView;
@property (weak  , nonatomic) IBOutlet EHILocationFilterWidgetView *filterWidgetView;
@property (weak  , nonatomic) IBOutlet UIView *filterContainerView;
@property (weak  , nonatomic) IBOutlet NSLayoutConstraint *centerXConstraint;
@end

@implementation EHILocationsMapListHeaderView

- (void)awakeFromNib
{
    [super awakeFromNib];
   
    self.titleLabel.text = EHILocalizedString(@"locations_list_section_title", @"LOCATIONS LIST", @"Map locations list title");

    // force the icon to be a template image so we can tint it
    self.iconView.image       = [self.iconView.image imageWithRenderingMode:UIImageRenderingModeAlwaysTemplate];
    self.iconView.highlighted = YES;
    
    self.filterWidgetView.backgroundColor = [UIColor clearColor];
    self.filterContainerView.alpha        = 0.0f;
    
    [self.filterWidgetView updateWithModel:@(EHILocationFilterWidgetLayoutInsideContainer) metrics:nil];
}

- (void)setHighlighted:(BOOL)highlighted
{
    [super setHighlighted:highlighted];
   
    // force the image to be highlighted so that it tints
    self.iconView.highlighted = YES;
}

- (void)setProgress:(CGFloat)progress
{
    self.centerXConstraint.constant = -MAX((self.interpolationTotal * MIN(progress, 1.0f)), 0.0f);
    [UIView animateWithDuration:0.10 animations:^{
        self.filterContainerView.alpha = progress >= 0.9f ? 1.0f : 0.0f;
        self.alphaBackground.alpha     = progress >= 0.9f ? 1.0f : 0.7f;
        [self layoutIfNeeded];
    }];
}

- (CGFloat)interpolationTotal
{
    CGFloat width      = CGRectGetWidth(self.frame)/2;
    CGFloat container  = CGRectGetWidth(self.innerContainerView.frame)/2;
    CGFloat maxLeading = 20.0f;
    CGFloat total = width - (maxLeading + container);
    
    return MAX(total, 0.0f);
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = 56.0f
    };
}

@end
