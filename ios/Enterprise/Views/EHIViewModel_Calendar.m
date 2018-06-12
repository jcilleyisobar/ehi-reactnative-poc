//
//  EHIViewModel_Calendar.m
//  Enterprise
//
//  Created by Rafael Ramos on 10/04/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import <objc/runtime.h>
#import "EHIViewModel_Calendar.h"
#import "EHICalendarManager.h"
#import "EHIToastManager.h"
#import "EHIReservation.h"

static void *EHIViewModelCalendarReservationKey;

@implementation EHIViewModel (Calendar)

# pragma mark - Accessors

- (EHIReservation *)reservation
{
     return objc_getAssociatedObject(self, &EHIViewModelCalendarReservationKey);
}

- (void)setReservation:(EHIReservation *)reservation
{
     objc_setAssociatedObject(self, &EHIViewModelCalendarReservationKey, reservation, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}

- (void)addToCalendar
{
    [EHICalendarManager addEventForReservation:self.reservation handler:^(BOOL success, NSError *error) {
        NSString *message = success
            ? EHILocalizedString(@"confirmation_header_calendar_toast_success", @"ADDED TO CALENDAR", @"toast after successfully adding a reservation to the calendar")
            : EHILocalizedString(@"confirmation_header_calendar_toast_fail", @"COULD NOT CREATE EVENT", @"toast when adding reservation to the calendar failed");
        
        [EHIToastManager showMessage:message];
        
        if(error && [self respondsToSelector:@selector(addCalendarEventError:)]) {
            [self performSelector:@selector(addCalendarEventError:) withObject:error];
        }
        
        if(!error && [self respondsToSelector:@selector(addCalendarEventSuccess)]) {
            [self performSelector:@selector(addCalendarEventSuccess) withObject:nil];
        }
    }];
}

@end
