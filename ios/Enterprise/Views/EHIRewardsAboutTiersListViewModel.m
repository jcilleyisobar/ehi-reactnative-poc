//
//  EHIRewardsAboutTiersListViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 1/16/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIRewardsAboutTiersListViewModel.h"
#import "EHIViewModel_Subclass.h"
#import "EHIUser.h"

@interface EHIRewardsAboutTiersListViewModel ()
@property (strong, nonatomic) EHITierDetailsViewModel *plusModel;
@property (strong, nonatomic) EHITierDetailsViewModel *silverModel;
@property (strong, nonatomic) EHITierDetailsViewModel *goldModel;
@property (strong, nonatomic) EHITierDetailsViewModel *platinumModel;
@property (assign, nonatomic) EHIRewardsAboutTiersListSection selectedSection;
@end

@implementation EHIRewardsAboutTiersListViewModel

- (instancetype)init
{
    if(self = [super init]) {
        _tierStates = @{
            @(EHIRewardsAboutTiersListSectionPlus).description     : @(NO),
            @(EHIRewardsAboutTiersListSectionSilver).description   : @(NO),
            @(EHIRewardsAboutTiersListSectionGold).description     : @(NO),
            @(EHIRewardsAboutTiersListSectionPlatinum).description : @(NO)
        };
    }
    
    return self;
}

# pragma mark - Accessors

- (EHITierDetailsViewModel *)plusModel
{
    if(!_plusModel) {
        _plusModel = [self tierModelWithTier:EHIUserLoyaltyTierPlus];
    }
    
    return _plusModel;
}

- (EHITierDetailsViewModel *)silverModel
{
    if(!_silverModel) {
        _silverModel = [self tierModelWithTier:EHIUserLoyaltyTierSilver];
    }
    
    return _silverModel;
}

- (EHITierDetailsViewModel *)goldModel
{
    if(!_goldModel) {
        _goldModel = [self tierModelWithTier:EHIUserLoyaltyTierGold];
    }
    
    return _goldModel;
}

- (EHITierDetailsViewModel *)platinumModel
{
    if(!_platinumModel) {
        _platinumModel = [self tierModelWithTier:EHIUserLoyaltyTierPlatinum];
    }
    
    return _platinumModel;
}

- (EHITierDetailsViewModel *)tierModelWithTier:(EHIUserLoyaltyTier)tier
{
    return [[EHITierDetailsViewModel alloc] initWithTier:tier];
}

- (EHIRewardsAboutTiersListSection)currentSection
{
    switch(self.loyalty.tier) {
        case EHIUserLoyaltyTierPlus:
            return EHIRewardsAboutTiersListSectionPlus;
        case EHIUserLoyaltyTierSilver:
            return EHIRewardsAboutTiersListSectionSilver;
        case EHIUserLoyaltyTierGold:
            return EHIRewardsAboutTiersListSectionGold;
        case EHIUserLoyaltyTierPlatinum:
            return EHIRewardsAboutTiersListSectionPlatinum;
        default:
            return EHIRewardsAboutTiersListSectionPlus;
    }
}

- (UIColor *)colorForSection:(EHIRewardsAboutTiersListSection)section
{
    switch(section) {
        case EHIRewardsAboutTiersListSectionPlus:
            return EHILoyaltyTierColorForTier(EHIUserLoyaltyTierPlus);
        case EHIRewardsAboutTiersListSectionSilver:
            return EHILoyaltyTierColorForTier(EHIUserLoyaltyTierSilver);
        case EHIRewardsAboutTiersListSectionGold:
            return EHILoyaltyTierColorForTier(EHIUserLoyaltyTierGold);
        case EHIRewardsAboutTiersListSectionPlatinum:
            return EHILoyaltyTierColorForTier(EHIUserLoyaltyTierPlatinum);
        default:
            return EHILoyaltyTierColorForTier(EHIUserLoyaltyTierPlus);
    }
}

- (NSString *)titleForSection:(EHIRewardsAboutTiersListSection)section
{
    switch(section) {
        case EHIRewardsAboutTiersListSectionPlus:
            return EHILoyaltyTierAboutTitleForTier(EHIUserLoyaltyTierPlus);
        case EHIRewardsAboutTiersListSectionSilver:
            return EHILoyaltyTierAboutTitleForTier(EHIUserLoyaltyTierSilver);
        case EHIRewardsAboutTiersListSectionGold:
            return EHILoyaltyTierAboutTitleForTier(EHIUserLoyaltyTierGold);
        case EHIRewardsAboutTiersListSectionPlatinum:
            return EHILoyaltyTierAboutTitleForTier(EHIUserLoyaltyTierPlatinum);
        default:
            return EHILoyaltyTierAboutTitleForTier(EHIUserLoyaltyTierPlus);
    }
}

- (void)selectSection:(EHIRewardsAboutTiersListSection)section
{
    [self trackActionInSection:section];
    
    NSMutableDictionary *tiers = [self.tierStates mutableCopy];
    NSString *tierKey = @(section).description;
    BOOL currentState = [self.tierStates[@(section).description] boolValue];
    
    [tiers setValue:@(!currentState) forKey:tierKey];
    
    self.tierStates = [tiers copy];
    self.selectedSection = section;
}

//
// Helpers
//

- (void)trackActionInSection:(EHIRewardsAboutTiersListSection)section
{
    NSString *action = nil;
    switch(section) {
        case EHIRewardsAboutTiersListSectionPlus:
            action = EHIAnalyticsRewardBenefitsAuthActionPlusTier;
            break;
        case EHIRewardsAboutTiersListSectionSilver:
            action = EHIAnalyticsRewardBenefitsAuthActionSilverTier;
            break;
        case EHIRewardsAboutTiersListSectionGold:
            action = EHIAnalyticsRewardBenefitsAuthActionGoldTier;
            break;
        case EHIRewardsAboutTiersListSectionPlatinum:
            action = EHIAnalyticsRewardBenefitsAuthActionPlatinumTier;
            break;
        default:
            break;
    }
    
    [EHIAnalytics trackAction:action handler:nil];
}

- (EHIUserLoyalty *)loyalty
{
    return [EHIUser currentUser].profiles.basic.loyalty;
}

@end
