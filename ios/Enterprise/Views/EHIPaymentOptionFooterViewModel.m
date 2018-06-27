//
//  EHIPaymentOptionFooterViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 10/10/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIPaymentOptionFooterViewModel.h"
#import "EHIViewModel_Subclass.h"
#import "EHIWebViewModel.h"
#import "EHIReservationBuilder.h"

@implementation EHIPaymentOptionFooterViewModel

- (NSString *)prepayTitle
{
    return EHILocalizedString(@"eu_terms_footer_text", @"Rental Terms & Conditions", @"");
}

- (NSString *)termTitle
{
    return EHILocalizedString(@"reservation_payment_cancellation_policy_text", @"Payment Cancellation Policy", @"");
}

# pragma mark - Actions

- (void)showPrepay
{
    EHIReservation *reservation = [EHIReservationBuilder sharedInstance].reservation;
    self.router
        .transition
        .present(EHIScreenTermsAndConditions)
        .object(reservation)
        .start(nil);
}

-(void)showTerms
{
    [EHIAnalytics trackAction:EHIAnalyticsResActionPrepayPolicy handler:nil];
    
    [[[EHIWebViewModel alloc] initWithType:EHIWebContentTypePrepayTermsAndConditions] present];
}

@end
