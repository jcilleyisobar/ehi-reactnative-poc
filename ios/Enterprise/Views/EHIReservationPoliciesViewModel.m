//
//  EHIReservationPoliciesViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 4/9/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIReservationPoliciesViewModel.h"
#import "NSAttributedString+Construction.h"
#import "EHIReservationBuilder.h"

@interface EHIReservationPoliciesViewModel ()
@property (strong, nonatomic) EHIReservation *reservation;

@end

@implementation EHIReservationPoliciesViewModel

- (instancetype)initWithModel:(id)model
{
    if (self = [super initWithModel:model]) {
        self.reservation = model;
        _headerText = EHILocalizedString(@"reservation_about_your_rental_section_title", @"ABOUT YOUR RENTAL", @"");
        _headerDetails = EHILocalizedString(@"reservation_about_your_rental_section_subtitle", @"We don't want you to have any unwanted surprises when hiring a car.", @"");
        _keyFactsButtonText = EHILocalizedString(@"reservation_about_your_rental_key_facts_link", @"Key Rental Facts", @"");
        
        _keyFactsDetails = EHIAttributedStringBuilder.new
            .text(EHILocalizedString(@"reservation_about_your_rental_key_facts_subtitle", @"An overview of your rental terms.", @""))
            .newline
            .append([NSAttributedString attributedStringListWithItems:@[
                EHILocalizedString(@"reservation_about_your_rental_key_facts_inclusions_exclusions", @"Inclusions & Exclusions", @""),
                EHILocalizedString(@"reservation_about_your_rental_key_facts_additional_costs", @"Potential Additional Costs & more", @"")
            ] formatting:NO]).string;
        
        _policyButtonText = EHILocalizedString(@"reservation_about_your_rental_rental_policies_link", @"Rental Specific Policies", @"");
        _policyDetails = self.reservation.businessLeisureGenericDisclaimer.ehi_stripHtml ?: EHILocalizedString(@"reservation_about_your_rental_rental_policies_subtitle", @"A detailed look at your rental.", @"");
    }
    return self;
}

#pragma mark - Actions

- (void)showPolicies
{
    [EHIAnalytics trackAction:EHIAnalyticsResActionTerms handler:nil];
    
    self.router.transition
        .push(EHIScreenPolicies).object(self.reservation.policies).start(nil);
}

- (void)showKeyFacts
{
    self.router.transition
        .push(EHIScreenKeyFacts).object(self.reservation).start(nil);
}

#pragma mark - Accessors

- (BOOL)shouldHideKeyFacts
{
    return !self.reservation.keyFactsPolicies.count || !self.reservation.isEuropeanUnion;
}

- (BOOL)shouldHidePolicies
{
    return !self.reservation.policies.count;
}

@end
