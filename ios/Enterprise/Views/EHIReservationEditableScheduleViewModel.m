//
//  EHIReservationEditableScheduleViewModel.m
//  Enterprise
//
//  Created by mplace on 3/4/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIReservationEditableScheduleViewModel.h"
#import "EHIReservationBuilder.h"
#import "EHICalendarViewController.h"
#import "EHITimePickerViewController.h"

@interface EHIReservationEditableScheduleViewModel () <EHIReservationBuilderReadinessListener>
@property (nonatomic, readonly)  EHIReservationBuilder *builder;
@property (strong   , nonatomic) NSDate *oldPickupDate;
@property (strong   , nonatomic) NSDate *oldReturnDate;
@property (nonatomic, readonly)  BOOL isPickup;
@property (nonatomic, readonly)  BOOL isNew;
@end

@implementation EHIReservationEditableScheduleViewModel

- (instancetype)initWithModel:(id)model
{
    if (self = [super initWithModel:model]) {
        _dateSelectionButtonTitle = EHILocalizedString(@"date_select_section_title", @"SELECT DATE", @"");
        _timeSelectionButtonTitle = EHILocalizedString(@"time_select_section_title", @"SELECT TIME", @"");
    }
    
    return self;
}

- (void)didBecomeActive
{
    [super didBecomeActive];
    
    // Hanging on to peviously selected days if the user doesn't select any and navigates back
    if (!self.builder.pickupDate && !self.builder.returnDate &&
        self.oldPickupDate && self.oldReturnDate) {
        self.builder.pickupDate = self.oldPickupDate;
        self.builder.returnDate = self.oldReturnDate;
    }
  
    [self.builder waitForReadiness:self];
}

# pragma mark - Reactions

- (void)builderIsReady:(EHIReservationBuilder *)builder
{
    [MTRReactor autorun:self action:@selector(updateDateSelectionState:)];
    [MTRReactor autorun:self action:@selector(invalidateDateInfo:)];
    [MTRReactor autorun:self action:@selector(updateDateSelectability:)];
}

- (void)updateDateSelectability:(MTRComputation *)computation
{
    self.shouldEnableDateSelection = [self dateSelectionEnabledForBuilder:self.builder];
    self.shouldEnableTimeSelection = [self timeSelectionEnabledForBuilder:self.builder];
}

- (void)updateDateSelectionState:(MTRComputation *)computation
{
    self.state = [self stateForBuilder:self.builder];
}

- (void)invalidateDateInfo:(MTRComputation *)computation
{
    NSDate *date = (self.type == EHIReservationScheduleViewTypePickup) ? self.builder.pickupDate : self.builder.returnDate;
    NSDate *time = (self.type == EHIReservationScheduleViewTypePickup) ? self.builder.pickupTime : self.builder.returnTime;
    
    // format and localize title as weekday or month+date
    self.dateTitle = [date ehi_stringForTemplate:@"EE MMM d"];
    
    // format and localize time
    self.timeTitle = [time ehi_localizedTimeString];    
}

//
// Helpers
//

- (EHIReservationScheduleSelectionState)stateForBuilder:(EHIReservationBuilder *)builder
{
    NSDate *date = self.isPickup ? builder.pickupDate : builder.returnDate;
    NSDate *time = self.isPickup ? builder.pickupTime : builder.returnTime;

    if(!date) {
        return EHIReservationScheduleSelectionStateBegin;
    } else if(!time) {
        return EHIReservationScheduleSelectionStateDateSelected;
    } else {
        return EHIReservationScheduleSelectionStateTimeSelected;
    }
}

- (BOOL)dateSelectionEnabledForBuilder:(EHIReservationBuilder *)builder
{
    // disable the date selection button on the return view if the pickup date and time haven't been chosen
    if(self.type == EHIReservationScheduleViewTypeReturn) {
        return builder.pickupDate != nil;
    }
    // pickup date selection button is always enabled
    else {
        return YES;
    }
}

- (BOOL)timeSelectionEnabledForBuilder:(EHIReservationBuilder *)builder
{
    // disable the time selection button on the return view if the pickup time hasn't been chosen
    if(self.type == EHIReservationScheduleViewTypeReturn) {
        return builder.pickupTime != nil;
    }
    // pickup time selection button is always enabled
    else {
        return builder.returnDate != nil;
    }
}

# pragma mark - Actions

- (void)startSelectionWithStep:(EHIReservationSchedulingStep)step
{
    BOOL timeSelection = step >= EHIReservationSchedulingStepPickupTime;
    self.oldPickupDate = self.builder.pickupDate;
    self.oldReturnDate = self.builder.returnDate;

    self.builder.currentSchedulingStep = step;
   
    // for date edits, we can push the calendar normally
    if(!timeSelection) {
        self.router.transition
            .push(EHIScreenReservationCalendar).start(nil);
    }
    // run super-hacky multi-vc transition to push the calendar on secretly
    else {
        self.router.transition
            .push(EHIScreenReservationCalendar)
            .push(EHIScreenReservationTimeSelect).start(nil);
    }
}

# pragma mark - Passthrough

- (EHIReservationBuilder *)builder
{
    return [EHIReservationBuilder sharedInstance];
}

# pragma mark - Accessors

- (BOOL)isPickup
{
    return self.type == EHIReservationScheduleViewTypePickup;
}

@end
