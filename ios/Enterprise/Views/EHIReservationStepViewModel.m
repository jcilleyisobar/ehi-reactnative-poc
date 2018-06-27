//
//  EHIReservationStepViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 9/21/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel_CountrySpecific.h"
#import "EHIReservationStepViewModel_Subclass.h"
#import "EHIUser.h"
#import "EHIExtrasViewModel.h"
#import "EHIPaymentOptionViewModel.h"

@implementation EHIReservationStepViewModel

- (void)showNextScreenWithCarClass:(EHICarClass *)carClass
{
    BOOL skipPaymentOptions = [self skipPaymentOptions];
    
    [EHIAnalytics trackAction:EHIAnalyticsResActionSelectClass handler:^(EHIAnalyticsContext *context) {
        [self.builder encodeClassSelection:carClass context:context];
    }];
    
    if(skipPaymentOptions) {
        [self showExtrasWithCarClass:carClass];
    } else {
        [self showPaymentOptionsWithCarClass:carClass];
    }
}

- (void)showExtrasWithCarClass:(EHICarClass *)carClass
{
    EHIExtrasViewModel *viewModel = [EHIExtrasViewModel new];
    [viewModel updateWithModel:carClass];
    
    self.router.transition.push(EHIScreenReservationExtras).object(viewModel).start(nil);
}

- (void)showPaymentOptionsWithCarClass:(EHICarClass *)carClass
{
    EHIPaymentOptionViewModel *viewModel = [EHIPaymentOptionViewModel new];
    [viewModel updateWithModel:carClass];
    
    self.router.transition.push(EHIScreenReservationRateSelect).object(viewModel).start(nil);
}

- (BOOL)skipPaymentOptions
{
    BOOL prepayEnabled   = self.builder.reservation.prepayEnabled;
    BOOL isCorporateUser = self.builder.reservation.contractDetails.contractType == EHIContractTypeCorporate;
    BOOL reservationHasRates = [self reservationHasRates];

    return !prepayEnabled || isCorporateUser || !reservationHasRates;
}

- (BOOL)reservationHasRates
{
    EHICarClass *carClass = self.builder.reservation.selectedCarClass;
    
    BOOL hasPrepayRates   = NO;
    BOOL hasPayLaterRates = NO;
    
    if (carClass.vehicleRates) {
        hasPrepayRates   = [carClass vehicleRateForPrepay:YES] != nil;
        hasPayLaterRates = [carClass vehicleRateForPrepay:NO] != nil;
    }
    else if (carClass.charges) {
        hasPrepayRates   = [carClass chargeForPrepay:YES] != nil;
        hasPayLaterRates = [carClass chargeForPrepay:NO] != nil;
    }
    BOOL hasPoints = carClass.redemptionPoints > 0;
    if(self.isModify) {
        switch (self.builder.reservation.selectedPaymentOption) {
            case EHIReservationPaymentOptionPayNow:
                return hasPayLaterRates;
            case EHIReservationPaymentOptionPayLater:
                return hasPrepayRates || hasPoints;
            case EHIReservationPaymentOptionRedeemPoints:
                return hasPrepayRates || hasPayLaterRates;
            default: break;
        }
    }
    
    return YES;
}

- (EHICarClassCharge *)chargesForCarClass:(EHICarClass *)carClass
{
    BOOL usePrepay = [self usePrepay:carClass];

    // In any rare case where we cannot get the Pay Later pricing, then the Prepay pricing will be shown.
    return [carClass chargeForPrepay:usePrepay] ?: [carClass chargeForPrepay:YES];
}

- (EHICarClassVehicleRate *)vehicleRatesForCarClass:(EHICarClass *)carClass
{
    BOOL usePrepay = [self usePrepay:carClass];
    
    // In any rare case where we cannot get the Pay Later pricing, then the Prepay pricing will be shown.
    return [carClass vehicleRateForPrepay:usePrepay] ?: [carClass vehicleRateForPrepay:YES];
}

- (BOOL)usePrepay:(EHICarClass *)carClass
{
    BOOL defaultToPayLater = self.defaultPayment == EHICarClassChargeTypePayLater;
    BOOL supportsPrePay    = carClass.supportsPrepay;
    BOOL isPrePay          = self.isPrepay;
    
    return !defaultToPayLater && (supportsPrePay || isPrePay);
}

# pragma mark - Accessors

- (BOOL)isPrepay
{
    return self.builder.reservation.selectedPaymentOption == EHIReservationPaymentOptionPayNow;
}

- (BOOL)isModify
{
    return self.builder.isModifyingReservation;
}

# pragma mark - Passthrough

- (EHIReservationBuilder *)builder
{
    return [EHIReservationBuilder sharedInstance];
}

@end
