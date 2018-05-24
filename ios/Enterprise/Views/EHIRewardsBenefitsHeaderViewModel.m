//
//  EHIRewardsBenefitsHeaderViewModel.m
//  Enterprise
//
//  Created by frhoads on 1/9/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//


#import "EHIRewardsBenefitsHeaderViewModel.h"
#import "EHILoyaltyTierDataProvider.h"
#import "EHIUserLoyalty.h"
#import "EHISettings.h"
#import "EHIUser.h"

#define EHIAlmostNextTierDays 3
#define EHIAlmostNextTierRentals 1
#define EHIPointsThreshold 1000

@interface EHIRewardsBenefitsHeaderViewModel()
@property (strong, nonatomic) EHIUserLoyalty *userLoyalty;
@property (assign, nonatomic) NSInteger tierLevel;
@end

@implementation EHIRewardsBenefitsHeaderViewModel

- (instancetype)initWithModel:(EHIUserLoyalty *)model
{
    if(self = [super initWithModel:model]) {
        if(model) {
            _userLoyalty = model;
            _tierLevel   = self.savedTierLevel;
            
            [self updateHeaderType];
        }
    }
    
    return self;
}

- (void)updateHeaderType
{
    NSInteger totalPoints = self.userLoyalty.pointsToDate;
    
    if([self isNewMember]) {
        self.headerType = EHIRewardsBenefitsHeaderTypeNewMember;
    } else if([self isNewTier]) {
        self.headerType = [self calculateNewTier];
    } else if([self almostNewTierRentals]) {
        self.headerType = EHIRewardsBenefitsHeaderTypeAlmostRentals;
    } else if([self almostNewTierDays]) {
        self.headerType = EHIRewardsBenefitsHeaderTypeAlmostDays;
    } else if(totalPoints >= EHIPointsThreshold) {
        self.headerType = EHIRewardsBenefitsHeaderTypePoints;
    } else {
        self.headerType = EHIRewardsBenefitsHeaderTypeDefault;
    }
    
    [self constructHeader];
}

- (void)constructHeader
{
    EHIRewardsBenefitsHeaderType type = self.headerType;
    NSInteger daysToNextGoal = self.remaningDaysToNextTier;
    
    switch (type) {
        case EHIRewardsBenefitsHeaderTypeDefault: {
            self.headerTitle     = EHILocalizedString(@"rewards_welcome_back", @"Welcome back #{name}", @"");
            self.headerTitle     = [self.headerTitle ehi_applyReplacementMap:@{
                                       @"name" : [EHIUser currentUser].firstName
                                    }];
            self.headerSubtitle  = @"";
            self.headerImageName = @"rewards_sun_down_header";
            break;
        }
        case EHIRewardsBenefitsHeaderTypeSilver: {
            self.headerTitle     = EHILocalizedString(@"rewards_welcome_you_made_tier", @"You made #{tier} tier!", @"");
            self.headerTitle     = [self.headerTitle ehi_applyReplacementMap:@{
                                       @"tier" : EHILoyaltyTierTitleForTier(self.loyaltyTier)
                                   }];
            
            self.headerSubtitle  = EHILocalizedString(@"rewards_welcome_tier_info", @"You are now getting #{percent} bonus points and #{number} free upgrade", @"");
            self.headerSubtitle  = [self.headerSubtitle ehi_applyReplacementMap:@{
                                       @"percent" : EHILoyaltyTierBonusForTier(self.loyaltyTier),
                                       @"number"  : EHILoyaltyTierUpgradesInYearForTier(self.loyaltyTier)
                                    }];
            self.headerImageName  = @"rewards_fireworks_header";
            break;
        }
        case EHIRewardsBenefitsHeaderTypeGold: {
            self.headerTitle     = EHILocalizedString(@"rewards_welcome_you_made_tier", @"You made #{tier} tier!", @"");
            self.headerTitle     = [self.headerTitle ehi_applyReplacementMap:@{
                                       @"tier" : EHILoyaltyTierTitleForTier(self.loyaltyTier)
                                   }];
            
            self.headerSubtitle  = EHILocalizedString(@"rewards_welcome_tier_info_plural", @"You are now getting #{percent} bonus points and #{number} free upgrades", @"");
            self.headerSubtitle  = [self.headerSubtitle ehi_applyReplacementMap:@{
                                       @"percent" : EHILoyaltyTierBonusForTier(self.loyaltyTier),
                                       @"number"  : EHILoyaltyTierUpgradesInYearForTier(self.loyaltyTier)
                                    }];
            self.headerImageName = @"rewards_fireworks_header";
            break;
        }
        case EHIRewardsBenefitsHeaderTypePlatinum: {
            self.headerTitle     = EHILocalizedString(@"rewards_welcome_you_made_tier", @"You made #{tier} tier!", @"");
            self.headerTitle     = [self.headerTitle ehi_applyReplacementMap:@{
                                       @"tier" : EHILoyaltyTierTitleForTier(self.loyaltyTier)
                                   }];
            
            self.headerSubtitle  = EHILocalizedString(@"rewards_welcome_tier_info_plural", @"You are now getting #{percent} bonus points and #{number} free upgrades", @"");
            self.headerSubtitle  = [self.headerSubtitle ehi_applyReplacementMap:@{
                                       @"percent" : EHILoyaltyTierBonusForTier(self.loyaltyTier),
                                       @"number"  : EHILoyaltyTierUpgradesInYearForTier(self.loyaltyTier)
                                    }];
            self.headerImageName = @"rewards_fireworks_header";
            break;
        }
        case EHIRewardsBenefitsHeaderTypeNewMember: {
            self.headerTitle     = EHILocalizedString(@"enroll_confirmation_title", @"Welcome to Enterprise Plus!", @"");
            self.headerSubtitle  = EHILocalizedString(@"rewards_welcome_earn_points_toward_free_rental_day", @"Earn points toward free rental days and elite status with every qualifying rental", @"");
            self.headerImageName = @"rewards_sun_up_header";
            break;
        }
        case EHIRewardsBenefitsHeaderTypeAlmostRentals:{
            self.headerTitle    = EHILocalizedString(@"rewards_welcome_you_are_almost_there", @"You're almost there", @"");
            self.headerSubtitle = EHILocalizedString(@"rewards_welcome_almost_next_tier", @"Just #{number} more rental until you reach #{tier} tier", @"");
            self.headerSubtitle = [self.headerSubtitle ehi_applyReplacementMap:@{
                                      @"number": @"1",
                                      @"tier"  : EHILoyaltyTierTitleForTier(self.goalTier)
                                   }];
            self.headerImageName = @"rewards_default_header";
            break;
        }
        case EHIRewardsBenefitsHeaderTypeAlmostDays: {
            self.headerTitle        = EHILocalizedString(@"rewards_welcome_you_are_almost_there", @"You're almost there", @"");
            self.headerSubtitle = daysToNextGoal > 1
                ? EHILocalizedString(@"rewards_welcome_almost_gold_platinum_tier_plural", @"Just #{days} more rental days until you reach the next tier", @"")
                : EHILocalizedString(@"rewards_welcome_almost_gold_platinum_tier", @"Just #{day} more rental day until you reach the next tier", @"");
            
            self.headerSubtitle     = [self.headerSubtitle ehi_applyReplacementMap:@{
                                          @"days": @(daysToNextGoal).description,
                                          @"day" : @(daysToNextGoal).description
                                      }];
            self.headerImageName    = @"rewards_default_header";
            break;
        }
        case EHIRewardsBenefitsHeaderTypePoints:
        {
            self.headerTitle     = EHILocalizedString(@"rewards_welcome_back", @"Welcome back #{name}", @"");
            self.headerTitle     = [self.headerTitle ehi_applyReplacementMap:@{
                                       @"name" : [EHIUser currentUser].firstName
                                   }];
            NSString *subTileOne = EHILocalizedString(@"rewards_welcome_you_have_a_lot_of_points", @"You have a lot of points!", @"");
            NSString *subTileThree = EHILocalizedString(@"rewards_welcome_start_a_new_rental", @"Start a new rental to see if you can get a free day!", @"");
            self.headerSubtitle  = [NSString stringWithFormat:@"%@\n%@",subTileOne, subTileThree];
            self.headerImageName = @"rewards_default_header";
            break;
        }
        default:
            break;
    }
}

- (BOOL)isNewMember
{
    EHIUserLoyaltyTier tier = self.loyaltyTier;
    NSInteger memberKey = self.tierLevel;
    BOOL hasMemberKey   = memberKey <= 0;
    BOOL validTier      = tier != EHIUserLoyaltyTierPlus || tier != EHIUserLoyaltyTierUnknown;
    BOOL zeroPoints     = self.userLoyalty.pointsToDate == 0;
    
    return zeroPoints && !hasMemberKey && validTier;
}

- (BOOL)isNewTier
{
    BOOL isPlusLevel = self.loyaltyTier      == EHIUserLoyaltyTierPlus;
    BOOL isUnknown   = [self savedTierLevel] == EHIUserLoyaltyTierUnknown;
    if(self.loyaltyTier != self.tierLevel && !isPlusLevel && !isUnknown) {
        return YES;
    }
    
    return NO;
}

- (BOOL)almostNewTierRentals
{
    NSInteger rentalsToNextGoal = self.userLoyalty.goal.remainingRentals;
    BOOL isPlatinum = self.loyaltyTier == EHIUserLoyaltyTierPlatinum;
    return rentalsToNextGoal <= EHIAlmostNextTierRentals && !isPlatinum;
}

- (BOOL)almostNewTierDays
{
    BOOL almostNewTierDays = self.remaningDaysToNextTier <= EHIAlmostNextTierDays;
    NSInteger memberKey    = self.loyaltyTier;
    BOOL silverOrGold      = memberKey == EHIUserLoyaltyTierSilver || memberKey == EHIUserLoyaltyTierGold;

    return almostNewTierDays && silverOrGold;
}

- (EHIRewardsBenefitsHeaderType)calculateNewTier
{
    EHIUserLoyaltyTier tier = self.loyaltyTier;
    switch(tier) {
        case EHIUserLoyaltyTierSilver:
            return EHIRewardsBenefitsHeaderTypeSilver;
        case EHIUserLoyaltyTierGold:
            return EHIRewardsBenefitsHeaderTypeGold;
        case EHIUserLoyaltyTierPlatinum:
            return EHIRewardsBenefitsHeaderTypePlatinum;
        default:
            return EHIRewardsBenefitsHeaderTypeDefault;
    }
}

- (EHIUserLoyaltyTier)loyaltyTier
{
    return self.userLoyalty.tier;
}

- (EHIUserLoyaltyTier)goalTier
{
    return self.userLoyalty.goal.tier;
}

- (NSInteger)remaningDaysToNextTier
{
    return self.userLoyalty.goal.remainingRentalDays;
}

- (NSInteger)savedTierLevel
{
    EHIUserLoyalty *loyalty = self.userLoyalty;
    EHIUserLoyaltyTier tierLevel = [EHISettings tierForLoyalty:loyalty];
    
    return tierLevel;
}

@end
