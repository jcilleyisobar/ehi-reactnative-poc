//
//  EHILocationTimesSlice.m
//  Enterprise
//
//  Created by Ty Cobb on 1/20/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel_Subclass.h"
#import "EHILocationTimesSlice.h"
#import "EHILocationTimes.h"

@implementation EHILocationTimesSlice


- (void)parseDictionary:(NSMutableDictionary *)dictionary
{
    [super parseDictionary:dictionary];
    
    [self dictionary:dictionary convertDateForKey:@key(self.open)];
    [self dictionary:dictionary convertDateForKey:@key(self.close)];
}

- (void)dictionary:(NSMutableDictionary *)dictionary convertDateForKey:(NSString *)key
{
    NSString *timeString = [dictionary[key] stringByReplacingOccurrencesOfString:@":" withString:@""];
    // get the integer time the service sends us (HHMM)
    NSInteger serviceTime = [timeString integerValue];
    // convert that to an NSDate with today as the day
    [dictionary setValue:[NSDate ehi_dateFromTime:serviceTime] forKey:key];
}

# pragma mark - Accessors

- (NSString *)displayText
{
    // convert our dates to strings
    NSString *open  = self.open.ehi_localizedTimeString;
    NSString *close = self.close.ehi_localizedTimeString;
   
    return [NSString stringWithFormat:@"%@ - %@", open, close];
}

- (BOOL)isFirstSlice
{
    if(!self.times) {
        EHIDomainDebug(EHILogDomainModels, @"didn't set the times properly for: %@", self);
    }
    
    return [self.times.slices indexOfObject:self] == 0;
}

# pragma mark - Mappings

+ (NSDictionary *)mappings:(EHILocationTimesSlice *)model
{
    return @{
        @"open_time"  : @key(model.open),
        @"close_time" : @key(model.close),
    };
}

@end
