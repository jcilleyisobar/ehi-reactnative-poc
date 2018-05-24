//
//  EHIOnboardingBenefitsViewModel.m
//  Enterprise
//
//  Created by Stu Buchbinder on 1/18/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIOnboardingBenefitsViewModel.h"

@implementation EHIOnboardingBenefitsViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _title = EHILocalizedString(@"rewards_plus_benefits_title", @"Plus many more benefits", @"");
    }
    return self;
}

- (NSArray<NSString *> *)benefits
{
    return @[
             EHILocalizedString(@"rewards_first_benefit_description", @"With tiers, the more you rent the more you earn", @""),
             EHILocalizedString(@"rewards_second_benefit_description", @"No blackout dates for redeeming free rental days", @""),
             EHILocalizedString(@"rewards_third_benefit_description", @"Earn yearly free upgrades", @"")];
}

@end
