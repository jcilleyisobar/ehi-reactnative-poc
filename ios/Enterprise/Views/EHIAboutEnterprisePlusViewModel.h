//
//  EHIAboutEnterprisePlusViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 12/22/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIRewardsAnalyticsViewModel.h"

typedef NS_ENUM(NSInteger, EHIAboutEnterprisePlusSection) {
    EHIAboutEnterprisePlusSectionHeader,
    EHIAboutEnterprisePlusSectionPoints,
    EHIAboutEnterprisePlusSectionTiersHeader,
    EHIAboutEnterprisePlusSectionTiers,
    EHIAboutEnterprisePlusSectionFooter,
    EHIAboutEnterprisePlusSectionLegal
};

@class EHIRewardsAboutTiersListViewModel;
@interface EHIAboutEnterprisePlusViewModel : EHIRewardsAnalyticsViewModel <MTRReactive>
@property (copy  , nonatomic, readonly) NSString *title;
@property (strong, nonatomic, readonly) EHIViewModel *headerModel;
@property (strong, nonatomic, readonly) NSArray *pointsModels;
@property (strong, nonatomic, readonly) EHIViewModel *tiersHeaderModel;
@property (strong, nonatomic, readonly) EHIRewardsAboutTiersListViewModel *tiersModel;
@property (strong, nonatomic, readonly) EHIViewModel *footerModel;
@property (strong, nonatomic, readonly) EHIModel *legalModel;
@end
