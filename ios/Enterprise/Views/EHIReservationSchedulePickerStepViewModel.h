//
//  EHIReservationSchedulingStepModel.h
//  Enterprise
//
//  Created by Ty Cobb on 3/26/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIReservationSchedulingStep.h"

@interface EHIReservationSchedulePickerStepViewModel : EHIViewModel <MTRReactive>

/** The text to display for the current step */
@property (copy  , nonatomic, readonly) NSString *text;
/** @c YES if this is the current reservation building step */
@property (assign, nonatomic, readonly) BOOL isCurrentStep;

/** Constructs a model for the given step enumeration value */
- (instancetype)initWithStep:(EHIReservationSchedulingStep)step;
/** Selects this step as the current scheduling step */
- (void)selectStep;

@end
