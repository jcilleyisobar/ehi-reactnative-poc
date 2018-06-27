//
//  EHIAboutEnterprisePlusTierCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 12/23/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIAboutEnterprisePlusTierCell.h"
#import "EHIRewardsAboutTiersListViewModel.h"
#import "EHITierDetailsView.h"

@interface EHIAboutEnterprisePlusTierCell ()
@property (strong, nonatomic) EHIRewardsAboutTiersListViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIView *plusTitleView;
@property (weak  , nonatomic) IBOutlet UILabel *plusTitleLabel;
@property (weak  , nonatomic) IBOutlet EHITierDetailsView *plusDetailsView;

@property (weak  , nonatomic) IBOutlet UIView *silverTitleView;
@property (weak  , nonatomic) IBOutlet UILabel *silverTitleLabel;
@property (weak  , nonatomic) IBOutlet EHITierDetailsView *silverDetailsView;

@property (weak  , nonatomic) IBOutlet UIView *goldTitleView;
@property (weak  , nonatomic) IBOutlet UILabel *goldTitleLabel;
@property (weak  , nonatomic) IBOutlet EHITierDetailsView *goldDetailsView;

@property (weak  , nonatomic) IBOutlet UIView *platinumTitleView;
@property (weak  , nonatomic) IBOutlet UILabel *platinumTitleLabel;
@property (weak  , nonatomic) IBOutlet EHITierDetailsView *platinumDetailsView;

@property (weak  , nonatomic) IBOutlet UIView *containerView;
@end

@implementation EHIAboutEnterprisePlusTierCell

# pragma mark - Reactions

- (void)registerReactions:(EHIRewardsAboutTiersListViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:^(MTRComputation *computation) {
        self.plusTitleView.backgroundColor = [self.viewModel colorForSection:EHIRewardsAboutTiersListSectionPlus];
        self.plusTitleLabel.text = [self.viewModel titleForSection:EHIRewardsAboutTiersListSectionPlus];
        
        self.silverTitleView.backgroundColor = [self.viewModel colorForSection:EHIRewardsAboutTiersListSectionSilver];
        self.silverTitleLabel.text = [self.viewModel titleForSection:EHIRewardsAboutTiersListSectionSilver];
        
        self.goldTitleView.backgroundColor = [self.viewModel colorForSection:EHIRewardsAboutTiersListSectionGold];
        self.goldTitleLabel.text = [self.viewModel titleForSection:EHIRewardsAboutTiersListSectionGold];
        
        self.platinumTitleView.backgroundColor = [self.viewModel colorForSection:EHIRewardsAboutTiersListSectionPlatinum];
        self.platinumTitleLabel.text = [self.viewModel titleForSection:EHIRewardsAboutTiersListSectionPlatinum];
    }];
    
    model.bind.map(@{
        source(model.plusModel)     : dest(self, .plusDetailsView.viewModel),
        source(model.silverModel)   : dest(self, .silverDetailsView.viewModel),
        source(model.goldModel)     : dest(self, .goldDetailsView.viewModel),
        source(model.platinumModel) : dest(self, .platinumDetailsView.viewModel),
    });
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.containerView.frame) + 8.0f
    };
}

@end
