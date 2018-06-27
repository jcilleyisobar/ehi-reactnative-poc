//
//  EHIRentalsFooterCellViewModel.m
//  Enterprise
//
//  Created by fhu on 4/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIRentalsFooterViewModel.h"
#import "EHIViewModel_Subclass.h"
#import "EHIConfiguration.h"
#import "EHIInfoModalViewModel.h"

@implementation EHIRentalsFooterViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _startRentalButtonText = EHILocalizedString(@"rentals_start_reservation_button", @"START A NEW RENTAL", @"start rental button text for rentals footer");
        _contactButtonText     = EHILocalizedString(@"rentals_footer_contact_button_text", @"CONTACT US", @"button text for contact button in rentals footer");
        _cannotFindButtonText  = EHILocalizedString(@"rentals_dont_see_rentals", @"I DON'T SEE MY RENTAL", @"");
        _lookupButtonText      = EHILocalizedString(@"standard_lookup_rental_button_text", @"LOOK UP RENTAL", @"standard lookup rental button text");
    }
    
    return self;
}

# pragma mark - Actions

- (void)startRental
{
    self.router.transition
        .push(EHIScreenLocations).start(nil);
}

- (void)lookupRental
{
    self.router.transition
    .present(EHIScreenRentalLookup).start(nil);
}

- (void)callHelpNumber
{
    [EHIAnalytics trackAction:EHIAnalyticsRentalsActionContactUs handler:nil];
    
    // TODO: verify this is correct
    [UIApplication ehi_promptPhoneCall:EHIConfiguration.configuration.primarySupportPhone.number];
}

- (void)cannotFindRental
{
    EHIInfoModalViewModel *modal = [EHIInfoModalViewModel new];
    modal.title = EHILocalizedString(@"rentals_cannot_find_title", @"Don't see your rental?", @"");
    modal.details = EHILocalizedString(@"rentals_cannot_find_details", @"At this time only rentals made at our airport locations can be tracked. We are working to include past reservations at all locations in the near future. For further assistance, please call our customer support.", @"");
    modal.firstButtonTitle  = EHILocalizedString(@"rentals_cannot_find_call_us_button", @"CALL US", @"");
    modal.secondButtonTitle = EHILocalizedString(@"standard_close_button", @"CLOSE", @"").uppercaseString;
    
    [modal present:^(NSInteger index, BOOL canceled) {
        if(index == 0) {
            [UIApplication ehi_promptPhoneCall:[EHIConfiguration configuration].primarySupportPhone.number];
        }
        
        return YES;
    }];
}

- (NSString *)contactButtonImageName
{
    return @"icon_phone_03";
}

# pragma mark - Generator

+ (instancetype)viewModelWithMode:(EHIRentalsMode)mode
{
    EHIRentalsFooterViewModel *viewModel = [EHIRentalsFooterViewModel new];
    viewModel.hidesFindButton = mode == EHIRentalsModeUpcoming;
    viewModel.hidesLookupButton = mode == EHIRentalsModePast;
    return viewModel;
}

@end
