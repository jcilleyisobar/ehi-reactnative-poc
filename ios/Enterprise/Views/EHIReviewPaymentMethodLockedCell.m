//
//  EHIReviewPaymentMethodLockedCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 12/1/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIReviewPaymentMethodLockedCell.h"
#import "EHIReviewPaymentMethodLockedViewModel.h"
#import "EHIButton.h"

@interface EHIReviewPaymentMethodLockedCell ()
@property (strong, nonatomic) EHIReviewPaymentMethodLockedViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UILabel *paymentTitleLabel;
@property (weak  , nonatomic) IBOutlet EHIButton *termsButton;
@end

@implementation EHIReviewPaymentMethodLockedCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIReviewPaymentMethodLockedViewModel new];
    }
    
    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIReviewPaymentMethodLockedViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.paymentTitle) : dest(self, .paymentTitleLabel.text),
        source(model.termsTitle)   : dest(self, .termsButton.ehi_title)
    });
}

# pragma mark - Actions

- (IBAction)didTapTerms:(EHIButton *)sender
{
    [self.viewModel showTerms];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.termsButton.frame) + EHIMediumPadding
    };
}

@end
