//
//  EHIReviewPrepayPaymentCell.m
//  Enterprise
//
//  Created by cgross on 1/18/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIReviewPrepayPaymentCell.h"
#import "EHIReviewPrepayPaymentViewModel.h"
#import "EHIButton.h"
#import "EHIWebViewModel.h"

@interface EHIReviewPrepayPaymentCell ()
@property (strong, nonatomic) EHIReviewPrepayPaymentViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet EHIButton *removeButton;
@property (weak  , nonatomic) IBOutlet UILabel *creditCardAddedLabel;
@property (weak  , nonatomic) IBOutlet UIView *creditCardContainer;
@property (weak  , nonatomic) IBOutlet UIView *termsContainer;
@property (weak  , nonatomic) IBOutlet EHIButton *termsButton;
@end

@implementation EHIReviewPrepayPaymentCell

# pragma mark - Reactions

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    self.creditCardContainer.layer.borderWidth = 1;
    self.creditCardContainer.layer.borderColor = [UIColor ehi_grayColor2].CGColor;
}

- (void)registerReactions:(EHIReviewPrepayPaymentViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.prepayTitle)            : dest(self, .titleLabel.text),
        source(model.creditCardButtonTitle)  : dest(self, .creditCardAddedLabel.text),
        source(model.removeCreditCardButton) : dest(self, .removeButton.ehi_title),
        source(model.termsTitle)             : dest(self, .termsButton.ehi_title)
    });
}

# pragma mark - Actions

- (IBAction)didTapPaymentButton:(id)sender
{
    [self ehi_performAction:@selector(didTapAddPrepayPaymentMethodForPrepayPaymentCell:) withSender:self];
}

- (IBAction)didTapTermsButton:(id)sender
{
    [[[EHIWebViewModel alloc] initWithType:EHIWebContentTypePrepayTermsAndConditions] push];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize){
        .width = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.termsContainer.frame) + EHILightPadding
    };
}

@end
