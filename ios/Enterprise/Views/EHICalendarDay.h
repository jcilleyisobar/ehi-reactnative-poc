//
//  EHIReservationCalendarDay.h
//  Enterprise
//
//  Created by Ty Cobb on 3/24/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "Reactor.h"

@interface EHICalendarDay : NSObject <MTRReactive>

/** The index of this day in the overall list of days */
@property (assign, nonatomic) NSUInteger index;
/** @c YES if this day is within the active month */
@property (assign, nonatomic) BOOL isWithinActiveMonth;
/** @c YES if the location is open on this day */
@property (assign, nonatomic) BOOL isOpen;
/** @c YES if the day can be selected for reservation */
@property (assign, nonatomic) BOOL isWithinSelectableRange;
/** The stringified name for this day */
@property (copy, nonatomic, readonly) NSString *title;
/** The stringified name for this day's month */
@property (copy, nonatomic, readonly) NSString *monthTitle;

/** The @c NSDate corresponding to this calendar day */
@property (strong, nonatomic, readonly) NSDate *date;
/** @c YES if this day is the first in its month */
@property (assign, nonatomic, readonly) BOOL isFirstInMonth;
/** @c YES if this day is the first in its week */
@property (assign, nonatomic, readonly) BOOL isFirstInWeek;
/** @c YES if this day is the last in its week */
@property (assign, nonatomic, readonly) BOOL isLastInWeek;
/** @c YES if the day lands on a week that borders two months */
@property (assign, nonatomic, readonly) BOOL isWithinBorderWeek;
/** @c YES if the day is today */
@property (assign, nonatomic, readonly) BOOL isToday;
/** @c YES if the day is selectable */
@property (assign, nonatomic, readonly) BOOL isSelectable;

/** Constructs a new calendar day from the date */
- (instancetype)initWithDate:(NSDate *)date;

@end
