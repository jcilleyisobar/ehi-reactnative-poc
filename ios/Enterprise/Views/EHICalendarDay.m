//
//  EHIReservationCalendarDay.m
//  Enterprise
//
//  Created by Ty Cobb on 3/24/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICalendarDay.h"
#import "Reactor.h"

@implementation EHICalendarDay

+ (void)initialize
{
    [super initialize];
    
    [MTRReactor reactify:self.class];
}

- (instancetype)initWithDate:(NSDate *)date
{
    if(self = [super init]) {
        _date = date;
    }
    
    return self;
}

# pragma mark - Accessors

- (NSString *)title
{
    return [self.date ehi_stringForTemplate:@"d"];
}

- (NSString *)monthTitle
{
    return [self.date ehi_stringForTemplate:@"MMMM yyy"];
}

- (BOOL)isFirstInMonth
{
    return [self.date ehi_isFirstInMonth];
}

- (BOOL)isFirstInWeek
{
    return [self.date ehi_isWeekday:EHIWeekdayFirst];
}

- (BOOL)isLastInWeek
{
    return [self.date ehi_isWeekday:EHIWeekdayLast];
}

- (BOOL)isToday
{
    return [self.date ehi_isToday];
}

- (BOOL)isSelectable
{
    return self.isWithinSelectableRange && self.isOpen;
}

# pragma mark - MTRReactive

+ (NSArray *)reactiveProperties:(EHICalendarDay *)day
{
    return @[
        @key(day.isWithinActiveMonth),
        @key(day.isOpen),
    ];
}

@end
