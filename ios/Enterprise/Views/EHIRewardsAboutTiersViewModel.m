//
//  EHIRewardsAboutTiersViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 1/16/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIRewardsAboutTiersViewModel.h"
#import "EHIUserLoyalty.h"
#import "EHIUser.h"
#import "EHIRewardsAboutTiersHeaderViewModel.h"
#import "EHIRewardsBenefitsFooterViewModel.h"
#import "EHILoyaltyTierDataProvider.h"

@interface EHIRewardsAboutTiersViewModel ()
@property (strong, nonatomic) EHIUserLoyalty *loyalty;
@property (strong, nonatomic) EHIRewardsAboutTiersHeaderViewModel *headerModel;
@property (strong, nonatomic) EHIRewardsAboutTiersListViewModel *tiersModel;
@property (strong, nonatomic) EHIRewardsBenefitsFooterViewModel *footerModel;
@property (strong, nonatomic) EHIModel *legalModel;
@end

@implementation EHIRewardsAboutTiersViewModel

- (NSString *)title
{
    return EHILocalizedString(@"rewards_about_tier_title", @"About Tier Benefits", @"");
}

- (EHIRewardsAboutTiersHeaderViewModel *)headerModel
{
    if(!_headerModel) {
        _headerModel = [[EHIRewardsAboutTiersHeaderViewModel alloc] initWithModel:self.loyalty];
    }

    return _headerModel;
}

- (EHIRewardsAboutTiersListViewModel *)tiersModel
{
    if(!_tiersModel) {
        _tiersModel = [EHIRewardsAboutTiersListViewModel new];
    }

    return _tiersModel;
}

- (EHIRewardsBenefitsFooterViewModel *)footerModel
{
    if(!_footerModel) {
        _footerModel = [EHIRewardsBenefitsFooterViewModel new];
    }
    
    return _footerModel;
}

- (EHIModel *)legalModel
{
    if(!_legalModel) {
        _legalModel = [EHIModel placeholder];
    }
    
    return _legalModel;
}

# pragma mark - Passthrough

- (EHIUserLoyalty *)loyalty
{
    if(!_loyalty) {
        _loyalty = [EHIUser currentUser].profiles.basic.loyalty;
    }

    return _loyalty;
}

@end
