//
//  EHIViewModel_Calendar.h
//  Enterprise
//
//  Created by Rafael Ramos on 10/04/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@class EHIReservation;
@interface EHIViewModel (Calendar)

@property (strong, nonatomic) EHIReservation *reservation;

- (void)addToCalendar;

@end

@protocol EHIViewModelCalendarResult <NSObject> @optional
- (void)addCalendarEventSuccess;
- (void)addCalendarEventError:(NSError *)error;
@end
