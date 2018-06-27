//
//  EHIRewardsBenefitsAuthViewModel.h
//  Enterprise
//
//  Created by frhoads on 12/22/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIRewardsAnalyticsViewModel.h"
#import "EHIAboutEnterprisePlusFooterViewModel.h"

typedef NS_ENUM(NSUInteger, EHIRewardsBenefitsSection) {
    EHIRewardsBenefitsSectionHeader,
    EHIRewardsBenefitsSectionsPoints,
    EHIRewardsBenefitsSectionsStatus,
    EHIRewardsBenefitsSectionNextTier,
    EHIRewardsBenefitsSectionFooter,
    EHIRewardsBenefitsSectionLegal
};

@class EHISectionHeaderModel;
@class EHIRewardsBenefitsTierGaugeViewModel;
@class EHIRewardsBenefitsHeaderViewModel;
@interface EHIRewardsBenefitsAuthViewModel : EHIRewardsAnalyticsViewModel <MTRReactive>

@property (copy  , nonatomic, readonly) NSString *title;
@property (copy  , nonatomic, readonly) NSString *footerTitle;
@property (strong, nonatomic, readonly) EHIRewardsBenefitsTierGaugeViewModel *tierGaugeModel;
@property (strong, nonatomic, readonly) EHIRewardsBenefitsHeaderViewModel *headerModel;
@property (strong, nonatomic, readonly) EHIViewModel *statusModel;
@property (strong, nonatomic, readonly) EHIViewModel *pointsModel;
@property (strong, nonatomic, readonly) EHIModel *footerModel;
@property (strong, nonatomic, readonly) EHIModel *legalModel;

- (EHISectionHeaderModel *)headerForSection:(EHIRewardsBenefitsSection)section;
- (void)selectItemAtIndexPath:(NSIndexPath *)indexPath;

@end
