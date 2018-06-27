//
//  EHIPromotionDetailsPolicyCellViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 3/28/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIPromotionDetailsPolicyCellViewModel.h"
#import "EHIWebViewModel.h"
#import "EHIAnalytics.h"

@implementation EHIPromotionDetailsPolicyCellViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _policiesButtonTitle = EHILocalizedString(@"weekend_special_terms_and_conditions_button_title", @"TERMS & CONDITIONS", @"");
    }
    
    return self;
}

- (void)didTapPolicies
{
    [EHIAnalytics trackAction:EHIAnalyticsWkndPromoDetailsActionTerms handler:nil];

    NSString *htmlString = [NSLocale ehi_country].weekendSpecial.termsAndConditions;
    [[[EHIWebViewModel alloc] initWithType:EHIWebContentTypeWeekendSpecialTermsAndConditions htmlString:htmlString] push];
}

@end
