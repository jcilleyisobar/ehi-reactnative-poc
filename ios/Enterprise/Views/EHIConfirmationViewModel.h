//
//  EHIConfirmationViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 4/21/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservation.h"
#import "EHILocation.h"
#import "EHICarClass.h"
#import "EHIDriverInfo.h"
#import "EHIReviewSectionHeaderViewModel.h"
#import "EHIInformationBannerViewModel.h"
#import "EHIReservationScheduleCellViewModel.h"
#import "EHIReservationPoliciesViewModel.h"
#import "EHIReservationSublistViewModel.h"
#import "EHIDeliveryCollectionCellViewModel.h"
#import "EHIReservationRentalPriceTotalViewModel.h"
#import "EHIReservationPaymentMethod.h"
#import "EHIConfirmationAppStoreRateViewModel.h"
#import "EHIReservationPriceSublistViewModel.h"
#import "EHIConfirmationManageReservationViewModel.h"

typedef NS_ENUM(NSUInteger, EHIConfirmationSection) {
    EHIConfirmationSectionHeader,
    EHIConfirmationManageReservation,
    EHIConfirmationSectionAppStoreRate,
    EHIConfirmationSectionAssistance,
    EHIConfirmationSectionRentalHeader,
    EHIConfirmationSectionDiscount,
    EHIConfirmationSectionBanner,
    EHIConfirmationSectionPickupReturn,
    EHIConfirmationSectionPickup,
    EHIConfirmationSectionReturn,
    EHIConfirmationSectionSchedule,
    EHIConfirmationSectionCarClass,
    EHIConfirmationSectionIncludedExtras,
    EHIConfirmationSectionMandatoryExtras,
    EHIConfirmationSectionOptionalExtras,
    EHIConfirmationSectionPriceDetails,
    EHIConfirmationSectionPriceTotal,
    EHIConfirmationSectionDriverInfo,
    EHIConfirmationSectionFlightDetails,
    EHIConfirmationSectionAdditionalInfo,
    EHIConfirmationSectionPaymentOption,
    EHIConfirmationSectionDeliveryCollection,
    EHIConfirmationSectionPaymentMethod,
    EHIConfirmationSectionActions,
    EHIConfirmationSectionPolicies,
    EHIConfirmationSectionTermsAndConditions
};

@class EHIConfirmationActionsViewModel;
@interface EHIConfirmationViewModel : EHIViewModel <MTRReactive>

@property (strong, nonatomic, readonly) EHIReservation *reservation;

@property (strong, nonatomic) EHIReservation *discount;
@property (copy  , nonatomic) NSString *title;
@property (copy  , nonatomic) NSString *confirmationNumber;
@property (copy  , nonatomic) NSDate *pickupDate;
@property (copy  , nonatomic) NSDate *returnDate;
@property (strong, nonatomic) EHIInformationBannerViewModel *bannerModel;
@property (strong, nonatomic) EHIModel *assistanceModel;
@property (strong, nonatomic) EHIConfirmationAppStoreRateViewModel *appStoreRateModel;
@property (strong, nonatomic) EHICarClass *carClass;
@property (strong, nonatomic) EHIPrice *totalPrice;
@property (strong, nonatomic) EHIReservationRentalPriceTotalViewModel *totalPriceViewModel;
@property (strong, nonatomic) id<EHIPriceContext> priceContext;
@property (strong, nonatomic) EHIDriverInfo *driverInfo;
@property (strong, nonatomic) EHIAirline *airline;
@property (copy  , nonatomic) NSArray *additionalInfos;
@property (strong, nonatomic) EHIReservationScheduleCellViewModel *scheduleViewModel;
@property (strong, nonatomic) EHIReservationPoliciesViewModel *policiesViewModel;
@property (strong, nonatomic) EHIReservationPriceSublistViewModel *priceSublistModel;
@property (strong, nonatomic) EHIConfirmationManageReservationViewModel *manageReservationModel;
@property (copy  , nonatomic) NSArray *mandatoryExtras;
@property (copy  , nonatomic) NSArray *includedExtras;
@property (copy  , nonatomic) NSArray *optionalExtras;
@property (strong, nonatomic) NSArray *deliveryCollectionViewModels;
@property (strong, nonatomic) EHIModel *paymentMethod;
@property (nonatomic, readonly) BOOL isLoading;
@property (strong, nonatomic) EHIModel *termsModel;
@property (strong, nonatomic) EHIConfirmationActionsViewModel *defaultActionsModel;
@property (strong, nonatomic) EHILocation *pickupSectionModel;
@property (strong, nonatomic) EHILocation *returnSectionModel;
@property (strong, nonatomic) EHILocation *pickupReturnSectionModel;

// context
@property (assign, nonatomic) BOOL isFromReviewScreen;

- (BOOL)allowRateAppPopupWithLastDateShown:(NSDate*)lastDate;

// actions
- (void)showQuickPickup;
- (void)dismissConfirmation;
- (void)selectItemAtIndexPath:(NSIndexPath *)indexPath;
- (void)presentedAppleStoreRate;
- (void)promptAppleStoreRate;

// accessors
- (EHIReviewSectionHeaderViewModel *)headerForSection:(EHIConfirmationSection)section;

@end
