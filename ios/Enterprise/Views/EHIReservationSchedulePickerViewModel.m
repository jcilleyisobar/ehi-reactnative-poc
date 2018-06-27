//
//  EHIReservationSchedulePickerViewModel.m
//  Enterprise
//
//  Created by Ty Cobb on 3/26/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIReservationSchedulePickerViewModel.h"
#import "EHIReservationSchedulePickerStepViewModel.h"

@implementation EHIReservationSchedulePickerViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _pickupTitle = EHILocalizedString(@"reservation_pickup_date_cell_title", @"PICK-UP", @"title for a cell allowing user to select a pickup date");
        _returnTitle = EHILocalizedString(@"reservation_return_date_cell_title", @"RETURN", @"title for a cell allowing user to select a return date");
        
        // pre-build all the picker steps
        _steps = @[
            [[EHIReservationSchedulePickerStepViewModel alloc] initWithStep:EHIReservationSchedulingStepPickupDate],
            [[EHIReservationSchedulePickerStepViewModel alloc] initWithStep:EHIReservationSchedulingStepReturnDate],
            [[EHIReservationSchedulePickerStepViewModel alloc] initWithStep:EHIReservationSchedulingStepPickupTime],
            [[EHIReservationSchedulePickerStepViewModel alloc] initWithStep:EHIReservationSchedulingStepReturnTime],
        ];
    }
    
    return self;
}

@end
