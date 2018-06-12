//
//  EHIExtrasTermsViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 16/04/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIExtrasTermsViewModel.h"
#import "EHIReservationBuilder.h"

@implementation EHIExtrasTermsViewModel

# pragma mark - Acccessors

- (NSString *)title
{
    return EHILocalizedString(@"eu_terms_footer_text", @"Rental Terms & Conditions", @"");
}

# pragma mark - Actions

- (void)showTerms
{
    EHIReservation *reservation = [EHIReservationBuilder sharedInstance].reservation;
    self.router
        .transition
        .present(EHIScreenTermsAndConditions)
        .object(reservation)
        .start(nil);
}

@end
