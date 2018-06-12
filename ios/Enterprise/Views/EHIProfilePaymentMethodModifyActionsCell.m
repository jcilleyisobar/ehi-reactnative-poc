//
//  EHIProfilePaymentMethodModifyActionsCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 9/27/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIProfilePaymentMethodModifyActionsCell.h"
#import "EHIProfilePaymentMethodModifyActionsViewModel.h"
#import "EHIButton.h"
#import "EHIRestorableConstraint.h"

@interface EHIProfilePaymentMethodModifyActionsCell ()
@property (strong, nonatomic) EHIProfilePaymentMethodModifyActionsViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIView *containerView;
@property (weak  , nonatomic) IBOutlet UILabel *paymentTitleLabel;
@property (weak  , nonatomic) IBOutlet EHIButton *editButton;
@property (weak  , nonatomic) IBOutlet EHIButton *deleteButton;

@property (weak  , nonatomic) IBOutlet UIView *preferredContainerView;
@property (weak  , nonatomic) IBOutlet UILabel *preferredLabel;
@property (weak, nonatomic) IBOutlet EHIRestorableConstraint *creditCardWidthConstraint;
@property (weak, nonatomic) IBOutlet UIImageView *cardImage;
@property (weak  , nonatomic) IBOutlet UIView *expiredContainerView;
@property (weak  , nonatomic) IBOutlet UILabel *expiredLabel;
@end

@implementation EHIProfilePaymentMethodModifyActionsCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIProfilePaymentMethodModifyActionsViewModel new];
    }
    
    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIProfilePaymentMethodModifyActionsViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidatePreferred:)];
    [MTRReactor autorun:self action:@selector(invalidateExpired:)];
    [MTRReactor autorun:self action:@selector(invalidateCardImage:)];
    
    model.bind.map(@{
        source(model.paymentTitle)  : dest(self, .paymentTitleLabel.text),
        source(model.preferedTitle) : dest(self, .preferredLabel.text),
        source(model.expiredTitle)  : dest(self, .expiredLabel.text),
        source(model.editTitle)     : dest(self, .editButton.ehi_title)
    });
}

- (void)invalidatePreferred:(MTRComputation *)computation
{
    BOOL isPrefered = self.viewModel.isPreferred;
    
    MASLayoutPriority priority = isPrefered ? MASLayoutPriorityDefaultLow : MASLayoutPriorityRequired;
    
    [self.preferredContainerView mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@0.0).priority(priority);
    }];
}

- (void)invalidateExpired:(MTRComputation *)computation
{
    BOOL isExpired = self.viewModel.isExpired;
    
    MASLayoutPriority priority = isExpired ? MASLayoutPriorityDefaultLow : MASLayoutPriorityRequired;
    
    [self.expiredContainerView mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@0.0).priority(priority);
    }];
}

- (void)invalidateCardImage:(MTRComputation *)computation
{
    NSString *cardImage = self.viewModel.cardImage;
    self.cardImage.ehi_imageName = cardImage;
    self.creditCardWidthConstraint.isDisabled = (cardImage == nil);
}

# pragma mark - Actions

- (IBAction)didTapEdit:(EHIButton *)sender
{
    [self ehi_performAction:@selector(didTapEditPayment:) withSender:self];
}

- (IBAction)didTapDelete:(EHIButton *)sender
{
    [self ehi_performAction:@selector(didTapDeletePayment:) withSender:self];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetHeight(self.containerView.frame) + EHIHeaviestPadding
    };
}

@end
