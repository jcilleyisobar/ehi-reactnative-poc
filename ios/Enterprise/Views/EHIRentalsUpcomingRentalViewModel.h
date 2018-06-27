//
//  EHIRentalsUpcomingRentalCellViewModel.h
//  Enterprise
//
//  Created by fhu on 4/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIRentalsUpcomingRentalViewModel : EHIViewModel <MTRReactive>

@property (copy, nonatomic) NSString *reservationTime;
@property (copy, nonatomic) NSString *location;
@property (copy, nonatomic) NSString *confirmationNumber;
@property (assign, nonatomic) BOOL shouldHideArrow;

@end
