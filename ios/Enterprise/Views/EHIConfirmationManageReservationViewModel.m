//
//  EHIConfirmationManageReservationViewModel.m
//  Enterprise
//
//  Created by Bruno Fernandes Campos on 9/14/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIConfirmationManageReservationViewModel.h"
#import "EHICalendarManager.h"
#import "EHIToastManager.h"

@interface EHIConfirmationManageReservationViewModel ()
@property (assign, nonatomic) BOOL shouldExpand;
@end

@implementation EHIConfirmationManageReservationViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _title               = EHILocalizedString(@"confirmation_manage_reservation_title", @"MANAGE YOUR RESERVATION", @"");
        _calendarButtonTitle = EHILocalizedString(@"confirmation_header_calendar_button_title", @"ADD TO CALENDAR", @"title for calendar button");
        _modifyButtonTitle   = EHILocalizedString(@"reservation_confirmation_minimal_modify_title", @"MODIFY", @"Title for the confirmation screen modify button");
        _cancelButtonTitle   = EHILocalizedString(@"reservation_confirmation_minimal_cancel_title", @"CANCEL", @"Title for the confirmation screen cancel button");
        _shouldExpand = NO;
    }
    
    return self;
}

# pragma mark - Actions

- (void)toggleViewClicked
{
    self.shouldExpand = !self.shouldExpand;
}

@end
