//
//  EHICalendarHeaderViewModel.m
//  Enterprise
//
//  Created by Ty Cobb on 3/24/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICalendarHeaderViewModel.h"

@interface EHICalendarHeaderViewModel ()
@property (strong, nonatomic) EHICalendarDay *day;
@property (copy  , nonatomic) NSString *monthTitle;
@end

@implementation EHICalendarHeaderViewModel

- (void)updateWithModel:(EHICalendarDay *)day
{
    [super updateWithModel:day];

    if([day isKindOfClass:[EHICalendarDay class]]) {
        self.day = day;
    }
}

# pragma mark - Day

- (void)setDay:(EHICalendarDay *)day
{
    if(_day != day) {
        _day = day;
        self.monthTitle = day.monthTitle;
    }
}

# pragma mark - Accessors

- (NSString *)closedTitle
{
    return EHILocalizedString(@"reservation_calendar_closed_dates", @"CLOSED DATES", @"Title for the 'CLOSED DATES' label");
}

- (NSArray *)weekdayTitles
{
    // get the values from our enum
    NSArray *weekdays = @(EHIWeekdaySunday).upTo(EHIWeekdaySaturday)
    // map them into abbreviated weekday strings
    .map(^(NSNumber *weekday) {
        NSDate *date = [NSDate ehi_weekday:weekday.integerValue];
        return [date ehi_stringForTemplate:@"EEEEE"];
    });
    
    // rotate the list based on the first day of the week. YOLOKit's rotate is broken.
    NSInteger pivot = EHIResolveWeekday(EHIWeekdayFirst) - 1;
    return weekdays.last(weekdays.count - pivot).concat(weekdays.first(pivot));
    
    return weekdays;   
}

# pragma mark - MTRReactive

+ (NSArray *)nonreactiveProperties:(EHICalendarHeaderViewModel *)model
{
    return @[
        @key(model.day),
    ];
}

@end
