//
//  EHICalendarViewModel.h
//  Enterprise
//
//  Created by Ty Cobb on 3/23/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHICalendarDay.h"

@interface EHICalendarViewModel : EHIViewModel <MTRReactive>

/** Title for the calendar screen */
@property (copy, nonatomic, readonly) NSString *title;
/** Title for the calendar's action button */
@property (copy, nonatomic, readonly) NSString *actionTitle;
/** @c YES if the action button should be enabled */
@property (assign, nonatomic, readonly) BOOL actionIsDisabled;
/** The total number of days visible on the calendar */
@property (assign, nonatomic, readonly) NSInteger numberOfDays;
/** The total number of months visible on the calendar */
@property (assign, nonatomic, readonly) NSInteger numberOfMonths;
/** The day corresponding to the first visible month */
@property (strong, nonatomic, readonly) EHICalendarDay *firstDayForActiveMonth;
/** The range, indexed on the first date, of currently selected dates; @c NSRangeNull if nothing is selected */
@property (assign, nonatomic, readonly) NSRange selectedRange;
/** @c YES if the range has both a pickup and return index */
@property (assign, nonatomic, readonly) BOOL selectedRangeIsComplete;
/** @c YES if the location hours are currently loading */
@property (assign, nonatomic, readonly) BOOL isLoading;

/** Returns the calendar day, if available, for the the specific index path */
- (EHICalendarDay *)dayAtIndexPath:(NSIndexPath *)indexPath;
/** Returns the calendar day, if available, for the first day in a specific month */
- (EHICalendarDay *)firstDayInMonthAtIndexPath:(NSIndexPath *)indexPath;
/** Returns the index of the given date in the entire calendar */
- (NSInteger)rowIndexForDate:(NSDate *)date;

/** Selects either the start- or end- date at the specified index path */
- (void)selectDateAtIndexPath:(NSIndexPath *)indexPath;
/** Sets the active month to the month corresponding to this index path */
- (void)updateActiveMonthWithIndexPath:(NSIndexPath *)indexPath;
/** Transitions to the next in the itinerary flow */
- (void)initiateTransition;
/** Shows the toast for the current scheduling step */
- (void)showToast;

@end
