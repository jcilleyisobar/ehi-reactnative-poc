//
//  NSDate+Utility.m
//  Enterprise
//
//  Created by Ty Cobb on 2/26/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "NSDate+Utility.h"

#define NSCalendarCalendarUnits (NSCalendarUnitTimeZone | NSCalendarUnitCalendar)

@implementation NSDate (Utility)

+ (NSDate *)ehi_today
{
    NSDateComponents *components = [[NSDate date] ehi_components:NSCalendarDayGranularity];
    return [components date];
}

+ (NSDate *)ehi_dateFromTime:(NSInteger)time
{
    return [self ehi_dateFromDate:[NSDate date] time:time];
}

+ (NSDate *)ehi_dateFromDate:(NSDate *)date time:(NSInteger)time
{
    NSDateComponents *components = [date ehi_components:NSCalendarDayGranularity];
    
    components.hour   = time / 100;
    components.minute = time % 100;
    
    return [components date];
}

+ (NSDate *)ehi_dateFromDate:(NSDate *)date timeDate:(NSDate *)time
{
    NSDateComponents *dateComponents = [date ehi_components:NSCalendarDayGranularity];
    NSDateComponents *timeComponents = [time ehi_components:NSCalendarMinuteGranularity];
    
    dateComponents.hour   = timeComponents.hour;
    dateComponents.minute = timeComponents.minute;
    
    return [dateComponents date];
}


+ (NSDate *)ehi_weekday:(EHIWeekday)weekday
{
    return [self ehi_weekday:weekday forWeekOfDate:[NSDate date]];
}

+ (NSDate *)ehi_weekday:(EHIWeekday)weekday forWeekOfDate:(NSDate *)date
{
    NSDateComponents *components = [date ehi_components:NSCalendarWeekOfYearGranularity];
    components.weekday = EHIResolveWeekday(weekday);
    
    return [components date];
}

+ (NSArray *)monthNames
{
    NSDateFormatter *formatter = [NSDateFormatter new];
    formatter.locale = [NSLocale autoupdatingCurrentLocale];
    
    return [formatter standaloneMonthSymbols];
}

+ (NSDate *)ehi_januaryFirstOfNextYear
{
    NSDateComponents *components = [[NSDate date] ehi_components:NSCalendarDayGranularity];
    [components setDay:1];
    [components setMonth:1];
    [components setYear:components.year + 1];
    return [[NSCalendar currentCalendar] dateFromComponents:components];
}

+ (NSDate *)ehi_januaryFirstOfThisYear
{
    NSDateComponents *components = [[NSDate date] ehi_components:NSCalendarDayGranularity];
    [components setDay:1];
    [components setMonth:1];
    return [[NSCalendar currentCalendar] dateFromComponents:components];
}

# pragma mark - Operations

- (NSInteger)ehi_valueForUnit:(NSCalendarUnit)unit
{
    return [[NSCalendar currentCalendar] component:unit fromDate:self];
}

- (NSDate *)ehi_addDays:(NSInteger)days
{
    return [[NSCalendar currentCalendar] dateByAddingUnit:NSCalendarUnitDay value:days toDate:self options:kNilOptions];
}

- (NSDate *)ehi_addMonths:(NSInteger)months
{
    return [[NSCalendar currentCalendar] dateByAddingUnit:NSCalendarUnitMonth value:months toDate:self options:kNilOptions];
}

- (NSDate *)ehi_addMinutes:(NSInteger)minutes
{
    return [[NSCalendar currentCalendar] dateByAddingUnit:NSCalendarUnitMinute value:minutes toDate:self options:kNilOptions];
}

- (NSDate *)ehi_weekday:(EHIWeekday)weekday
{
    return [NSDate ehi_weekday:weekday forWeekOfDate:self];
}

- (NSDate *)ehi_firstInMonth
{
    NSDateComponents *components = [self ehi_components:NSCalendarDayGranularity];
    components.day = 1;
    return [components date];
}

- (NSDate *)ehi_lastInMonth
{
    NSDateComponents *components = [self ehi_components:NSCalendarDayGranularity];
    components.month += 1;
    components.day = 0;
    return [components date];
}

# pragma mark - Evaluation

- (BOOL)ehi_isToday
{
    return [self ehi_isEqual:[NSDate date] granularity:NSCalendarDayGranularity];
}

- (BOOL)ehi_isWeekday:(EHIWeekday)weekday
{
    NSDateComponents *components =
        [[NSCalendar currentCalendar] components:NSCalendarUnitWeekday fromDate:self];
    return components.weekday == EHIResolveWeekday(weekday);
}

- (BOOL)ehi_isFirstInMonth
{
    NSDateComponents *components =
        [[NSCalendar currentCalendar] components:NSCalendarUnitDay fromDate:self];
    return components.day == 1;
}

- (BOOL)ehi_isBetweenDate:(NSDate *)start andDate:(NSDate *)end
{
    return [self compare:start] != NSOrderedAscending && [self compare:end] != NSOrderedDescending;
}

- (BOOL)ehi_isEqual:(NSDate *)other granularity:(NSCalendarUnit)granularity
{
    NSComparisonResult result = [self ehi_compare:other granularity:granularity];
    return result == NSOrderedSame;
}

- (BOOL)ehi_isBefore:(NSDate *)date
{
    return [self compare:date] == NSOrderedAscending;
}

- (BOOL)ehi_isAfter:(NSDate *)date
{
    return [self compare:date] == NSOrderedDescending;
}

- (BOOL)ehi_isSame:(NSDate *)date
{
    return [self compare:date] == NSOrderedSame;
}

- (BOOL)ehi_isPast
{
    return [self ehi_isBefore:[NSDate date]];
}

- (BOOL)ehi_isFuture
{
    return [self ehi_isAfter:[NSDate date]];
}

- (NSDate *)ehi_time
{
    return [[self ehi_components:NSCalendarMinuteGranularity] date];
}

- (NSComparisonResult)ehi_compare:(NSDate *)other granularity:(NSCalendarUnit)granularity
{
    NSDate *date1 = [self ehi_components:granularity].date;
    NSDate *date2 = [other ehi_components:granularity].date;
    
    return [date1 compare:date2];
}

- (NSInteger)ehi_daysUntilDate:(NSDate *)futureDate
{
    if(!futureDate) {
        return 0;
    }
   
    NSDateComponents *components =
        [[NSCalendar currentCalendar] components:NSCalendarUnitDay fromDate:self toDate:futureDate options:kNilOptions];
    
    return [components day];
}

- (NSInteger)ehi_hoursUntilDate:(NSDate *)futureDate
{
    if(!futureDate) {
        return 0;
    }
    
    NSDateComponents *components =
        [[NSCalendar currentCalendar] components:NSCalendarUnitHour fromDate:self toDate:futureDate options:kNilOptions];
    
    return [components hour];
}

# pragma mark - Componentizing

- (NSDateComponents *)ehi_components:(NSCalendarUnit)units
{
    NSDateComponents *components = [[NSCalendar currentCalendar] components:units | NSCalendarCalendarUnits fromDate:self];
    return components;
}

- (NSDate *)ehi_clampComponents:(NSCalendarUnit)units
{
    return [[self ehi_components:units] date];
}

# pragma mark - Helpers

EHIWeekday EHIResolveWeekday(EHIWeekday weekday)
{
    // if this is the first wekday, resolve to whatever the calendar specifies
    if(weekday == EHIWeekdayFirst) {
        return NSCalendar.currentCalendar.firstWeekday;
    }
    // if this is the last weekday, use the weekday one day behind the first weekday
    else if(weekday == EHIWeekdayLast) {
        return NSRangeWrap((NSRange){ .location = EHIWeekdaySunday, .length = 6 }, NSCalendar.currentCalendar.firstWeekday - 1);
    }
    
    return weekday;
}

@end
