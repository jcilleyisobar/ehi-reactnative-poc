//
//  EHIReservationScheduleCellViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 4/23/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservationScheduleCellViewModel.h"

@implementation EHIReservationScheduleCellViewModel

- (instancetype)initWithPickupDate:(NSDate *)pickupDate returnDate:(NSDate *)returnDate
{
    if(self = [super init]) {
        _pickupDate = pickupDate;
        _returnDate = returnDate;
    }
    return self;
}

@end
