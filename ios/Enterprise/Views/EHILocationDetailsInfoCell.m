//
//  EHILocationDetailsInfoCell.m
//  Enterprise
//
//  Created by Ty Cobb on 2/25/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILocationDetailsInfoCell.h"
#import "EHILocationDetailsInfoViewModel.h"
#import "EHIButton.h"
#import "EHILabel.h"
#import "EHIRestorableConstraint.h"

@interface EHILocationDetailsInfoCell ()
@property (nonatomic, readonly) EHILocationDetailsInfoViewModel *viewModel;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet EHILabel *addressLabel;
@property (weak, nonatomic) IBOutlet EHIButton *phoneNumberButton;
@property (weak, nonatomic) IBOutlet EHIButton *directionsButton;
@property (weak, nonatomic) IBOutlet EHIButton *wayfindingButton;
@property (weak, nonatomic) IBOutlet EHIButton *favoritesButton;
@property (weak, nonatomic) IBOutlet EHIRestorableConstraint *favoritesButtonWidth;
@property (weak, nonatomic) IBOutlet EHIRestorableConstraint *exoticsImageViewHeight;

@property (weak, nonatomic) IBOutlet UIView *afterHoursContainerView;
@property (weak, nonatomic) IBOutlet EHILabel *afterHoursLabel;

@property (nonatomic, readonly) UIView *bottomView;
@end

@implementation EHILocationDetailsInfoCell

- (id)initWithCoder:(NSCoder *)decoder
{
    if(self = [super initWithCoder:decoder]) {
        self.viewModel = [EHILocationDetailsInfoViewModel new];
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
  
    // set the type of the buttonst
    self.favoritesButton.type  = EHIButtonTypeFavorite;
    self.directionsButton.type = EHIButtonTypeDirections;
    self.addressLabel.copyable = YES;
    
    self.directionsButton.imageHorizontalAlignment = UIControlContentHorizontalAlignmentLeft;
    self.directionsButton.titleLabel.textAlignment = NSTextAlignmentCenter;
    
    self.wayfindingButton.imageHorizontalAlignment = UIControlContentHorizontalAlignmentLeft;
    self.wayfindingButton.titleLabel.textAlignment = NSTextAlignmentCenter;
}

- (void)updateConstraints
{
    [super updateConstraints];
   
    self.favoritesButtonWidth.isDisabled = !self.viewModel.isOnBrand;
}

- (void)updateWithModel:(EHILocation *)model metrics:(EHILayoutMetrics *)metrics
{
    [super updateWithModel:model metrics:metrics];

    // animate the wayfinding button in if necessary
    CGFloat wayfindingAlpha = self.viewModel.hasWayfindingDirections ? 1.0f : 0.0f;
    if(self.wayfindingButton.alpha != wayfindingAlpha) {
        UIView.animate(wayfindingAlpha == 1.0f).duration(0.2).transform(^{
            self.wayfindingButton.alpha = wayfindingAlpha;
        }).start(nil);
    }
}

# pragma mark - Reactions

- (void)registerReactions:(EHILocationDetailsInfoViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateAfterHours:)];

    model.bind.map(@{
        source(model.title)           : dest(self, .titleLabel.attributedText),
        source(model.address)         : dest(self, .addressLabel.text),
        source(model.isFavorited)     : dest(self, .favoritesButton.selected),
        source(model.favoritesTitle)  : dest(self, .favoritesButton.ehi_title),
        source(model.phoneNumber)     : dest(self, .phoneNumberButton.ehi_title),
        source(model.directionsTitle) : dest(self, .directionsButton.ehi_title),
        source(model.wayfindingTitle) : dest(self, .wayfindingButton.ehi_title),
        source(model.afterHoursTitle) : dest(self, .afterHoursLabel.attributedText),
        source(model.hideExotics)     : dest(self, .exoticsImageViewHeight.isDisabled)
    });
}

- (void)invalidateAfterHours:(MTRComputation *)computation
{
    BOOL hide = self.viewModel.hideAfterHours;
    
    MASLayoutPriority priority = hide ? MASLayoutPriorityRequired : MASLayoutPriorityDefaultLow;
    
    [self.afterHoursContainerView mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@0.0f).priority(priority);
    }];
}

# pragma mark - Interface Actions

- (IBAction)didTapFavoritesButton:(UIButton *)button
{
    [self.viewModel toggleIsFavorited];
}

- (IBAction)didTapDirectionsFromTerminal:(id)sender
{
    [self.viewModel showDirectionsFromTerminal];
}

- (IBAction)didTapGetDirectionsButton:(id)sender
{
    [self.viewModel showDirections];
}

- (IBAction)didTapPhoneNumberButton:(id)sender
{
    [self.viewModel callLocation];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.bottomView.frame) + EHIMediumPadding
    };
}

- (UIView *)bottomView
{
    return self.viewModel.hasWayfindingDirections ? self.wayfindingButton : self.directionsButton;
}

@end
