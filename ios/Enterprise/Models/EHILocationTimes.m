//
//  EHILocationDay.m
//  Enterprise
//
//  Created by Ty Cobb on 1/20/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILocationTimes.h"
#import "EHIModel_Subclass.h"

@interface EHILocationTimes ()
@property (assign, nonatomic) BOOL isUnavailable;
@end

@implementation EHILocationTimes

+ (instancetype)modelWithDictionary:(NSDictionary *)dictionary
{
    EHILocationTimes *day = [super modelWithDictionary:dictionary];
    
    if (!day.slices) {
        // we need to add a dummy slice to make sure the UNAVAILABLE day shows up
        day.slices = (NSArray<EHILocationTimesSlice> *)@[[EHILocationTimesSlice new]];
        day.isUnavailable = YES;
    }
    
    // set the parent on each of this day's times
    for(EHILocationTimesSlice *times in day.slices) {
        times.times = day;
    }
    
    return day;
}

- (void)parseDictionary:(NSMutableDictionary *)dictionary
{
    [super parseDictionary:dictionary];
    
    NSString *dateString = dictionary[@key(self.date)];
    NSDate *date = [dateString ehi_date];
    if(date) {
        dictionary[@key(self.date)] = date;
    }
}

# pragma mark - Helpers

- (BOOL)isOpenForDate:(NSDate *)date
{
    if (self.isClosedAllDay) {
        return NO;
    }
    // If we get back an empty array of slices all times should be selectable
    if(self.isOpenAllDay || !self.slices.count) {
        return YES;
    }
    
    // we only care about the hour/minute portion of dates within here
    NSDate *time = [date ehi_clampComponents:NSCalendarUnitTime];
    
    for(EHILocationTimesSlice *slice in self.slices) {
        NSDate *openTime = [slice.open ehi_clampComponents:NSCalendarUnitTime];
        NSDate *closeTime = [slice.close ehi_clampComponents:NSCalendarUnitTime];
        
        if([time ehi_isBetweenDate:openTime andDate:closeTime]) {
            return YES;
        }
    }
    
    return NO;
}

- (BOOL)doesOpenAtDate:(NSDate *)date
{
    if(self.isOpenAllDay) {
        return NO;
    }
    
    // search the time slices if this location is not open all day
    NSDate *time = [date ehi_time];
    NSDate *next = [time ehi_addMinutes:30];
    for(EHILocationTimesSlice *slice in self.slices) {
        // if the open is between this time and the next (inclusive) time, then this is a boundary
        NSDate *open = [slice.open ehi_time];
            
        BOOL timeBeforeOpen    = [time ehi_compare:open granularity:NSCalendarUnitTime] == NSOrderedAscending;
        BOOL nextAtOrAfterOpen = [open ehi_compare:next granularity:NSCalendarUnitTime] != NSOrderedDescending;
            
        if(timeBeforeOpen && nextAtOrAfterOpen) {
            return YES;
        }
    }
    
    return NO;
}

- (BOOL)doesCloseAtDate:(NSDate *)date
{
    if(self.isOpenAllDay) {
        return NO;
    }
    
    // search the time slices if this location is not open all day
    NSDate *time = [date ehi_time];
    NSDate *next = [time ehi_addMinutes:30];
    for(EHILocationTimesSlice *slice in self.slices) {
        // if the close is between this (inclusive) time and the next time, then this is a boundary
        NSDate *close = [slice.close ehi_time];
    
        BOOL timeAtOrBeforeClose = [time ehi_compare:close granularity:NSCalendarUnitTime] != NSOrderedDescending;
        BOOL nextAfterClose      = [close ehi_compare:next granularity:NSCalendarUnitTime] == NSOrderedAscending;
        
        if(timeAtOrBeforeClose && nextAfterClose) {
            return YES;
        }
    }
    
    return NO;
}

# pragma mark - Accessors

- (NSString *)displayText
{
    return [self.date ehi_localizedDateString];
}

- (BOOL)isToday
{
    return [self.date ehi_isToday];
}

# pragma mark - Mappings

+ (NSDictionary *)mappings:(EHILocationTimes *)model
{
    return @{
        @"hours"            : @key(model.slices),
        @"open_close_times" : @key(model.slices),
        @"open_all_day"     : @key(model.isOpenAllDay),
        @"open24"           : @key(model.isOpenAllDay),
        @"open24Hours"      : @key(model.isOpenAllDay),
        @"closed_all_day"   : @key(model.isClosedAllDay),
        @"closed"           : @key(model.isClosedAllDay),
        @"day"              : @key(model.name),
    };
}

@end
