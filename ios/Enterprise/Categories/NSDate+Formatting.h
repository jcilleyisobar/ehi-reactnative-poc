//
//  NSDate+Formatting.h
//  Enterprise
//
//  Created by Ty Cobb on 1/5/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@import Foundation;

@interface NSDate (Formatting)

/** Returns a localized date string for presentation */
- (NSString *)ehi_localizedDateString;
/** Returns a localized short date string for presentation */
- (NSString *)ehi_localizedShortDateString;
/** Returns a localized medium date string for presentation */
- (NSString *)ehi_localizedMediumDateString;
/** Returns a localized time string for presentation */
- (NSString *)ehi_localizedTimeString;
/** Returns a localized string using the given template */
- (NSString *)ehi_stringForTemplate:(NSString *)string;

/** Returns a string using the standard services date format */
- (NSString *)ehi_string;
/** Returns a string using the standard services date-time format */
- (NSString *)ehi_dateTimeString;
/** Returns a string using the given date format */
- (NSString *)ehi_stringWithFormat:(NSString *)format;
/** Returns a localized date using the given masked date string, based on current locale */
+ (NSString *)ehi_localizedMaskedDate:(NSString *)maskedDate;

@end

@interface NSString (Dates)

/** Generates a date using standard service date format */
- (NSDate *)ehi_date;
/** Generates a date assuming standard service date-time format */
- (NSDate *)ehi_dateTime;
/** Generates a date assuming EHI's weekday-date-time format */
- (NSDate *)ehi_dateTimeWeekday;
/** Generates a date assuming EHI's date-time-time-zone format */
- (NSDate *)ehi_dateTimeTimeZone;
/** Generate a date using the specified date format */
- (NSDate *)ehi_dateWithFormat:(NSString *)format;

@end
