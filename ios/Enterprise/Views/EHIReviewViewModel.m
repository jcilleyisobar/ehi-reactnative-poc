//
//  EHIReviewViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 4/2/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReviewViewModel.h"
#import "EHIReservationStepViewModel_Subclass.h"
#import "EHIPlaceholder.h"
#import "EHIExtrasViewModel.h"
#import "EHIDriverInfoViewModel.h"
#import "EHIConfirmationViewModel.h"
#import "EHIInfoModalViewModel.h"
#import "EHIServices+User.h"
#import "EHIUserManager.h"
#import "EHIToastManager.h"
#import "EHIFormFieldViewModel+AdditionalInfo.h"
#import "EHIReviewHeaders.h"
#import "EHIFlightDetailsViewModel.h"
#import "EHIAdditionalInformationViewModel.h"
#import "EHIPaymentViewModel.h"
#import "EHISettings.h"
#import "EHIReservationBookStateBuilder.h"

@interface EHIReviewViewModel () <EHIReservationBuilderReadinessListener, EHIFormFieldDelegate>
@property (strong, nonatomic) EHIReviewHeaders *headers;
@end

@implementation EHIReviewViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _title = self.isModify
            ? EHILocalizedString(@"reservation_modify_review_navigation_title", @"Modify Rental", @"navigation bar title for in modify reservation review screen")
            : EHILocalizedString(@"reservation_review_navigation_title", @"Review & Book",  @"navigation bar title for reservation review screen");
        
        _policiesViewModel    = [[EHIReservationPoliciesViewModel alloc] initWithModel:[EHIReservationBuilder sharedInstance].reservation];
        _headers = [EHIReviewHeaders new];

        // allow travel purpose selection when not selected prerates and reservation has additional benefits
        if(!self.isModify && !self.builder.travelPurposeSelectedPreRates && self.builder.reservation.contractDetails.contractHasAdditionalBenefits) {
            _travelPurposeModel = [EHIModel placeholder];
            
            // default travel purpose to business if it has not yet been set
            if(self.builder.travelPurpose == EHIReservationTravelPurposeNone) {
                self.builder.travelPurpose = EHIReservationTravelPurposeBusiness;
            }
        }
                
        self.priceSublistModel = [[EHIReservationPriceSublistViewModel alloc] initWithCarClass:self.builder.selectedCarClass prepay:self.builder.reservation.prepaySelected];

        [self invalidateBookButton];
        [self invalidateRedemptionBanner];
        [self invalidateModifyPrepayBanner];
        [self invalidatePaymentMethodEnforcingTerms:YES];
    }
    
    return self;
}

- (void)didInitialize
{
    [super didInitialize];
    
    [self fetchVehicleUpgrades];
}

- (void)didBecomeActive
{
    [super didBecomeActive];
    
    if(self.builder.reservationIsModified && self.isModify) {
        [self invalidateModifyPrepayBanner];
    }
    
    [self.builder waitForReadiness:self];
}

//
// Helpers
//

- (void)fetchVehicleUpgrades
{
    // fetch if we're allowed and don't have upgrade vehicles to surface
    if(!self.builder.allowsVehicleUpgrade || self.builder.hasUpgradedVehicles) {
        return;
    }
    
    self.isLoading = YES;
    
    EHIReservationHandler handler = ^(EHIReservation *reservation, EHIServicesError *error) {
        //we always get a cross error back here, so we're going to consume it and now show the "call us" alert
        [error consume];
        self.isLoading = NO;
        
        if(!error.hasFailed) {
            self.carClassUpgrade = reservation.upgradeCarClassDetails.firstObject;
            [self invalidateAnalyticsContext];
        }
    };
    
    if(self.isModify) {
        [[EHIServices sharedInstance] modifyUpgradesForReservation:self.builder.reservation handler:handler];
    } else {
        [[EHIServices sharedInstance] fetchUpgradesForReservation:self.builder.reservation handler:handler];
    }
}

# pragma mark - Reactions

- (void)builderIsReady:(EHIReservationBuilder *)builder
{
    [MTRReactor autorun:self action:@selector(updateDiscount:)];
    [MTRReactor autorun:self action:@selector(updateDates:)];
    [MTRReactor autorun:self action:@selector(updateExtras:)];
    [MTRReactor autorun:self action:@selector(updateCarClassPrice:)];
    [MTRReactor autorun:self action:@selector(updateCarClassUpgrade:)];
    [MTRReactor autorun:self action:@selector(updateFlightDetails:)];
    [MTRReactor autorun:self action:@selector(updateDeliveryCollection:)];
    [MTRReactor autorun:self action:@selector(updateBillingInformation:)];
    [MTRReactor autorun:self action:@selector(updateBookButton:)];
    [MTRReactor autorun:self action:@selector(updateAdditionalInfo:)];
    
    builder.bind.map(@{
        source(builder.pickupLocation)      : dest(self, .pickupLocation),
        source(builder.returnLocation)      : dest(self, .returnLocation),
        source(builder.isOneWayReservation) : dest(self, .isOneWayReservation),
        source(builder.selectedCarClass)    : dest(self, .carClass),
        source(builder.driverInfo)          : ^(EHIDriverInfo *driverInfo) {
            self.driverInfoModel = driverInfo ?: [EHIPlaceholder new];
        }
    });
    
    [self invalidatePriceSublist];
}

- (void)updateDiscount:(MTRComputation *)computation
{
    self.discount    = self.builder.discount ? self.builder.reservation : nil;
    self.bannerModel = [EHIInformationBannerViewModel modelWithType:[self bannerTypeForBuilder:self.builder]];
    
    // if the contract attached is setup for 3rd party notification, show the banner
    EHIContractDetails *contract = self.builder.reservation.contractDetails;
    if(contract.thirdPartyEmailRequired) {
        EHIContractNotificationFlow flow = self.isModify ? EHIContractNotificationFlowModify : EHIContractNotificationFlowDefault;
        self.discountNotifyModel = [[EHIContractNotificationViewModel alloc] initWithContract:contract flow:flow];
    }
}

- (void)updateDates:(MTRComputation *)computation
{
    EHIReservation *reservation = self.builder.reservation;
    self.scheduleViewModel = [[EHIReservationScheduleCellViewModel alloc] initWithPickupDate:reservation.pickupTime returnDate:reservation.returnTime];
}

- (void)updateCarClassPrice:(MTRComputation *)computation
{
    self.totalPrice = self.builder.totalPrice;
    
    EHICarClassPriceDifference *priceDifference = self.priceSublistModel.carClass.unpaidRefundDifference;
    EHIReservationRentalPriceTotalLayout layout = priceDifference == nil ? EHIReservationRentalPriceTotalLayoutReview : EHIReservationRentalPriceTotalLayoutUnpaidRefund;
    
    EHIPrice *paidAmount   = [(EHIReservationPaymentMethod *)self.builder.reservation.reservationPayments.firstObject amount];
    EHIPrice *actualAmount = nil;
    if(layout == EHIReservationRentalPriceTotalLayoutUnpaidRefund) {
        EHICarClassPriceDifference *difference = self.builder.reservation.selectedCarClass.unpaidRefundDifference;
        actualAmount = [difference eligibleForCurrencyConvertion] ? difference.paymentDifference : nil;
    }

    BOOL showPaymentOption = [self canChangePaymentOption];
    self.totalPriceViewModel = [[EHIReservationRentalPriceTotalViewModel alloc] initWithModel:self.builder.selectedCarClass
                                                                               prepaySelected:self.builder.reservation.prepaySelected
                                                                                   paidAmount:paidAmount
                                                                                 actualAmount:actualAmount
                                                                              showOtherOption:showPaymentOption
                                                                                       layout:layout
                                                                                 isSecretRate:self.isSecretRate];
    
    self.paymentChangeModel  = showPaymentOption ? [[EHIReviewPaymentChangeViewModel alloc] initWithPriceContext:self.carClass.prepayDifference prepay:self.isPrepay] : nil;
}

- (BOOL)canChangePaymentOption
{
    BOOL supportsPrepay  = self.builder.reservation.selectedCarClass.supportsPrepay;
    BOOL redeemingPoints = self.builder.reservation.selectedPaymentOption == EHIReservationPaymentOptionRedeemPoints;
    BOOL usingContract   = self.builder.reservation.contractDetails.contractType == EHIContractTypeCorporate;
    BOOL isBlocking      = self.builder.reservation.blockModifyPickupLocation && self.isModify;
    
    BOOL hasRates = NO;
    switch (self.builder.reservation.selectedPaymentOption) {
        case EHIReservationPaymentOptionPayNow:
            hasRates = [self hasRatesForPrePay:NO];
            break;
        case EHIReservationPaymentOptionPayLater:
            hasRates = [self hasRatesForPrePay:YES];
            break;
        default: break;
    }
    
    return supportsPrepay && hasRates && !redeemingPoints && !usingContract && !isBlocking;
}

- (BOOL)hasRatesForPrePay:(BOOL)prepay
{
    EHICarClass *selectedCarClass = self.builder.reservation.selectedCarClass;
    
    return [selectedCarClass priceContextForPrepay:prepay] != nil;
}

- (void)updateCarClassUpgrade:(MTRComputation *)computation
{
    BOOL isRedeeming = self.builder.daysRedeemed != 0;
    self.carClassUpgrade = isRedeeming ? nil : self.builder.reservation.upgradeCarClassDetails.firstObject;
}

- (void)updateExtras:(MTRComputation *)computation
{
    // group extras by their status so that they can be fetched per-section
    NSDictionary *groupedExtras = self.builder.selectedExtras.groupBy(^(EHICarClassExtra *extra) {
        return @(extra.status);
    });
    
    self.includedExtras  = groupedExtras[@(EHICarClassExtraStatusIncluded)];
    self.mandatoryExtras = groupedExtras[@(EHICarClassExtraStatusMandatory)];
    // show edit row if no optional extras exist
    NSArray *optionalExtras = groupedExtras[@(EHICarClassExtraStatusOptional)];
    NSArray *waivedExtras   = groupedExtras[@(EHICarClassExtraStatusWaived)];
    
    optionalExtras = (optionalExtras ?: @[]).concat(waivedExtras);

    // if has 'no added extras', fallback the 'optional extras' to give a user a chance to opt-in
    self.optionalExtras = optionalExtras.count ? optionalExtras : @[[EHIModel placeholder]];
    
    // hack to show section title only for first item in section
    [self.includedExtras.firstObject setShouldShowSectionTitle:YES];
    [self.mandatoryExtras.firstObject setShouldShowSectionTitle:YES];
    
    if(![self.optionalExtras.firstObject isPlaceholder]) {
        [self.optionalExtras.firstObject setShouldShowSectionTitle:YES];
    }
}

- (void)updateFlightDetails:(MTRComputation *)computation
{
    // no flight details if user is not booking with airport
    if(self.builder.reservation.pickupLocation.type != EHILocationTypeAirport) {
        self.flightDetailsModel = nil;
    }
    // otherwise, show current airline details or button to input airline
    else if(self.builder.airline){
        self.flightDetailsModel = self.builder.airline;
    } else {
        self.flightDetailsModel = [EHIModel placeholder];
    }
}

- (void)updateDeliveryCollection:(MTRComputation *)computation
{
    // suppress delivery and collections and show whatever is already on the reservation
    if(self.isModify) {
        self.deliveryCollectionModels = [EHIDeliveryCollectionCellViewModel viewModelsForReservation:self.builder.reservation];
        return;
    }
    
    BOOL allowsDeliveryCollection = self.builder.reservation.allowsDelivery || self.builder.reservation.allowsCollection;
    BOOL isLeisure                = self.builder.travelPurpose == EHIReservationTravelPurposeLeisure;
    BOOL isBlacklisted            = !self.builder.reservation.contractDetails.contractHasAdditionalBenefits;
    
    // expose view model(s)
    if(allowsDeliveryCollection && !isLeisure && !isBlacklisted) {
        self.deliveryCollectionModels = [EHIDeliveryCollectionCellViewModel viewModelsForReservationAllowsButton:self.builder.reservation];
    }
    else {
        self.deliveryCollectionModels = nil;
    }
}

- (void)updateBillingInformation:(MTRComputation *)computation
{
    EHIReservation *reservation = self.builder.reservation;

    // suppress billing options and show whatever is already on the reservation
    if(self.isModify) {
        EHIUserPaymentMethod *paymentMethod = nil;
        BOOL isCorporate = reservation.isCorporate;
        BOOL isPrepay    = self.isPrepay;
        if(isPrepay && self.shouldUseSelectedPayment) {
            paymentMethod = self.builder.paymentMethod;
        } else if(isCorporate) {
            paymentMethod = reservation.billingAccount ?: [EHIUserPaymentMethod emptyPaymentMethod];
        }
        self.selectedPaymentModel = paymentMethod
            ? [[EHIReviewPaymentMethodViewModel alloc] initWithModel:paymentMethod
                                                    forCorporateFlow:isCorporate
                                                            inModify:self.isModify]
            : nil;
        
        return;
    }

    BOOL travelingOnLeisure     = self.builder.travelPurpose == EHIReservationTravelPurposeLeisure;
    BOOL billingAccountExists   = reservation.contractDetails.billingAccountExists;
    BOOL contractHasBenefits    = reservation.contractDetails.contractHasAdditionalBenefits;
    BOOL customerAcceptsBilling = reservation.contractDetails.customerAcceptsBilling;
    
    [MTRReactor nonreactive:^{
        
        // if business leisure info is not allowed, we need to default to the correct payment method type and hide the payment section
        if(!contractHasBenefits && billingAccountExists) {
            // if the contract accepts billing, default to the billing payment type, otherwise opt out of billing
            EHIUserPaymentMethod *paymentMethod = [EHIUserPaymentMethod existingBillingMethod];
            
            // update the builder with the payment method
            self.builder.paymentMethod = paymentMethod;
        }
        
        // if contract has additional benefits but customer does not accept billing, hide the payment section and default to pay at counter
        if(contractHasBenefits && !customerAcceptsBilling) {
            // update the builder with the payment method
            self.builder.paymentMethod = nil;
        }
        
        // if contract has additional benefits as well as customer accepts billing, show the payment options section
        if(contractHasBenefits && customerAcceptsBilling && !travelingOnLeisure) {
            if(!self.paymentOptionsModel) {
                self.paymentOptionsModel = [[EHIReviewPaymentOptionsViewModel alloc] initWithModel:self.builder.reservation];
            }
        }
        
        // wipe out payment when traveling on leisure
        if(travelingOnLeisure) {
            self.paymentOptionsModel = nil;
            self.builder.paymentMethod = nil;
        }
    }];
}

- (void)updateBookContent
{
    [self updateBookButton:nil];
}

- (void)updateBookButton:(MTRComputation *)computation
{
	EHIReservationBuilder *builder      = self.builder;
	EHIReservation *reservation         = builder.reservation;
	EHIUserPaymentMethod *paymentMethod = builder.paymentMethod;

	BOOL usingPreferredPayment = self.usingPreferredPaymentMethod;
    if(usingPreferredPayment) {
        self.bookButtonEnabled = self.quickBookTermsRead;
    } else {
        self.bookButtonEnabled = YES;
    }
	
	BOOL isPrepay     = reservation.prepaySelected;
    BOOL needsAddCard = reservation.collectNewCardInModify;
    BOOL isModify     = self.isModify;

    NSString *buttonTitle = EHIReservationBookStateBuilder.new
        .modify(isModify)
        .prepay(isPrepay)
        .collectsNewCreditCard(needsAddCard)
        .paymentMethod(paymentMethod).title;
    
    BOOL addedCreditCard      = builder.creditCardAdded;
    id<EHIPriceContext> price = self.totalPrice;
    NSString *discount        = reservation.contractDetails.name ?: paymentMethod.lastFour ?: reservation.discountCode;
	EHICarClassPriceDifference *priceDifference = builder.selectedCarClass.unpaidRefundDifference;

    NSString *buttonSubtitle = EHIReservationBookStateBuilder.new
        .prepay(isPrepay)
        .addedCreditCard(addedCreditCard)
        .paymentMethod(paymentMethod)
        .currencyConversion(price)
        .discount(discount)
        .priceDifference(priceDifference).subtitle;
    
    // apply style and format
    EHIAttributedStringBuilder *stringBuilder = EHIAttributedStringBuilder.new.lineSpacing(8).color([UIColor whiteColor])
        .text(buttonTitle).fontStyle(EHIFontStyleBold, 18.0);
    
    if(buttonSubtitle.length > 0) {
		stringBuilder.lineSpacing(0).newline.appendText(buttonSubtitle).lineSpacing(0).fontStyle(EHIFontStyleRegular, 14.0);
    }
    
    self.bookButtonStringTitle    = buttonTitle;
    self.bookButtonStringSubtitle = buttonSubtitle;
    self.bookButtonTitle = stringBuilder.string;
}

- (void)updateAdditionalInfo:(MTRComputation *)computation
{
    NSArray *additionalInfo = self.builder.reservation.contractDetails.additionalInformation;
    if(additionalInfo.count > 0) {
        self.additionalInfoModel =[[EHIReviewAdditionalInfoViewModel alloc] initWithAdditionalInfo:self.builder.discount.additionalInformation];
    } else {
        self.additionalInfoModel = nil;
    }
}

- (void)setReservationCommitState:(EHIReservationCommitState)reservationCommitState
{
    _reservationCommitState = reservationCommitState;
    
    if(reservationCommitState == EHIReservationCommitStateFailed) {
        [self removeCreditCard];
    }
}

//
// Helper
//

- (EHIInformationBannerType)bannerTypeForBuilder:(EHIReservationBuilder *)builder
{
    if(builder.isEmeraldReservation && builder.discount == nil) {
        return EHIInformationBannerTypeEmeraldReview;
    }
    
    return EHIInformationBannerTypeNone;
}

#pragma mark - Actions

- (void)upgradeCarClass
{
    self.isLoading = YES;
    
    [[EHIServices sharedInstance] selectUpgrade:self.carClassUpgrade reservation:self.builder.reservation inModify:self.isModify handler:^(EHIReservation *reservation, EHIServicesError *error) {
        self.isLoading = NO;
    
        if(!error.hasFailed) {
            self.builder.reservation.upgradeDetails = self.carClassUpgrade;

            [EHIToastManager showMessage:EHILocalizedString(@"review_reservation_upgrade_success", @"You've been upgraded!", @"")];
                        
            // insert our new car class
            self.carClass        = self.builder.selectedCarClass;
            self.carClassUpgrade = nil;
            
            // manually rerun reactions with updated price data
//            [self rerunReactions];
            [self invalidatePriceSublist];
            [self updateCarClassPrice:nil];
            [self updateBookButton:nil];
            
            [self.builder synchronizeReservationOnContext:nil];
            
            [EHIAnalytics trackAction:EHIAnalyticsResActionUpgradeNow handler:nil];
        }
    }];
}

# pragma mark - Invalidation

- (void)rerunReactions
{
    // manually rerun reaction to show upgrade again, if it exists
    [self fetchVehicleUpgrades];
    
    // manually rerun reactions with updated pricing
    [self invalidatePaymentMethodEnforcingTerms:YES];
    [self updateCarClassPrice:nil];
    [self updateBookButton:nil];
    [self invalidateRedemptionBanner];
    [self invalidateModifyPrepayBanner];
    
    [self.builder synchronizeReservationOnContext:nil];
    
    [self invalidatePriceSublist];
}

- (void)invalidateBookButton
{
    BOOL modifyingPrepay = self.isModify && self.builder.reservation.prepaySelected;
    _hideBookButton = modifyingPrepay && !self.builder.reservationIsModified;
}

- (void)invalidatePriceSublist
{
    [MTRReactor nonreactive:^{
        self.priceSublistModel = [[EHIReservationPriceSublistViewModel alloc] initWithCarClass:self.builder.selectedCarClass prepay:self.builder.reservation.prepaySelected];
    }];
}

- (void)invalidateRedemptionBanner
{
    BOOL showRedemptionBanner = ![self shouldHideRedemptionBanner];
    self.redemptionViewModel = showRedemptionBanner ? [EHIRedemptionPointsViewModel modelWithType:EHIRedemptionBannerTypeReview] : nil;
}

- (void)invalidateModifyPrepayBanner
{
    BOOL showModifyBanner = self.isModify && self.builder.reservation.prepaySelected && self.builder.reservationWasPrepay;

    if(showModifyBanner) {
        if(!self.modifyPrepayViewModel) {
            EHIPrice *originalPrice = [self.builder originalPricePrepaySelected:YES];
            self.modifyPrepayViewModel = [[EHIReviewModifyPrepayBannerViewModel alloc] initWithModel:originalPrice];
        }
        
        BOOL isAirport  = self.builder.modifiedReservationPickupLocation.type == EHILocationTypeAirport;
        BOOL isNALocale = [NSLocale ehi_isUSAOrCanada];
        self.modifyPrepayViewModel.isNAAirport = isAirport && isNALocale;
        self.modifyPrepayViewModel.updated = !self.hideBookButton;
    } else {
        self.modifyPrepayViewModel = nil;
    }
}

- (void)invalidatePaymentMethodEnforcingTerms:(BOOL)enforceTerms
{
    // ignore payment method when using a corporate contract
    BOOL usingContract = self.builder.reservation.contractDetails.contractType == EHIContractTypeCorporate;
    if(usingContract) {
        return;
    }
    
    BOOL usePreferredPayment = self.usingPreferredPaymentMethod;
    BOOL hasPreferredPayment = [EHIUser currentUser].preferredPaymentAccount != nil;
    if(usePreferredPayment && hasPreferredPayment && enforceTerms) {
        self.builder.paymentMethod = [EHIUser currentUser].preferredPaymentAccount;
    }
    
    if(self.shouldUseSelectedPayment) {
        self.prepayViewModel      = nil;
        self.selectedPaymentModel = [[EHIReviewPaymentMethodViewModel alloc] initWithModel:self.builder.paymentMethod];
        self.selectedPaymentModel.showTermsToggle = usePreferredPayment;
    } else {
        self.prepayViewModel      = self.builder.paymentMethod != nil ? [EHIReviewPrepayPaymentViewModel new] : nil;
        self.selectedPaymentModel = nil;
    }
    
    // compute terms and conditions
    self.showQuickBook = usePreferredPayment && enforceTerms;
    self.quickBookTermsRead = !enforceTerms;
}

- (BOOL)shouldUseSelectedPayment {
    EHIUserPaymentMethod *selectedPaymentMethod = self.builder.paymentMethod;
    
    EHIUserPaymentMethod *profilePaymentMethod = ([EHIUser currentUser].paymentMethods ?: @[]).find(^(EHIUserPaymentMethod *paymentMethod){
        return [paymentMethod.paymentReferenceId isEqualToString:selectedPaymentMethod.paymentReferenceId];
    });
    
    BOOL isLogged = self.isLogged;
    BOOL canUseCustomPayment = [NSLocale ehi_shouldAllowProfilePaymentEdit];
    BOOL existsInProfile     = profilePaymentMethod != nil;
    
    return canUseCustomPayment && existsInProfile && isLogged;
}

- (BOOL)invalidateAdditionalInfo
{
    self.shouldScrollToAdditionalInfo = [self needsAdditionalInfo];
    return self.shouldScrollToAdditionalInfo;
}

- (void)removePoints
{
    self.isLoading = YES;
    self.carClass.daysToRedeem = 0;
    
    EHIReservationHandler handler = ^(EHIReservation *reservation, EHIServicesError *error) {
        self.isLoading = NO;
        
        if(!error.hasFailed) {
            [self rerunReactions];
        }
    };
    
    [[EHIServices sharedInstance] selectCarClass:self.carClass
                                     reservation:self.builder.reservation
                                        inModify:self.isModify
                                    selectPrepay:NO
                                         handler:handler];
}

- (void)changePaymentType
{
    self.isLoading = YES;
    
    EHIReservationHandler handler = ^(EHIReservation *reservation, EHIServicesError *error) {
        self.isLoading = NO;
        
        if(!error.hasFailed) {
            // reset credit card info on reservation builder
            // TODO: we should really get this back from services!
            [self removeCreditCard];
            [self rerunReactions];
        }
    };
    
    [[EHIServices sharedInstance] changePaymentTypeForCarClass:self.carClass reservation:self.builder.reservation inModify:self.isModify handler:handler];
    
    [EHIAnalytics trackAction:self.builder.reservation.prepaySelected ? EHIAnalyticsResActionReviewSelectPayLater : EHIAnalyticsResActionReviewSelectPayNow handler:nil];
}

- (void)removeCreditCard
{
    self.builder.reservation.creditCard3dsValidation = nil;
    self.builder.creditCardAdded = NO;
    self.builder.paymentMethod   = nil;
    self.selectedPaymentModel    = nil;
    self.prepayViewModel         = nil;
    
    BOOL wipePaymentMethod = self.builder.paymentMethod.paymentType == EHIUserPaymentTypeCard;
    if(wipePaymentMethod) {
        self.builder.paymentMethod = nil;
    }
}

- (void)addPrepayPaymentMethod
{
    __weak __typeof(self) welf = self;
    void (^paymentHandler)(NSString *) = ^(NSString *panguiPaymentId) {

        EHIUserPaymentMethod *payment;
        
        // add a pangui payment as EHIPaymentMethod, because the reservation needs it's reference id.
        if(panguiPaymentId) {
            payment = ([EHIUser currentUser].payment.paymentMethods ?: @[]).find(^(EHIUserPaymentMethod *paymentMethod){
                return [paymentMethod.paymentReferenceId isEqualToString:panguiPaymentId];
            });
            
            // set one-time payment if no payment method was found in user's profile
            if(!payment) {
                payment = [EHIUserPaymentMethod oneTimePaymentMethod:panguiPaymentId];
            }
            
        } else {
            payment = [EHIUserPaymentMethod creditCardPaymentMethod];
        }
        
        welf.builder.paymentMethod   = payment;
        [welf invalidatePaymentMethodEnforcingTerms:NO];
    };
    
    BOOL canSelectCard = [NSLocale ehi_shouldAllowSelectPayment];
    BOOL hasCards      = [EHIUser currentUser].creditCardPaymentMethods.count > 0;
    BOOL isLogged      = self.isLogged;
    BOOL canSelectPayment = canSelectCard && hasCards && isLogged;
    
    [EHIAnalytics trackAction:EHIAnalyticsResActionAddPaymentMethod handler:nil];
    
    if(canSelectPayment) {
        self.router.transition.push(EHIScreenSelectPayment).handler(paymentHandler).start(nil);
    } else {
        self.router.transition.push(EHIScreenPayment).handler(paymentHandler).object(@(EHIPaymentViewStyleReservation)).start(nil);
    }

}

- (void)didTapPrepayPayment
{
    if(self.builder.creditCardAdded) {
        [EHIAnalytics trackAction:EHIAnalyticsResActionReviewRemoveCreditCard handler:nil];
        
        [self removeCreditCard];
        [self rerunReactions];
    } else {
        [self addPrepayPaymentMethod];
    }
}

- (void)footerButtonPressed
{
    if(self.usingPreferredPaymentMethod) {
        if(!self.quickBookTermsRead) {
            NSString *message = EHILocalizedString(@"review_prepay_na_terms_not_selected", @"You must agree to the terms and conditions to continue", @"");
            [EHIToastManager showMessage:message];
            return;
        }
    }

    if(self.invalidateAdditionalInfo) {
        [EHIToastManager showMessage:EHILocalizedString(@"review_please_enter_additional_information", @"Please provide the required additional information.", @"")];
        return;
    }


    // we need to check if the button is enabled, because now it can respond to touches, even in a disabled state.
    if(!self.bookButtonEnabled) {
        return;
    }
    
    void (^addPaymentBlock)() = ^{
        [self addPrepayPaymentMethod];
    };
    
    void (^bookReservationBlock)() = ^{
        [self bookReservation];
    };
    
    BOOL isModify     = self.isModify;
    BOOL isPrepay     = self.builder.reservation.prepaySelected;
    BOOL needsAddCard = self.builder.reservation.collectNewCardInModify;
    EHIUserPaymentMethod *paymentMethod = self.builder.paymentMethod;
    
    EHIReservationBookStateTitleFlow flow = EHIReservationBookStateBuilder.new
        .modify(isModify)
        .prepay(isPrepay)
        .collectsNewCreditCard(needsAddCard)
        .paymentMethod(paymentMethod).titleState;
    
    if(flow == EHIReservationBookStateTitleFlowAddPayment) {
        addPaymentBlock();
    } else {
        bookReservationBlock();
    }
}

- (void)bookReservation
{
    
#if EHIReservationMock
    [self showConfirmation];
#else
    // track the user tapping on this button
    [EHIAnalytics trackAction:EHIAnalyticsResActionBookRental handler:nil];
    
    // start in the loading state
    self.reservationCommitState = EHIReservationCommitStateLoading;
    
    if([NSLocale ehi_shouldCommitReservationWith3dsCheck]) {
        BOOL needNewCardInModify = self.isModify && self.builder.reservation.collectNewCardInModify;
        BOOL shouldCheck3Ds = self.isPrepay && (!self.isModify || needNewCardInModify);
        [self.builder commitReservationWith3DSCheck:shouldCheck3Ds handler:^(EHIServicesError *error) {
            self.reservationCommitState = error ? EHIReservationCommitStateFailed : EHIReservationCommitStateSuccessful;
        }];
    } else {
        [self.builder commitOrModifyReservationWithHandler:^(EHIServicesError *error) {
            self.reservationCommitState = error ? EHIReservationCommitStateFailed : EHIReservationCommitStateSuccessful;
        }];
    }
  
#endif
}

- (BOOL)needsAdditionalInfo
{
    return (self.builder.discount.additionalInformation ?: @[]).any(^(EHIContractAdditionalInfo *info){
        NSString *value = [self.builder additionalInfoForKey:info.uid].value;
        return info.isRequired && value == nil;
    });
}

- (void)showConfirmation
{
    EHIConfirmationViewModel *viewModel = [[EHIConfirmationViewModel alloc] initWithModel:self.builder.reservation];
    viewModel.isFromReviewScreen = YES;
    
    self.router.transition
        .push(EHIScreenConfirmation).object(viewModel).start(nil);
}

- (void)showContractToastIfNeeded
{
    EHIContractDetails *contract = self.builder.reservation.contractDetails;
    if(contract.thirdPartyEmailRequired) {
        NSString *message = EHILocalizedString(@"confirmation_cancel_contract_success_message", @"Trip details have been forwarded to the administrator/s for #{contract_name}", @"");
        message = [message ehi_applyReplacementMap:@{ @"contract_name": contract.name ?: @"" }];
        [EHIToastManager showMessage:message];
    }
}

- (void)showAddPayment
{
    [self addPrepayPaymentMethod];
}

- (void)didSelectedCell
{
    self.hideBookButton = NO;
}

# pragma mark - Selection

- (BOOL)shouldSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    return indexPath.section != EHIReviewSectionLocation
        && indexPath.section != EHIReviewSectionPaymentMethod;
}

- (void)selectItem:(NSUInteger)item inSection:(NSUInteger)section
{
    // track the appropriate action for the selection
    NSString *action = [self analyticsActionForSelectionInSection:(EHIReviewSection)section];
    if(action) {
        [EHIAnalytics trackAction:action handler:nil];
    }
  
    // run the appropriate transition
    switch((EHIReviewSection)section) {
        case EHIReviewSectionLocation:
            self.hideBookButton = NO;
            [self.builder editInfoForReservationStep:EHIReservationStepLocation]; break;
        case EHIReviewSectionPickupReturn:
            self.hideBookButton = NO;
            [self.builder editInfoForReservationStep:EHIReservationStepItinerary]; break;
        case EHIReviewSectionCarClass:
            self.hideBookButton = NO;
            [self.builder editInfoForReservationStep:EHIReservationStepClassSelect]; break;
        case EHIReviewSectionAddedExtras:
            self.hideBookButton = NO;
            [self didSelectExtras]; break;
        case EHIReviewSectionDriverInfo:
            self.hideBookButton = NO;
            [self didSelectDriverInfo]; break;
        case EHIReviewSectionFlightDetails:
            self.hideBookButton = NO;
            [self didSelectFlightDetails]; break;
        case EHIReviewSectionDeliveryCollection:
            [self showDeliveryCollection]; break;
        case EHIReviewSectionAdditionalInfo:
            self.hideBookButton = NO;
            [self showAdditionalInfo]; break;
        case EHIReviewSectionTermsAndConditions:
            self.router.transition.present(EHIScreenTermsAndConditions).object(self.builder.reservation).start(nil); break;
        default: break;
    }
}

- (void)didSelectExtras
{
    EHIExtrasViewModel *viewModel = [EHIExtrasViewModel new];
    
    [viewModel setIsEditing:YES];
    [viewModel updateWithModel:self.builder.reservation.selectedCarClass];
    
    self.router.transition
        .push(EHIScreenReservationExtras).object(viewModel).start(nil);
}

- (void)didSelectDriverInfo
{
    EHIDriverInfoViewModel *viewModel = [EHIDriverInfoViewModel new];
    viewModel.isEditing = YES;
    
    self.router.transition
        .push(EHIScreenReservationDriverInfo).object(viewModel).start(nil);
}

- (void)didSelectFlightDetails
{
    self.router.transition
    .push(EHIScreenReservationFlightDetails).object(@(EHIFlightDetailsStateNone)).start(nil);
}

- (void)showDeliveryCollection
{
    self.router.transition
        .push(EHIScreenReservationDeliveryCollection).start(nil);
}

- (void)showAdditionalInfo
{
    EHIAdditionalInformationViewModel *model = [[EHIAdditionalInformationViewModel alloc] initWithFlow:EHIAdditionalInformationFlowReview];

    __weak __typeof(self) welf = self;
    self.router.transition.push(EHIScreenReservationAdditionalInfo)
        .object(model)
        .handler(^(BOOL submitted, EHIServicesError *error) {
            [welf invalidateAdditionalInfo];
            welf.router.transition.pop(1).start(nil);
        }).start(nil);
}

- (BOOL)shouldHideRedemptionBanner
{
    return self.builder.hideRedemption;
}

- (void)togglePrepayQuickBookTerms
{
    self.quickBookTermsRead = !self.quickBookTermsRead;
    self.showQuickBook      = !self.quickBookTermsRead;
}

//
// Helpers
//

- (BOOL)isSecretRate
{
    return self.carClass.isSecretRateAfterCarSelected;
}

- (NSString *)analyticsActionForSelectionInSection:(EHIReviewSection)section
{
    switch(section) {
        case EHIReviewSectionLocation:
            return EHIAnalyticsResActionChangeLoc;
        case EHIReviewSectionPickupReturn:
            return EHIAnalyticsResActionChangeDate;
        case EHIReviewSectionCarClass:
            return EHIAnalyticsResActionChangeVehicle;
        case EHIReviewSectionAddedExtras:
            return EHIAnalyticsResActionChangeExtras;
        default: return nil;
    }
}

- (BOOL)usingPreferredPaymentMethod
{
    BOOL isQuickBookAllowed = [NSLocale ehi_shouldAllowQuickBookReservation];
    BOOL selectPreferred    = [EHISettings sharedInstance].selectPreferredPaymentMethodAutomatically;
    BOOL isUsingPrepay      = self.builder.reservation.prepaySelected;
    BOOL isLogged           = self.isLogged;
    BOOL isModify           = self.isModify;
    
    return isQuickBookAllowed && selectPreferred && isUsingPrepay && isLogged && !isModify;
}

- (BOOL)isLogged
{
    EHIUserManager *userManager = [EHIUserManager sharedInstance];
    return userManager.currentUser != nil && !userManager.isEmeraldUser;
}

- (EHIPrice *)unpaidRefundDifference
{
    return self.carClass.unpaidRefundDifference.viewDifference;
}

- (EHITermsAndConditionsCellViewModel *)termsModel
{
    EHITermsAndConditionsCellViewModel *model = EHITermsAndConditionsCellViewModel.new;
    model.layout = EHITermsAndConditionsLayoutReview;
    return model;
}

#pragma mark - EHIFormFieldDelegate

- (void)formFieldViewModelDidChangeValue:(EHIViewModel *)viewModel;
{
    self.hideBookButton = NO;
    EHIFormFieldViewModel *model = (EHIFormFieldViewModel *)viewModel;
    if(model) {
        NSString *value = [model.inputValue isKindOfClass:[NSDate class]] ? [model.inputValue ehi_dateTimeString] : model.inputValue;
        
        [self.builder setAdditionalInfo:value forKey:model.uid];
    }
}

# pragma mark - Accessors

- (void)setCarClassUpgrade:(EHICarClass *)carClassUpgrade
{
    EHIPrice *price = [carClassUpgrade upgradeDifferenceForPrepay:self.builder.reservation.prepaySelected].viewDifference;
    if(price && price.amount > 0) {
        _carClassUpgrade = carClassUpgrade;
    } else {
        _carClassUpgrade = nil;
    }
}

- (void)setHideBookButton:(BOOL)hideBookButton
{
    _hideBookButton = hideBookButton;
    
    self.builder.reservationIsModified = !hideBookButton;
}

- (void)setQuickBookTermsRead:(BOOL)quickBookTermsRead
{
    _quickBookTermsRead = quickBookTermsRead;
    
    self.selectedPaymentModel.readTerms = quickBookTermsRead;
}

- (EHIReviewSectionHeaderViewModel *)headerForSection:(EHIReviewSection)section
{
    return [self.headers headerForSection:section];
}

- (BOOL)isOneWayReservation
{
    return self.builder.isOneWayReservation;
}

- (NSAttributedString *)quickBookTerms
{
    NSString *policiesText = EHILocalizedString(@"review_prepay_policies_read", @"I have read the #{policies}", @"");
    NSString *policiesName = EHILocalizedString(@"general_prepay_policies", @"Prepayment Policy Terms & Conditions", @"");
    
    __weak __typeof(self) welf = self;
    NSAttributedString *attributedPoliciesName =
    [NSAttributedString attributedStringWithString:policiesName
                                              font:[UIFont ehi_fontWithStyle:EHIFontStyleRegular size:14.0f]
                                             color:[UIColor ehi_lightGreenColor]
                                        tapHandler:^{
                                            [welf.selectedPaymentModel showTerms];
                                        }];
    
    EHIAttributedStringBuilder *policiesBuilder = EHIAttributedStringBuilder.new
    .text(policiesText).fontStyle(EHIFontStyleRegular, 14.0f).replace(@"#{policies}", attributedPoliciesName);
    
    policiesBuilder.attributes(@{NSBaselineOffsetAttributeName: @1});
    
    return policiesBuilder.string;
}

- (EHIModel *)paymentMethodLocked
{
    BOOL isLocked = self.builder.reservation.blockModifyPickupLocation;
    BOOL isModify = self.isModify;
    
    return isLocked && isModify ? [EHIModel placeholder] : nil;
}

- (EHIReservationPriceButtonSubtitleType)priceSubtitleType
{
    BOOL hasUnpaid = [self unpaidRefundDifference] != nil;
    BOOL isModify  = self.isModify;
    BOOL isPrepay  = self.isPrepay;
    
    return hasUnpaid && isModify && isPrepay ? EHIReservationPriceButtonSubtitleTypeUpdatedTotal : EHIReservationPriceButtonSubtitleTypeTotalCost;
}

- (EHIReservationPriceButtonType)priceType
{
    return self.isSecretRate ? EHIReservationPriceButtonTypeSecretRate : EHIReservationPriceButtonTypePrice;
}

@end
