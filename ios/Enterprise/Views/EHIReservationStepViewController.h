//
//  EHIReservationFlowStepViewController.h
//  Enterprise
//
//  Created by mplace on 3/11/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewController.h"
#import "EHIReservationBuilder.h"

#define EHITransitionAnimationDuration (0.2)
#define EHIExtrasScrollInAnimationDuration (0.5)

@interface EHIReservationStepViewController : EHIViewController
/** Computed property for accessing the shared reservation builder */
@property (nonatomic, readonly) EHIReservationBuilder *builder;
@end
