//
//  EHIReservationSchedulePickerViewModel.h
//  Enterprise
//
//  Created by Ty Cobb on 3/26/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIReservationSchedulingStep.h"

@interface EHIReservationSchedulePickerViewModel : EHIViewModel <MTRReactive>

/** Title for the schedule 'pickup' section */
@property (copy, nonatomic, readonly) NSString *pickupTitle;
/** Title for the schedule 'return' section */
@property (copy, nonatomic, readonly) NSString *returnTitle;
/** An array of @c EHIReservationSchedulePickerStepViewModel indexed by @c EHIReservationSchedulingStep */
@property (copy, nonatomic, readonly) NSArray *steps;

@end
