//
//  EHIReservationCarClass.h
//  Enterprise
//
//  Created by mplace on 2/19/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"
#import "EHICarClassCharge.h"
#import "EHICarClassCategory.h"
#import "EHICarClassExtras.h"
#import "EHICarClassPriceSummary.h"
#import "EHICarClassPriceDifference.h"
#import "EHICarClassVehicleRate.h"
#import "EHICarClassFeature.h"
#import "EHICarClassMileage.h"
#import "EHIImage.h"
#import "EHICarClassFilter.h"
#import "EHIFilters.h"
#import "EHIAnalyticsEncodable.h"
#import "EHICarClassFetch.h"

typedef NS_ENUM(NSInteger, EHICarClassStatus) {
    EHICarClassStatusUnknown,
    EHICarClassStatusSoldOut,
    EHICarClassStatusOnRequest,
    EHICarClassStatusOnRequestAtPromotionRate,
    EHICarClassStatusOnRequestAtContractRate,
    EHICarClassStatusRestrictedAtRetailRate,
    EHICarClassStatusRestrictedAtPromotionRate,
    EHICarClassStatusRestrictedAtContractRate,
    EHICarClassStatusAvailableAtRetailRate,
    EHICarClassStatusAvailableAtPromotionRate,
    EHICarClassStatusAvailableAtContractRate,
};

typedef NS_ENUM(NSInteger, EHICarClassRedemptionLimitReason) {
    EHICarClassRedemptionLimitReasonNotAllowed,
    EHICarClassRedemptionLimitReasonBusinessLimit,
    EHICarClassRedemptionLimitReasonDurationLimit,
    EHICarClassRedemptionLimitReasonPointsBalanceLimit,
};


@interface EHICarClass : EHIModel <EHIAnalyticsEncodable>

@property (copy  , nonatomic, readonly) NSString *name;
@property (copy  , nonatomic, readonly) NSString *details;
@property (copy  , nonatomic, readonly) NSString *makeModel;
@property (copy  , nonatomic, readonly) NSString *code;
@property (copy  , nonatomic, readonly) NSString *availabilityPhoneNumber;
@property (copy  , nonatomic, readonly) NSString *truckUrl;

@property (assign, nonatomic, readonly) NSInteger passengerCapacity;
@property (assign, nonatomic, readonly) NSInteger luggageCapacity;

@property (strong, nonatomic, readonly) EHICarClassCategory *category;
@property (strong, nonatomic, readonly) EHICarClassMileage *mileage;

@property (copy  , nonatomic, readonly) NSArray<EHICarClassFeature> *features;
@property (copy  , nonatomic, readonly) NSArray<EHICarClassFeature> *featuresBrief;

@property (assign, nonatomic, readonly) BOOL isSpecialtyVehicle;
@property (assign, nonatomic, readonly) BOOL wasPreviouslySelected;
@property (assign, nonatomic, readonly) BOOL requiresTermsAndConditions;
@property (copy  , nonatomic, readonly) NSString *termsAndConditions;

@property (copy  , nonatomic, readonly) NSArray<EHIImage> *images;
@property (copy  , nonatomic, readonly) NSArray<EHICarClassFilter> *filters;
@property (copy  , nonatomic, readonly) NSString *licensePlate;
@property (copy  , nonatomic, readonly) NSString *licenseState;
@property (copy  , nonatomic, readonly) NSString *make;
@property (copy  , nonatomic, readonly) NSString *model;
@property (copy  , nonatomic, readonly) NSString *color;
@property (copy  , nonatomic, readonly) NSString *classDriven;
@property (copy  , nonatomic, readonly) NSString *classChanged;
@property (assign, nonatomic, readonly) EHICarClassStatus status;
@property (strong, nonatomic) NSArray<EHICarClassCharge> *charges;
@property (strong, nonatomic) NSArray<EHICarClassVehicleRate> *vehicleRates;
@property (strong, nonatomic, readonly) NSArray<EHICarClassPriceDifference> *priceDifferences;


// redemption
@property (assign, nonatomic, readonly) NSInteger redemptionPoints;
@property (assign, nonatomic, readonly) NSInteger maxRedemptionDays;
@property (assign, nonatomic, readonly) EHICarClassRedemptionLimitReason maxRedemptionDaysReason;
@property (assign, nonatomic) NSInteger daysToRedeem;
@property (assign, nonatomic) NSInteger pointsUsed;

// distance
@property (copy, nonatomic, readonly) NSString *odometerStart;
@property (copy, nonatomic, readonly) NSString *odometerEnd;
@property (copy, nonatomic, readonly) NSString *distanceTraveled;
@property (copy, nonatomic, readonly) NSString *distanceUnit;

// computed propertes
@property (assign, nonatomic, readonly) BOOL isSoldOut;
@property (assign, nonatomic, readonly) BOOL isRedemptionAllowed;
@property (assign, nonatomic, readonly) BOOL canRedeemPoints;
@property (assign, nonatomic, readonly) BOOL isPromotionalRate;
@property (assign, nonatomic, readonly) BOOL isNegotiatedRate;
@property (assign, nonatomic, readonly) BOOL isUnpromoted;
@property (assign, nonatomic, readonly) BOOL isAutomaticTransmission;
@property (assign, nonatomic, readonly) BOOL isOnRequest;
@property (assign, nonatomic, readonly) BOOL requiresCallForAvailability;
@property (assign, nonatomic, readonly) BOOL usesCallForAvailabilityLink;
@property (assign, nonatomic, readonly) BOOL supportsPrepay;
@property (assign, nonatomic, readonly) BOOL hasUnpaidRefund;
@property (assign, nonatomic, readonly) BOOL hasRefundAmount;
@property (assign, nonatomic, readonly) BOOL isSecretRate;
@property (assign, nonatomic, readonly) BOOL isSecretRateAfterCarSelected;
@property (strong, nonatomic, readonly) EHICarClassPriceDifference *prepayDifference;
@property (strong, nonatomic, readonly) EHICarClassPriceDifference *unpaidRefundDifference;
@property (copy  , nonatomic, readonly) NSString *transmission;
@property (copy  , nonatomic, readonly) NSString *makeModelOrSimilar;



- (id<EHIPriceContext>)priceContextForPrepay:(BOOL)prepay;
- (EHICarClassVehicleRate *)vehicleRateForPrepay:(BOOL)prepay;
- (EHICarClassCharge *)chargeForPrepay:(BOOL)prepay;
- (EHICarClassPriceDifference *)upgradeDifferenceForPrepay:(BOOL)prepay;

/** Returns @YES if the car class should be included in the filter */
- (BOOL)matchesFilter:(EHIFilter *)filter;

@end

EHIAnnotatable(EHICarClass);
