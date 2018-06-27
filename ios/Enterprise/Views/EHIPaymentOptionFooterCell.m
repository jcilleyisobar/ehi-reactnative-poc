//
//  EHIPaymentOptionFooterCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 10/10/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIPaymentOptionFooterCell.h"
#import "EHIPaymentOptionFooterViewModel.h"
#import "EHIButton.h"

@interface EHIPaymentOptionFooterCell ()
@property (strong, nonatomic) EHIPaymentOptionFooterViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIButton *prepayButton;
@property (weak  , nonatomic) IBOutlet EHIButton *termsButton;
@end

@implementation EHIPaymentOptionFooterCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIPaymentOptionFooterViewModel new];
    }

    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIPaymentOptionFooterViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.prepayTitle) : dest(self, .prepayButton.ehi_title),
        source(model.termTitle) : dest(self, .termsButton.ehi_title),
    });
}

# pragma mark - Actions

- (IBAction)didTapPrepay:(UIButton *)sender
{
    [self.viewModel showPrepay];
}

- (IBAction)didTapTerms:(UIButton *)sender
{
    [self.viewModel showTerms];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.prepayButton.frame) + EHIHeaviestPadding
    };
}

@end
