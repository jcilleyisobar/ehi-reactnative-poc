//
//  EHISelectPaymentItemCell.m
//  Enterprise
//
//  Created by Stu Buchbinder on 10/26/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHISelectPaymentItemCell.h"
#import "EHISelectPaymentItemViewModel.h"
#import "EHILabel.h"
#import "EHIButton.h"
#import "EHIRestorableConstraint.h"

@interface EHISelectPaymentItemCell()
@property (strong, nonatomic) EHISelectPaymentItemViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIView *bottomDivider;
@property (weak  , nonatomic) IBOutlet EHIButton *editButton;
@property (weak  , nonatomic) IBOutlet UIButton *selectToggleButton;
@property (weak  , nonatomic) IBOutlet EHILabel *aliasLabel;

@property (weak  , nonatomic) IBOutlet EHILabel *paymentTitleLabel;
@property (weak  , nonatomic) IBOutlet EHILabel *paymentSubtitleLabel;
@property (weak  , nonatomic) IBOutlet UIImageView *cardImage;

@property (weak  , nonatomic) IBOutlet UIView *saveContainerView;
@property (weak  , nonatomic) IBOutlet UIButton *saveToggleButton;
@property (weak  , nonatomic) IBOutlet UILabel *saveLabel;

@end

@implementation EHISelectPaymentItemCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if (self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHISelectPaymentItemViewModel new];
    }
    return self;
}

#pragma mark - Layout

- (void)willMoveToSuperview:(UIView *)newSuperview
{
    [super willMoveToSuperview:newSuperview];
    
    self.paymentTitleLabel.disablesAutoShrink    = YES;
    self.paymentSubtitleLabel.disablesAutoShrink = YES;
}

#pragma mark - Reactions

- (void)registerReactions:(EHISelectPaymentItemViewModel *)model
{
    [super registerReactions:model];

    [MTRReactor autorun:self action:@selector(invalidateSaveContainer:)];
    
    model.bind.map(@{
        source(model.aliasTitle)   : dest(self, .aliasLabel.text),
        source(model.isSelected)   : dest(self, .selectToggleButton.selected),
        source(model.isSaved)      : dest(self, .saveToggleButton.selected),
        source(model.saveTitle)    : dest(self, .saveLabel.text),
        source(model.paymentTitle) : dest(self, .paymentTitleLabel.text),
        source(model.expiredTitle) : dest(self, .paymentSubtitleLabel.text),
        source(model.editTitle)    : dest(self, .editButton.ehi_title),
        source(model.cardImage)    : dest(self, .cardImage.ehi_imageName)
    });
}

- (void)invalidateSaveContainer:(MTRComputation *)computation
{
    BOOL hide = !self.viewModel.showSaveToggle;
    
    MASLayoutPriority priority = hide ? MASLayoutPriorityRequired : MASLayoutPriorityDefaultLow;

    [self setNeedsUpdateConstraints];
    [self.saveContainerView mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@0.0f).priority(priority);
    }];

    [UIView animateWithDuration:0.3f animations:^{
        [self layoutIfNeeded];
    }];
}

#pragma mark - Actions

- (IBAction)didTapPaymentToggle:(UIButton *)sender
{
    [self ehi_performAction:@selector(didTapPaymentToggle:) withSender:self];
}

- (IBAction)didTapSaveToggle:(UIButton *)sender
{
    [self.viewModel toggleSave];
}

- (IBAction)didTapEditButton:(id)sender
{
    [self.viewModel editPayment];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.saveContainerView.frame) + EHIMediumPadding
    };
}

@end
