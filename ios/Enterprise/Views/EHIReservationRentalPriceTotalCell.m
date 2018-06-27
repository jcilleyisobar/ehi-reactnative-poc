//
//  EHIReservationRentalPriceTotalCell.m
//  Enterprise
//
//  Created by mplace on 3/26/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservationRentalPriceTotalCell.h"
#import "EHIReservationRentalPriceTotalViewModel.h"
#import "EHIRestorableConstraint.h"
#import "EHIButton.h"

@interface EHIReservationRentalPriceTotalCell ()
@property (strong, nonatomic) EHIReservationRentalPriceTotalViewModel *viewModel;

@property (weak, nonatomic) IBOutlet UIView  *topDivider;
@property (weak, nonatomic) IBOutlet UIView  *contentContainer;
@property (weak, nonatomic) IBOutlet UIView  *totalContainer;
@property (weak, nonatomic) IBOutlet UIView  *paymentOptionContainer;
@property (weak, nonatomic) IBOutlet UIView  *transparencyContainer;
@property (weak, nonatomic) IBOutlet UIView  *transparencyAmountContainer;
@property (weak, nonatomic) IBOutlet UIView  *priceDifferenceContainer;

@property (weak, nonatomic) IBOutlet UILabel *totalTitleLabel;
@property (weak, nonatomic) IBOutlet UILabel *totalLabel;
@property (weak, nonatomic) IBOutlet UILabel *transparencyTitleLabel;
@property (weak, nonatomic) IBOutlet UILabel *transparencyLabel;
@property (weak, nonatomic) IBOutlet UILabel *originalTotalLabel;
@property (weak, nonatomic) IBOutlet UILabel *paidAmountLabel;
@property (weak, nonatomic) IBOutlet UILabel *paidAmountTitleLabel;
@property (weak, nonatomic) IBOutlet UILabel *updatedTotalTitleLabel;
@property (weak, nonatomic) IBOutlet UILabel *updatedTotalLabel;
@property (weak, nonatomic) IBOutlet UILabel *endOfRentalTitleLabel;
@property (weak, nonatomic) IBOutlet UILabel *actualAmountLabel;

@property (weak, nonatomic) IBOutlet EHIButton *paymentOptionButton;

@end

@implementation EHIReservationRentalPriceTotalCell

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIReservationRentalPriceTotalViewModel new];
    }
    
    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIReservationRentalPriceTotalViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidatePriceDifference:)];
    [MTRReactor autorun:self action:@selector(invalidateTransparency:)];
        
    model.bind.map(@{
        source(model.showTopDivider)            : ^(NSNumber *show) {
                                                    self.topDivider.hidden = !show.boolValue;
                                                },
        source(model.totalTitle)                : dest(self, .totalTitleLabel.text),
        source(model.updatedTotalTitle)         : dest(self, .updatedTotalTitleLabel.text),
        source(model.updatedTotalLabel)         : dest(self, .updatedTotalLabel.attributedText),
        source(model.paidAmountTitle)           : dest(self, .paidAmountTitleLabel.text),
        source(model.paidAmountLabel)           : dest(self, .paidAmountLabel.attributedText),
        source(model.originalTotal)             : dest(self, .originalTotalLabel.text),
        source(model.endOfRental)               : dest(self, .endOfRentalTitleLabel.text),
        source(model.actualAmount)              : dest(self, .actualAmountLabel.text),
        source(model.total)                     : dest(self, .totalLabel.attributedText),
        source(model.transparencyTitle)         : dest(self, .transparencyTitleLabel.text),
        source(model.transparency)              : dest(self, .transparencyLabel.attributedText),
        source(model.otherPaymentOptionTotal)   : dest(self, .paymentOptionButton.ehi_title),
    });
    
    [MTRReactor autorun:^(MTRComputation *computation) {
        MASLayoutPriority constraintPriority = self.viewModel.showOtherPaymentOption ? MASLayoutPriorityDefaultLow : MASLayoutPriorityRequired;
        
        [self.paymentOptionContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
            make.height.equalTo(@(0.0f)).with.priority(constraintPriority);
        }];
    }];
}

- (void)invalidatePriceDifference:(MTRComputation *)computation
{
    BOOL show = self.viewModel.layout == EHIReservationRentalPriceTotalLayoutUnpaidRefund;
    
    MASLayoutPriority priority = show ? MASLayoutPriorityDefaultLow : MASLayoutPriorityRequired;
    [self.priceDifferenceContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@0.0).priority(priority);
    }];
}

- (void)invalidateTransparency:(MTRComputation *)computation
{
    BOOL show = self.viewModel.showsTransparency;
    
    MASLayoutPriority priority = show ? MASLayoutPriorityDefaultLow : MASLayoutPriorityRequired;
    [self.transparencyContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@0.0).priority(priority);
    }];
    
    BOOL hideTransparencyAmount = self.viewModel.layout == EHIReservationRentalPriceTotalLayoutUnpaidRefund;
    
    priority = hideTransparencyAmount ? MASLayoutPriorityRequired : MASLayoutPriorityDefaultLow;
    [self.transparencyAmountContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.width.equalTo(@0.0).priority(priority);
    }];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetHeight(self.contentContainer.frame)
    };
}

# pragma mark - Actions

- (IBAction)didTapChangePaymentTypeButton:(id)sender
{
    [self.viewModel didTapChangePayment];
    [self ehi_performAction:@selector(didTapChangePaymentTypeForPriceTotalCell:) withSender:self];
}

@end
