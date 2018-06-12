//
//  EHIRewardsBenefitsFooterViewModel.m
//  Enterprise
//
//  Created by frhoads on 1/5/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIRewardsBenefitsFooterViewModel.h"
#import "EHIViewModel_Subclass.h"

@implementation EHIRewardsBenefitsFooterViewModel

- (NSString *)title
{
    return EHILocalizedString(@"rewards_welcome_enterprise_plus_program_details", @"ENTERPRISE PLUS PROGRAM DETAILS", @"");
}

- (void)showAboutEnterprisePlus
{
    [EHIAnalytics trackAction:EHIAnalyticsRewardBenefitsAuthActionProgramDetails handler:nil];
    
    self.router.transition.push(EHIScreenAboutEnterprisePlus).start(nil);
}

@end
