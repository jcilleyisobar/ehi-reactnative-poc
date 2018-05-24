//
//  EHIDashboardExtendRentalViewModel.h
//  Enterprise
//
//  Created by mplace on 6/17/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIDashboardExtendRentalViewModel : EHIViewModel <MTRReactive>
/** Title for the extend rental modal */
@property (copy, nonatomic, readonly) NSString *title;
/** Details text for the extend rental modal */
@property (copy, nonatomic, readonly) NSString *detailsTitle;
/** Reservation title for the extend rental modal */
@property (copy, nonatomic, readonly) NSString *reservationTitle;
/** Title for the action button */
@property (copy, nonatomic, readonly) NSString *actionButtonTitle;
/** Reservation ticket number on the active rental */
@property (copy, nonatomic) NSString *ticketNumber;
/** @YES if there is no ticket number to display */
@property (assign, nonatomic) BOOL shouldHideTicketNumber;

/** Prompt to call return location */
- (void)performAction;
/** Dismiss modal */
- (void)dismiss;

@end
