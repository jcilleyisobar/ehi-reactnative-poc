//
//  EHISettingsPrivacyCell.m
//  Enterprise
//
//  Created by Alex Koller on 6/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHISettingsControlCell.h"
#import "EHISettingsControlViewModel.h"
#import "EHIRestorableConstraint.h"

NS_ASSUME_NONNULL_BEGIN

@interface EHISettingsControlCell ()
@property (strong, nonatomic) EHISettingsControlViewModel *viewModel;
@property (strong, nonatomic) UITapGestureRecognizer *infoTapGesture;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *subtitleLabel;
@property (weak  , nonatomic) IBOutlet UIImageView *arrow;
@property (weak  , nonatomic) IBOutlet UISwitch *toggle;
@property (weak  , nonatomic) IBOutlet UIView *infoContainer;
@property (weak  , nonatomic) IBOutlet UIView *toggleContainer;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *infoContainerWidth;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *toggleContainerWidth;
@property (weak  , nonatomic) IBOutlet UIView *detailContainer;
@property (weak  , nonatomic) IBOutlet UILabel *detailLabel;
@end

@implementation EHISettingsControlCell

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    self.infoTapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(didTapInfoContainer:)];
    [self.infoContainer addGestureRecognizer:self.infoTapGesture];
}

# pragma mark - Reactions

- (void)registerReactions:(EHISettingsControlViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateSubtitle:)];
    [MTRReactor autorun:self action:@selector(invalidateAction:)];
    [MTRReactor autorun:self action:@selector(invalidateDetailVisibility:)];
    [MTRReactor autorun:self action:@selector(invalidateToggleVisibility:)];
    
    model.bind.map(@{
        source(model.title)             : dest(self, .titleLabel.text),
        source(model.detailsAttributed) : dest(self, .detailLabel.attributedText),
        source(model.hidesArrow)        : dest(self, .arrow.hidden),
        source(model.toggleEnabled)     : dest(self, .toggle.on),
    });
}

- (void)invalidateSubtitle:(MTRComputation *)computation
{
    NSString *subtitle = self.viewModel.subtitle;
    CGFloat titleSize  = subtitle ? 16 : 18;
    
    self.titleLabel.font = [UIFont ehi_fontWithStyle:EHIFontStyleLight size:titleSize];
    self.subtitleLabel.text = subtitle;
}

- (void)invalidateAction:(MTRComputation *)computation
{
    BOOL isAction = self.viewModel.isAction;
    
    self.titleLabel.textColor = isAction ? [UIColor ehi_greenColor] : [UIColor ehi_blackColor];
}

- (void)invalidateDetailVisibility:(MTRComputation *)computation
{
    BOOL showsDetail = self.viewModel.hidesDetailIcon;
    
    // don't respond to taps if details are showing
    self.infoTapGesture.enabled = !showsDetail;
    
    // hide detail info icon
    self.infoContainerWidth.isDisabled = showsDetail;
}

- (void)invalidateToggleVisibility:(MTRComputation *)computation
{
    BOOL hidesToggle = self.viewModel.hidesToggle;
    
    self.toggle.hidden = hidesToggle;
    self.toggleContainerWidth.isDisabled = hidesToggle;
}

# pragma mark - Actions

- (void)didTapInfoContainer:(id)sender
{
    [self.viewModel showDetailsModal];
}

- (IBAction)didTapSwitch:(UISwitch *)sender
{
    [self.viewModel enableToggle:sender.on];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    UIView *bottomView = self.viewModel.hidesDetails ? self.toggleContainer : self.detailContainer;
    
    return (CGSize) {
        .width = EHILayoutValueNil,
        .height = CGRectGetMaxY(bottomView.frame)
    };
}

@end

NS_ASSUME_NONNULL_END