//
//  EHIRewardsAnalyticsViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 2/6/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIRewardsAnalyticsViewModel.h"
#import "EHIViewModel_Subclass.h"
#import "EHILoyaltyTierDataProvider.h"
#import "EHIUser.h"

@implementation EHIRewardsAnalyticsViewModel

- (void)updateAnalyticsContext:(EHIAnalyticsContext *)context
{
    EHIUserLoyalty *loyalty = [EHIUser currentUser].profiles.basic.loyalty;
    if(loyalty) {
        context[EHIAnalyticsRewardsCountryKey] = [NSLocale ehi_country].code ?: @"";
        context[EHIAnalyticsRewardsTierKey]    = EHILoyaltyTierTitleForTier(loyalty.tier) ?: @"";
        context[EHIAnalyticsRewardsPointsKey]  = @(loyalty.pointsToDate);
    }
}

@end
