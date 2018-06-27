//
//  EHIUserRental.h
//  Enterprise
//
//  Created by fhu on 4/17/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"
#import "EHILocation.h"
#import "EHICarClass.h"
#import "EHICarClassPriceSummary.h"
#import "EHIWatchEncodable.h"
#import "EHIAddress.h"
#import "EHIUserPaymentMethod.h"

@interface EHIUserRental : EHIModel <EHIWatchEncodable>

@property (copy  , nonatomic) NSDate *pickupDate;
@property (copy  , nonatomic) NSDate *returnDate;
@property (copy  , nonatomic) NSString *pickupTimeDisplay;
@property (copy  , nonatomic) NSString *returnTimeDisplay;
@property (copy  , nonatomic) NSString *lastName;
@property (copy  , nonatomic) NSString *firstName;
@property (copy  , nonatomic) NSString *contractName;
@property (strong, nonatomic, readonly) EHIAddress *address;
@property (copy  , nonatomic, readonly) NSString *membershipNumber;
@property (copy  , nonatomic, readonly) NSString *rentalAgreementNumber;
@property (copy  , nonatomic, readonly) NSString *rateMyRideUrl;
@property (copy  , nonatomic, readonly) NSString *confirmationNumber;
@property (copy  , nonatomic, readonly) NSString *ticketNumber;
@property (copy  , nonatomic, readonly) NSString *invoiceNumber;
@property (copy  , nonatomic, readonly) NSString *pointsEarned;
@property (copy  , nonatomic, readonly) NSString *vatNumber;
@property (strong, nonatomic, readonly) EHIPrice *priceSummary;
@property (strong, nonatomic, readonly) EHILocation *pickupLocation;
@property (strong, nonatomic, readonly) EHILocation *returnLocation;
@property (strong, nonatomic) EHICarClass *carClassDetails;
@property (assign, nonatomic, readonly) NSInteger points;
@property (strong, nonatomic, readonly) NSArray<EHIUserPaymentMethod> *paymentDetails;
@property (assign, nonatomic) BOOL isCurrent;
@property (assign, nonatomic, readonly) BOOL requiresWatermark;

// computed
@property (assign, readonly) BOOL isOneWay;
@property (assign, readonly) BOOL isReturningAfterHours;
@property (assign, readonly) BOOL hasAfterHoursInfo;

@end

EHIAnnotatable(EHIUserRental);
