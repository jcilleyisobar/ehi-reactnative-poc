//
//  EHIKeyFactsFooterViewModel.m
//  Enterprise
//
//  Created by fhu on 11/4/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHIKeyFactsFooterViewModel.h"
#import "EHIServices+Reservation.h"
#import "EHIReservationBuilder.h"
#import "EHIDataStore.h"
#import "EHICountry.h"

@interface EHIKeyFactsFooterViewModel()
@property (strong, nonatomic) EHIReservation *reservation;
@end

@implementation EHIKeyFactsFooterViewModel


- (void)updateWithModel:(id)model
{
    self.footerText  = EHILocalizedString(@"key_facts_rules_of_the_road_traffic_laws", @"Your rental is subject to traffic laws.", @"");
    self.footerLinkText = EHILocalizedString(@"key_facts_rules_of_the_road_view", @"Your rental is subject to traffic laws.", @"");
    self.shouldHideTopDivider = YES;
    if([model isKindOfClass:[EHIReservation class]]) {
        self.reservation = model;
    }
}

#pragma mark - Accessors

- (void)setReservation:(EHIReservation *)reservation
{
    _reservation = reservation;
    EHICountry *country = self.reservation.returnLocation.address.country;
    self.phoneLinkText = country.disputePhone;
    self.emailLinkText = country.disputeEmail;
    self.subHeaderText = nil;
}

# pragma mark - Actions

- (void)emailTapped
{
    [UIApplication ehi_promptUrl:self.emailLinkText];
}

- (void)footerTapped
{
    [UIApplication ehi_promptUrl:self.reservation.rulesOfTheRoadUrl];
}

- (void)phoneTapped
{
    [UIApplication ehi_promptPhoneCall:self.phoneLinkText];
}

@end
