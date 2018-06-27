//
//  NSDate+Formatting.m
//  Enterprise
//
//  Created by Ty Cobb on 1/5/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "NSDate+Formatting.h"
#import "NSDate+MaskingTests.h"

#define EHIDateServicesLocaleIdentifer @"en_US_POSIX"

#define EHIDateTemplateDate @"EEEMMMdd"

#define EHIDateFormatDate @"yyyy-MM-dd"
#define EHIDateFormatDateTime @"yyyy-MM-dd'T'HH:mm"
#define EHIDateFormatDateTimeWeekday @"E, yyyy-MM-dd'T'HH:mm"
#define EHIDateFormatDateTimeTimeZone @"yyyy-MM-dd'T'HH:mm:ss.SSSZ"

@implementation NSDate (Formatting)

# pragma mark - Localization

- (NSString *)ehi_localizedDateString
{
    return [[self.class ehi_localizedFormatterWithTemplate:EHIDateTemplateDate] stringFromDate:self];
}

- (NSString *)ehi_localizedShortDateString
{
    return [[self.class ehi_localizedFormatterWithDateStyle:NSDateFormatterShortStyle timeStyle:NSDateFormatterNoStyle] stringFromDate:self];
}

- (NSString *)ehi_localizedMediumDateString
{
    return [[self.class ehi_localizedFormatterWithDateStyle:NSDateFormatterMediumStyle timeStyle:NSDateFormatterNoStyle] stringFromDate:self];
}

- (NSString *)ehi_localizedTimeString
{
    return [[self.class ehi_localizedFormatterWithDateStyle:NSDateFormatterNoStyle timeStyle:NSDateFormatterShortStyle] stringFromDate:self];
}

- (NSString *)ehi_stringForTemplate:(NSString *)template
{
    return [[self.class ehi_localizedFormatterWithTemplate:template] stringFromDate:self];
}

+ (NSString *)ehi_localizedMaskedDate:(NSString *)maskedDate
{
    return [self ehi_localizedMaskedDate:maskedDate usingLocale:NSLocale.autoupdatingCurrentLocale];
}

# pragma mark - Formats

- (NSString *)ehi_dateTimeString
{
    return [NSDate ehi_stringFromDate:self withFormat:EHIDateFormatDateTime];
}

- (NSString *)ehi_stringWithFormat:(NSString *)format
{
    return [NSDate ehi_stringFromDate:self withFormat:format];
}

# pragma mark - Defaults

- (NSString *)ehi_string
{
    return [[self ehi_stringWithFormat:EHIDateFormatDate] uppercaseString];
}

+ (NSString *)ehi_stringFromInterval:(NSInteger)interval
{
    return [[self ehi_stringFromInterval:interval withFormat:EHIDateFormatDate] uppercaseString];
}

# pragma mark - Stringifiers

+ (NSString *)ehi_stringFromDate:(NSDate *)date withFormat:(NSString *)format
{
    return [[self ehi_formatterForFormat:format] stringFromDate:date];
}

+ (NSString *)ehi_stringFromInterval:(NSInteger)interval withFormat:(NSString *)format
{
    return [[NSDate dateWithTimeIntervalSince1970:interval] ehi_stringWithFormat:format];
}

# pragma mark - Dateifiers

+ (NSDate *)ehi_dateFromString:(NSString *)string withFormat:(NSString *)format
{
    NSDateFormatter *formatter = [self ehi_formatterForFormat:format].copy;
    NSDate *date = [formatter dateFromString:string];
    if(!date) {
        formatter.lenient = YES;
        date = [formatter dateFromString:string];
    }
    return date;
}

# pragma mark - Formatter Caching

+ (NSDateFormatter *)ehi_localizedFormatterWithDateStyle:(NSDateFormatterStyle)dateStyle timeStyle:(NSDateFormatterStyle)timeStyle
{
    NSString *key = [NSString stringWithFormat:@"com.EHIDateFormatDate.%@.%lu.%lu", [NSTimeZone localTimeZone].name, (unsigned long)dateStyle, (unsigned long)timeStyle];
    NSDateFormatter *formatter = [self formatterForKey:key];
    
    if(!formatter) {
        formatter = [self createForKey:key];
        
        formatter.dateStyle = dateStyle;
        formatter.timeStyle = timeStyle;
    }
    
    return formatter;
}

+ (NSDateFormatter *)ehi_localizedFormatterWithTemplate:(NSString *)template
{
    if(!template) {
        return nil;
    }
    
    NSString *key = [NSString stringWithFormat:@"com.EHIDateFormatDate.%@.%@", [NSTimeZone localTimeZone].name, template];
    NSDateFormatter *formatter = [self formatterForKey:key];
    
    if(!formatter) {
        formatter = [self createForKey:key];
        formatter.dateFormat = [NSDateFormatter dateFormatFromTemplate:template options:0 locale:formatter.locale];
    }
    
    return formatter;
}

+ (NSDateFormatter *)ehi_formatterForFormat:(NSString *)format
{
    if(!format) {
        return nil;
    }
   
    NSString *key = [NSString stringWithFormat:@"com.EHIDateFormatDateting.%@.%@", [NSTimeZone localTimeZone].name, format];
    NSDateFormatter *formatter = [self formatterForKey:key];
    
    if(!formatter) {
        formatter = [self createForKey:key];
        formatter.dateFormat = format;
        formatter.locale = [NSLocale localeWithLocaleIdentifier:EHIDateServicesLocaleIdentifer];
    }
    
    return formatter;
}

//
// Helpers
//

+ (NSDateFormatter *)formatterForKey:(NSString *)key
{
    return NSThread.currentThread.threadDictionary[key];
}

+ (NSDateFormatter *)createForKey:(NSString *)key
{
    NSDateFormatter *formatter = [NSDateFormatter new];
    formatter.locale    = NSLocale.autoupdatingCurrentLocale;
    formatter.timeZone  = NSTimeZone.localTimeZone;
    
    NSThread.currentThread.threadDictionary[key] = formatter;
    
    return formatter;
}

@end

@implementation NSString (Dates)

- (NSDate *)ehi_date
{
    return [self ehi_dateWithFormat:EHIDateFormatDate];
}

- (NSDate *)ehi_dateTime
{
    return [self ehi_dateWithFormat:EHIDateFormatDateTime];
}

- (NSDate *)ehi_dateTimeWeekday
{
    return [self ehi_dateWithFormat:EHIDateFormatDateTimeWeekday];
}

- (NSDate *)ehi_dateTimeTimeZone
{
    return [self ehi_dateWithFormat:EHIDateFormatDateTimeTimeZone];
}

- (NSDate *)ehi_dateWithFormat:(NSString *)format
{
    return [NSDate ehi_dateFromString:self withFormat:format];
}

@end
