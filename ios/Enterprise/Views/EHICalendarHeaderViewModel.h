//
//  EHICalendarHeaderViewModel.h
//  Enterprise
//
//  Created by Ty Cobb on 3/24/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHICalendarDay.h"

@interface EHICalendarHeaderViewModel : EHIViewModel <MTRReactive>

/** The text for the month label */
@property (copy, nonatomic, readonly) NSString *monthTitle;
/** The text for the "CLOSED DATES" label */
@property (copy, nonatomic, readonly) NSString *closedTitle;
/** The text for the weekday labels */
@property (copy, nonatomic, readonly) NSArray *weekdayTitles;

@end
