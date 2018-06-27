//
//  EHIReservationEditableScheduleViewModel.h
//  Enterprise
//
//  Created by mplace on 3/4/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservationScheduleViewModel.h"
#import "EHIReservationScheduleViewLayout.h"
#import "EHIReservationSchedulingStep.h"

typedef NS_ENUM(NSInteger, EHIReservationScheduleSelectionState) {
    EHIReservationScheduleSelectionStateBegin,
    EHIReservationScheduleSelectionStateDateSelected,
    EHIReservationScheduleSelectionStateTimeSelected,
};

@interface EHIReservationEditableScheduleViewModel : EHIReservationScheduleViewModel <MTRReactive>

@property (copy  , nonatomic) NSString *dateSelectionButtonTitle;
@property (copy  , nonatomic) NSString *timeSelectionButtonTitle;
@property (assign, nonatomic) BOOL shouldEnableDateSelection;
@property (assign, nonatomic) BOOL shouldEnableTimeSelection;
@property (assign, nonatomic) EHIReservationScheduleSelectionState state;

// navigate to the time selection screen
- (void)startSelectionWithStep:(EHIReservationSchedulingStep)step;

@end
