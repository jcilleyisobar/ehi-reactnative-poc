//
//  EHISelectPaymentFooterCell.m
//  Enterprise
//
//  Created by Stu Buchbinder on 10/27/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHISelectPaymentFooterCell.h"
#import "EHISelectPaymentFooterViewModel.h"
#import "EHIClickableLabel.h"
#import "EHIToggleButton.h"
#import "EHIButton.h"

@interface EHISelectPaymentFooterCell ()
@property (strong, nonatomic) EHISelectPaymentFooterViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIButton *continueButton;
@property (weak  , nonatomic) IBOutlet EHILabel *termsLabel;
@property (weak  , nonatomic) IBOutlet EHIToggleButton *termsToggle;
@property (weak  , nonatomic) IBOutlet UIView *termsContainerView;
@end

@implementation EHISelectPaymentFooterCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if (self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHISelectPaymentFooterViewModel new];
    }
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    self.termsToggle.style = EHIToggleButtonStyleWhite;
}

#pragma mark - Reactions

- (void)registerReactions:(EHISelectPaymentFooterViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateTermsContainer:)];
    
    model.bind.map(@{
        source(model.termsRead)              : dest(self, .termsToggle.selected),
        source(model.terms)                  : dest(self, .termsLabel.attributedText),
        source(model.continueTitle)          : dest(self, .continueButton.ehi_title),
        source(model.continueButtonDisabled) : dest(self, .continueButton.isFauxDisabled)
    });
}

- (void)invalidateTermsContainer:(MTRComputation *)computation
{
    MASLayoutPriority priority = self.viewModel.showTerms ? MASLayoutPriorityDefaultLow : MASLayoutPriorityRequired;
    
    [self.termsContainerView mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@(0)).priority(priority);
    }];
    
    [UIView animateWithDuration:0.3 animations:^{
        [self layoutIfNeeded];
    }];
}

#pragma mark - Actions

- (IBAction)didTapContinue:(id)sender
{
    [self.viewModel didTapContinue];
    
    [self ehi_performAction:@selector(didTapContinue:) withSender:self];
}

- (IBAction)didTapTermsToggle:(id)sender
{
    [self.viewModel toggleTermsRead];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height =  CGRectGetMaxY(self.continueButton.frame) + EHIMediumPadding
    };
}

@end
