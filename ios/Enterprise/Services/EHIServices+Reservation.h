//
//  EHIServices+Reservation.h
//  Enterprise
//
//  Created by mplace on 2/20/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIServices.h"
#import "EHIReservation.h"
#import "EHIDriverInfo.h"
#import "EHIDeliveryCollectionInfo.h"
#import "EHICreditCard.h"
#import "EHI3DSData.h"
#import "EHITermsCountries.h"

typedef void (^EHIReservationHandler)(EHIReservation *, EHIServicesError *);

@class EHIUser;
@interface EHIServices (Reservation)
- (id<EHINetworkCancelable>)initiateReservation:(EHIReservation *)reservation handler:(EHIReservationHandler)handler;
- (id<EHINetworkCancelable>)fetchCarClass:(EHICarClassFetch *)fetchModel reservation:(EHIReservation *)reservation handler:(void (^)(EHICarClass *, EHIServicesError *))handler;
- (id<EHINetworkCancelable>)selectCarClass:(EHICarClass *)carClass reservation:(EHIReservation *)reservation handler:(EHIReservationHandler)handler;
- (id<EHINetworkCancelable>)selectCarClass:(EHICarClass *)carClass reservation:(EHIReservation *)reservation inModify:(BOOL)inModify selectPrepay:(BOOL)selectPrepay handler:(EHIReservationHandler)handler;
- (id<EHINetworkCancelable>)updateExtras:(NSArray *)extras forReservation:(EHIReservation *)reservation handler:(EHIReservationHandler)handler;
- (id<EHINetworkCancelable>)fetchUpgradesForReservation:(EHIReservation *)reservation handler:(EHIReservationHandler)handler;
- (id<EHINetworkCancelable>)selectUpgrade:(EHICarClass *)upgradedCarClass reservation:(EHIReservation *)reservation inModify:(BOOL)inModify handler:(EHIReservationHandler)handler;
- (id<EHINetworkCancelable>)addDelivery:(EHIDeliveryCollectionInfo *)delivery collection:(EHIDeliveryCollectionInfo *)collection toReservation:(EHIReservation *)reservation handler:(EHIReservationHandler)handler;
- (id<EHINetworkCancelable>)commitReservation:(EHIReservation *)reservation handler:(EHIReservationHandler)handler;
- (id<EHINetworkCancelable>)fetchReservation:(EHIReservation *)reservation handler:(EHIReservationHandler)handler;
- (id<EHINetworkCancelable>)cancelReservation:(EHIReservation *)reservation handler:(void(^)(EHIServicesError *))handler;
- (id<EHINetworkCancelable>)fetchRentalForConfirmation:(NSString *)confirmation firstName:(NSString *)firstName lastName:(NSString *)lastName handler:(EHIReservationHandler)handler;
- (id<EHINetworkCancelable>)changePaymentTypeForCarClass:(EHICarClass *)carClass reservation:(EHIReservation *)reservation inModify:(BOOL)inModify handler:(EHIReservationHandler)handler;
- (id<EHINetworkCancelable>)fetchTermsAndConditionsForReservation:(EHIReservation *)reservation handler:(void (^)(EHITermsCountries *, EHIServicesError *))handler;
- (id<EHINetworkCancelable>)associateReservation:(EHIReservation *)reservation withUser:(EHIUser *)user handler:(EHIReservationHandler)handler;
@end

@interface EHIServices (ReservationModify)
- (id<EHINetworkCancelable>)modifyDateAndLocation:(EHIReservation *)reservation handler:(EHIReservationHandler)handler;
- (id<EHINetworkCancelable>)updateAvailableCarClasses:(EHIReservation *)reservation handler:(EHIReservationHandler)handler;
- (id<EHINetworkCancelable>)modifyCarClass:(EHICarClass *)carClass reservation:(EHIReservation *)reservation handler:(EHIReservationHandler)handler;
- (id<EHINetworkCancelable>)modifyExtras:(NSArray *)extras forReservation:(EHIReservation *)reservation handler:(EHIReservationHandler)handler;
- (id<EHINetworkCancelable>)modifyUpgradesForReservation:(EHIReservation *)reservation handler:(EHIReservationHandler)handler;
- (id<EHINetworkCancelable>)modifyDriver:(EHIDriverInfo *)driverInfo airline:(EHIAirline *)airline reservation:(EHIReservation *)reservation handler:(EHIReservationHandler)handler;
- (id<EHINetworkCancelable>)modifyAdditionalInfo:(NSArray<EHIContractAdditionalInfoValue> *)additionalInfo reservation:(EHIReservation *)reservation handler:(EHIReservationHandler)handler;
- (id<EHINetworkCancelable>)modifyDelivery:(EHIDeliveryCollectionInfo *)delivery collection:(EHIDeliveryCollectionInfo *)collection onReservation:(EHIReservation *)reservation handler:(EHIReservationHandler)handler;
- (id<EHINetworkCancelable>)modifyPaymentMethod:(EHIUserPaymentMethod *)paymentMethod reservation:(EHIReservation *)reservation handler:(EHIReservationHandler)handler;
- (id<EHINetworkCancelable>)commitModifyReservation:(EHIReservation *)reservation handler:(EHIReservationHandler)handler;
@end
