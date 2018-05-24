//
//  EHIConfirmationManageReservationViewModelViewModel.h
//  Enterprise
//
//  Created by Bruno Fernandes Campos on 9/14/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIConfirmationReservationOptionsViewModel.h"
#import "EHIViewModel_Calendar.h"

@interface EHIConfirmationManageReservationViewModel : EHIConfirmationReservationOptionsViewModel <MTRReactive>

@property (assign, nonatomic, readonly) BOOL shouldExpand;

@property (copy, nonatomic, readonly) NSString *title;
@property (copy, nonatomic, readonly) NSString *calendarButtonTitle;
@property (copy, nonatomic, readonly) NSString *modifyButtonTitle;
@property (copy, nonatomic, readonly) NSString *cancelButtonTitle;

- (void)toggleViewClicked;

@end
