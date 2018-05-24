//
//  EHILocationDetailsHoursViewModel.m
//  Enterprise
//
//  Created by Ty Cobb on 2/26/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILocationDetailsHoursViewModel.h"
#import "EHILocationTimesSlice.h"
#import "EHILocationTimes.h"

@interface EHILocationDetailsHoursViewModel ()
@property (copy  , nonatomic) NSString *date;
@property (copy  , nonatomic) NSString *time;
@property (assign, nonatomic) BOOL isToday;
@end

@implementation EHILocationDetailsHoursViewModel

- (void)updateWithModel:(EHILocationTimesSlice *)times
{
    [super updateWithModel:times];
    
    self.date = [self dateFromTimes:times];
    self.time = [self timeFromTimes:times];
    self.isToday = times.times.isToday;
}

# pragma mark - Translation

- (NSString *)dateFromTimes:(EHILocationTimesSlice *)times
{
    // if this is not the first time, don't display the date
    if(!times.isFirstSlice) {
        return nil;
    }
    // return the custom string if this date is today
    else if(times.times.isToday) {
        return EHILocalizedString(@"location_details_today", @"TODAY", @"Date title for the 'Open Today' hours");
    }
    
    // otherwise, format the date normally
    return times.times.displayText;
}

- (NSString *)timeFromTimes:(EHILocationTimesSlice *)times
{
    if (times.times.isUnavailable) {
        return EHILocalizedString(@"location_hours_unavailable_label", @"UNAVAILABLE", @"time slice unavailable");
    }
    else if(times.times.isOpenAllDay) {
        return EHILocalizedString(@"location_details_open_all_day", @"Open 24 Hours", @"Text for location times 'Open All Day'");
    }
    else if(times.times.isClosedAllDay) {
        return EHILocalizedString(@"location_details_hours_closed", @"CLOSED", @"Text for location times 'Closed All Day'");
    }
    
    return times.displayText;
}

@end
