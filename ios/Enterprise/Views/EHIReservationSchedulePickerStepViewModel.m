//
//  EHIReservationSchedulingStepModel.m
//  Enterprise
//
//  Created by Ty Cobb on 3/26/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIReservationSchedulePickerStepViewModel.h"
#import "EHIReservationBuilder.h"
#import "EHIToastManager.h"

@interface EHIReservationSchedulePickerStepViewModel () <EHIReservationBuilderReadinessListener>
@property (copy  , nonatomic) NSString *text;
@property (assign, nonatomic) BOOL isCurrentStep;
@property (nonatomic, readonly) EHIReservationSchedulingStep step;
@property (nonatomic, readonly) NSString *calloutText;
@property (nonatomic, readonly) EHIReservationBuilder *builder;
@end

@implementation EHIReservationSchedulePickerStepViewModel

- (instancetype)initWithStep:(EHIReservationSchedulingStep)step
{
    if(self = [super initWithModel:nil]) {
        _step = step;
    }
    
    return self;
}

- (void)didBecomeActive
{
    [super didBecomeActive];
   
    [self.builder waitForReadiness:self];
}

# pragma mark - Reactions

- (void)builderIsReady:(EHIReservationBuilder *)builder
{
    [MTRReactor autorun:self action:@selector(invalidateCurrentStep:)];
    [MTRReactor autorun:self action:@selector(invalidateVisibleText:)];
}

- (void)invalidateCurrentStep:(MTRComputation *)computation
{
    // need independent reactive property on this view model so that the view isn't dependent on builder readiness
    self.isCurrentStep = [self computeIsCurrentStep];
}

- (void)invalidateVisibleText:(MTRComputation *)computation
{
    // force a dependency on one of the builder's date propertiesjj
    NSString *dateText = [self dateText];
    // implicitly depend on the current step
    self.text = [self computeIsCurrentStep] ? self.calloutText : dateText;
}

//
// Helpers
//

- (BOOL)computeIsCurrentStep
{
    return self.builder.currentSchedulingStep == self.step;
}

# pragma mark - Actions

- (void)selectStep
{
    // if the user tried to tap on the active step, let's fire an informative toast
    if(self.isCurrentStep) {
        [self.builder showToastForCurrentSchedulingStep];
    }
    // otherwise, update the builder step to this step if possible
    else {
        [self.builder transitionBackToSchedulingStep:self.step];
    }
}

# pragma mark - Accessors

- (NSString *)dateText
{
    switch(self.step) {
        case EHIReservationSchedulingStepPickupDate:
            return [self.builder.pickupDate ehi_localizedDateString];
        case EHIReservationSchedulingStepPickupTime:
            return [self.builder.pickupTime ehi_localizedTimeString];
        case EHIReservationSchedulingStepReturnDate:
            return [self.builder.returnDate ehi_localizedDateString];
        case EHIReservationSchedulingStepReturnTime:
            return [self.builder.returnTime ehi_localizedTimeString];
        default: return nil;
    }
}

- (NSString *)calloutText
{
    switch(self.step) {
        case EHIReservationSchedulingStepPickupDate:
            return EHILocalizedString(@"reservation_scheduler_pickup_date_callout", @"Select pick-up date", @"Callout for schedular pick-up date row");
        case EHIReservationSchedulingStepReturnDate:
            return EHILocalizedString(@"reservation_scheduler_return_date_callout", @"Select return date", @"Callout for schedular return date row");
        case EHIReservationSchedulingStepPickupTime:
            return EHILocalizedString(@"reservation_scheduler_pickup_time_callout", @"Select pick-up time", @"Callout for schedular pick-up time row");
        case EHIReservationSchedulingStepReturnTime:
            return EHILocalizedString(@"reservation_scheduler_return_time_callout", @"Select return time", @"Callout for schedular return time row");
        default: return nil;
    }
}

- (EHIReservationBuilder *)builder
{
    return [EHIReservationBuilder sharedInstance];
}

@end
