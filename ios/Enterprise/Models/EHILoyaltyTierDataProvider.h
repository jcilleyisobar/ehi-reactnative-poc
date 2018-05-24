//
//  EHILoyaltyTierDataProvider.h
//  Enterprise
//
//  Created by Rafael Ramos on 1/10/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

typedef NS_ENUM(NSUInteger, EHIUserLoyaltyTier) {
    EHIUserLoyaltyTierPlus,
    EHIUserLoyaltyTierSilver,
    EHIUserLoyaltyTierGold,
    EHIUserLoyaltyTierPlatinum,
    EHIUserLoyaltyTierUnknown,
};

NS_INLINE NSValueTransformer * EHILoyaltyTierTypeTransform()
{
    EHIMapTransformer *transformer = [[EHIMapTransformer alloc] initWithMap:@{
        @"Plus"     : @(EHIUserLoyaltyTierPlus),
        @"Silver"   : @(EHIUserLoyaltyTierSilver),
        @"Gold"     : @(EHIUserLoyaltyTierGold),
        @"Platinum" : @(EHIUserLoyaltyTierPlatinum),
    }];
    
    transformer.defaultValue = @(EHIUserLoyaltyTierUnknown);
    
    return transformer;
}

NS_INLINE NSString * EHILoyaltyTierTitleForTier(EHIUserLoyaltyTier tier)
{
    switch(tier) {
        case EHIUserLoyaltyTierPlus:
            return EHILocalizedString(@"rewards_loyalty_tier_plus_title", @"Plus", @"");
        case EHIUserLoyaltyTierSilver:
            return EHILocalizedString(@"rewards_loyalty_tier_silver_title", @"Silver", @"");
        case EHIUserLoyaltyTierGold:
            return EHILocalizedString(@"rewards_loyalty_tier_gold_title", @"Gold", @"");
        case EHIUserLoyaltyTierPlatinum:
            return EHILocalizedString(@"rewards_loyalty_tier_platinum_title", @"Platinum", @"");
        default:
            return nil;
    }
}

NS_INLINE NSString * EHILoyaltyTierAboutTitleForTier(EHIUserLoyaltyTier tier)
{
    switch(tier) {
        case EHIUserLoyaltyTierPlus:
            return EHILocalizedString(@"about_e_p_tier_plus_title", @"PLUS", @"");
        case EHIUserLoyaltyTierSilver:
            return EHILocalizedString(@"about_e_p_tier_silver_title", @"SILVER", @"");
        case EHIUserLoyaltyTierGold:
            return EHILocalizedString(@"about_e_p_tier_gold_title", @"GOLD", @"");
        case EHIUserLoyaltyTierPlatinum:
            return EHILocalizedString(@"about_e_p_tier_platinum_title", @"PLATINUM", @"");
        default:
            return nil;
    }
}

NS_INLINE UIColor * EHILoyaltyTierColorForTier(EHIUserLoyaltyTier tier)
{
    switch(tier) {
        case EHIUserLoyaltyTierPlus:
            return [UIColor ehi_greenColor];
        case EHIUserLoyaltyTierSilver:
            return [UIColor ehi_silverColor];
        case EHIUserLoyaltyTierGold:
            return [UIColor ehi_goldColor];
        case EHIUserLoyaltyTierPlatinum:
            return [UIColor blackColor];
        default:
            return [UIColor blackColor];
    }
}

NS_INLINE NSInteger EHILoyaltyTotalRentalsForTier(EHIUserLoyaltyTier tier)
{
    switch(tier) {
        case EHIUserLoyaltyTierPlus:
            return 5;
        case EHIUserLoyaltyTierSilver:
            return 6;
        case EHIUserLoyaltyTierGold:
            return 12;
        case EHIUserLoyaltyTierPlatinum:
            return 24;
        case EHIUserLoyaltyTierUnknown:
            return 0;
    }
}

NS_INLINE NSString * EHILoyaltyTierRentalsForTier(EHIUserLoyaltyTier tier)
{
    NSInteger totalRentals = EHILoyaltyTotalRentalsForTier(tier);
    return @(totalRentals).description;
}

NS_INLINE NSInteger EHILoyaltyTotalTierDaysForTier(EHIUserLoyaltyTier tier)
{
    switch(tier) {
        case EHIUserLoyaltyTierGold:
            return 40;
        case EHIUserLoyaltyTierPlatinum:
            return 85;
        case EHIUserLoyaltyTierPlus:
        case EHIUserLoyaltyTierSilver:
        case EHIUserLoyaltyTierUnknown:
            return -1;
    }
}

NS_INLINE NSString * EHILoyaltyTierDaysForTier(EHIUserLoyaltyTier tier)
{
    NSInteger totalDays = EHILoyaltyTotalTierDaysForTier(tier);
    return totalDays >= 0 ? @(totalDays).description : nil;
}

NS_INLINE NSInteger EHILoyaltyTierTotalUpgradesInYearForTier(EHIUserLoyaltyTier tier)
{
    switch(tier) {
        case EHIUserLoyaltyTierGold:
            return 2;
        case EHIUserLoyaltyTierPlatinum:
            return 4;
        case EHIUserLoyaltyTierPlus:
            return -1;
        case EHIUserLoyaltyTierSilver:
            return 1;
        case EHIUserLoyaltyTierUnknown:
            return 0;
    }
}

NS_INLINE NSString * EHILoyaltyTierUpgradesInYearForTier(EHIUserLoyaltyTier tier)
{
    NSInteger upgradeTotal = EHILoyaltyTierTotalUpgradesInYearForTier(tier);
    return upgradeTotal >= 0 ? @(upgradeTotal).description : EHILocalizedString(@"about_e_p_tier_plus_free", @"Free", @"");
}

NS_INLINE NSInteger EHILoyaltyTierTotalBonusForTier(EHIUserLoyaltyTier tier)
{
    switch(tier) {
        case EHIUserLoyaltyTierSilver:
            return 10;
        case EHIUserLoyaltyTierGold:
            return 15;
        case EHIUserLoyaltyTierPlatinum:
            return 20;
        case EHIUserLoyaltyTierPlus:
        case EHIUserLoyaltyTierUnknown:
            return 0;
    }
}

NS_INLINE NSString * EHILoyaltyTierBonusForTier(EHIUserLoyaltyTier tier)
{
    NSInteger bonusTotal = EHILoyaltyTierTotalBonusForTier(tier);
    return bonusTotal > 0 ? [NSString stringWithFormat:@"%ld%%", (long)bonusTotal] : @(bonusTotal).description;
}
