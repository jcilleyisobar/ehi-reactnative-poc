//
//  EHIRewardsAboutTiersHeaderViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 1/16/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIRewardsAboutTiersHeaderViewModel.h"
#import "EHIUserLoyalty.h"

@interface EHIRewardsAboutTiersHeaderViewModel ()
@property (copy, nonatomic) NSString *title;
@property (copy, nonatomic) NSString *subtitle;
@end

@implementation EHIRewardsAboutTiersHeaderViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
    	if([model isKindOfClass:[EHIUserLoyalty class]]) {
            _title    = [self buildTitleWithLoyalty:model];
            _subtitle = [self buildSubtitleWithLoyalty:model];
        }
    }
    
    return self;
}

- (NSString *)buildTitleWithLoyalty:(EHIUserLoyalty *)loyalty
{
    EHIUserLoyaltyTier tier = loyalty.tier;
    if(tier == EHIUserLoyaltyTierPlus) {
        return EHILocalizedString(@"reward_earn_points_plus_tier_text", @"Earn points with every qualifying rental", @"");
    } else {
        NSString *title     = EHILocalizedString(@"rewards_about_tier_members_get", @"#{tier} Tier members get", @"");
        NSString *tierTitle = EHILoyaltyTierTitleForTier(tier) ?: @"";
        
        return [title ehi_applyReplacementMap:@{
                    @"tier" : tierTitle
                }];
    }
}

- (NSString *)buildSubtitleWithLoyalty:(EHIUserLoyalty *)loyalty
{
    EHIUserLoyaltyTier tier = loyalty.tier;
    if(tier == EHIUserLoyaltyTierPlus) {
        return nil;
    }
    
    NSString *percent = EHILoyaltyTierBonusForTier(tier) ?: @"";
    NSString *number  = EHILoyaltyTierUpgradesInYearForTier(tier) ?: @"";
    NSString *subtitle = EHILocalizedString(@"rewards_about_tier_members_bonus", @"#{percent} bonus points and\n#{number} upgrades per year", @"");
    
    return [subtitle ehi_applyReplacementMap:@{
               @"percent" : percent,
               @"number"  : number
            }];
}


@end
