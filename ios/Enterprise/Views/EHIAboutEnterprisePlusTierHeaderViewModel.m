//
//  EHIAboutEnterprisePlusTierHeaderViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 12/23/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIAboutEnterprisePlusTierHeaderViewModel.h"

@implementation EHIAboutEnterprisePlusTierHeaderViewModel

- (NSString *)title
{
    return EHILocalizedString(@"about_e_p_tiers_title", @"How tiers work", @"");
}

- (NSString *)firstLine
{
    return EHILocalizedString(@"about_e_p_tiers_detail_first_line", @"The more you rent,", @"");
}

- (NSString *)secondLine
{
    return EHILocalizedString(@"about_e_p_tiers_detail_second_line", @"the more you earn.", @"");
}

@end
