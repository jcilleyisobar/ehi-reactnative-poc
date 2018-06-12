//
//  EHIGaugeTierViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 1/11/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIGaugeTierViewModel.h"
#import "EHIUserLoyalty.h"

@interface EHIGaugeTierViewModel ()
@property (strong, nonatomic) EHIGaugeViewModel *gaugeModel;
@property (strong, nonatomic) EHIUserLoyalty *loyalty;
@property (assign, nonatomic) EHIGaugeTierTotalType totalType;
@end

@implementation EHIGaugeTierViewModel

- (instancetype)initWithUserLoyalty:(EHIUserLoyalty *)loyalty total:(EHIGaugeTierTotalType)totalType
{
    if(self = [super init]) {
        _loyalty   = loyalty;
        _totalType = totalType;
    }
    
    return self;
}

- (EHIGaugeViewModel *)gaugeModel
{
    if(!_gaugeModel) {
        BOOL userIsPlusTier = self.loyalty.tier == EHIUserLoyaltyTierPlus;
        BOOL totalByRental  = self.totalType == EHIGaugeTierTotalTypeRentals;
        
        NSInteger remainingTotal = self.userAmount;
        NSInteger nextTierTotal  = self.tierTotal;
        
        CGFloat total = (userIsPlusTier && totalByRental) ? nextTierTotal - remainingTotal : ((nextTierTotal - remainingTotal) * 100.0f) / nextTierTotal;
        _gaugeModel = [[EHIGaugeViewModel alloc] initWithLoyalty:self.loyalty fill:total];
    }
    
    return _gaugeModel;
}

- (NSAttributedString *)progressTitle
{
    NSString *tierTitle = EHILoyaltyTierTitleForTier(self.tier);
    UIColor *tierColor  = EHILoyaltyTierColorForTier(self.tier);
    
    NSString *progress   = EHILocalizedString(@"rewards_gauge_progress_reach", @"Progress to reach", @"");
    NSString *tierString = EHILocalizedString(@"rewards_gauge_progress_tier", @"Tier", @"");
    
    tierTitle = [NSString stringWithFormat:@"%@ %@", tierTitle, tierString];
    
    return EHIAttributedStringBuilder.new.fontStyle(EHIFontStyleBold, 18.0f)
            .appendText(progress).space
            .appendText(tierTitle).space
            .color(tierColor).space
            .string;
}

- (NSAttributedString *)currentAmount
{
    NSInteger remainingTotal = self.userAmount;
    NSInteger nextTierTotal  = self.tierTotal;
    NSString *total  = @(nextTierTotal - remainingTotal).description;
    CGFloat fontSize = self.singleGauge ? 55.0f : 40.0f;
    
    return EHIAttributedStringBuilder.new.fontStyle(EHIFontStyleLight, fontSize).appendText(total).string;
}

- (NSString *)unitTitle
{
    switch(self.totalType) {
        case EHIGaugeTierTotalTypeRentals:
            return EHILocalizedString(@"rewards_welcome_rentals", @"Rentals", @"");
        case EHIGaugeTierTotalTypeDays:
            return EHILocalizedString(@"rewards_welcome_days", @"Days", @"");
    }
}

- (NSString *)total
{
    NSInteger tierTotal = self.tierTotal;
    NSString *total = EHILocalizedString(@"rewards_welcome_of_total", @"of #{number} total", @"");
    
    return [total ehi_applyReplacementMap:@{
        @"number" : @(tierTotal).description
    }];
}

//
// Helpers
//

- (NSInteger)userAmount
{
    return self.totalType == EHIGaugeTierTotalTypeRentals
            ? self.loyalty.goal.remainingRentals
            : self.loyalty.goal.remainingRentalDays;
}

- (NSInteger)tierTotal
{
    return self.totalType == EHIGaugeTierTotalTypeRentals
            ? self.loyalty.goal.nextTierRentals
            : self.loyalty.goal.nextTierRentalDays;
}

- (EHIUserLoyaltyTier)tier
{
    return self.loyalty.tier;
}

- (EHIUserLoyaltyTier)goalTier
{
    return self.loyalty.goal.tier;
}

- (BOOL)singleGauge
{
    // consider plus as single gauge
    return self.tier == EHIUserLoyaltyTierPlus;
}

@end
