//
//  EHISingleDateCalendarViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 5/9/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHICalendarPlacardViewModel.h"
#import "EHICalendarDay.h"
#import "EHISingleDateCalendarEnums.h"

typedef void (^EHISingleDateCalendarHandler)(NSDate *pickupDate, NSDate *returnDate);

@class EHICalendarDay;
@interface EHISingleDateCalendarViewModel : EHIViewModel <MTRReactive>

@property (copy  , nonatomic, readonly) NSString *title;
@property (copy  , nonatomic, readonly) NSString *actionTitle;
@property (assign, nonatomic, readonly) NSInteger numberOfDays;
@property (assign, nonatomic, readonly) NSInteger numberOfMonths;
@property (strong, nonatomic, readonly) EHICalendarDay *firstDayForActiveMonth;
@property (strong, nonatomic, readonly) EHICalendarPlacardViewModel *placardModel;
@property (assign, nonatomic, readonly) NSRange selectedRange;
@property (strong, nonatomic) NSDate *pickupDate;
@property (strong, nonatomic) NSDate *returnDate;
@property (copy  , nonatomic) EHISingleDateCalendarHandler handler;
@property (assign, nonatomic) EHISingleDateCalendarFlow flow;

- (EHICalendarDay *)dayAtIndexPath:(NSIndexPath *)indexPath;
- (EHICalendarDay *)firstDayInMonthAtIndexPath:(NSIndexPath *)indexPath;
- (NSInteger)rowIndexForDate:(NSDate *)date;
- (void)selectDateAtIndexPath:(NSIndexPath *)indexPath;
- (void)updateActiveMonthWithIndexPath:(NSIndexPath *)indexPath;
- (void)didTapContinue;
/* Exposed for tests */
- (NSString *)validationMessage;
- (void)selectDay:(EHICalendarDay *)day;
@end
