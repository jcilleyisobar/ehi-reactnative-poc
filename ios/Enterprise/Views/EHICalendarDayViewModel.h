//
//  EHICalendarDayViewModel.h
//  Enterprise
//
//  Created by Ty Cobb on 3/23/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHICalendarDay.h"

typedef NS_ENUM(NSInteger, EHICalendarDayStyle) {
    EHICalendarDayStyleNone,
    EHICalendarDayStyleSolo,
    EHICalendarDayStyleCombined,
    EHICalendarDayStyleLeft,
    EHICalendarDayStyleRight,
};

@interface EHICalendarDayViewModel : EHIViewModel <MTRReactive>
/** The title for the day */
@property (copy, nonatomic, readonly) NSString *title;
/** @c YES if this day is selectable for reservation */
@property (assign, nonatomic, readonly) BOOL isSelectable;
/** @c YES if the day is within the active month */
@property (assign, nonatomic, readonly) BOOL isWithinActiveMonth;
/** @c YES if the day is today */
@property (assign, nonatomic, readonly) BOOL isToday;
/** Determines which endpoint, if any, of the selection this day is */
@property (assign, nonatomic) EHICalendarDayStyle style;
@end
