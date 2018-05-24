//
//  EHIRewardsBenefitsTierGaugeCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 1/11/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIRewardsBenefitsTierGaugeCell.h"
#import "EHIRewardsBenefitsTierGaugeViewModel.h"
#import "EHIGaugeTierView.h"

@interface EHIRewardsBenefitsTierGaugeCell ()
@property (strong, nonatomic) EHIRewardsBenefitsTierGaugeViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UILabel *progressLabel;
@property (weak  , nonatomic) IBOutlet UILabel *orLabel;

@property (weak  , nonatomic) IBOutlet UIView *singleContainerView;
@property (weak  , nonatomic) IBOutlet UIView *doubleContainerView;

// gauges
@property (weak  , nonatomic) IBOutlet EHIGaugeTierView *gaugeTierView;
@property (weak  , nonatomic) IBOutlet EHIGaugeTierView *rentalsGaugeTierView;
@property (weak  , nonatomic) IBOutlet EHIGaugeTierView *daysGaugeTierView;
@end

@implementation EHIRewardsBenefitsTierGaugeCell

# pragma mark - Reactions

- (void)registerReactions:(EHIRewardsBenefitsTierGaugeViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateTierGauges:)];
    
    model.bind.map(@{
        source(model.progressTitle)     : dest(self, .progressLabel.attributedText),
        source(model.orTitle)           : dest(self, .orLabel.text),
        source(model.daysGaugeModel)    : dest(self, .daysGaugeTierView.viewModel),
        source(model.rentalsGaugeModel) : ^(id model) {
                                            self.gaugeTierView.viewModel = model;
                                            self.rentalsGaugeTierView.viewModel = model;
                                        }
    });
}

- (void)invalidateTierGauges:(MTRComputation *)computation
{
    BOOL show = self.viewModel.useDoubleGauge;
    
    if(show) {
        [self.singleContainerView mas_remakeConstraints:^(MASConstraintMaker *make) {
            make.height.equalTo(@0.0).priority(MASLayoutPriorityRequired);
        }];
    } else {
        [self.doubleContainerView mas_remakeConstraints:^(MASConstraintMaker *make) {
            make.height.equalTo(@0.0).priority(MASLayoutPriorityRequired);
        }];
    }
}

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.doubleContainerView.frame)
    };
}

@end
