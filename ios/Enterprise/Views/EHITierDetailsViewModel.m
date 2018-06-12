//
//  EHITierDetailsViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 1/19/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHITierDetailsViewModel.h"

@interface EHITierDetailsViewModel ()
@property (assign, nonatomic) EHIUserLoyaltyTier tier;
@property (strong, nonatomic) EHITierItemViewModel *goalsModel;
@property (strong, nonatomic) EHITierItemViewModel *benefitsModel;
@end

@implementation EHITierDetailsViewModel

- (instancetype)initWithTier:(EHIUserLoyaltyTier)tier
{
    if(self = [super init]) {
        self.tier = tier;
    }
    
    return self;
}

- (void)setTier:(EHIUserLoyaltyTier)tier
{
    _tier = tier;
    self.goalsModel    = [[EHITierItemViewModel alloc] initWithTier:tier
                                                                                  type:EHIAboutEnterprisePlusTierTypeGoals];
    self.benefitsModel = [[EHITierItemViewModel alloc] initWithTier:tier
                                                                                  type:EHIAboutEnterprisePlusTierTypeBenefits];
}

# pragma mark - Goals

- (NSString *)goalTitle
{
    return EHILocalizedString(@"about_e_p_tier_reach_title", @"TO REACH THIS TIER YOU NEED:", @"");
}

# pragma mark - Benefits

-(NSString *)benefitsTitle
{
    return EHILocalizedString(@"about_e_p_tier_benefits_title", @"BENEFITS INCLUDE:", @"");
}

@end
