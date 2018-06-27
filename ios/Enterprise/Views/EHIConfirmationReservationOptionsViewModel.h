//
//  EHIConfirmationReservationOptionsViewModel.h
//  Enterprise
//
//  Created by Bruno Fernandes Campos on 9/21/17.
//Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@class EHIReservation;
@interface EHIConfirmationReservationOptionsViewModel : EHIViewModel <MTRReactive>

@property (strong, nonatomic, readonly) EHIReservation *reservation;

@property (assign, nonatomic, readonly) BOOL isModifyLoading;
@property (assign, nonatomic, readonly) BOOL isCancelationLoading;

// computed
@property (assign, nonatomic, readonly) BOOL disableModify;
@property (assign, nonatomic, readonly) BOOL disableCancel;

- (void)cancelReservation;
- (void)modifyReservation;

@end
