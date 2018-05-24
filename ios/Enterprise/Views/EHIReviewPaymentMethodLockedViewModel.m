//
//  EHIReviewPaymentMethodLockedViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 12/1/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIReviewPaymentMethodLockedViewModel.h"
#import "EHIViewModel_Subclass.h"
#import "EHIWebViewModel.h"

@implementation EHIReviewPaymentMethodLockedViewModel

# pragma mark - Accessors

- (NSString *)paymentTitle
{
    return EHILocalizedString(@"modify_payment_cant_modify_title", @"Payment cannot be modified", @"");
}

- (NSString *)termsTitle
{
    return EHILocalizedString(@"general_prepay_policies", @"Prepayment Policy Terms & Conditions", @"");
}

# pragma mark - Actions

- (void)showTerms
{
    [EHIAnalytics trackAction:EHIAnalyticsResActionPrepayPolicy handler:nil];
    
    [[[EHIWebViewModel alloc] initWithType:EHIWebContentTypePrepayTermsAndConditions] push];
}

@end
