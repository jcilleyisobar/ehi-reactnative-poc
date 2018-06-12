//
//  EHIReviewViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 4/2/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservationStepViewModel.h"
#import "EHILocation.h"
#import "EHIInformationBannerViewModel.h"
#import "EHIReservationScheduleCellViewModel.h"
#import "EHIReservationPoliciesViewModel.h"
#import "EHIRedemptionPointsViewModel.h"
#import "EHIReviewPaymentOptionsViewModel.h"
#import "EHIDeliveryCollectionCellViewModel.h"
#import "EHIReservation.h"
#import "EHISectionHeaderModel.h"
#import "EHIReviewPrepayPaymentViewModel.h"
#import "EHIReservationRentalPriceTotalViewModel.h"
#import "EHIContractNotificationViewModel.h"
#import "EHIReviewModifyPrepayBannerViewModel.h"
#import "EHIReservationPriceSublistViewModel.h"
#import "EHIReviewSectionHeaderViewModel.h"
#import "EHIReviewAdditionalInfoViewModel.h"
#import "EHIReviewPaymentChangeViewModel.h"
#import "EHIReviewPaymentMethodViewModel.h"
#import "EHIReservationPriceButtonType.h"
#import "EHITermsAndConditionsCellViewModel.h"

typedef NS_ENUM(NSUInteger, EHIReviewSection) {
    EHIReviewSectionPaymentChange,
    EHIReviewSectionModifyPrepay,
    EHIReviewSectionRedemption,
    EHIReviewSectionRentalHeader,
    EHIReviewSectionDiscount,
    EHIReviewSectionDiscountNotify,
    EHIReviewSectionBanner,
    EHIReviewSectionLocation,
    EHIReviewSectionPickupReturn,
    EHIReviewSectionCarClass,
    EHIReviewSectionCarClassUpgrade,
    EHIReviewSectionIncludedExtras,
    EHIReviewSectionMandatoryExtras,
    EHIReviewSectionAddedExtras,
    EHIReviewSectionPriceDetails,
    EHIReviewSectionPriceTotal,
    EHIReviewSectionDriverInfo,
    EHIReviewSectionFlightDetails,
    EHIReviewSectionAdditionalInfo,
    EHIReviewSectionTravelPurpose,
    EHIReviewSectionDeliveryCollection,
    EHIReviewSectionPrepayPayment,
    EHIReviewSectionPaymentOptions,
    EHIReviewSectionPaymentMethod,
    EHIReviewSectionPaymentMethodLocked,
    EHIReviewSectionPolicies,
    EHIReviewSectionTermsAndConditions
};

typedef NS_ENUM(NSUInteger, EHIReservationCommitState) {
    EHIReservationCommitStateNotStarted,
    EHIReservationCommitStateLoading,
    EHIReservationCommitStateSuccessful,
    EHIReservationCommitStateFailed
};

@interface EHIReviewViewModel : EHIReservationStepViewModel <MTRReactive>

@property (strong, nonatomic) EHIReviewPaymentChangeViewModel *paymentChangeModel;
@property (strong, nonatomic) EHIReservationPriceSublistViewModel *priceSublistModel;
@property (strong, nonatomic) id<EHIPriceContext> totalPrice;
@property (strong, nonatomic) EHICarClass *carClass;
@property (strong, nonatomic) EHICarClass *carClassUpgrade;
@property (strong, nonatomic) EHILocation *pickupLocation;
@property (strong, nonatomic) EHILocation *returnLocation;
@property (copy  , nonatomic) NSArray *includedExtras;
@property (copy  , nonatomic) NSArray *mandatoryExtras;
@property (copy  , nonatomic) NSArray *optionalExtras;
@property (strong, nonatomic) EHIReservationScheduleCellViewModel *scheduleViewModel;
@property (strong, nonatomic) EHIReservationRentalPriceTotalViewModel *totalPriceViewModel;
@property (strong, nonatomic) EHIReservationPoliciesViewModel *policiesViewModel;
@property (strong, nonatomic) EHIReviewPaymentOptionsViewModel *paymentOptionsModel;
@property (strong, nonatomic) EHIReviewPaymentMethodViewModel *selectedPaymentModel;
@property (strong, nonatomic) EHIModel *paymentMethodLocked;
@property (strong, nonatomic) EHIInformationBannerViewModel *bannerModel;
@property (strong, nonatomic) EHIReviewPrepayPaymentViewModel *prepayViewModel;
@property (strong, nonatomic) EHIReviewModifyPrepayBannerViewModel *modifyPrepayViewModel;
@property (strong, nonatomic) EHIReviewAdditionalInfoViewModel *additionalInfoModel;
@property (strong, nonatomic) EHIModel *driverInfoModel;
@property (strong, nonatomic) EHIModel *flightDetailsModel;
@property (strong, nonatomic) EHIModel *travelPurposeModel;
@property (strong, nonatomic) NSArray *deliveryCollectionModels;
@property (strong, nonatomic) EHIReservation *discount;
@property (strong, nonatomic) EHIRedemptionPointsViewModel *redemptionViewModel;
@property (strong, nonatomic) EHIContractNotificationViewModel *discountNotifyModel;
@property (strong, nonatomic) EHITermsAndConditionsCellViewModel *termsModel;

@property (copy  , nonatomic) NSString *title;
@property (copy  , nonatomic) NSAttributedString *bookButtonTitle;
@property (copy  , nonatomic) NSAttributedString *quickBookTerms;
@property (assign, nonatomic) BOOL showQuickBook;
@property (assign, nonatomic) BOOL quickBookTermsRead;
@property (assign, nonatomic) BOOL bookButtonEnabled;
@property (assign, nonatomic) BOOL shouldScrollToAdditionalInfo;
@property (assign, nonatomic) EHIReservationPriceButtonSubtitleType priceSubtitleType;
@property (assign, nonatomic) EHIReservationPriceButtonType priceType;

@property (assign, nonatomic) EHIReservationCommitState reservationCommitState;
@property (assign, nonatomic) BOOL isOneWayReservation;
@property (assign, nonatomic) BOOL isLoading;
@property (assign, nonatomic) BOOL hideBookButton;

- (void)upgradeCarClass;
- (void)showDeliveryCollection;
- (void)footerButtonPressed;
- (void)showConfirmation;
- (void)showAddPayment;
- (void)removePoints;
- (void)changePaymentType;
- (void)addPrepayPaymentMethod;
- (void)didTapPrepayPayment;
- (void)showContractToastIfNeeded;
- (void)didSelectedCell;
- (void)togglePrepayQuickBookTerms;
- (void)invalidateModifyPrepayBanner;
- (void)updateBookContent;

- (EHIReviewSectionHeaderViewModel *)headerForSection:(EHIReviewSection)section;
- (BOOL)shouldSelectItemAtIndexPath:(NSIndexPath *)indexPath;
- (void)selectItem:(NSUInteger)item inSection:(NSUInteger)section;
- (void)formFieldViewModelDidChangeValue:(EHIViewModel *)viewModel;

# pragma mark - Tests

@property (copy, nonatomic) NSString *bookButtonStringTitle;
@property (copy, nonatomic) NSString *bookButtonStringSubtitle;

- (void)updateBookButton:(MTRComputation *)computation;
- (void)updateCarClassPrice:(MTRComputation *)computation;

@end
