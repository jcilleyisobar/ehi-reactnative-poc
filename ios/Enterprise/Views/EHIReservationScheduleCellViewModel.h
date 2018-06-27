//
//  EHIReservationScheduleCellViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 4/23/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIReservationScheduleCellViewModel : EHIViewModel

- (instancetype)initWithPickupDate:(NSDate *)pickupDate returnDate:(NSDate *)returnDate;

@property (copy  , nonatomic) NSDate *pickupDate;
@property (copy  , nonatomic) NSDate *returnDate;
@property (assign, nonatomic) BOOL showTopDivider;

@end
