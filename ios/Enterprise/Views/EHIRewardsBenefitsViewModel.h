//
//  EHIRewardsBenefitsViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 6/11/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIUser.h"

@interface EHIRewardsBenefitsViewModel : EHIViewModel <MTRReactive>

@property (strong, nonatomic) NSAttributedString *plusBenefits;
@property (strong, nonatomic) NSAttributedString *silverBenefits;
@property (strong, nonatomic) NSAttributedString *goldBenefits;
@property (strong, nonatomic) NSAttributedString *platinumBenefits;
@property (assign, nonatomic) EHIUserLoyaltyTier selectedTier;
@property (assign, nonatomic, readonly) EHIUserLoyaltyTier tier;

@end
