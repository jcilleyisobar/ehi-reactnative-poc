

//
//  EHIKeyFactsContentCell.m
//  Enterprise
//
//  Created by fhu on 11/4/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHIKeyFactsContentCell.h"
#import "EHIKeyFactsContentViewModel.h"
#import "EHILabel.h"
#import "EHIRestorableConstraint.h"

@interface EHIKeyFactsContentCell()
@property (strong, nonatomic) EHIKeyFactsContentViewModel *viewModel;

@property (weak, nonatomic) IBOutlet EHILabel *linkLabel;
@property (weak, nonatomic) IBOutlet EHIRestorableConstraint *arrowWidth;
@property (weak, nonatomic) IBOutlet UILabel *exclusionLabel;
@property (weak, nonatomic) IBOutlet UILabel *contentLabel;
@property (weak, nonatomic) IBOutlet UIView *divider;
@property (weak, nonatomic) IBOutlet EHIRestorableConstraint *blackDividerHeight;
@property (weak, nonatomic) IBOutlet UIView *linkContainer;
@property (weak, nonatomic) IBOutlet UIView *exclusionContainer;
@property (weak, nonatomic) IBOutlet UIView *contentContainer;
@property (weak, nonatomic) IBOutlet UIView *mainContainer;
@end

@implementation EHIKeyFactsContentCell

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    self.linkLabel.insetsForPreferredWidthRelativeToParent = (UIEdgeInsets){ .right = self.arrowWidth.restorableValue };
    self.linkLabel.disablesAutoShrink = YES;
}

- (void)registerReactions:(EHIKeyFactsContentViewModel *)model
{
    model.bind.map(@{
        source(model.linkText)      : dest(self, .linkLabel.attributedText),
        source(model.contentText)   : dest(self, .contentLabel.text),
        source(model.exclusionText) : dest(self, .exclusionLabel.text)
    });
    
    [MTRReactor autorun:self action:@selector(invalidateContent:)];
    [MTRReactor autorun:self action:@selector(invalidateDivider:)];
}

- (void)invalidateDivider:(MTRComputation *)computation
{
    self.blackDividerHeight.isDisabled = !self.viewModel.hasBlackDivider;
}

- (void)invalidateContent:(MTRComputation *)computation
{
    MASLayoutPriority constraintPriority = self.viewModel.linkText ? MASLayoutPriorityDefaultLow : MASLayoutPriorityRequired;
    [self.linkContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@(0.0f)).with.priority(constraintPriority);
    }];
    
    constraintPriority = self.viewModel.hasExclusion ? MASLayoutPriorityDefaultLow : MASLayoutPriorityRequired;
    [self.exclusionContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@(0.0f)).with.priority(constraintPriority);
    }];
    
    constraintPriority = self.viewModel.contentText ? MASLayoutPriorityDefaultLow : MASLayoutPriorityRequired;
    [self.contentContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@(0.0f)).with.priority(constraintPriority);
    }];
}

#pragma mark - Actions

- (IBAction)didSelectLink:(id)sender
{
    [self.viewModel selectLink];
}

- (IBAction)didSelectExclusions:(id)sender
{
    [self.viewModel selectExclusions];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.mainContainer.frame),
    };
}

@end
