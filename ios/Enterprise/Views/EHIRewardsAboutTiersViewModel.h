//
//  EHIRewardsAboutTiersViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 1/16/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIRewardsAnalyticsViewModel.h"
#import "EHIRewardsAboutTiersListViewModel.h"

typedef NS_ENUM(NSInteger, EHIRewardsAboutTiersSection) {
    EHIRewardsAboutTiersSectionHeader,
    EHIRewardsAboutTiersSectionTiers,
    EHIRewardsAboutTiersSectionFooter,
    EHIRewardsAboutTiersSectionLegal
};

@class EHIRewardsAboutTiersHeaderViewModel;
@class EHIRewardsBenefitsFooterViewModel;
@interface EHIRewardsAboutTiersViewModel : EHIRewardsAnalyticsViewModel <MTRReactive>
@property (copy  , nonatomic, readonly) NSString *title;
@property (strong, nonatomic, readonly) EHIRewardsAboutTiersHeaderViewModel *headerModel;
@property (strong, nonatomic, readonly) EHIRewardsAboutTiersListViewModel *tiersModel;
@property (strong, nonatomic, readonly) EHIRewardsBenefitsFooterViewModel *footerModel;
@property (strong, nonatomic, readonly) EHIModel *legalModel;
@end
