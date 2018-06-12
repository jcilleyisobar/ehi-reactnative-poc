//
//  EHILocationsMapFiltersTipView.m
//  Enterprise
//
//  Created by Rafael Machado on 03/07/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHILocationsMapFiltersTipView.h"
#import "EHILocationConflictCollapsableLayer.h"

@interface EHILocationsMapFiltersTipView ()
@property (weak, nonatomic) IBOutlet UILabel *tipTitleLabel;
@property (weak, nonatomic) IBOutlet UILabel *tipDetailsLabel;
@property (weak, nonatomic) IBOutlet UIButton *closeButton;

@property (weak, nonatomic) IBOutlet NSLayoutConstraint *tipTitleTopConstraint;
@end

@implementation EHILocationsMapFiltersTipView

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    self.tipLayer.arrowHeight = self.tipTitleTopConstraint.constant/2;
    self.tipLayer.padding     = self.tipTitleTopConstraint.constant * 2;
    self.tipLayer.fillColor   = [[UIColor blackColor] colorWithAlphaComponent:0.75].CGColor;
    self.tipLayer.lineWidth   = 1.0f;
    
    self.tipTitleLabel.text   = EHILocalizedString(@"locations_map_filter_tip_title", @"TIP:", @"");
    self.tipDetailsLabel.text = EHILocalizedString(@"locations_map_filter_tip_detail", @"Try it! Filter by your travel times!", @"");
    
    UIImage *image = [[UIImage imageNamed:@"icon_x_gray_02"] imageWithRenderingMode:UIImageRenderingModeAlwaysTemplate];
    [self.closeButton setImage:image forState:UIControlStateNormal];
    [self.closeButton setTintColor:[UIColor whiteColor]];
    [self.closeButton setImageEdgeInsets:UIEdgeInsetsMake(5.0f, 5.0f, 5.0f, 5.0f)];
}

# pragma mark - Actions

- (IBAction)didTapClose:(UIButton *)button
{
    [self ehi_performAction:@selector(filterTipDidTapClose:) withSender:self];
}

# pragma mark - Layer

- (void)setArrowHeight:(CGFloat)arrowHeight
{
    self.tipLayer.arrowHeight = arrowHeight;
}

- (void)setPadding:(CGFloat)padding
{
    self.tipLayer.padding = padding;
}

- (EHILocationConflictCollapsableLayer *)tipLayer
{
    return (EHILocationConflictCollapsableLayer *)self.layer;
}

+ (Class)layerClass
{
    return EHILocationConflictCollapsableLayer.class;
}

+ (BOOL)isReplaceable
{
    return YES;
}

@end
