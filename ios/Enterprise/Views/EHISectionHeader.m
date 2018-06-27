//
//  EHISectionHeader.m
//  Enterprise
//
//  Created by mplace on 1/30/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHISectionHeader.h"
#import "EHIButton.h"
#import "EHIRestorableConstraint.h"

@interface EHISectionHeader ()
@property (weak  , nonatomic) IBOutlet UIView *containerView;
@property (strong, nonatomic) UIColor *defaultBackgroundColor;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet UIImageView *iconImageView;
@property (weak  , nonatomic) IBOutlet EHIButton *actionButton;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *iconImageWidth;
@property (weak  , nonatomic) IBOutlet UIView *actionContainer;
@property (weak  , nonatomic) IBOutlet UIView *fancyDivider;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *fancyDividerHeight;
@end

@implementation EHISectionHeader

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    self.defaultBackgroundColor = self.backgroundColor;
}

- (void)updateWithModel:(EHISectionHeaderModel *)model metrics:(EHILayoutMetrics *)metrics
{
    [super updateWithModel:model metrics:metrics];
 
    // update data outlets
    if(model.title) {
        self.titleLabel.text = model.title;
    } else {
        self.titleLabel.attributedText = model.attributedTitle;
    }
   
    if(model.style & EHISectionHeaderStyleImage) {
        [self.iconImageView setImage:[UIImage imageNamed:model.iconName]];
    }
	
	if(model.style & EHISectionHeaderStyleAction) {
        self.actionButton.ehi_title = model.actionButtonTitle;
	}

    // show/hide UI elements based on our style
    self.divider.hidden            = !(model.style & EHISectionHeaderStyleDivider);
    self.iconImageWidth.isDisabled = !(model.style & EHISectionHeaderStyleImage);
    
    MASLayoutPriority priority = !(model.style & EHISectionHeaderStyleAction) ? MASLayoutPriorityRequired : MASLayoutPriorityDefaultLow;
    
    [self.actionContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.width.equalTo(@0.0).priority(priority);
    }];
    
    // update other styling properties
    self.backgroundColor          = model.backgroundColor ?: self.defaultBackgroundColor;
    self.titleLabel.numberOfLines = model.style & EHISectionHeaderStyleWrapText ? 0 : 1;
    
    // update divider styling
    BOOL isFancy = model.dividerStyle == EHISectionHeaderDividerStyleFancy;
    self.fancyDividerHeight.isDisabled = !isFancy;
    
    if(metrics.primaryFont) {
        self.titleLabel.font = metrics.primaryFont;
    }
    
    if(metrics.secondaryFont) {
        self.actionButton.titleLabel.font = metrics.secondaryFont;
    }
}

# pragma mark - View Actions

- (IBAction)didTapActionButton:(id)sender
{
    [self ehi_performAction:@selector(sectionHeaderDidTapActionButton:) withSender:self];
}

# pragma mark - Layout

+ (EHILayoutMetrics *)defaultMetrics
{
    EHILayoutMetrics *metrics = [super defaultMetrics];
    metrics.fixedSize = (CGSize){ .width = EHILayoutValueNil, .height = 36.0f };
    return metrics;
}

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.containerView.frame) + EHIHeaviestPadding
    };
}

@end
