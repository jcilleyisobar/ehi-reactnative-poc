//
//  EHISingleDateCalendarViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 5/9/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHISingleDateCalendarViewModel.h"
#import "EHIToast.h"
#import "EHIToastManager.h"
#import "EHIViewModel_Subclass.h"

typedef NS_ENUM(NSInteger, EHISingleDateCalendarDateValidation) {
    EHISingleDateCalendarDateValidationValid,
    EHISingleDateCalendarDateValidationInvalidPickup,
    EHISingleDateCalendarDateValidationInvalidReturn
};

static NSString *const kEHISingleDateCalendarMessageDateFormat = @"MMM dd";

@interface EHISingleDateCalendarViewModel ()
@property (assign, nonatomic) EHISingleDateCalendarType type;
@property (copy  , nonatomic) NSArray *days;
@property (copy  , nonatomic) NSArray *firstDays;
@property (strong, nonatomic) EHICalendarPlacardViewModel *placardModel;
@property (strong, nonatomic) EHICalendarDay *firstDayForActiveMonth;
@property (assign, nonatomic) NSRange selectedRange;
@property (assign, nonatomic) BOOL didShowToast;
@end

@implementation EHISingleDateCalendarViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        if([model isKindOfClass:NSNumber.class]) {
            _type = (EHISingleDateCalendarType)[model integerValue];
        }

        _selectedRange = NSRangeNull;
        _placardModel  = [[EHICalendarPlacardViewModel alloc] initWithModel:@(self.type)];
    }
    
    return self;
}

- (void)navigateBack
{
    [super navigateBack];
    
    [self hideToast];
}

# pragma mark - Accessors

- (void)setPickupDate:(NSDate *)pickupDate
{
    _pickupDate = pickupDate;
    
    EHICalendarDay *day = (self.days ?: @[]).find(^(EHICalendarDay *day){
        return [day.date isEqual:pickupDate];
    });
    
    if(self.type == EHISingleDateCalendarTypePickup){
        [self selectDay:day];
    }
}

- (void)setReturnDate:(NSDate *)returnDate
{
    _returnDate = returnDate;
    
    EHICalendarDay *day = (self.days ?: @[]).find(^(EHICalendarDay *day){
        return [day.date isEqual:returnDate];
    });
    
    if(self.type == EHISingleDateCalendarTypeReturn){
        [self selectDay:day];
    }
}

- (NSString *)actionTitle
{
    return EHILocalizedString(@"time_picker_button_title", @"SELECT" , @"");
}

- (NSString *)title
{
    if(self.type == EHISingleDateCalendarTypePickup) {
        return EHILocalizedString(@"date_select_pickup_title", @"Pick-up Date", @"");
    } else {
        return EHILocalizedString(@"date_select_return_title", @"Return Date", @"");
    }
}

- (NSArray *)days
{
    if(!_days) {
        NSDate *firstValidDate = [NSDate ehi_today];
        NSDate *lastValidDate  = [firstValidDate ehi_addDays:360];

        NSDate *firstDate = [[firstValidDate ehi_firstInMonth] ehi_weekday:EHIWeekdayFirst];
        NSDate *lastDate  = [lastValidDate ehi_lastInMonth];

        _days = @(0)
            .upTo([firstDate ehi_daysUntilDate:lastDate])
            .map(^(NSNumber *offset) {
                return [[EHICalendarDay alloc] initWithDate:[firstDate ehi_addDays:offset.integerValue]];
            }).each(^(EHICalendarDay *day, NSUInteger index) {
                day.isOpen = YES;
                day.index  = index;
                day.isWithinSelectableRange = [day.date ehi_isBetweenDate:firstValidDate andDate:lastValidDate];
            });
    }

    return _days;
}

- (NSArray *)firstDays
{
    if(!_firstDays) {
        _firstDays = (self.days ?: @[]).select(^(EHICalendarDay *day) {
            return day.isFirstInMonth;
        });
    }

    return _firstDays;
}

- (NSInteger)numberOfDays
{
    return self.days.count;
}

# pragma mark - Days

- (EHICalendarDay *)dayAtIndexPath:(NSIndexPath *)indexPath
{
    return self.days[indexPath.item];
}

- (NSInteger)rowIndexForDate:(NSDate *)date
{
    // find the number of days from the first date; if the date was before our first date, we'll use the 0th row
    NSInteger daysFromFirstDate = MAX([self.firstDay.date ehi_daysUntilDate:date], 0);
    // divide this by the number of days per week to get the expected row
    return daysFromFirstDate / EHIDaysPerWeek;
}

- (EHICalendarDay *)firstDay
{
    return self.days.firstObject;
}

# pragma mark - Months

- (NSInteger)numberOfMonths
{
    return self.firstDays.count;
}

- (EHICalendarDay *)firstDayInMonthAtIndexPath:(NSIndexPath *)indexPath
{
    return self.firstDays[indexPath.item];
}

- (void)updateActiveMonthWithIndexPath:(NSIndexPath *)indexPath
{
    EHICalendarDay *day = [self firstDayInMonthAtIndexPath:indexPath];
    if(![day isEqual:self.firstDayForActiveMonth]) {
        self.firstDayForActiveMonth = day;
    }
}

- (void)setFirstDayForActiveMonth:(EHICalendarDay *)firstDayForActiveMonth
{
    _firstDayForActiveMonth = firstDayForActiveMonth;
    
    (self.days ?: @[]).each(^(EHICalendarDay *day){
        day.isWithinActiveMonth = [day.date ehi_isEqual:firstDayForActiveMonth.date granularity:NSCalendarUnitMonth];
    });
}

# pragma mark - Selection

- (void)selectDateAtIndexPath:(NSIndexPath *)indexPath
{
    EHICalendarDay *day = [self dayAtIndexPath:indexPath];
    [self selectDay:day];
}

- (void)selectDay:(EHICalendarDay *)day
{
    if(!day.isSelectable) {
        return;
    }

    if(self.type == EHISingleDateCalendarTypePickup) {
        _pickupDate = day.date;
    } else {
        _returnDate = day.date;
    }

    [self handleDateSelection];
    
    self.placardModel.date = day.date;
    self.selectedRange = (NSRange){ .location = day.index };
}

- (void)didTapContinue
{
    if(self.flow == EHISingleDateCalendarFlowLocationsFilter) {
        [EHIAnalytics trackAction:EHIAnalyticsActionSelectDate handler:^(EHIAnalyticsContext *context) {
            context[EHIAnalyticsFilterReturnBeforePickUpMessageDisplay] = @(self.didShowToast);
            context.macroEvent = EHIAnalyticsMacroEventLocationSelectDateTime;
        }];
    }

    NSDate *pickupDate = self.pickupDate;
    NSDate *returnDate = self.returnDate;
    EHISingleDateCalendarDateValidation validationStatus = self.validationStatus;
    switch(validationStatus) {
        case EHISingleDateCalendarDateValidationValid:
            break;
        case EHISingleDateCalendarDateValidationInvalidPickup:
            returnDate = nil;
            break;
        case EHISingleDateCalendarDateValidationInvalidReturn:
            pickupDate = nil;
            break;
    }
    
    [self hideToast];

    ehi_call(self.handler)(pickupDate, returnDate);
}

- (void)handleDateSelection
{
    [self hideToast];

    NSString *message = [self validationMessage];
    if(message != nil) {
        self.didShowToast = YES;
        return [self showToast:message];
    }
}

- (NSString *)validationMessage
{
    EHISingleDateCalendarDateValidation validationStatus = self.validationStatus;
    
    switch(validationStatus) {
        case EHISingleDateCalendarDateValidationValid:
            return nil;
        case EHISingleDateCalendarDateValidationInvalidPickup: {
            NSString *message = EHILocalizedString(@"date_select_invalid_pickup_date", @"Your pick-up date is after your return (#{date})\nContinuing will clear your return date", @"");
            NSDate *returnDate = self.returnDate;
            message = [message ehi_applyReplacementMap:@{
                @"date" : [returnDate ehi_stringForTemplate:kEHISingleDateCalendarMessageDateFormat] ?: @""
            }];
            
            return message;
        }
        case EHISingleDateCalendarDateValidationInvalidReturn: {
            NSString *message = EHILocalizedString(@"date_select_invalid_return_date", @"Your pick-up date is after your return (#{date})\nContinuing will clear your return date", @"");
            NSDate *pickupDate = self.pickupDate;
            message = [message ehi_applyReplacementMap:@{
                @"date" : [pickupDate ehi_stringForTemplate:kEHISingleDateCalendarMessageDateFormat] ?: @""
            }];
            
            return message;
        }
    }
}

- (EHISingleDateCalendarDateValidation)validationStatus
{
    NSDate *pickupDate = self.pickupDate;
    NSDate *returnDate = self.returnDate;
    
    if(self.type == EHISingleDateCalendarTypePickup && returnDate) {
        if(![pickupDate ehi_isSame:returnDate] && ![pickupDate ehi_isBefore:returnDate]) {
            return EHISingleDateCalendarDateValidationInvalidPickup;
        }
    }
    if(self.type == EHISingleDateCalendarTypeReturn && pickupDate) {
        if(![returnDate ehi_isSame:pickupDate] && ![returnDate ehi_isAfter:pickupDate]) {
            return EHISingleDateCalendarDateValidationInvalidReturn;
        }
    }
    
    return EHISingleDateCalendarDateValidationValid;
}

- (void)showToast:(NSString *)message
{
    EHIToast *toast = [EHIToast new];
    toast.message   = message;
    toast.duration  = (NSTimeInterval)999.0f;
    toast.position  = EHIToastPositionBottom;
    
    [EHIToastManager showToast:toast];
}

- (void)hideToast
{
    [EHIToastManager hideActiveToast];
}

@end
