//
//  EHIAboutEnterprisePlusHeaderViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 12/23/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIAboutEnterprisePlusHeaderViewModel.h"

@implementation EHIAboutEnterprisePlusHeaderViewModel

- (NSString *)title
{
    return EHILocalizedString(@"about_e_p_points_title", @"We've made it simple:", @"");
}

- (NSString *)detail
{
    return EHILocalizedString(@"about_e_p_points_detail", @"dollars spent equals points earned.", @"");
}

@end
