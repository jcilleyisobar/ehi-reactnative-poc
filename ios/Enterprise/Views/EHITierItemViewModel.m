//
//  EHIAboutEnterprisePlusTierViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 1/3/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHITierItemViewModel.h"

@interface EHITierItemViewModel ()
@property (assign, nonatomic) EHIUserLoyaltyTier tier;
@property (assign, nonatomic) EHIAboutEnterprisePlusTierType type;
@end

@implementation EHITierItemViewModel

- (instancetype)initWithTier:(EHIUserLoyaltyTier)tier type:(EHIAboutEnterprisePlusTierType)type
{
    if(self = [super init]) {
        _tier = tier;
        _type = type;
    }
    
    return self;
}

# pragma mark - Accessors

- (NSString *)firstInfoTitle
{
    switch(self.type) {
        case EHIAboutEnterprisePlusTierTypeGoals:
            return EHILocalizedString(@"rewards_welcome_rentals", @"Rentals", @"");
        case EHIAboutEnterprisePlusTierTypeBenefits:
            return self.benefitsFreeUpgradeTitle;
    }
}

- (NSString *)firstInfo
{
    switch(self.type) {
        case EHIAboutEnterprisePlusTierTypeGoals:
            switch(self.tier) {
                case EHIUserLoyaltyTierPlus:
                    return [NSString stringWithFormat:@"%@-%@",
                            @(0).description,
                            @(EHILoyaltyTotalRentalsForTier(self.tier)).description];
                case EHIUserLoyaltyTierSilver:
                    return [NSString stringWithFormat:@"%@-%@",
                            @(EHILoyaltyTotalRentalsForTier(self.tier)).description,
                            @(EHILoyaltyTotalRentalsForTier(EHIUserLoyaltyTierGold) -1).description];
                default:
                    return EHILoyaltyTierRentalsForTier(self.tier);
            }
        case EHIAboutEnterprisePlusTierTypeBenefits:
            return EHILoyaltyTierUpgradesInYearForTier(self.tier);
    }
}

- (NSString *)orTitle
{
    switch(self.type) {
        case EHIAboutEnterprisePlusTierTypeGoals:
            switch(self.tier) {
                case EHIUserLoyaltyTierPlus:
                case EHIUserLoyaltyTierSilver:
                    return nil;
                default:
                    return EHILocalizedString(@"rewards_or_title", @"OR", @"");
            }
        case EHIAboutEnterprisePlusTierTypeBenefits:
            return nil;
    }
}

- (NSString *)secondInfoTitle
{
    switch(self.type) {
        case EHIAboutEnterprisePlusTierTypeGoals:
            switch(self.tier) {
                case EHIUserLoyaltyTierPlus:
                    return nil;
                case EHIUserLoyaltyTierSilver:
                    return nil;
                default:
                    return self.goalDaysTitle;
            }
        case EHIAboutEnterprisePlusTierTypeBenefits:
            return self.benefitsBonusTitle;
    }
}

- (NSString *)secondInfo
{
    switch(self.type) {
        case EHIAboutEnterprisePlusTierTypeGoals:
            return EHILoyaltyTierDaysForTier(self.tier);
        case EHIAboutEnterprisePlusTierTypeBenefits:
            return self.tier != EHIUserLoyaltyTierPlus ? EHILoyaltyTierBonusForTier(self.tier) : nil;
    }
}

# pragma mark - Goals

- (NSString *)goalDaysTitle
{
    return EHILocalizedString(@"rewards_welcome_days", @"Days", @"");
}

# pragma mark - Benefits

- (NSString *)benefitsFreeUpgradeTitle
{
    switch(self.tier) {
        case EHIUserLoyaltyTierPlus:
            return EHILocalizedString(@"about_e_p_tier_plus_membership", @"membership", @"");
        case EHIUserLoyaltyTierGold:
        case EHIUserLoyaltyTierPlatinum:
        case EHIUserLoyaltyTierSilver:
        case EHIUserLoyaltyTierUnknown:
            return EHILocalizedString(@"about_e_p_tier_upgrade", @"Free upgrades per year", @"");
    }
}

- (NSString *)benefitsBonusTitle
{
    switch(self.tier) {
        case EHIUserLoyaltyTierPlus:
            return EHILocalizedString(@"reward_earn_points_plus_tier_text", @"Earn points with every qualifying rental", @"");
        case EHIUserLoyaltyTierGold:
        case EHIUserLoyaltyTierPlatinum:
        case EHIUserLoyaltyTierSilver:
        case EHIUserLoyaltyTierUnknown:
            return EHILocalizedString(@"about_e_p_tier_bonus_points", @"Bonus Points", @"");
    }
}

@end
