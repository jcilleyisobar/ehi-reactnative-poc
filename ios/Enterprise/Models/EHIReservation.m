//
//  EHIReservation.m
//  Enterprise
//
//  Created by mplace on 2/19/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel_Subclass.h"
#import "EHIReservation.h"
#import "EHIHistoryManager.h"
#import "EHIConfiguration.h"

#define EHIRulesOfTheRoadUrl @"http://ec.europa.eu/transport/road_safety/index_"

@interface EHIReservation ()
@property (copy  , nonatomic) NSString *rulesOfTheRoadUrl;
@property (copy  , nonatomic) NSArray<EHICarClass> *carClasses;
@end

@implementation EHIReservation

- (void)updateWithDictionary:(NSDictionary *)dictionary forceDeletions:(BOOL)forceDeletions
{
    [super updateWithDictionary:dictionary forceDeletions:forceDeletions];
    
    // associate additional info values to their descriptions on the reservation contract, if any
    [self linkAdditionalInfoToContract];
    
    if(self.pickupLocation.shouldMoveVansToEndOfList) {
        self.carClasses = (id)(self.carClasses ?: @[]).partition(^(EHICarClass *class) {
            return !class.category.isVan;
        }).flatten;
    }
    
    // if our airline doesn't have a name, attempt to get it from the pickup location
    if(self.airline && !self.airline.details) {
        [self graftAirlineDetails];
    }
    
    // temporary until orchestration moves the redemption values down into the car class
    EHICarClass *model;
    NSDictionary *carClassDictionary = dictionary[@key(self.selectedCarClass)];
    
    // replace selectCarClass's pointsUsed/daysToRedeem with reservation values if not provided by services
    if([carClassDictionary[@key(model.pointsUsed)] integerValue] == 0) {
        self.selectedCarClass.pointsUsed = self.pointsUsed;
    }
    if([carClassDictionary[@key(model.daysToRedeem)] integerValue] == 0) {
        self.selectedCarClass.daysToRedeem = self.daysToRedeem;
    }
    
    if (self.selectedCarClass && self.selectedCarClass.charges.count == 0) {
        EHICarClass *carClass = (id)(self.carClasses ?: @[]).find(^(EHICarClass *class) {
            return [class.code isEqualToString:self.selectedCarClass.code];
        });
        
        self.selectedCarClass.charges = carClass.charges;
    }
}

- (void)parseDictionary:(NSMutableDictionary *)dictionary
{
    [super parseDictionary:dictionary];
   
    [dictionary ehi_transform:@key(self.pickupTime) selector:@selector(ehi_dateTime)];
    [dictionary ehi_transform:@key(self.returnTime) selector:@selector(ehi_dateTime)];
}

//
// Helpers
//

- (void)linkAdditionalInfoToContract
{
    EHIContractAdditionalInfo *additionalInfo;
    NSDictionary *contractAdditionalInfoMap = (self.contractDetails.additionalInformation ?: @[]).ehi_keyBy(@key(additionalInfo.uid));
    
    // link values to their descriptions on the contract
    (self.additionalInfo ?: @[]).each(^(EHIContractAdditionalInfoValue *infoValue) {
        // get the corresponding info
        EHIContractAdditionalInfo *additionalInfo = contractAdditionalInfoMap[infoValue.uid];
        
        // link the objects
        [infoValue linkContractAdditionalInfo:additionalInfo];
    });
}

- (void)graftAirlineDetails
{
    EHIAirline *airline = (self.pickupLocation.airlines ?: @[]).find(^(EHIAirline *airline) {
        return [self.airline.code isEqualToString:airline.code];
    });
    
    if(airline.details) {
        [self.airline updateWithDictionary:@{
            @key(airline.details) : airline.details,
        }];
    }
}

# pragma mark - Accessors

- (NSString *)rulesOfTheRoadUrl
{
    return _rulesOfTheRoadUrl ?: [NSString stringWithFormat:@"%@%@.htm", EHIRulesOfTheRoadUrl, [NSLocale ehi_language].lowercaseString];
}

- (BOOL)isPast
{
    NSArray *reservations = [EHIHistoryManager sharedInstance].pastReservations;
    for(EHIReservation *reservation in reservations) {
        // we want literal pointer equality here.
        if(reservation == self) {
            return YES;
        }
    }
    
    return NO;
}

- (BOOL)isOneWay
{
    return ![self.pickupLocation.uid isEqualToString:self.returnLocation.uid];
}

- (NSInteger)customerValue
{
    return [self.selectedCarClass vehicleRateForPrepay:self.prepaySelected].priceSummary.viewTotal.amount * 100;
}

- (BOOL)hasPickupAtLocationCloseTime
{
    return [self.pickupLocation hasCloseTime:self.pickupTime];
}

- (BOOL)hasReturnAtLocationCloseTime
{
    return [self.returnLocation hasCloseTime:self.returnTime];
}

- (NSString *)travelPurposeString
{
    switch (self.travelPurpose) {
        case EHIReservationTravelPurposeBusiness:
            return @"BUSINESS";
        case EHIReservationTravelPurposeLeisure:
            return @"LEISURE";
        case EHIReservationTravelPurposeNone:
            return nil;
    }
}

- (BOOL)isReservationBookingSystemEcars
{
    return self.pickupLocation.reservationBookingSystem == EHILocationReservationBookingSystemEcars;
}

- (BOOL)isUsingWeekendSpecial
{
    NSString *weekendSpecialCode = [NSLocale ehi_country].weekendSpecial.code;
    return self.contractDetails && [self.contractDetails.uid isEqualToString:weekendSpecialCode];
}

- (BOOL)prepayEnabled
{
    BOOL hasGateway = self.paymentGateway != EHIPaymentGatewayProcessorUnknown;
    
    BOOL hasPrepayPrices     = (self.carClasses ?: @[]).any(^(EHICarClass *carClass){
        BOOL hasCharges = [carClass chargeForPrepay:YES] != nil;
        BOOL hasRates   = [carClass vehicleRateForPrepay:YES] != nil;
        return hasCharges || hasRates;
    });
    
    return hasGateway && hasPrepayPrices;
}

- (BOOL)isCorporate
{
    return self.contractDetails.contractType == EHIContractTypeCorporate;
}

- (EHILocationType)pickupLocationType
{
    return self.pickupLocation.type;
}

- (EHILocationType)returnLocationType
{
    return self.returnLocation.type;
}

- (void)updateSelectedPaymentOption
{
    if (self.prepaySelected) {
        self.selectedPaymentOption = EHIReservationPaymentOptionPayNow;
    } else if (self.selectedCarClass.daysToRedeem > 0) {
        self.selectedPaymentOption = EHIReservationPaymentOptionRedeemPoints;
    } else {
        self.selectedPaymentOption = EHIReservationPaymentOptionPayLater;
    }
}

# pragma mark - Mappings

+ (NSDictionary *)mappings:(EHIReservation *)model
{
    return @{
        @"res_session_id"                : @key(model.uid),
        @"pickup_location"               : @key(model.pickupLocation),
        @"return_location"               : @key(model.returnLocation),
        @"pickup_time"                   : @key(model.pickupTime),
        @"return_time"                   : @key(model.returnTime),
        @"after_hours_return"            : @key(model.allowsAfterHoursReturn),
        @"renter_age"                    : @key(model.renterAge),
        @"car_classes"                   : @key(model.carClasses),
        @"car_classes_filters"           : @key(model.carClassesFilters),
        @"excluded_extras"               : @key(model.excludedExtras),
        @"car_class_details"             : @key(model.selectedCarClass),
        @"confirmation_number"           : @key(model.confirmationNumber),
        @"prefill_deep_link_url"         : @key(model.prefillUrl),
        @"rules_of_the_road_url"         : @key(model.rulesOfTheRoadUrl),
        @"delivery_allowed"              : @key(model.allowsDelivery),
        @"collection_allowed"            : @key(model.allowsCollection),
        @"upgrade_car_class_details"     : @key(model.upgradeCarClassDetails),
        @"upgrade_vehicle_possible"      : @key(model.allowsVehicleUpgrade),
        @"prepay_selected"               : @key(model.prepaySelected),
        @"european_union_country"        : @key(model.isEuropeanUnion),
        @"alternative_pickup_locations"  : @key(model.alternativePickupLocations),
        @"alternative_return_locations"  : @key(model.alternativeReturnLocations),
        @"driver_info"                   : @key(model.driverInfo),
        @"airline_info"                  : @key(model.airline),
        @"contract_details"              : @key(model.contractDetails),
        @"contract_number"               : @key(model.discountCode),
        @"vehicle_logistics"             : @key(model.vehicleLogistics),
        @"additional_information"        : @key(model.additionalInfo),
        @"reservation_status"            : @key(model.status),
        @"billing_account"               : @key(model.billingAccount),
        @"selected_payment_method"       : @key(model.paymentMethod),
        @"reservation_eligibility"       : @key(model.eligibility),
        @"key_facts_policies"            : @key(model.keyFactsPolicies),
        @"payments"                      : @key(model.reservationPayments),
        @"prepay_policies"               : @key(model.prepayPolicies),
        @"business_leisure_generic_disclaimer"      : @key(model.businessLeisureGenericDisclaimer),
        @"extras_no_longer_available_after_upgrade" : @key(model.prohibitsExtrasAfterUpgrade),
        @"eplus_points_used"                        : @key(model.pointsUsed),
        @"redemption_day_count"                     : @key(model.daysToRedeem),
        @"cancellation_details"                     : @key(model.cancellationDetails),
        @"prepay_payment_processor"                 : @key(model.paymentGateway),
        @"block_modify_pickup_location"             : @key(model.blockModifyPickupLocation),
        @"collect_new_payment_card_in_modify"       : @key(model.collectNewCardInModify)
    };
}

+ (void)registerTransformers:(EHIReservation *)model
{
    [self key:@key(model.status) registerMap:@{
        @"PR" : @(EHIReservationStatusPending),
        @"OP" : @(EHIReservationStatusOpen),
        @"CF" : @(EHIReservationStatusConfirmed),
        @"CO" : @(EHIReservationStatusCheckedOut),
        @"NS" : @(EHIReservationStatusNoShow),
        @"CN" : @(EHIReservationStatusCanceled),
        @"CL" : @(EHIReservationStatusClosed),
    } defaultValue:@(EHIReservationStatusUnknown)];

    [self key:@key(model.paymentGateway) registerTransformer:EHIPaymentGatewayTransformer()];
}

# pragma mark - Analytics

+ (void)encodeWithContext:(EHIAnalyticsContext *)context instance:(nullable EHIReservation *)instance
{
    context[EHIAnalyticsResConfNumberKey]    = instance.confirmationNumber;
    context[EHIAnalyticsResContractKey]      = instance.contractDetails.uid;
    context[EHIAnalyticsResRenterAgeKey]     = instance.renterAge ? @(instance.renterAge) : nil;
    context[EHIAnalyticsResLineOfBizKey]     = instance.pickupLocation.hasMotorcycles ? @"Motorcycle" : nil;
    context[EHIAnalyticsResTransactionType]  = instance.currentFlow == EHIReservationBuilderFlowModify ? @"Modify" : @"Original";
    context[EHIAnalyticsResPaymentTypeKey]   = instance.prepaySelected ? @"PayNow" : @"PayLater";
    
    EHIPrice *cancellationFee = instance.cancellationDetails.feeView;
    if(cancellationFee) {
        context[EHIAnalyticsResCancellationFeeKey] = @(cancellationFee.amount);
    }

    BOOL upgraded = instance.upgradeDetails != nil;
    context[EHIAnalyticsResUpgradeDisplayKey]  = @((BOOL)(instance.upgradeCarClassDetails.count != 0));
    context[EHIAnalyticsResUpgradeSelectedKey] = @(upgraded);

    if (upgraded) {
        context[EHIAnalyticsResUpgradeValueKey]    = @([instance.upgradeDetails upgradeDifferenceForPrepay:instance.prepaySelected].viewDifference.amount);
    }
    
    // encode the car class
    [context encode:[EHICarClass class] encodable:instance.selectedCarClass];
}

# pragma mark - EHIEncodableObject

+ (NSArray *)encodableKeys:(EHIReservation *)model
{
    return @[
        @key(model.pickupLocation),
        @key(model.returnLocation),
        @key(model.pickupTime),
        @key(model.returnTime),
        @key(model.renterAge),
    ];
}

@end
