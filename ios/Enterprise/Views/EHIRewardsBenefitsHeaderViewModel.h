//
//  EHIRewardsBenefitsHeaderViewModel.h
//  Enterprise
//
//  Created by frhoads on 1/9/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

typedef NS_ENUM(NSUInteger, EHIRewardsBenefitsHeaderType) {
    EHIRewardsBenefitsHeaderTypeDefault,
    EHIRewardsBenefitsHeaderTypeSilver,
    EHIRewardsBenefitsHeaderTypeGold,
    EHIRewardsBenefitsHeaderTypePlatinum,
    EHIRewardsBenefitsHeaderTypeNewMember,
    EHIRewardsBenefitsHeaderTypeAlmostRentals,
    EHIRewardsBenefitsHeaderTypeAlmostDays,
    EHIRewardsBenefitsHeaderTypePoints,
};

@interface EHIRewardsBenefitsHeaderViewModel : EHIViewModel
@property (assign, nonatomic) EHIRewardsBenefitsHeaderType headerType;
@property (copy  , nonatomic) NSString *headerTitle;
@property (copy  , nonatomic) NSString *headerSubtitle;
@property (copy  , nonatomic) NSString *headerImageName;
@end
