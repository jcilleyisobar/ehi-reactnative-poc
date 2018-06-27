//
//  EHIRentalsPastRentalViewModel.h
//  Enterprise
//
//  Created by fhu on 4/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIRentalsPastRentalViewModel : EHIViewModel <MTRReactive>

@property (copy, nonatomic) NSString *reservationTime;
@property (copy, nonatomic) NSString *location;
@property (copy, nonatomic) NSString *confirmationText;
@property (copy, nonatomic) NSString *vehicleText;
@property (copy, nonatomic) NSString *totalTitle;
@property (copy, nonatomic) NSAttributedString *total;
@property (copy, nonatomic) NSString *creditCardName;
@property (copy, nonatomic) NSString *creditCardNumber;
@property (copy, nonatomic) NSString *receiptTitle;

- (void)displayInvoice;

@end
