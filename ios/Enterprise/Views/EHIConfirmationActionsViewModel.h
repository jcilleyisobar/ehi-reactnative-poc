//
//  EHIConfirmationActionsViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 4/21/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIConfirmationReservationOptionsViewModel.h"

@interface EHIConfirmationActionsViewModel : EHIConfirmationReservationOptionsViewModel <MTRReactive>

@property (copy  , nonatomic, readonly) NSString *returnHomeTitle;
@property (copy  , nonatomic, readonly) NSString *modifyTitle;
@property (copy  , nonatomic, readonly) NSString *cancelReservationTitle;

- (void)returnToDashboard;

@end
