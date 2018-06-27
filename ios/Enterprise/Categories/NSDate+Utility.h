//
//  NSDate+Utility.h
//  Enterprise
//
//  Created by Ty Cobb on 2/26/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

typedef NS_ENUM(NSInteger, EHIWeekday) {
    EHIWeekdayFirst,
    EHIWeekdaySunday,
    EHIWeekdayMonday,
    EHIWeekdayTuesday,
    EHIWeekdayWednesday,
    EHIWeekdayThursday,
    EHIWeekdayFriday,
    EHIWeekdaySaturday,
    EHIWeekdayLast,
};

#define EHIHoursPerDay   24
#define EHIDaysPerWeek   (EHIWeekdaySaturday)
#define EHIDaysPerYear   (365)
#define EHISecondsPerDay (24 * 60 * 60)

#define NSCalendarUnitTime (NSCalendarUnitHour | NSCalendarUnitMinute)

#define NSCalendarDayGranularity        (NSCalendarUnitYear | NSCalendarUnitMonth | NSCalendarUnitDay)
#define NSCalendarMinuteGranularity     (NSCalendarDayGranularity | NSCalendarUnitHour | NSCalendarUnitMinute)
#define NSCalendarWeekOfYearGranularity (NSCalendarUnitYearForWeekOfYear |  NSCalendarUnitWeekOfYear | NSCalendarUnitWeekday)

@interface NSDate (Utility)

/** @c YES if the date takes place today */
@property (nonatomic, readonly) BOOL ehi_isToday;

/** Returns a date at midnight today */
+ (NSDate *)ehi_today;

/** 
 @brief Creates a date for the specified time, today
    
 Pass-through to @c ehi_dateFromDate:time:, with today the @c date parameter. See
 that method for complete documentation.
*/

+ (NSDate *)ehi_dateFromTime:(NSInteger)time;

/**
 @brief Creates a date for the specified time
 
 The previous time components of the @c date are discarded. The time is expected 
 to be an integer in the format @c HHMM.

 @param day  The date to set the time for
 @param time An integer representing the time
 
 @return An @c NSDate for this date and time specified
*/

+ (NSDate *)ehi_dateFromDate:(NSDate *)date time:(NSInteger)time;

/**
 @brief Creates a date for the specified date and time
 
 The previous time components of the @c date are discarded.
 
 @param day  The date to set the time for
 @param time the date to use for the time component
 
 @return An @c NSDate for this date and time specified
 */

+ (NSDate *)ehi_dateFromDate:(NSDate *)date timeDate:(NSDate *)time;

/**
 @brief Finds the weekday within the current week
 This is a pass-through to @c +ehi_weekday:forWeekOfDate:
*/

+ (NSDate *)ehi_weekday:(EHIWeekday)weekday;

/**
 @brief Finds the weekday for the week of the given date
 
 @param weekday The weekday to find
 @param date    The date to source the week from
 
 @return The date for this weekday
*/

+ (NSDate *)ehi_weekday:(EHIWeekday)weekday forWeekOfDate:(NSDate *)date;


/**
 @brief Calculates the janurary first of next year date
 
 @return The next year january first date
 */

+ (NSDate *)ehi_januaryFirstOfNextYear;

/**
 @brief Calculates the janurary first of this year date
 
 @return This year january first date
 */

+ (NSDate *)ehi_januaryFirstOfThisYear;


/**
 @brief The standalone month symbols for the receiver.
 
 @return An array with all the month names
 */
+ (NSArray *)monthNames;

/**
 Returns the number of days that lie between the receiver and the future date. Returns 
 zero if the future date is @c nil.
 
 @param futureDate A date in the future
 @return An integer value of days between the receiver and the future date
*/

- (NSInteger)ehi_daysUntilDate:(NSDate *)futureDate;

/**
 Returns the number of hours that lie between the receiver and the future date. Returns
 zero if the future date is @c nil.
 
 @param futureDate A date in the future
 @return An integer value of hours between the receiver and the future date
*/

- (NSInteger)ehi_hoursUntilDate:(NSDate *)futureDate;

/**
 Compares the dates across the specified unit
 
 @param other       The date to compare to the receiver
 @param granularity The unit granularity to compare across
*/

- (NSComparisonResult)ehi_compare:(NSDate *)other granularity:(NSCalendarUnit)granularity;

/**
 @brief Tests if the dates are equal across the specified units
 
 This method is a pass-through to @c ehi_comapre:granularity:
*/

- (BOOL)ehi_isEqual:(NSDate *)other granularity:(NSCalendarUnit)granularity;

/**
 @brief Returns the components for this date in the current calendar
 The calendar / time zone are added automatically to the parameterized @c units.
 @param units The calendar units to componentize
*/

- (NSDateComponents *)ehi_components:(NSCalendarUnit)units;

/**
 @brief Recreates the receiver using only the specified units
 
 @param units The calendar units with which to destruct and reconstruct the receiver
*/
- (NSDate *)ehi_clampComponents:(NSCalendarUnit)units;

/**
 Returns whether the reveiver lies within the date range. Inclusive on both ends.
 
 @param start The start date for the range
 @param end   The end date for the range
 
 @return @c YES if the date is between the parameterized dates
*/

- (BOOL)ehi_isBetweenDate:(NSDate *)start andDate:(NSDate *)end;
/** Returns @c YES if the date is the parameterized weekday */
- (BOOL)ehi_isWeekday:(EHIWeekday)weekday;
/** Returns @c YES if the date is the first in its month */
- (BOOL)ehi_isFirstInMonth;
/** Returns @c YES if the receiver is before the parameterized date */
- (BOOL)ehi_isBefore:(NSDate *)date;
/** Returns @c YES if the receiver is after the parameterized date */
- (BOOL)ehi_isAfter:(NSDate *)date;
/** Returns @c YES if the receiver is the same as the parameterized date */
- (BOOL)ehi_isSame:(NSDate *)date;
/** Returns @c YES if the receiver is some time in the past */
- (BOOL)ehi_isPast;
/** Returns @c YES if the receiver is some time in the future */
- (BOOL)ehi_isFuture;

/** Returns a new date by adding @c days to the receiver date */
- (NSDate *)ehi_addDays:(NSInteger)days;
/** Returns a new date by adding @c months to the receiver date */
- (NSDate *)ehi_addMonths:(NSInteger)months;
/** Returns a new date by adding @c minutes to the receiver date */
- (NSDate *)ehi_addMinutes:(NSInteger)minutes;

/** Returns the value for the given unit from the receiver */
- (NSInteger)ehi_valueForUnit:(NSCalendarUnit)unit;
/** Returns the day that is the weekday of the receiver's week */
- (NSDate *)ehi_weekday:(EHIWeekday)weekday;
/** Returns the first date in the receiver's month */
- (NSDate *)ehi_firstInMonth;
/** Returns the last date in the receiver's month */
- (NSDate *)ehi_lastInMonth;
/** Returns a date with non-time components masked out */
- (NSDate *)ehi_time;

/** Resolves this weekday into the canonical spectrum, mapping first / last accordingly */
extern EHIWeekday EHIResolveWeekday(EHIWeekday weekday);

@end
