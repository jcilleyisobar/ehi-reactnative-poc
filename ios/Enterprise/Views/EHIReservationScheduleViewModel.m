//
//  EHIReservationScheduleViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 4/23/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservationScheduleViewModel.h"

@implementation EHIReservationScheduleViewModel

- (void)updateWithModel:(id)model
{
    if([model isKindOfClass:[NSDate class]]) {
        [self updateWithDate:model];
    }
}

- (void)updateWithDate:(NSDate *)date
{
    // format and localize title as weekday or month+date
    self.dateTitle = [date ehi_stringForTemplate:@"EE MMM d"];
    
    // format and localize time
    self.timeTitle = [date ehi_localizedTimeString];
}

# pragma mark - Type

- (void)setType:(EHIReservationScheduleViewType)type
{
    _type = type;
    self.viewTitle = [self titleForType];
}

- (NSString *)titleForType
{
    switch(self.type) {
        case EHIReservationScheduleViewTypePickup:
            return EHILocalizedString(@"reservation_pickup_date_cell_title", @"PICK-UP", @"title for a cell allowing user to select a pickup date");
        case EHIReservationScheduleViewTypeReturn:
            return EHILocalizedString(@"reservation_return_date_cell_title", @"RETURN", @"title for a cell allowing user to select a return date");
    }
}

@end
