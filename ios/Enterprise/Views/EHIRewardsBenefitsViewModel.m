//
//  EHIRewardsBenefitsViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 6/11/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIRewardsBenefitsViewModel.h"
#import "EHIViewModel_Subclass.h"

@interface EHIRewardsBenefitsViewModel ()
@property (nonatomic, readonly) EHIUserLoyalty *loyalty;
@property (strong   , nonatomic) NSDictionary *tierBenefits;
@end

@implementation EHIRewardsBenefitsViewModel

- (void)didBecomeActive
{
    [super didBecomeActive];
    
    self.plusBenefits     = [self titleForTier:EHIUserLoyaltyTierPlus];
    self.silverBenefits   = [self titleForTier:EHIUserLoyaltyTierSilver];
    self.goldBenefits     = [self titleForTier:EHIUserLoyaltyTierGold];
    self.platinumBenefits = [self titleForTier:EHIUserLoyaltyTierPlatinum];
    self.selectedTier     = self.tier;
}

//
// Helpers
//

- (NSAttributedString *)titleForTier:(EHIUserLoyaltyTier)tier
{
    NSArray *benefits = self.tierBenefits[@(tier)];

    return [NSAttributedString attributedStringListWithItems:benefits];
}

# pragma mark - Accessors

- (NSDictionary *)tierBenefits
{
    if(_tierBenefits) {
        return _tierBenefits;
    }
    
    _tierBenefits = @{
        @(EHIUserLoyaltyTierPlus) : @[
            EHILocalizedString(@"rewards_plus_tier_benefit_one", @"You're a Plus! Member", @""),
            EHILocalizedString(@"rewards_plus_tier_benefit_two", @"No free rentals", @""),
        ],
        @(EHIUserLoyaltyTierSilver) : @[
            EHILocalizedString(@"rewards_silver_tier_benefit_one", @"TESTING", @""),
            EHILocalizedString(@"rewards_silver_tier_benefit_two", @"15% off Enterprise merchandise", @""),
        ],
        @(EHIUserLoyaltyTierGold) : @[
            EHILocalizedString(@"rewards_gold_tier_benefit_one", @"10% Merchandise", @""),
            EHILocalizedString(@"rewards_gold_tier_benefit_two", @"Free rides on Sundays", @""),
        ],
        @(EHIUserLoyaltyTierPlatinum) : @[
            EHILocalizedString(@"rewards_platinum_tier_benefit_one", @"Pets ride free", @""),
            EHILocalizedString(@"rewards_platinum_tier_benefit_two", @"10% Off Rentals", @""),
        ],
    };
    
    return _tierBenefits;
}

# pragma mark - Computed

- (EHIUserLoyaltyTier)tier
{
    return self.loyalty.tier;
}

# pragma mark - Passthrough

- (EHIUserLoyalty *)loyalty
{
    return [EHIUser currentUser].profiles.basic.loyalty;
}

@end
