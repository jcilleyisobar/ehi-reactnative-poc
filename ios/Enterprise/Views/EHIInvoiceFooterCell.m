//
//  EHIInvoiceFooterCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 7/20/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIInvoiceFooterCell.h"
#import "EHIInvoiceFooterViewModel.h"
#import "EHIRestorableConstraint.h"

@interface EHIInvoiceFooterCell ()
@property (strong, nonatomic) EHIInvoiceFooterViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UILabel *deductionLabel;
@property (weak  , nonatomic) IBOutlet UILabel *vatNumberLabel;
@property (weak  , nonatomic) IBOutlet UILabel *invoiceNumberLabel;
@property (weak  , nonatomic) IBOutlet UILabel *enterpriseBrandNameLabel;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *invoiceTopConstraint;
@end

@implementation EHIInvoiceFooterCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIInvoiceFooterViewModel new];
    }

    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIInvoiceFooterViewModel *)model
{
    [super registerReactions:model];

    [MTRReactor autorun:self action:@selector(invalidateLayout:)];
    
    model.bind.map(@{
        source(model.deductionMessage)    : dest(self, .deductionLabel.text),
        source(model.varNumber)           : dest(self, .vatNumberLabel.text),
        source(model.invoiceNumber)       : dest(self, .invoiceNumberLabel.text),
        source(model.enterpriseBrandName) : dest(self, .enterpriseBrandNameLabel.text)
    });
}

- (void)invalidateLayout:(MTRComputation *)computation
{
    BOOL disable = self.viewModel.varNumber == nil;
    
    self.invoiceTopConstraint.isDisabled = disable;
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.enterpriseBrandNameLabel.frame) + EHIMediumPadding * 4
    };
}

@end
