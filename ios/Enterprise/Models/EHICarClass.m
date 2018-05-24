//
//  EHIReservationCarClass.m
//  Enterprise
//
//  Created by mplace on 2/19/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel_Subclass.h"
#import "EHICarClass.h"
#import "EHIUser.h"
#import "EHISettings.h"

@interface EHICarClass ()
@property (copy  , nonatomic) NSString *makeModel;
@end

@implementation EHICarClass

- (void)updateWithDictionary:(NSDictionary *)dictionary forceDeletions:(BOOL)forceDeletions
{
    [super updateWithDictionary:dictionary forceDeletions:forceDeletions];

    for (EHICarClassVehicleRate *vehicleRate in self.vehicleRates) {
        // generate maps from line items so that we can link them
        EHICarClassExtra *extra;
        NSDictionary *lineItemMap = (vehicleRate.priceSummary.lineItems ?: @[]).ehi_keyBy(@key(extra.code));
        
        // give the line item a reference back to its car class parent
        (vehicleRate.priceSummary.lineItems ?: @[]).each(^(EHICarClassPriceLineItem *lineItem) {
            [lineItem linkCarClass:self];
        });
        
        // enumerate the extras so that we can link them against infos and line items
        NSArray *extras = vehicleRate.extras.all;
        for(EHICarClassExtra *extra in extras) {
            // get the corresponding info and line item
            EHICarClassPriceLineItem *lineItem = lineItemMap[extra.code];
            
            // link line item to extra
            [lineItem linkExtra:extra];
        }
    }
}

# pragma mark - Accessors

- (BOOL)isSoldOut
{
    return self.status == EHICarClassStatusSoldOut;
}

- (BOOL)isRedemptionAllowed
{
    return self.maxRedemptionDaysReason != EHICarClassRedemptionLimitReasonNotAllowed;
}

- (BOOL)canRedeemPoints
{
    return self.maxRedemptionDaysReason == EHICarClassRedemptionLimitReasonPointsBalanceLimit
        && self.maxRedemptionDays > 0
        && self.isRedemptionAllowed;
}

- (BOOL)requiresCallForAvailability
{
    return self.status == EHICarClassStatusRestrictedAtRetailRate
        || self.status == EHICarClassStatusRestrictedAtPromotionRate
        || self.status == EHICarClassStatusRestrictedAtContractRate;
}

- (BOOL)usesCallForAvailabilityLink
{
    return self.requiresCallForAvailability && self.truckUrl != nil;
}

- (NSString *)makeModel
{
    // if we have it, return it
    if(_makeModel) {
        return _makeModel;
    }
    
    return self.make && self.model ? [[NSString alloc] initWithFormat:@"%@ %@", self.make, self.model] : @"";
}

- (NSString *)makeModelOrSimilar
{
    // because the make model strings from the service have trailing white space of varying length
    NSString *sanitizedMakeModel = [self.makeModel stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]];
    
    NSString *format = EHILocalizedString(@"reservation_car_class_make_model_title", @"#{make_model} or similar", @"");
    NSString *makeModelTitle = [format ehi_applyReplacementMap:@{
        @"make_model" : sanitizedMakeModel,
    }];
    
    return makeModelTitle;
}

- (NSString *)transmission
{
    EHICarClassFeature *transmission = self.features.find(^(EHICarClassFeature *feature) {
        return feature.code == EHICarClassFeatureCodeAutomaticTransmission
            || feature.code == EHICarClassFeatureCodeManualTransmission;
    });
    
    return transmission.details;
}

- (BOOL)isAutomaticTransmission
{
    return self.features.any(^(EHICarClassFeature *feature) {
        return feature.code == EHICarClassFeatureCodeAutomaticTransmission;
    });
}

- (BOOL)isOnRequest
{
    return self.status == EHICarClassStatusOnRequest
        || self.status == EHICarClassStatusOnRequestAtPromotionRate
        || self.status == EHICarClassStatusOnRequestAtContractRate;
}

- (BOOL)isNegotiatedRate
{
    return self.status == EHICarClassStatusAvailableAtContractRate
        || self.status == EHICarClassStatusOnRequestAtContractRate;
}

- (BOOL)isPromotionalRate
{
    return self.status == EHICarClassStatusAvailableAtPromotionRate
        || self.status == EHICarClassStatusOnRequestAtPromotionRate;
}

- (BOOL)isUnpromoted
{
    return !self.isPromotionalRate && !self.isNegotiatedRate && self.status != EHICarClassStatusUnknown;
}

- (BOOL)isSecretRate
{
    BOOL selectedAnyExtras = self.selectedAnyExtras;
    BOOL isChargesEmpty    = self.charges.firstObject == nil;
    
    return isChargesEmpty && selectedAnyExtras;
}

- (BOOL)isSecretRateAfterCarSelected
{
    BOOL selectedAnyExtras = self.selectedAnyExtras;
    EHICarClassPriceSummary *priceSummary = [self vehicleRateForPrepay:NO].priceSummary;
    
    return !priceSummary.hasChargedItems && selectedAnyExtras;
}

- (BOOL)selectedAnyExtras
{
    return [self vehicleRateForPrepay:NO].extras.selectedByUser.count > 0;
}

- (EHICarClassPriceDifference *)prepayDifference
{
    return (self.priceDifferences ?: @[]).find(^(EHICarClassPriceDifference *difference) {
        return difference.type == EHICarClassPriceDifferenceTypePrepay;
    });
}

- (EHICarClassPriceDifference *)unpaidRefundDifference
{
    return (self.priceDifferences ?: @[]).find(^(EHICarClassPriceDifference *difference){
        return difference.type == EHICarClassPriceDifferenceTypeUnpaidRefundAmount;
    });
}

- (BOOL)hasUnpaidRefund
{
    return self.unpaidRefundDifference != nil;
}

- (BOOL)hasRefundAmount
{
    EHICarClassPriceDifference *price = self.unpaidRefundDifference;
    BOOL aHackBecauseItWasReturningTheWrongPointer = [price isKindOfClass:[EHIPrice class]];
    if(aHackBecauseItWasReturningTheWrongPointer) {
        return [(EHIPrice *)price amount] < 0;
    }
    
    return price.viewDifference.amount < 0;
}


# pragma mark - Pricing Prepay / Pay Later

- (BOOL)supportsPrepay
{
    return [self priceContextForPrepay:YES] != nil;
}

- (id<EHIPriceContext>)priceContextForPrepay:(BOOL)prepay
{
    if (self.vehicleRates.count > 0){
        return [self vehicleRateForPrepay:prepay].priceSummary;
    } else if (self.charges.count > 0){
        return [self chargeForPrepay:prepay];
    }
    return nil;
}

- (EHICarClassVehicleRate *)vehicleRateForPrepay:(BOOL)prepay
{
    return (self.vehicleRates ?: @[]).find(^(EHICarClassVehicleRate *rate) {
        EHICarClassChargeType type = prepay ? EHICarClassChargeTypePrepay : EHICarClassChargeTypePayLater;
        return rate.type == type;
    });
}

- (EHICarClassCharge *)chargeForPrepay:(BOOL)prepay
{
    return (self.charges ?: @[]).find(^(EHICarClassCharge *charge) {
        EHICarClassChargeType type = prepay ? EHICarClassChargeTypePrepay : EHICarClassChargeTypePayLater;
        return charge.type == type;
    });
}

- (EHICarClassPriceDifference *)upgradeDifferenceForPrepay:(BOOL)prepay
{
    return (self.priceDifferences ?: @[]).find(^(EHICarClassPriceDifference *difference) {
        EHICarClassPriceDifferenceType type = prepay ? EHICarClassPriceDifferenceTypeUpgradePrepay : EHICarClassPriceDifferenceTypeUpgrade;
        return difference.type == type;
    });
}

# pragma mark - Filter

- (BOOL)matchesFilter:(EHIFilter *)filter
{
    switch (filter.type) {
        case EHIFilterTypeTransmission:
            return [self matchesTransmissionFilter:filter.value];
        case EHIFilterTypePassengerCapacity:
            return [self matchesPassengerCapacityFilter:filter.value];
        case EHIFilterTypeClass:
            return [self matchesClassFilter:filter.value];
        default:
            return NO;
    }
}

- (BOOL)matchesTransmissionFilter:(EHICarClassFeatureCode)transmissionCode
{
    EHICarClassFilter *transmissionFilter = self.filters.find(^(EHICarClassFilter *filter) {
        return filter.type == EHIFilterTypeTransmission;
    });
    
    return (EHICarClassFeatureCode)transmissionFilter.code == transmissionCode;
}

- (BOOL)matchesPassengerCapacityFilter:(NSInteger)passengerCapacity
{
    EHICarClassFilter *capacityFilter = self.filters.find(^(EHICarClassFilter *filter) {
        return filter.type == EHIFilterTypePassengerCapacity;
    });
    
    return capacityFilter.code >= passengerCapacity;
}

- (BOOL)matchesClassFilter:(NSInteger)classCode
{
    EHICarClassFilter *classFilter = self.filters.find(^(EHICarClassFilter *filter) {
        return filter.type == EHIFilterTypeClass;
    });
    
    return classFilter.code == classCode;
}

# pragma mark - Mappings

+ (NSDictionary *)mappings:(EHICarClass *)model
{
    return @{
        @"make_model_or_similar_text"         : @key(model.makeModel),
        @"charge"                             : @key(model.charges),
        @"features_shorts"                    : @key(model.featuresBrief),
        @"people_capacity"                    : @key(model.passengerCapacity),
        @"luggage_capacity"                   : @key(model.luggageCapacity),
        @"previously_selected"                : @key(model.wasPreviouslySelected),
        @"specialty_vehicle"                  : @key(model.isSpecialtyVehicle),
        @"terms_and_conditions_required"      : @key(model.requiresTermsAndConditions),
        @"call_for_availability_phone_number" : @key(model.availabilityPhoneNumber),
        @"truck_url"                          : @key(model.truckUrl),
        @"license_plate"                      : @key(model.licensePlate),
        @"license_state"                      : @key(model.licenseState),
        @"vehicle_rates"                      : @key(model.vehicleRates),
        @"price_differences"                  : @key(model.priceDifferences),
        @"mileage_info"                       : @key(model.mileage),
        @"redemption_points"                  : @key(model.redemptionPoints),
        @"eplus_max_redemption_days"          : @key(model.maxRedemptionDays),
        @"eplus_max_redemption_days_reason"   : @key(model.maxRedemptionDaysReason),
        @"eplus_points_used"                  : @key(model.pointsUsed),
        @"redemption_day_count"               : @key(model.daysToRedeem),
        @"description"                        : @key(model.termsAndConditions),
        @"starting_odometer"                  : @key(model.odometerStart),
        @"ending_odometer"                    : @key(model.odometerEnd),
        @"distance_traveled"                  : @key(model.distanceTraveled),
        @"distance_unit"                      : @key(model.distanceUnit),
        @"vehicle_class_driven"               : @key(model.classDriven),
        @"vehicle_class_charged"               : @key(model.classChanged),
    };
}

+ (void)registerTransformers:(EHICarClass *)model
{
    [self key:@key(model.status) registerMap:@{
        @"SOLD_OUT"                       : @(EHICarClassStatusSoldOut),
        @"ON_REQUEST"                     : @(EHICarClassStatusOnRequest),
        @"ON_REQUEST_AT_PROMOTIONAL_RATE" : @(EHICarClassStatusOnRequestAtPromotionRate),
        @"ON_REQUEST_AT_CONTRACT_RATE"    : @(EHICarClassStatusOnRequestAtContractRate),
        @"RESTRICTED_AT_RETAIL_RATE"      : @(EHICarClassStatusRestrictedAtRetailRate),
        @"RESTRICTED_AT_PROMOTIONAL_RATE" : @(EHICarClassStatusRestrictedAtPromotionRate),
        @"RESTRICTED_AT_CONTRACT_RATE"    : @(EHICarClassStatusRestrictedAtContractRate),
        @"AVAILABLE_AT_RETAIL_RATE"       : @(EHICarClassStatusAvailableAtRetailRate),
        @"AVAILABLE_AT_PROMOTIONAL_RATE"  : @(EHICarClassStatusAvailableAtPromotionRate),
        @"AVAILABLE_AT_CONTRACT_RATE"     : @(EHICarClassStatusAvailableAtContractRate),
    } defaultValue:@(EHICarClassStatusUnknown)];
    
    [self key:@key(model.maxRedemptionDaysReason) registerMap:@{
        @"BUSINESS_LIMIT"                 : @(EHICarClassRedemptionLimitReasonBusinessLimit),
        @"DURATION_LIMIT"                 : @(EHICarClassRedemptionLimitReasonDurationLimit),
        @"POINTS_BALANCE_LIMIT"           : @(EHICarClassRedemptionLimitReasonPointsBalanceLimit),
        @"REDEMPTION_NOT_ALLOWED"         : @(EHICarClassRedemptionLimitReasonNotAllowed),
    } defaultValue:@(EHICarClassRedemptionLimitReasonNotAllowed)];
}

# pragma mark - EHIAnalyticsEncodable

+ (void)encodeWithContext:(EHIAnalyticsContext *)context instance:(nullable EHICarClass *)instance
{
    BOOL prepaySelected = [context[EHIAnalyticsResPaymentTypeKey] isEqualToString:@"PayNow"];
    NSArray *selectedExtras = [instance vehicleRateForPrepay:prepaySelected].extras.selected;
   
    // encode class / extra info
    context[EHIAnalyticsResCarClassKey] = instance.code;
    context[EHIAnalyticsResExtrasKey]   = !selectedExtras ? nil : selectedExtras.map(^(EHICarClassExtra *extra) {
        return extra.code;
    });
   
    BOOL payNow   = [instance vehicleRateForPrepay:YES] != nil;
    BOOL payLater = [instance vehicleRateForPrepay:NO] != nil;
    BOOL redem    = instance.redemptionPoints > 0;
    
    context[EHIAnalyticsResPayNowAvailableKey]     = @(payNow);
    context[EHIAnalyticsResPayLaterAvailableKey]   = @(payLater);
    context[EHIAnalyticsResRedemptionAvailableKey] = @(redem);
    
    // encode price info
    EHICarClassPriceSummary *priceSummary = [instance vehicleRateForPrepay:prepaySelected].priceSummary;
    context[EHIAnalyticsResCurrencyKey] = priceSummary.viewTotal.code;
    context[EHIAnalyticsResPriceKey]    = priceSummary.viewTotal ? @(priceSummary.viewTotal.amount) : nil;
    
    // encode redemption info when user is logged in
    if ([EHIUser currentUser] != nil) {
        context[EHIAnalyticsResRedemptionShowPointsKey] = [EHISettings sharedInstance].redemptionHidePoints ? @"Hide" : @"Show";

        NSInteger totalLengthOfReservation = [context[EHIAnalyticsResLengthKey] integerValue];
        context[EHIAnalyticsResRedemptionDaysKey]    = @(instance.daysToRedeem);
        context[EHIAnalyticsResRedemptionPointsKey]  = @(instance.redemptionPoints * instance.daysToRedeem);
        context[EHIAnalyticsResRedemptionPartialKey] = @((BOOL)(totalLengthOfReservation > instance.daysToRedeem && instance.daysToRedeem > 0));
        context[EHIAnalyticsResRedemptionMaxDaysKey] = @(instance.maxRedemptionDays);
    }
}

@end
