//
//  EHIReviewPaymentMethodCell.m
//  Enterprise
//
//  Created by Stu Buchbinder on 11/1/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIReviewPaymentMethodCell.h"
#import "EHIReviewPaymentMethodViewModel.h"
#import "EHIButton.h"

@interface EHIReviewPaymentMethodCell()
@property (strong, nonatomic) EHIReviewPaymentMethodViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *paymentTitleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *subtitleLabel;
@property (weak  , nonatomic) IBOutlet UIImageView *cardImage;
@property (weak  , nonatomic) IBOutlet EHIButton *termsButton;
@property (weak  , nonatomic) IBOutlet UIView *termsContainer;
@property (weak  , nonatomic) IBOutlet UIView *termsToggleContainer;
@property (weak  , nonatomic) IBOutlet UIButton *readTermsToggle;
@property (weak  , nonatomic) IBOutlet UILabel *termsLabel;
@property (weak  , nonatomic) IBOutlet UIImageView *arrowButton;
@property (weak  , nonatomic) IBOutlet UIView *dividerView;
@end

@implementation EHIReviewPaymentMethodCell

#pragma mark - Accessibility

- (void)registerAccessibilityIdentifiers
{
    [super registerAccessibilityIdentifiers];
    
    self.paymentTitleLabel.accessibilityIdentifier = EHIConfirmationPaymentMethodKey;
}

#pragma mark - Reactions

- (void)registerReactions:(EHIReviewPaymentMethodViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateTermsToggle:)];
    [MTRReactor autorun:self action:@selector(invalidateViewForCorpFlow:)];
    
    model.bind.map(@{
        source(model.title)                 : dest(self, .titleLabel.text),
        source(model.paymentTitle)          : dest(self, .paymentTitleLabel.text),
        source(model.subtitle)              : dest(self, .subtitleLabel.text),
        source(model.terms)                 : dest(self, .termsButton.ehi_title),
        source(model.readTermsTitle)        : dest(self, .termsLabel.attributedText),
        source(model.readTerms)             : dest(self, .readTermsToggle.selected),
        source(model.isCorporateFlow)       : dest(self, .paymentTitleLabel.hidden),
        source(model.cardImage)             : dest(self, .cardImage.ehi_imageName)
    });
}

- (void)invalidateTermsToggle:(MTRComputation *)computation
{
    BOOL hide = !self.viewModel.showTermsToggle;
    
    MASLayoutPriority priority = hide ? MASLayoutPriorityRequired : MASLayoutPriorityDefaultLow;
    
    [self.termsToggleContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@0.0f).priority(priority);
    }];
}

-(void)invalidateViewForCorpFlow:(MTRComputation *)computation
{
    BOOL isCorporateFlow = self.viewModel.isCorporateFlow;
    if (isCorporateFlow) {
        [self.cardImage mas_remakeConstraints:^(MASConstraintMaker *make) {
            make.height.equalTo(@0.0f).priority(MASLayoutPriorityRequired);
        }];
        [self.arrowButton mas_remakeConstraints:^(MASConstraintMaker *make) {
            make.height.equalTo(@0.0f).priority(MASLayoutPriorityRequired);
        }];
        [self.termsContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
            make.height.equalTo(@0.0f).priority(MASLayoutPriorityRequired);
        }];
        [self.dividerView mas_remakeConstraints:^(MASConstraintMaker *make) {
            make.height.equalTo(@0.0f).priority(MASLayoutPriorityRequired);
        }];
    }
}

#pragma mark - Actions

-(IBAction)didTapTermsButton:(id)sender
{
    [self.viewModel showTerms];
}

- (IBAction)didTapReadTerms:(UIButton *)sender
{
    [self ehi_performAction:@selector(reviewPaymentMethodDidToggleReadTerms:) withSender:self];
}

- (IBAction)didTap:(UIControl *)sender
{
    BOOL shouldHandleTouches = self.viewModel.shouldHandleTouches;
    if(shouldHandleTouches) {
        [self ehi_performAction:@selector(reviewPaymentMethodDidTap:) withSender:self];
    }
}

#pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.termsToggleContainer.frame) + EHILightPadding
    };
}

@end
