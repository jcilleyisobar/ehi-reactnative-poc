//
//  EHIInvoiceRentalInfoCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 7/20/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIInvoiceRentalInfoCell.h"
#import "EHIInvoiceRentalInfoViewModel.h"

@interface EHIInvoiceRentalInfoCell ()
@property (strong, nonatomic) EHIInvoiceRentalInfoViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIView *containerView;
@property (weak  , nonatomic) IBOutlet UILabel *rentalDateLabel;
@property (weak  , nonatomic) IBOutlet UILabel *rentalNumberLabel;
@property (weak  , nonatomic) IBOutlet UILabel *contractLabel;
@end

@implementation EHIInvoiceRentalInfoCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIInvoiceRentalInfoViewModel new];
    }

    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIInvoiceRentalInfoViewModel *)model
{
    [super registerReactions:model];

    model.bind.map(@{
        source(model.rentalDate)      : dest(self, .rentalDateLabel.text),
        source(model.rentalAgreement) : dest(self, .rentalNumberLabel.text),
        source(model.contractInfo)    : dest(self, .contractLabel.text)
    });
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
