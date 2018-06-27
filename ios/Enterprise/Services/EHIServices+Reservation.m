//
//  EHIServices+Reservation.m
//  Enterprise
//
//  Created by mplace on 2/20/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIServices+Reservation.h"
#import "EHIServices_Private.h"
#import "EHISettings.h"
#import "EHIUserManager.h"
#import "EHIUser.h"

@implementation EHIServices (Reservation)

- (id<EHINetworkCancelable>)initiateReservation:(EHIReservation *)reservation handler:(EHIReservationHandler)handler
{
#if EHIReservationMock
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeNone post:@"mock://reservation.json"];
#else
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeGBORental post:@"/reservations/%@/%@/initiate", kEHIServicesBrandPathKey, kEHIServicesChannelPathKey];
    [request body:^(EHINetworkRequest *request) {
        request[@"pickup_location_id"] = reservation.pickupLocationId;
        request[@"return_location_id"] = reservation.returnLocationId;
        request[@"pickup_time"] = reservation.pickupTime.ehi_dateTimeString;
        request[@"return_time"] = reservation.returnTime.ehi_dateTimeString;
        request[@"contract_number"] = reservation.discountCode;
        request[@"travel_purpose"]  = reservation.travelPurposeString;
        request[@"additional_information"] = reservation.additionalInfo;
        request[@"auth_pin"]        = reservation.pinAuth;
        request[@"enable_north_american_prepay_rates"] = EHIStringifyFlag(YES);

        // always fetch rates for authed users
        if([EHIUser currentUser] != nil) {
            request[@"show_redemption_rate_to_client"] = EHIStringifyFlag(YES);
        }

        // only send the age if we have one
        if(reservation.renterAge > 0) {
            request[@"renter_age"]  = @(reservation.renterAge);
        }
    }];
#endif

    // kick off request
    return [self startRequest:request updateModel:reservation asynchronously:YES handler:handler];
}

- (id<EHINetworkCancelable>)fetchCarClass:(EHICarClassFetch *)fetchModel reservation:(EHIReservation *)reservation handler:(void (^)(EHICarClass *, EHIServicesError *))handler;
{
#if EHIReservationMock
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeNone get:@"mock://car_class.json"];
#else
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeGBORental get:@"/reservations/%@/%@/%@/carClassDetails",kEHIServicesBrandPathKey, kEHIServicesChannelPathKey, reservation.uid];
    [request parameters:^(EHINetworkRequest *request) {
        request[@"carClassCode"] = fetchModel.code;
        request[@"redemptionDayCount"] = @(fetchModel.daysToRedeem);
    }];
#endif

    // kick off request
    return [self startRequest:request handler:^(id response, EHIServicesError *error){
        EHICarClass *carClass = [EHICarClass modelWithDictionary:response[@"car_class_details"] ?: @{}];
        ehi_call(handler)(carClass, error);
    }];
}

- (id<EHINetworkCancelable>)selectCarClass:(EHICarClass *)carClass reservation:(EHIReservation *)reservation handler:(EHIReservationHandler)handler
{
    return [self selectCarClass:carClass reservation:reservation inModify:NO selectPrepay:NO handler:handler];
}

- (id<EHINetworkCancelable>)selectCarClass:(EHICarClass *)carClass reservation:(EHIReservation *)reservation inModify:(BOOL)inModify handler:(EHIReservationHandler)handler
{
    return [self selectCarClass:carClass reservation:reservation inModify:inModify selectPrepay:NO handler:handler];
}

- (id<EHINetworkCancelable>)selectCarClass:(EHICarClass *)carClass reservation:(EHIReservation *)reservation inModify:(BOOL)inModify selectPrepay:(BOOL)selectPrepay handler:(EHIReservationHandler)handler
{
#if EHIReservationMock
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeNone post:@"mock://car_class.json"];
#else
    NSString *requestUrl = inModify
    ? @"/reservations/%@/%@/modify/%@/selectCarClass"
    : @"/reservations/%@/%@/%@/selectCarClass";
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeGBORental post:requestUrl, kEHIServicesBrandPathKey, kEHIServicesChannelPathKey, reservation.uid];
    [request body:^(EHINetworkRequest *request) {
        request[@"car_class_code"] = carClass.code;
        request[@"redemption_day_count"] = @(carClass.daysToRedeem);
        request[@"prepay_selected"] = EHIStringifyFlag(selectPrepay);
    }];
#endif

    // kick off request
    return [self startRequest:request updateModel:reservation forceDeletions:YES asynchronously:YES handler:^(EHIReservation *reservation, EHIServicesError *error) {
        // need to merge status/redemption state because its not coming back on the details car class
        NSString *statusKey = @key(carClass.status);
        [reservation.selectedCarClass setValue:[carClass valueForKey:statusKey] ?: @(0) forKey:statusKey];
        reservation.upgradeDetails = nil;

        // update the selected payment method on the reservation
        [reservation updateSelectedPaymentOption];

        ehi_call(handler)(reservation, error);
    }];
}

- (id<EHINetworkCancelable>)updateExtras:(NSArray *)extras forReservation:(EHIReservation *)reservation handler:(EHIReservationHandler)handler
{
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeGBORental post:@"/reservations/%@/%@/%@/extras", kEHIServicesBrandPathKey, kEHIServicesChannelPathKey, reservation.uid];
    [request body:^(EHINetworkRequest *request) {
        request[@"extras"] = extras;
    }];

    // kick off request
    return [self startRequest:request updateModel:reservation asynchronously:YES handler:handler];
}

- (id<EHINetworkCancelable>)fetchUpgradesForReservation:(EHIReservation *)reservation handler:(EHIReservationHandler)handler
{
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeGBORental get:@"/reservations/%@/%@/%@/upgrade", kEHIServicesBrandPathKey, kEHIServicesChannelPathKey, reservation.uid];

    return [self startRequest:request updateModel:reservation asynchronously:YES handler:handler];
}

- (id<EHINetworkCancelable>)selectUpgrade:(EHICarClass *)upgradedCarClass reservation:(EHIReservation *)reservation inModify:(BOOL)inModify handler:(EHIReservationHandler)handler
{
    NSString *requestUrl = inModify
    ? @"/reservations/%@/%@/modify/%@/selectUpgrade"
    : @"/reservations/%@/%@/%@/selectUpgrade";

    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeGBORental put:requestUrl, kEHIServicesBrandPathKey, kEHIServicesChannelPathKey, reservation.uid];
    [request body:^(EHINetworkRequest *request) {
        request[@"car_class_code"] = upgradedCarClass.code;
    }];

    return [self startRequest:request updateModel:reservation asynchronously:YES handler:handler];
}

- (id<EHINetworkCancelable>)addDelivery:(EHIDeliveryCollectionInfo *)delivery collection:(EHIDeliveryCollectionInfo *)collection toReservation:(EHIReservation *)reservation handler:(EHIReservationHandler)handler;
{
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeGBORental post:@"/reservations/%@/%@/%@/dcDetails", kEHIServicesBrandPathKey, kEHIServicesChannelPathKey, reservation.uid];
    [request body:^(EHINetworkRequest *request) {
        request[@"delivery"]   = delivery;
        request[@"collection"] = collection;
    }];

    // kick off request
    return [self startRequest:request handler:^(id response, EHIServicesError *error) {
        // remove in preparation for possible deletion (forceDeletions is dangerous for entire res object)
        reservation.vehicleLogistics.deliveryInfo   = nil;
        reservation.vehicleLogistics.collectionInfo = nil;

        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
            // update the pass in reservation
            [reservation updateWithDictionary:response];
            // send update model back in the handler
            dispatch_async(dispatch_get_main_queue(), ^{
                ehi_call(handler)(reservation, error);
            });
        });
    }];
}

- (id<EHINetworkCancelable>)commitReservation:(EHIReservation *)reservation handler:(EHIReservationHandler)handler
{
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeGBORental post:@"reservations/%@/%@/%@/commit",  kEHIServicesBrandPathKey, kEHIServicesChannelPathKey, reservation.uid];
    
    [request body:^(EHINetworkRequest *request) {
        request[@"driver_info"]                 = reservation.driverInfo;
        request[@"driver_info"][@"source_code"] = kEHIServicesParameterSourceCodeKey;
        request[@"travel_purpose"]              = reservation.travelPurposeString;
        request[@"airline_information"]         = reservation.airline;
        request[@"additional_information"]      = reservation.additionalInfo;
        request[@"prepay3_dspa_res"]            = reservation.creditCard3dsValidation;

        // encode the payment at the root level of the request body
        EHIUserPaymentMethod *paymentMethod = reservation.paymentMethod;
        if(paymentMethod.isCustom) {
            request[@"billing_account_type"] = @"CUSTOM";
            request[@"billing_account"]      = paymentMethod.paymentReferenceId;
        } else if(paymentMethod.isExisting) {
            request[@"billing_account_type"] = @"EXISTING";
        } else {
            if(paymentMethod.paymentReferenceId.length > 0) request[@"payment_ids"] = @[paymentMethod.paymentReferenceId];
        }
    }];
    
    // kick off request
    return [self startRequest:request updateModel:reservation forceDeletions:YES asynchronously:YES handler:handler];
}

- (id<EHINetworkCancelable>)cancelReservation:(EHIReservation *)reservation handler:(void (^)(EHIServicesError *))handler
{
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeGBORental post:@"reservations/%@/%@/%@/cancel", kEHIServicesBrandPathKey, kEHIServicesChannelPathKey, reservation.uid];
    [request body:^(EHINetworkRequest *request) {
        request[@"confirmation_number"] = reservation.confirmationNumber;
    }];

    // kick off request
    return [self startRequest:request handler:^(id response, EHIServicesError *error) {
        ehi_call(handler)(error);
    }];
}

- (id<EHINetworkCancelable>)fetchReservation:(EHIReservation *)reservation handler:(EHIReservationHandler)handler
{
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeGBORental get:@"reservations/%@/%@/%@", kEHIServicesBrandPathKey, kEHIServicesChannelPathKey, reservation.confirmationNumber];
    // kick off request
    return [self startRequest:request parseModel:[EHIReservation class] asynchronously:YES handler:handler];
}

- (id<EHINetworkCancelable>)fetchRentalForConfirmation:(NSString *)confirmation firstName:(NSString *)firstName lastName:(NSString *)lastName handler:(EHIReservationHandler)handler
{
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeGBORental get:@"reservations/%@/%@/%@", kEHIServicesBrandPathKey, kEHIServicesChannelPathKey, confirmation];
    [request parameters:^(EHINetworkRequest *request) {
        request[@"firstName"] = [firstName stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]];
        request[@"lastName"] = [lastName stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]];
        request[@"enableNorthAmericanPrepayRates"] = EHIStringifyFlag(YES);

    }];

    return [self startRequest:request parseModel:[EHIReservation class] asynchronously:YES handler:handler];
}

- (id<EHINetworkCancelable>)changePaymentTypeForCarClass:(EHICarClass *)carClass reservation:(EHIReservation *)reservation inModify:(BOOL)inModify handler:(EHIReservationHandler)handler
{
    return [self selectCarClass:carClass reservation:reservation inModify:inModify selectPrepay:!reservation.prepaySelected handler:handler];
}

- (id<EHINetworkCancelable>)fetchTermsAndConditionsForReservation:(EHIReservation *)reservation handler:(void (^)(EHITermsCountries *, EHIServicesError *))handler
{
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeGBORental get:@"reservations/%@/%@/%@/rentalTermsAndConditions", kEHIServicesBrandPathKey, kEHIServicesChannelPathKey, reservation.uid];
    return [self startRequest:request parseModel:[EHITermsCountries class] asynchronously:YES handler:handler];
}

- (id<EHINetworkCancelable>)associateReservation:(EHIReservation *)reservation withUser:(EHIUser *)user handler:(EHIReservationHandler)handler
{
    NSString *loyaltyType = user.profiles.basic.loyalty.program == EHIUserLoyaltyProgramEnterprisePlus ? @"EP" : @"EC";

    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeGBORental
                                                        put:@"reservations/%@/%@/%@/%@/%@",
                                  kEHIServicesBrandPathKey,
                                  kEHIServicesChannelPathKey,
                                  reservation.uid,
                                  loyaltyType,
                                  user.individualId];

    return [self startRequest:request parseModel:[EHIReservation class] asynchronously:YES handler:^(EHIReservation *response, EHIServicesError *error) {
        ehi_call(handler)(reservation, error);
    }];
}

@end

@implementation EHIServices (ReservationModify)

- (id<EHINetworkCancelable>)modifyDateAndLocation:(EHIReservation *)reservation handler:(EHIReservationHandler)handler
{
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeGBORental post:@"reservations/%@/%@/modify/%@/initiate", kEHIServicesBrandPathKey, kEHIServicesChannelPathKey, reservation.uid];
    [request body:^(EHINetworkRequest *request) {
        request[@"pickup_location_id"] = reservation.pickupLocationId;
        request[@"return_location_id"] = reservation.returnLocationId;
        request[@"pickup_time"] = reservation.pickupTime.ehi_dateTimeString;
        request[@"return_time"] = reservation.returnTime.ehi_dateTimeString;
    }];

    // kick off request
    return [self startRequest:request updateModel:reservation asynchronously:YES handler:handler];
}

- (id<EHINetworkCancelable>)updateAvailableCarClasses:(EHIReservation *)reservation handler:(EHIReservationHandler)handler
{
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeGBORental get:@"reservations/%@/%@/modify/%@/availableCarClasses", kEHIServicesBrandPathKey, kEHIServicesChannelPathKey, reservation.uid];

    return [self startRequest:request updateModel:reservation asynchronously:YES handler:handler];
}

- (id<EHINetworkCancelable>)modifyCarClass:(EHICarClass *)carClass reservation:(EHIReservation *)reservation handler:(EHIReservationHandler)handler
{
    return [self selectCarClass:carClass reservation:reservation inModify:YES handler:handler];
}

- (id<EHINetworkCancelable>)modifyExtras:(NSArray *)extras forReservation:(EHIReservation *)reservation handler:(EHIReservationHandler)handler
{
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeGBORental post:@"reservations/%@/%@/modify/%@/extras",  kEHIServicesBrandPathKey, kEHIServicesChannelPathKey, reservation.uid];
    [request body:^(EHINetworkRequest *request) {
        request[@"extras"] = extras;
    }];

    // kick off request
    return [self startRequest:request updateModel:reservation asynchronously:YES handler:handler];
}

- (id<EHINetworkCancelable>)modifyUpgradesForReservation:(EHIReservation *)reservation handler:(EHIReservationHandler)handler
{
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeGBORental get:@"reservations/%@/%@/modify/%@/getUpgrades", kEHIServicesBrandPathKey, kEHIServicesChannelPathKey,  reservation.uid];

    return [self startRequest:request updateModel:reservation asynchronously:YES handler:handler];
}

- (id<EHINetworkCancelable>)modifyDriver:(EHIDriverInfo *)driverInfo airline:(EHIAirline *)airline reservation:(EHIReservation *)reservation handler:(EHIReservationHandler)handler
{
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeGBORental post:@"/reservations/%@/%@/modify/%@/renter", kEHIServicesBrandPathKey, kEHIServicesChannelPathKey, reservation.uid];
    
    [request headers:^(EHINetworkRequest *request) {
        request[EHIRequestHeaderContentType] = EHIRequestParamJSONCharsetUTF8;
    }];
    
    [request body:^(EHINetworkRequest *request) {
        request[@"renter_info"] = driverInfo;
        request[@"airline_information"] = airline;
    }];

    return [self startRequest:request updateModel:reservation asynchronously:YES handler:handler];
}

- (id<EHINetworkCancelable>)modifyAdditionalInfo:(NSArray<EHIContractAdditionalInfoValue> *)additionalInfo reservation:(EHIReservation *)reservation handler:(EHIReservationHandler)handler
{
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeGBORental post:@"reservations/%@/%@/modify/%@/additionalInfo", kEHIServicesBrandPathKey, kEHIServicesChannelPathKey, reservation.uid];
    [request body:^(EHINetworkRequest *request) {
        request[@"additional_information"] = additionalInfo;
    }];

    return [self startRequest:request updateModel:reservation asynchronously:YES handler:handler];
}

- (id<EHINetworkCancelable>)modifyDelivery:(EHIDeliveryCollectionInfo *)delivery collection:(EHIDeliveryCollectionInfo *)collection onReservation:(EHIReservation *)reservation handler:(EHIReservationHandler)handler
{
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeGBORental post:@"/reservations/%@/%@/modify/%@/dcDetails", kEHIServicesBrandPathKey, kEHIServicesChannelPathKey, reservation.uid];
    [request body:^(EHINetworkRequest *request) {
        request[@"delivery"]   = delivery;
        request[@"collection"] = collection;
    }];

    // kick off request
    return [self startRequest:request handler:^(id response, EHIServicesError *error) {
        // remove in preparation for possible deletion (forceDeletions is dangerous for entire res object)
        reservation.vehicleLogistics.deliveryInfo   = nil;
        reservation.vehicleLogistics.collectionInfo = nil;

        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
            // update the pass in reservation
            [reservation updateWithDictionary:response];
            // send update model back in the handler
            dispatch_async(dispatch_get_main_queue(), ^{
                ehi_call(handler)(reservation, error);
            });
        });
    }];
}

- (id<EHINetworkCancelable>)modifyPaymentMethod:(EHIUserPaymentMethod *)paymentMethod reservation:(EHIReservation *)reservation handler:(EHIReservationHandler)handler
{
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeGBORental post:@"/reservations/%@/%@/modify/%@/paymentMethod", kEHIServicesBrandPathKey, kEHIServicesChannelPathKey, reservation.uid];
    [request body:^(EHINetworkRequest *request) {
        if(paymentMethod.isCustom) {
            request[@"billing_account_type"] = @"CUSTOM";
            request[@"billing_account"]      = paymentMethod.paymentReferenceId;
        } else if(paymentMethod.isExisting) {
            request[@"billing_account_type"] = @"EXISTING";
        } else {
            if(paymentMethod.paymentReferenceId.length > 0) request[@"payment_ids"] = @[paymentMethod.paymentReferenceId];
        }
    }];

    return [self startRequest:request updateModel:reservation asynchronously:YES handler:handler];
}

- (id<EHINetworkCancelable>)commitModifyReservation:(EHIReservation *)reservation handler:(EHIReservationHandler)handler
{
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeGBORental post:@"reservations/%@/%@/modify/%@/commit", kEHIServicesBrandPathKey, kEHIServicesChannelPathKey, reservation.uid];
    [request body:^(EHINetworkRequest *request) {
        //payment_id vs payment_ids required for modify
        request[@"payment_id"]       = reservation.paymentMethod.paymentReferenceId;
        request[@"prepay3_dspa_res"] = reservation.creditCard3dsValidation;
    }];

    return [self startRequest:request updateModel:reservation forceDeletions:YES asynchronously:YES handler:handler];
}
@end
