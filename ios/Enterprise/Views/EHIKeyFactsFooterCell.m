//
//  EHIKeyFactsFooterCell.m
//  Enterprise
//
//  Created by fhu on 11/4/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHIKeyFactsFooterCell.h"
#import "EHIKeyFactsFooterViewModel.h"
#import "EHIRestorableConstraint.h"

@interface EHIKeyFactsFooterCell()
@property (strong, nonatomic) EHIKeyFactsFooterViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UILabel *headerLabel;
@property (weak  , nonatomic) IBOutlet UILabel *subHeaderLabel;
@property (weak  , nonatomic) IBOutlet UILabel *phoneLinkLabel;
@property (weak  , nonatomic) IBOutlet UILabel *emailLinkLabel;
@property (weak  , nonatomic) IBOutlet UILabel *footerLabel;
@property (weak  , nonatomic) IBOutlet UILabel *footerLinkLabel;
@property (weak  , nonatomic) IBOutlet UIView *subHeaderContainer;
@property (weak  , nonatomic) IBOutlet UIView *footerContainer;
@property (weak  , nonatomic) IBOutlet UIView *emailContainer;
@property (weak  , nonatomic) IBOutlet UIView *phoneContainer;

@property (assign  , nonatomic)  BOOL shouldHideTopDivider;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *topDividerHightConstraint;
@end

@implementation EHIKeyFactsFooterCell

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIKeyFactsFooterViewModel new];
    }
    
    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIKeyFactsFooterViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.headerText)           : dest(self, .headerLabel.text),
        source(model.subHeaderText)        : dest(self, .subHeaderLabel.text),
        source(model.phoneLinkText)        : dest(self, .phoneLinkLabel.text),
        source(model.emailLinkText)        : dest(self, .emailLinkLabel.text),
        source(model.footerText)           : dest(self, .footerLabel.text),
        source(model.footerLinkText)       : dest(self, .footerLinkLabel.text),
        source(model.shouldHideTopDivider) : dest(self, .shouldHideTopDivider)
    });
    
    [MTRReactor autorun:self action:@selector(invalidateContent:)];
    
    if(self.shouldHideTopDivider) {
        self.topDividerHightConstraint.isDisabled = YES;
    }
}

- (void)invalidateContent:(MTRComputation *)computation
{
    MASLayoutPriority constraintPriority = self.viewModel.phoneLinkText.length ? MASLayoutPriorityDefaultLow : MASLayoutPriorityRequired;
    [self.phoneContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@(0.0f)).with.priority(constraintPriority);
    }];
    
    constraintPriority = self.viewModel.emailLinkText.length ? MASLayoutPriorityDefaultLow : MASLayoutPriorityRequired;
    [self.emailContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@(0.0f)).with.priority(constraintPriority);
    }];
    
    constraintPriority = self.viewModel.headerText.length ? MASLayoutPriorityDefaultLow : MASLayoutPriorityRequired;
    [self.subHeaderContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@(0.0f)).with.priority(constraintPriority);
    }];
}

# pragma mark - Actions

- (IBAction)didTapNumberLink:(id)sender
{
    [self.viewModel phoneTapped];
}

- (IBAction)didTapEmailLink:(id)sender
{
    [self.viewModel emailTapped];
}

- (IBAction)didTapFooterLink:(id)sender
{
    [self.viewModel footerTapped];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize){
        .width = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.footerContainer.frame)
    };
}

@end
