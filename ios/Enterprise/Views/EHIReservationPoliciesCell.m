//
//  EHIReservationPoliciesCell.m
//  Enterprise
//
//  Created by Alex Koller on 4/9/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservationPoliciesCell.h"
#import "EHIReservationPoliciesViewModel.h"
#import "EHIButton.h"
#import "EHIRestorableConstraint.h"

@interface EHIReservationPoliciesCell () <UICollectionViewDelegate>
@property (strong, nonatomic) EHIReservationPoliciesViewModel *viewModel;

@property (weak  , nonatomic) IBOutlet UILabel *headerLabel;
@property (weak  , nonatomic) IBOutlet UILabel *headerDetailsLabel;
@property (weak  , nonatomic) IBOutlet EHIButton *keyFactsButton;
@property (weak  , nonatomic) IBOutlet UILabel *keyFactsDetails;
@property (weak  , nonatomic) IBOutlet EHIButton *policyButton;
@property (weak  , nonatomic) IBOutlet UILabel *policyDetails;

@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *fancyDividerHeightConstraint;
@property (weak  , nonatomic) IBOutlet UIView *topDivider;
@property (weak  , nonatomic) IBOutlet UIView *mainContainer;
@property (weak  , nonatomic) IBOutlet UIView *headerContainer;
@property (weak  , nonatomic) IBOutlet UIView *keyFactsContainer;
@property (weak  , nonatomic) IBOutlet UIView *policiesContainer;
@property (weak  , nonatomic) IBOutlet UIView *policiesButtonContainer;

@end

@implementation EHIReservationPoliciesCell

# pragma mark - Reactions

- (void)registerReactions:(EHIReservationPoliciesViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.headerText)        : dest(self, .headerLabel.text),
        source(model.headerDetails)     : dest(self, .headerDetailsLabel.text),
        source(model.keyFactsButtonText): dest(self, .keyFactsButton.ehi_title),
        source(model.keyFactsDetails)   : dest(self, .keyFactsDetails.attributedText),
        source(model.policyButtonText)  : dest(self, .policyButton.ehi_title),
        source(model.policyDetails)     : dest(self, .policyDetails.text)
    });
    
    [MTRReactor autorun:self action:@selector(invalidateContent:)];
}

- (void)invalidateContent:(MTRComputation *)computation
{
    MASLayoutPriority constraintPriority = self.viewModel.shouldHideKeyFacts ? MASLayoutPriorityRequired : MASLayoutPriorityDefaultLow;
    
    [self.keyFactsContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@(0.0f)).with.priority(constraintPriority);
    }];
    
    constraintPriority = self.viewModel.shouldHidePolicies ? MASLayoutPriorityRequired : MASLayoutPriorityDefaultLow;
    [self.policiesButtonContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@(0.0f)).with.priority(constraintPriority);
    }];
    
    BOOL hideDivider = self.viewModel.hideFancyDivider;
    self.fancyDividerHeightConstraint.isDisabled = hideDivider;
    self.topDivider.hidden = !hideDivider;
}

- (IBAction)selectKeyFacts:(id)sender
{
    [self.viewModel showKeyFacts];
}

- (IBAction)selectPolicies:(id)sender
{
    [self.viewModel showPolicies];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.mainContainer.frame)
    };
}

@end
