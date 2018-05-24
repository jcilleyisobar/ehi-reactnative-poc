//
//  EHIRewardsBenefitsTierGaugeViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 1/11/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIRewardsBenefitsTierGaugeViewModel.h"
#import "EHIUserLoyalty.h"

@interface EHIRewardsBenefitsTierGaugeViewModel ()
@property (strong, nonatomic) EHIUserLoyalty *loyalty;
@end

@implementation EHIRewardsBenefitsTierGaugeViewModel

- (instancetype)initWithModel:(EHIUserLoyalty *)model
{
    if(self = [super initWithModel:model]) {
        _loyalty = model;
        _rentalsGaugeModel = [[EHIGaugeTierViewModel alloc] initWithUserLoyalty:model total:EHIGaugeTierTotalTypeRentals];
        _daysGaugeModel    = [[EHIGaugeTierViewModel alloc] initWithUserLoyalty:model total:EHIGaugeTierTotalTypeDays];
    }
    
    return self;
}

- (EHIUserLoyaltyTier)tier
{
    return self.loyalty.tier;
}

- (EHIUserLoyaltyTier)goalTier
{
    return self.loyalty.goal.tier;
}

- (NSAttributedString *)progressTitle
{
    NSString *tierTitle = EHILoyaltyTierTitleForTier(self.goalTier);
    UIColor *tierColor  = EHILoyaltyTierColorForTier(self.goalTier);
    
    NSString *progress   = EHILocalizedString(@"rewards_gauge_progress_reach", @"Progress to reach", @"");
    NSString *tierString = EHILocalizedString(@"rewards_gauge_progress_tier", @"Tier", @"");
    
    tierTitle = [NSString stringWithFormat:@"%@ %@", tierTitle, tierString];
    
    return EHIAttributedStringBuilder.new.fontStyle(EHIFontStyleBold, 18.0f)
                .appendText(progress).space
                .appendText(tierTitle).space
                .color(tierColor).space
                .string;
}

- (NSString *)orTitle
{
    return EHILocalizedString(@"rewards_or_title", @"OR", @"");
}

- (BOOL)useDoubleGauge
{
    return self.loyalty.tier != EHIUserLoyaltyTierPlus;
}

@end
