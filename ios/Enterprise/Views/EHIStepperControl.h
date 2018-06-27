//
//  EHIReservationExtrasStepper.h
//  Enterprise
//
//  Created by fhu on 4/9/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIView.h"

@protocol EHIStepperControlDelegate;

@interface EHIStepperControl : EHIView
@property (weak  , nonatomic) IBOutlet id<EHIStepperControlDelegate> delegate;
/** Max number the stepper will count to */
@property (assign, nonatomic) NSInteger maxCount;
/** The current number the stepper is on */
@property (assign, nonatomic) NSInteger count;
/** @c YES to enable the plus button */
@property (assign, nonatomic) BOOL plusButtonEnabled;
/** @c YES to enable the minus button */
@property (assign, nonatomic) BOOL minusButtonEnabled;
/** @c YES if the increment/decrement buttons should remain interactive past the max/min */
@property (assign, nonatomic) BOOL shouldFauxDisableButtons;
@end

@protocol EHIStepperControlDelegate <NSObject> @optional
- (void)stepper:(EHIStepperControl *)stepper didUpdateValue:(NSInteger)integer;
- (NSAttributedString *)stepperTitleForCount:(NSInteger)count;
@end