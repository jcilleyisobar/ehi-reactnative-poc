//
//  EHIConfirmationPaymentOptionCell.m
//  Enterprise
//
//  Created by Michael Place on 7/7/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIConfirmationPaymentOptionCell.h"
#import "EHIConfirmationPaymentOptionViewModel.h"
#import "EHIButton.h"
#import "EHIRestorableConstraint.h"

@interface EHIConfirmationPaymentOptionCell ()
@property (strong, nonatomic) EHIConfirmationPaymentOptionViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIView *contentContainer;
@property (weak  , nonatomic) IBOutlet UIView *termsContainer;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *paymentTitleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *valueLabel;
@property (weak  , nonatomic) IBOutlet EHIButton *policiesButton;
@property (weak  , nonatomic) IBOutlet UIImageView *cardImage;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *creditCardWidthConstraint;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *termsHeightConstraint;
@end

@implementation EHIConfirmationPaymentOptionCell

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIConfirmationPaymentOptionViewModel new];
    }
    
    return self;
}

# pragma mark - Accessibility

- (void)registerAccessibilityIdentifiers
{
    [super registerAccessibilityIdentifiers];
    
    self.paymentTitleLabel.accessibilityIdentifier = EHIConfirmationPaymentMethodKey;
    self.valueLabel.accessibilityIdentifier = EHIConfirmationPaymentDetailsKey;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIConfirmationPaymentOptionViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.title)             : dest(self, .titleLabel.text),
        source(model.paymentTitle)      : dest(self, .paymentTitleLabel.text),
        source(model.value)             : dest(self, .valueLabel.text),
        source(model.policies)          : dest(self, .policiesButton.ehi_title),
        source(model.hidePoliciesLink)  : dest(self, .termsHeightConstraint.isDisabled)
    });
    
    [MTRReactor autorun:^(MTRComputation *computation) {
        NSString *cardImage = model.cardImage;
        
        self.cardImage.ehi_imageName = cardImage;
        self.creditCardWidthConstraint.isDisabled = cardImage == nil;
    }];
}

# pragma mark - Actions

- (IBAction)didTapTermsButton:(id)sender
{
    [self.viewModel showPolicies];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    UIView *view = self.viewModel.hidePoliciesLink ? self.valueLabel : self.termsContainer;
    CGRect bottomFrame = [view convertRect:view.bounds toView:self];
    
    return (CGSize) {
        .width = EHILayoutValueNil,
        .height = CGRectGetMaxY(bottomFrame) + EHIMediumPadding
    };

}

@end
