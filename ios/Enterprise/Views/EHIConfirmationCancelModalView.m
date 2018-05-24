//
//  EHIConfirmationCancelModalView.m
//  Enterprise
//
//  Created by Rafael Ramos on 2/16/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIConfirmationCancelModalView.h"
#import "EHIConfirmationCancelModalViewModel.h"

@interface EHIConfirmationCancelModalView ()
@property (strong, nonatomic) EHIConfirmationCancelModalViewModel *viewModel;

@property (weak  , nonatomic) IBOutlet UILabel *subtitleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *originalAmountTitle;
@property (weak  , nonatomic) IBOutlet UILabel *cancellationFeeTitle;
@property (weak  , nonatomic) IBOutlet UILabel *refundedAmountTitle;

@property (weak  , nonatomic) IBOutlet UILabel *originalAmount;
@property (weak  , nonatomic) IBOutlet UILabel *cancellationFee;
@property (weak  , nonatomic) IBOutlet UILabel *refundedAmount;

@property (weak  , nonatomic) IBOutlet UILabel *currencyConversionLabel;
@property (weak  , nonatomic) IBOutlet UIView *currencyConversionView;
@property (weak  , nonatomic) IBOutlet UILabel *convertedRefundLabel;
@property (weak  , nonatomic) IBOutlet UIView *convertedRefundView;

@end

@implementation EHIConfirmationCancelModalView

# pragma mark - Reactions

- (void)registerReactions:(EHIConfirmationCancelModalViewModel *)model
{
    [super registerReactions:model];

    [MTRReactor autorun:self action:@selector(invalidateCurrencyConversionContainer:)];
    [MTRReactor autorun:self action:@selector(invalidateRefundContainer:)];
    
    model.bind.map(@{
         source(model.subtitle)             : dest(self, .subtitleLabel.attributedText),
         source(model.originalAmountTile)   : dest(self, .originalAmountTitle.text),
         source(model.originalAmount)       : dest(self, .originalAmount.text),
         source(model.cancellationFeeTitle) : dest(self, .cancellationFeeTitle.text),
         source(model.cancellationFee)      : dest(self, .cancellationFee.text),
         source(model.refundedAmountTitle)  : dest(self, .refundedAmountTitle.text),
         source(model.refundedAmount)       : dest(self, .refundedAmount.attributedText),
         source(model.conversionSubtitle)   : dest(self, .currencyConversionLabel.text),
         source(model.convertedRefund)      : dest(self, .convertedRefundLabel.text)
    });
}

- (void)invalidateCurrencyConversionContainer:(MTRComputation *)computation
{
    BOOL hide = self.viewModel.conversionSubtitle == nil;
    
    MASLayoutPriority priority = hide ? MASLayoutPriorityRequired : MASLayoutPriorityDefaultLow;
    
    [self.currencyConversionView mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@0.0).priority(priority);
    }];
}

- (void)invalidateRefundContainer:(MTRComputation *)computation
{
    BOOL hide = self.viewModel.convertedRefund == nil;
    
    MASLayoutPriority priority = hide ? MASLayoutPriorityRequired : MASLayoutPriorityDefaultLow;
    
    [self.convertedRefundView mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@0.0).priority(priority);
    }];
}

@end
