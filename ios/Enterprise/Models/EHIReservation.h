//
//  EHIReservation.h
//  Enterprise
//
//  Created by mplace on 2/19/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIContractDetails.h"
#import "EHICarClass.h"
#import "EHILocation.h"
#import "EHIDriverInfo.h"
#import "EHIVehicleLogistics.h"
#import "EHIUserPaymentMethod.h"
#import "EHIContractAdditionalInfoValue.h"
#import "EHIReservationEligibility.h"
#import "EHIAnalyticsEncodable.h"
#import "EHIReservationBuilderFlow.h"
#import "EHICancellationDetails.h"
#import "EHIReservationPaymentMethod.h"
#import "EHIPaymentGateways.h"

#ifdef TESTS
    #define EHIReservationMock 1
#elif DEBUG
    #define EHIReservationMock (EHIMockEnabled && 1)
#else
    #define EHIReservationMock 0
#endif

typedef NS_ENUM(NSInteger, EHIReservationTravelPurpose) {
    EHIReservationTravelPurposeNone,
    EHIReservationTravelPurposeBusiness,
    EHIReservationTravelPurposeLeisure
};

typedef NS_ENUM(NSInteger, EHIReservationStatus) {
    EHIReservationStatusUnknown,
    EHIReservationStatusPending,
    EHIReservationStatusOpen,
    EHIReservationStatusConfirmed,
    EHIReservationStatusCheckedOut,
    EHIReservationStatusNoShow,
    EHIReservationStatusCanceled,
    EHIReservationStatusClosed,
};

@interface EHIReservation : EHIModel <EHIAnalyticsEncodable>

// populated after initiate request
@property (strong, nonatomic) EHILocation *pickupLocation;
@property (strong, nonatomic) EHILocation *returnLocation;
@property (copy  , nonatomic) NSDate *pickupTime;
@property (copy  , nonatomic) NSDate *returnTime;

@property (copy  , nonatomic, readonly) NSArray<EHICarClass> *carClasses;
@property (copy  , nonatomic, readonly) NSArray<EHICarClassFilters> *carClassesFilters;
@property (copy  , nonatomic, readonly) NSArray *excludedExtras;
@property (copy  , nonatomic, readonly) NSArray *alternativePickupLocations;
@property (copy  , nonatomic, readonly) NSArray *alternativeReturnLocations;
@property (copy  , nonatomic, readonly) NSArray *prohibitsExtrasAfterUpgrade;
@property (copy  , nonatomic, readonly) NSString *businessLeisureGenericDisclaimer;
@property (strong, nonatomic, readonly) EHIContractDetails *contractDetails;
@property (strong, nonatomic, readonly) EHIVehicleLogistics *vehicleLogistics;
@property (strong, nonatomic, readonly) EHIUserPaymentMethod *billingAccount;
@property (strong, nonatomic, readonly) NSArray<EHIReservationPaymentMethod> *reservationPayments;
@property (assign, nonatomic, readonly) EHIPaymentGatewayProcessor paymentGateway;
@property (assign, nonatomic, readonly) NSInteger renterAge;
@property (assign, nonatomic, readonly) BOOL allowsDelivery;
@property (assign, nonatomic, readonly) BOOL allowsCollection;
@property (assign, nonatomic, readonly) BOOL allowsAfterHoursReturn;
@property (assign, nonatomic, readonly) BOOL allowsVehicleUpgrade;
@property (assign, nonatomic, readonly) BOOL collectNewCardInModify;
@property (assign, nonatomic, readonly) BOOL contractHasAdditionalBenefits;
@property (assign, nonatomic, readonly) BOOL isEuropeanUnion;
@property (assign, nonatomic, readonly) BOOL blockModifyPickupLocation;

// populated after car class details request
@property (copy  , nonatomic, readonly) EHICarClass *selectedCarClass;

// populated after car class upgrade request
@property (copy  , nonatomic, readonly) NSArray<EHICarClass> *upgradeCarClassDetails;

// populated after commit request
@property (copy  , nonatomic, readonly) NSString *confirmationNumber;
@property (copy  , nonatomic, readonly) NSString *prefillUrl;
@property (copy  , nonatomic, readonly) NSString *rulesOfTheRoadUrl;
@property (copy  , nonatomic, readonly) NSArray<EHILocationPolicy> *policies;
@property (copy  , nonatomic, readonly) NSArray<EHILocationPolicy> *keyFactsPolicies;
@property (copy  , nonatomic, readonly) NSArray<EHILocationPolicy> *prepayPolicies;
@property (assign, nonatomic, readonly) EHIReservationStatus status;

// redemption
@property (assign, nonatomic) NSInteger pointsUsed;
@property (assign, nonatomic) NSInteger daysToRedeem;

/** the selected payment method (prepay, pay later, redemption) */
@property (assign, nonatomic) EHIReservationPaymentOption selectedPaymentOption;

// populated after retrieve
@property (strong, nonatomic, readonly) EHIReservationEligibility *eligibility;

// not from service
@property (assign, nonatomic) EHIReservationTravelPurpose travelPurpose;
@property (copy  , nonatomic, readonly) NSString *travelPurposeString;
@property (copy  , nonatomic, readonly) NSString *pickupLocationId;
@property (copy  , nonatomic, readonly) NSString *returnLocationId;
@property (copy  , nonatomic, readonly) NSString *discountCode;
@property (assign, nonatomic) EHIReservationBuilderFlow currentFlow;
@property (copy  , nonatomic, readonly) NSString *pinAuth;
@property (assign, nonatomic) BOOL hasToAssociate;

// computed properties
@property (assign, nonatomic, readonly) BOOL isPast;
@property (assign, nonatomic, readonly) BOOL isOneWay;
@property (assign, nonatomic, readonly) BOOL hasPickupAtLocationCloseTime;
@property (assign, nonatomic, readonly) BOOL hasReturnAtLocationCloseTime;
@property (assign, nonatomic, readonly) NSInteger customerValue;
@property (assign, nonatomic, readonly) BOOL isReservationBookingSystemEcars;
@property (assign, nonatomic, readonly) BOOL isUsingWeekendSpecial;
@property (assign, nonatomic, readonly) BOOL prepaySelected;
@property (assign, nonatomic, readonly) BOOL prepayEnabled;
@property (assign, nonatomic, readonly) BOOL isCorporate;
@property (assign, nonatomic, readonly) EHILocationType pickupLocationType;
@property (assign, nonatomic, readonly) EHILocationType returnLocationType;

// grafted on properties (for services consumption)
@property (strong, nonatomic) EHIDriverInfo *driverInfo;
@property (strong, nonatomic) EHIAirline *airline;
@property (copy  , nonatomic) NSArray<EHIContractAdditionalInfoValue> *additionalInfo;
@property (strong, nonatomic) EHIUserPaymentMethod *paymentMethod;
@property (strong, nonatomic) EHICreditCard *creditCard;

// 3d secure validation data
@property (copy  , nonatomic) NSString *creditCard3dsValidation;

// cancellation details
@property (strong, nonatomic) EHICancellationDetails *cancellationDetails;

// necessary information for analytics
@property (strong, nonatomic) EHICarClass *upgradeDetails;

- (void)updateSelectedPaymentOption;

@end
