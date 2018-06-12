//
//  EHICalendarViewModel.m
//  Enterprise
//
//  Created by Ty Cobb on 3/23/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHICalendarViewModel.h"
#import "EHIServices+Location.h"
#import "EHIReservationBuilder.h"
#import "EHIReservationBuilder+Analytics.h"

@interface EHICalendarViewModel () <EHIReservationBuilderReadinessListener>
@property (strong, nonatomic) NSArray *days;
@property (strong, nonatomic) NSArray *firstDaysInMonth;
@property (assign, nonatomic) NSRange selectedRange;
@property (strong, nonatomic) EHICalendarDay *firstDayForActiveMonth;
@property (assign, nonatomic) BOOL actionIsDisabled;
@property (assign, nonatomic) BOOL selectedRangeIsComplete;
@property (assign, nonatomic) BOOL isLoading;
@property (nonatomic, readonly) BOOL isSelectingReturnDate;
@property (nonatomic, readonly) EHILocation *location;
@property (nonatomic, readonly) EHILocationHours *hours;
@property (nonatomic, readonly) EHICalendarDay *firstDay;
@property (nonatomic, readonly) EHIReservationBuilder *builder;
@end

@implementation EHICalendarViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        // generate fixed titles
        _title = EHILocalizedString(@"reservation_calendar_title", @"Pick-up & Return Dates", @"Title for the reservation calendar screen");
        _actionTitle = EHILocalizedString(@"reservation_calendar_continue", @"CONTINUE" , @"Title for the 'continue' button");
        
        // generate the list of days to display on the calendar
        _days = [self generateDays];
        
        // filter out the first days for each month
        _firstDaysInMonth = _days.select(^(EHICalendarDay *day) {
            return day.isFirstInMonth;
        });
    }
    
    return self;
}

# pragma mark - EHIViewModel

- (void)didBecomeActive
{
    [super didBecomeActive];
  
    // synchronize with the builder when ready
    [self.builder waitForReadiness:self];
}

# pragma mark - Reactions

- (void)builderIsReady:(EHIReservationBuilder *)builder
{
    [MTRReactor autorun:self action:@selector(invalidateSelectionRange:)];
    [MTRReactor autorun:self action:@selector(invalidateActionButtonEnabled:)];
   
    // capture the correct open days when visible
    [self fetchHours];
}

- (void)invalidateSelectionRange:(MTRComputation *)computation
{
    EHICalendarDay *pickupDay = [self dayForDate:self.builder.pickupDate];
    EHICalendarDay *returnDay = [self dayForDate:self.builder.returnDate];

    NSRange selectedRange = NSRangeNull;
    
    if(pickupDay) {
        selectedRange.location = pickupDay.index;
    }
  
    if(returnDay) {
        selectedRange.length = returnDay.index - pickupDay.index;
    }
    
    self.selectedRange = selectedRange;
    self.selectedRangeIsComplete = pickupDay && returnDay;
}

- (void)invalidateActionButtonEnabled:(MTRComputation *)computation
{
    self.actionIsDisabled = !self.builder.pickupDate || !self.builder.returnDate;
}

# pragma mark - Days

- (EHICalendarDay *)dayAtIndexPath:(NSIndexPath *)indexPath
{
    return self.days[indexPath.item];
}

- (EHICalendarDay *)dayForDate:(NSDate *)date
{
    if(!date) {
        return nil;
    }
    
    NSInteger index = [self.firstDay.date ehi_daysUntilDate:date];
    return self.days[index];
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

- (NSInteger)numberOfDays
{
    return self.days.count;
}

- (NSArray *)generateDays
{
    // the first valid date is today
    NSDate *firstValidDate = [NSDate ehi_today];
    // the last valid date is 360 days from the first date
    NSDate *lastValidDate = [firstValidDate ehi_addDays:360];
   
    // start on the sunday of the first week of this month
    NSDate *firstDate = [[firstValidDate ehi_firstInMonth] ehi_weekday:EHIWeekdayFirst];
    // end on last day of the month of the last valid date
    NSDate *lastDate = [lastValidDate ehi_lastInMonth];
    // generate an offset for each day
    NSArray *days = @(0).upTo([firstDate ehi_daysUntilDate:lastDate]);
    
    // map the offsets into calendar days
    days = days.map(^(NSNumber *offset) {
        return [[EHICalendarDay alloc] initWithDate:[firstDate ehi_addDays:offset.integerValue]];
    })
    // update any day specific properties
    .each(^(EHICalendarDay *day, NSUInteger index) {
        day.index = index;
        day.isWithinSelectableRange = [day.date ehi_isBetweenDate:firstValidDate andDate:lastValidDate];
    });
    
    return days;
}

- (void)fetchHours
{
    // only show loading indicator if call takes unreasonable amount of time
    [self performSelector:@selector(startLoading) withObject:nil afterDelay:0.7];
    
    dispatch_group_t group = dispatch_group_create();
    
    // common hours handler for leaving dispatch group
    void (^sharedHandler)(EHILocation *, EHIServicesError *) = ^(EHILocation *location, EHIServicesError *error) {
        dispatch_group_leave(group);
    };

    dispatch_group_enter(group);
    [[EHIServices sharedInstance] updateHoursForLocation:self.builder.pickupLocation handler:sharedHandler];
    
    dispatch_group_enter(group);
    [[EHIServices sharedInstance] updateHoursForLocation:self.builder.returnLocation handler:sharedHandler];
    
    dispatch_group_notify(group, dispatch_get_main_queue(), ^{
        [NSObject cancelPreviousPerformRequestsWithTarget:self];
        
        [self setIsLoading:NO];
        [self invalidateOpenDaysForHours:self.hours];
    });
}

- (void)startLoading
{
    [self setIsLoading:YES];
}

- (void)invalidateOpenDaysForHours:(EHILocationHours *)hours
{
    for(EHICalendarDay *day in self.days) {
        EHILocationDay *locationDay = hours[day.date.ehi_string];
        
        if(!locationDay) {
            // Setting isOpen to YES to prevent the calendar from being totally unselectable in the case that we get back an empty hours array.
            day.isOpen = YES;
        } else {
            BOOL isSelectingReturnDate = self.isSelectingReturnDate;
            // by default, if standard times are open we're open
            // location is closed if we do not have hours
            BOOL isOpen = locationDay && !locationDay.standardTimes.isClosedAllDay;
            // if we're selecting the return date, we can also be open if the location supports after hours dropoff
            if(!isOpen && isSelectingReturnDate && locationDay.dropTimes) {
                isOpen = !locationDay.dropTimes.isClosedAllDay;
            }
            
            day.isOpen = isOpen;
        }
    }
}

# pragma mark - Months

- (NSInteger)numberOfMonths
{
    return self.firstDaysInMonth.count;
}

- (EHICalendarDay *)firstDayInMonthAtIndexPath:(NSIndexPath *)indexPath
{
    return self.firstDaysInMonth[indexPath.item];
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
    
    // update the active state of all the days
    for(EHICalendarDay *day in self.days) {
        day.isWithinActiveMonth = [day.date ehi_isEqual:firstDayForActiveMonth.date granularity:NSCalendarUnitMonth];
    }
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
    
    // if we have both dates then reset before doing anything
    if(self.builder.pickupDate && self.builder.returnDate) {
        self.builder.returnDate = nil;
        self.builder.pickupDate = nil;
    }
 
    // if we don't have a pickup date or this is before our pickup, then update that
    if(!self.builder.pickupDate || [day.date ehi_isBefore:self.builder.pickupDate]) {
        self.builder.pickupDate = day.date;
    }
    // otherwise we're free to update the return date
    else {
        self.builder.returnDate = day.date;
    }
   
    // update selectable days based on location open-ness
    [self invalidateOpenDaysForHours:self.hours];
}

- (void)initiateTransition
{
    // if we already have times selected, just go back to the itinerary screen
    if(self.builder.pickupTime && self.builder.returnTime) {
        self.router.transition
            .pop(1).start(nil);
    }
    // otherwise forge ahead to the time selection screen
    else {
        self.router.transition
            .push(EHIScreenReservationTimeSelect).start(nil);
    }
}

- (void)showToast
{
    [self.builder showToastForCurrentSchedulingStep];
}

# pragma mark - Accessors

- (BOOL)isSelectingReturnDate
{
    return self.builder.currentSchedulingStep == EHIReservationSchedulingStepReturnDate;
}

- (EHILocation *)location
{
    // for the return date, use the return location if available
    if(self.isSelectingReturnDate) {
        return self.builder.returnLocation ?: self.builder.pickupLocation;
    }
    
    return self.builder.pickupLocation;
}

- (EHILocationHours *)hours
{
    return self.location.hours;
}

- (EHIReservationBuilder *)builder
{
    return [EHIReservationBuilder sharedInstance];
}

# pragma mark - Analytics

- (void)updateAnalyticsContext:(EHIAnalyticsContext *)context
{
    [super updateAnalyticsContext:context];
    
    [self.builder synchronizeLocationsOnContext:context];
    [self.builder synchronizeDateTimeOnContext:context];
}

@end
