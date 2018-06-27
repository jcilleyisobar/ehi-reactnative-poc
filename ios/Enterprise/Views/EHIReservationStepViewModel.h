//
//  EHIReservationStepViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 9/21/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIReservationBuilder+Analytics.h"
#import "EHIPriceContext.h"

@interface EHIReservationStepViewModel : EHIViewModel <MTRReactive>
/** Whether changes are being made to this view models content */
@property (assign, nonatomic) BOOL isEditing;
/** Whether we are currently modifying a booked reservation */
@property (assign, nonatomic, readonly) BOOL isModify;
/** Whether prepay is selected for the current reservation */
@property (assign, nonatomic, readonly) BOOL isPrepay;

- (void)showNextScreenWithCarClass:(EHICarClass *)carClass;
- (EHICarClassCharge *)chargesForCarClass:(EHICarClass *)carClass;
- (EHICarClassVehicleRate *)vehicleRatesForCarClass:(EHICarClass *)carClass;
- (BOOL)usePrepay:(EHICarClass *)carClass;

@end
