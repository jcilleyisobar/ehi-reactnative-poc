//
//  EHITierItemViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 1/3/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIUserLoyaltyGoal.h"

typedef NS_ENUM(NSInteger, EHIAboutEnterprisePlusTierType) {
    EHIAboutEnterprisePlusTierTypeGoals,
    EHIAboutEnterprisePlusTierTypeBenefits
};

@interface EHITierItemViewModel : EHIViewModel <MTRReactive>

- (instancetype)initWithTier:(EHIUserLoyaltyTier)tier type:(EHIAboutEnterprisePlusTierType)type;

@property (copy  , nonatomic, readonly) NSString *firstInfoTitle;
@property (copy  , nonatomic, readonly) NSString *firstInfo;
@property (copy  , nonatomic, readonly) NSString *orTitle;
@property (copy  , nonatomic, readonly) NSString *secondInfoTitle;
@property (copy  , nonatomic, readonly) NSString *secondInfo;

@end
