//
//  EHIDashboardExtendRentalViewModel.m
//  Enterprise
//
//  Created by mplace on 6/17/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIDashboardExtendRentalViewModel.h"
#import "EHIViewModel_Subclass.h"
#import "EHIUserRental.h"
#import "EHIConfiguration.h"

@interface EHIDashboardExtendRentalViewModel ()
@property (strong, nonatomic) EHIUserRental *activeRental;
@end

@implementation EHIDashboardExtendRentalViewModel

- (instancetype)initWithModel:(id)model
{
    if (self = [super initWithModel:model]) {
        _title = EHILocalizedString(@"dashboard_current_rental_extend_rental_button_title", @"EXTEND RENTAL", @"title for the extend rental modal");
        _detailsTitle = EHILocalizedString(@"dashboard_extend_rental_details", @"Need more time? Call us and we will help you out.", @"details for the extend rental modal");
        _reservationTitle = EHILocalizedString(@"dashboard_extend_rental_reservation_title", @"Your Confirmation Number:", @"reservation title for the extend rental modal");
        _actionButtonTitle = EHILocalizedString(@"standard_button_call", @"CALL US", @"action button title for the extend rental modal");
    }
    
    return self;
}

- (void)updateWithModel:(id)model
{
    [super updateWithModel:model];
    
    if([model isKindOfClass:[EHIUserRental class]]) {
        self.activeRental = model;
    }
}

- (void)setActiveRental:(EHIUserRental *)activeRental
{
    _activeRental = activeRental;
    
    self.ticketNumber           = activeRental.ticketNumber;
    self.shouldHideTicketNumber = activeRental.ticketNumber.length == 0;
}

# pragma mark - Actions

- (void)performAction
{
    // prompt to call customer support
    [UIApplication ehi_promptPhoneCall:[EHIConfiguration configuration].primarySupportPhone.number];
}

# pragma mark - Navigation

- (void)dismiss
{
    self.router.transition
        .dismiss.start(nil);
}

@end
