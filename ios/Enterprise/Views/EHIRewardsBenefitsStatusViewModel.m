//
//  EHIRewardsBenefitsStatusViewModel.m
//  Enterprise
//
//  Created by frhoads on 1/3/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIRewardsBenefitsStatusViewModel.h"

@interface EHIRewardsBenefitsStatusViewModel()
@end

@implementation EHIRewardsBenefitsStatusViewModel

- (void)updateWithModel:(EHIUserLoyalty *)model
{
    [super updateWithModel:model];

    self.tierStatus = [self statusForTier:model.tier];
}

- (NSAttributedString *)statusForTier:(EHIUserLoyaltyTier)tier
{
    NSString *tierStatus = EHILocalizedString(@"rewards_welcome_current_tier", @"#{tier} Tier", @"");
    tierStatus = [tierStatus ehi_applyReplacementMap:@{
        @"tier" : EHILoyaltyTierTitleForTier(tier) ?: @"",
    }];

    return EHIAttributedStringBuilder.new
            .text(tierStatus)
            .fontStyle(EHIFontStyleBold, 22.0f)
            .color(EHILoyaltyTierColorForTier(tier))
            .string;
}


@end
