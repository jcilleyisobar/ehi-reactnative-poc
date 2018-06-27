//
//  EHIReviewPaymentChangeCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 10/14/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIReviewPaymentChangeCell.h"
#import "EHIReviewPaymentChangeViewModel.h"
#import "EHIButton.h"

@interface EHIReviewPaymentChangeCell ()
@property (strong, nonatomic) EHIReviewPaymentChangeViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIView *bottomDivider;
@property (weak  , nonatomic) IBOutlet UIView *cardIconContainer;
@property (weak  , nonatomic) IBOutlet EHIButton *paymentButton;
@end

@implementation EHIReviewPaymentChangeCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIReviewPaymentChangeViewModel new];
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    self.paymentButton.titleLabel.numberOfLines = 0;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIReviewPaymentChangeViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateCardContainer:)];
    
    model.bind.map(@{
        source(model.title) : dest(self, .paymentButton.ehi_title)
    });
}

- (void)invalidateCardContainer:(MTRComputation *)computation
{
    BOOL hide = self.viewModel.hideCardIcon;
    
    MASLayoutPriority priority = hide ? MASLayoutPriorityRequired : MASLayoutPriorityDefaultLow;
    
    [self.cardIconContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.width.equalTo(@0.0f).priority(priority);
    }];
}

# pragma mark - Actions

- (IBAction)didTapChangePayment:(id)sender
{
    [self.viewModel didTapChangePayment];
    [self ehi_performAction:@selector(didTapChangePayment:) withSender:self];
}

- (IBAction)didTapMoreInfo:(id)sender
{
    [self.viewModel didTapMoreInfo];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.bottomDivider.frame)
    };
}

@end
