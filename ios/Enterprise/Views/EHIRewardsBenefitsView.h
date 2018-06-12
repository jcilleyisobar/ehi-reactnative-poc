//
//  EHIRewardsBenefitsView.h
//  Enterprise
//
//  Created by Alex Koller on 6/11/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIView.h"

@interface EHIRewardsBenefitsView : EHIView

@end

@protocol EHIRewardsBenefitsViewActions <NSObject>
- (void)rewardsBenefitsViewDidLayoutTier:(EHIRewardsBenefitsView *)sender;
@end