//
//  EHICalendarManager.m
//  Enterprise
//
//  Created by cgross on 8/6/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICalendarManager.h"
#import <EventKit/EventKit.h>
#import "EHIConfiguration.h"

static EKEventStore *eventStore = nil;

@implementation EHICalendarManager

+ (void)requestAccess:(void (^)(BOOL granted, NSError *error))callback;
{
    if (eventStore == nil) {
        eventStore = [EKEventStore new];
    }
    
    // request permission
    [eventStore requestAccessToEntityType:EKEntityTypeEvent completion:callback];
}

+ (void)addEventForReservation:(EHIReservation *)reservation handler:(void (^)(BOOL success, NSError *error))handler
{
    [EHICalendarManager requestAccess:^(BOOL granted, NSError *error) {
        // bail out if no permission to create an event
        if (!granted) {
            dispatch_async(dispatch_get_main_queue(), ^{
                ehi_call(handler)(NO, error);
            });

            return;
        }
        
        NSString *titleText  = EHILocalizedString(@"calendar_event_title", @"Your Enterprise Car Rental - #{number}", @"calendar event title with confirmation number");
        titleText = [titleText ehi_applyReplacementMap:@{
            @"number" : reservation.confirmationNumber ?: @"",
        }];
        
        // remove previously created events with the same confirmation number to avoid duplicates
        // TODO: maybe we can check if an event already exists and update the button on the confirmation screen to "Remove From Calendar"
        NSPredicate *predicate = [eventStore predicateForEventsWithStartDate:[NSDate date]
                                                                     endDate:[NSDate dateWithTimeIntervalSinceNow:EHISecondsPerDay * EHIDaysPerYear]
                                                                   calendars:nil];
        
        [eventStore enumerateEventsMatchingPredicate:predicate usingBlock:^(EKEvent * _Nonnull event, BOOL * _Nonnull stop) {
            if ([event.title isEqualToString:titleText]) {
                NSError *error = nil;
                [eventStore removeEvent:event span:EKSpanThisEvent commit:YES error:&error];
                *stop = YES;
            }
        }];
        
        // location to show in the event
        EHILocation *eventLocation = reservation.pickupLocation;

        // event for the rental
        EKEvent *rentalEvent  = [EKEvent eventWithEventStore:eventStore];
        rentalEvent.calendar  = eventStore.defaultCalendarForNewEvents;
        rentalEvent.title     = titleText;
        rentalEvent.startDate = reservation.pickupTime;
        rentalEvent.endDate   = reservation.returnTime;
        rentalEvent.notes     = [NSString stringWithFormat:@"%@\n%@", eventLocation.localizedName, [EHIConfiguration configuration].primarySupportPhone.number];
        
        // add geolocation to show a map inside the event
        EKStructuredLocation* structuredLocation = [EKStructuredLocation locationWithTitle:eventLocation.localizedName];
        CLLocation* location = [[CLLocation new] initWithLatitude:eventLocation.position.latitude longitude:eventLocation.position.longitude];
        structuredLocation.geoLocation = location;
        [rentalEvent setValue:structuredLocation forKey:@"structuredLocation"];
        
        // add alarm
        EKAlarm *alarm = [EKAlarm alarmWithRelativeOffset: -1 * 60 * 15]; // default 15 minutes alert
        [rentalEvent addAlarm:alarm];
        
        // save event
        BOOL result = [eventStore saveEvent:rentalEvent span:EKSpanThisEvent commit:YES error:&error];
        
        dispatch_async(dispatch_get_main_queue(), ^{
            ehi_call(handler)(result, error);
        });
    }];
}

@end
