//
//  EHIInvoiceTripSummaryViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 7/19/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

typedef NS_ENUM(NSInteger, EHIInvoiceTripSummaryPhone) {
    EHIInvoiceTripSummaryPhoneReturnLocation,
    EHIInvoiceTripSummaryPhonePickupLocation,
};

@interface EHIInvoiceTripSummaryViewModel : EHIViewModel <MTRReactive>

@property (copy  , nonatomic) NSString *pickupTitle;
@property (copy  , nonatomic) NSString *pickupDate;
@property (copy  , nonatomic) NSString *pickupLocation;
@property (copy  , nonatomic) NSString *pickupCity;
@property (copy  , nonatomic) NSString *pickupPhone;

@property (copy  , nonatomic) NSString *returnTitle;
@property (copy  , nonatomic) NSString *returnDate;
@property (copy  , nonatomic) NSString *returnLocation;
@property (copy  , nonatomic) NSString *returnCity;
@property (copy  , nonatomic) NSString *returnPhone;

@property (copy  , nonatomic) NSString *totalTitle;
@property (copy  , nonatomic) NSString *totalPrice;

@property (assign, nonatomic) BOOL showPoints;
@property (copy  , nonatomic) NSString *pointsTitle;
@property (copy  , nonatomic) NSString *points;

- (void)promptCallTo:(EHIInvoiceTripSummaryPhone)location;

@end
