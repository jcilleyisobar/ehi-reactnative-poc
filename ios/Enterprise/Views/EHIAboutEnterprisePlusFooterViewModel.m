//
//  EHIAboutEnterprisePlusFooterViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 12/23/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIAboutEnterprisePlusFooterViewModel.h"
#import "EHIViewModel_Subclass.h"
#import "EHIWebViewModel.h"

@implementation EHIAboutEnterprisePlusFooterViewModel

- (NSString *)title
{
    return EHILocalizedString(@"eu_terms_screen_title", @"Terms & Conditions", @"");
}

# pragma mark - Actions

- (void)showDetails
{
    [EHIAnalytics trackAction:EHIAnalyticsRewardBenefitsAuthActionTerms handler:nil];
    
    [[[EHIWebViewModel alloc] initWithType:EHIWebContentTypeTermsAndConditions] present];
}

@end
