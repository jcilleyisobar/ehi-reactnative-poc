//
//  EHIRewardsBenefitsAuthViewModel.m
//  Enterprise
//
//  Created by frhoads on 12/22/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIRewardsBenefitsAuthViewModel.h"
#import "EHIViewModel_Subclass.h"
#import "EHIUserManager.h"
#import "EHIRewardsBenefitsPointsViewModel.h"
#import "EHIRewardsBenefitsStatusViewModel.h"
#import "EHISectionHeaderModel.h"
#import "EHIRewardsBenefitsTierGaugeViewModel.h"
#import "EHIRewardsBenefitsHeaderViewModel.h"
#import "EHISettings.h"

@interface EHIRewardsBenefitsAuthViewModel()
@property (strong, nonatomic) EHIModel *legalModel;
@property (assign, nonatomic) BOOL showLegal;
@end

@implementation EHIRewardsBenefitsAuthViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _title       = EHILocalizedString(@"rewards_title", @"Enterprise Plus", @"");
        _footerTitle = EHILocalizedString(@"rewards_welcome_enterprise_plus_program_details", @"ENTERPRISE PLUS PROGRAM DETAILS", @"");
        
        _headerModel = [[EHIRewardsBenefitsHeaderViewModel alloc] initWithModel:self.loyalty];
        _statusModel = [[EHIRewardsBenefitsStatusViewModel alloc] initWithModel:self.loyalty];
        _pointsModel = [[EHIRewardsBenefitsPointsViewModel alloc] initWithModel:self.loyalty];
        
        // platinum user's don't have a next tier area
        if(self.loyalty.tier != EHIUserLoyaltyTierPlatinum) {
            _tierGaugeModel = [[EHIRewardsBenefitsTierGaugeViewModel alloc] initWithModel:self.loyalty];
        }
    }
    return self;
}

- (void)selectItemAtIndexPath:(NSIndexPath *)indexPath
{
    //[self invalidateAnalyticsContext];
    
    switch(indexPath.section) {
        case EHIRewardsBenefitsSectionsPoints:{
            [EHIAnalytics trackAction:EHIAnalyticsRewardBenefitsAuthActionAboutPoints handler:nil];
            [self showPoints];
            break;
        }
        case EHIRewardsBenefitsSectionsStatus:{
            [EHIAnalytics trackAction:EHIAnalyticsRewardBenefitsAuthProgramDetails handler:nil];
            [self showStatus];
            break;
        }
        case EHIRewardsBenefitsSectionFooter:{
            [self showAboutEnterprisePlus];
            break;
        }
        default:
            break;
    }
    
    
}

- (void)showPoints
{
    self.router.transition
        .push(EHIScreenAboutPointsScreen)
        .start(nil);
}

- (void)showStatus
{
    self.router.transition
        .push(EHIScreenRewardsAboutTiers)
        .start(nil);
}

- (void)showAboutEnterprisePlus
{
    self.router.transition
        .push(EHIScreenAboutEnterprisePlus)
        .start(nil);
}

- (EHISectionHeaderModel *)headerForSection:(EHIRewardsBenefitsSection)section
{
    EHISectionHeaderModel *model = nil;
    switch(section) {
        case EHIRewardsBenefitsSectionsPoints:
            model = [EHISectionHeaderModel modelWithTitle:EHILocalizedString(@"rewards_welcome_your_points_and_tier_status", @"YOUR POINTS AND TIER STATUS", @"")];
            break;
        case EHIRewardsBenefitsSectionNextTier:
            model = [EHISectionHeaderModel modelWithTitle:EHILocalizedString(@"rewards_banner_next_tier", @"NEXT TIER", @"")];
            model.dividerStyle = EHISectionHeaderDividerStyleFancy;
            break;
        case EHIRewardsBenefitsSectionFooter:
        case EHIRewardsBenefitsSectionHeader:
        case EHIRewardsBenefitsSectionsStatus:
        case EHIRewardsBenefitsSectionLegal:
            return nil;
    }
    
    model.style = EHISectionHeaderStyleWrapText;
    
    return model;
}

# pragma mark - Passthrough

- (EHIUserBasicProfile *)profile
{
    return [EHIUser currentUser].profiles.basic;
}

- (EHIUserLoyalty *)loyalty
{
    return self.profile.loyalty;
}

- (EHIModel *)footerModel
{
    return [EHIModel placeholder];
}

- (EHIModel *)legalModel
{
    if(!_legalModel) {
        _legalModel = self.showLegal ? [EHIModel placeholder] : nil;
    }
    
    return _legalModel;
}

- (BOOL)showLegal
{
    EHIUserLoyaltyTier tierLevel = [EHISettings tierForLoyalty:self.loyalty];

    BOOL isPlusLevel = self.loyalty.tier == EHIUserLoyaltyTierPlus;
    BOOL isUnknown   = tierLevel == EHIUserLoyaltyTierUnknown;

    if(self.loyalty.tier != tierLevel && !isPlusLevel && !isUnknown) {
        [EHISettings saveTierOfLoyalty:self.loyalty];
        return YES;
    } else {
        // consider `EHIUserLoyaltyTierUnknown` as `null`
        if(tierLevel == EHIUserLoyaltyTierUnknown) {
            [EHISettings saveTierOfLoyalty:self.loyalty];
        }
        return NO;
    }
}

@end
