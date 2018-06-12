//
//  EHIRentalsCondensedReceiptViewModel.h
//  Enterprise
//
//  Created by cgross on 7/19/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIRentalsCondensedReceiptViewModel : EHIViewModel <MTRReactive>

@property (copy  , nonatomic, readonly) NSString *rentalAgreementNumber;
@property (assign, nonatomic, readonly) BOOL showContract;
@property (copy  , nonatomic, readonly) NSString *contract;

@property (copy  , nonatomic, readonly) NSString *pickupTitle;
@property (copy  , nonatomic, readonly) NSString *pickupDate;
@property (copy  , nonatomic, readonly) NSString *pickupLocation;
@property (copy  , nonatomic, readonly) NSString *pickupCity;

@property (copy  , nonatomic, readonly) NSString *returnTitle;
@property (copy  , nonatomic, readonly) NSString *returnDate;
@property (copy  , nonatomic, readonly) NSString *returnLocation;
@property (copy  , nonatomic, readonly) NSString *returnCity;

@property (copy  , nonatomic, readonly) NSString *totalTitle;
@property (copy  , nonatomic, readonly) NSString *totalPrice;

@property (assign, nonatomic, readonly) BOOL showPoints;
@property (copy  , nonatomic, readonly) NSString *pointsTitle;
@property (copy  , nonatomic, readonly) NSString *points;

@end
