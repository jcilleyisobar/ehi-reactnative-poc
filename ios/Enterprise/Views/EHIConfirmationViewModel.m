//
//  EHIConfirmationViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 4/21/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIConfirmationViewModel.h"
#import "EHILocationDetailsViewModel.h"
#import "EHIUserManager+DNR.h"
#import "EHIReservationBuilder+Analytics.h"
#import "EHIServices+Reservation.h"
#import "EHIConfirmationHeaders.h"
#import "EHISettings.h"
#import "EHIToastManager.h"
#import "EHIConfirmationInAppReviewViewModel.h"
#import "EHIConfirmationActionsViewModel.h"

@interface EHIConfirmationViewModel ()
@property (strong, nonatomic) id<EHINetworkCancelable> activeRequest;
@property (strong, nonatomic) EHIReservation *reservation;
@property (strong, nonatomic) EHIConfirmationHeaders *sectionHeaders;
@end

@implementation EHIConfirmationViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _title = EHILocalizedString(@"reservation_confirmation_navigation_title", @"Confirmation", @"navigation bar title for reservation confirmation screen");
        _sectionHeaders = [EHIConfirmationHeaders new];
    }
    
    return self;
}

- (EHIReviewSectionHeaderViewModel *)headerForSection:(EHIConfirmationSection)section
{
    return [self.sectionHeaders headerForSection:section];
}

- (void)didBecomeActive
{
    [super didBecomeActive];
    
    [EHIUserManager attemptToShowContinueDnrModalWithHandler:nil];
}

- (void)didResignActive
{
    [super didResignActive];
    
    [self.activeRequest cancel];
    [self setActiveRequest:nil];
}

- (void)updateWithModel:(id)model
{
    [super updateWithModel:model];
    
    [self incrementViewCount];
    
    if([model isKindOfClass:[EHIReservation class]]) {
        [self updateWithReservation:model];
    } else if ([model isKindOfClass:[EHIUserRental class]]) {
        [self updateWithUserRental:model];
    }
}

- (void)updateWithUserRental:(EHIUserRental *)userRental
{
    __block id<EHINetworkCancelable> request;
    request = [[EHIServices sharedInstance] fetchRentalForConfirmation:userRental.confirmationNumber firstName:userRental.firstName lastName:userRental.lastName handler:^(EHIReservation *reservation, EHIServicesError *error) {
        if(request != self.activeRequest) {
            return;
        }
        
        self.activeRequest = nil;
        
        if(!error.hasFailed) {
            [self updateWithReservation:reservation];
            // update analytics data once we get the reservation
            [self updateAnalyticsContext:[EHIAnalytics context] withReservation:reservation];
        }
    }];
    
    self.activeRequest = request;
}

- (void)updateWithReservation:(EHIReservation *)reservation
{
    self.reservation = reservation;
   
    // capture the selected car class locally for ease-of-reference
    EHICarClass *carClass = reservation.selectedCarClass;
    
    // capture ldt data
    self.returnDate      = reservation.returnTime;
    self.pickupDate      = reservation.pickupTime;
   
    // capture car class / pricing info
    EHICarClassPriceSummary *priceSummary = [reservation.selectedCarClass vehicleRateForPrepay:self.reservation.prepaySelected].priceSummary;
    
    self.carClass              = carClass;
    self.priceContext          = priceSummary;
    self.totalPrice            = priceSummary.viewTotal;
    BOOL isSecretRate          = reservation.selectedCarClass.isSecretRateAfterCarSelected;
    BOOL showRentalCostSection = priceSummary != nil;
    
    self.totalPriceViewModel = !showRentalCostSection ? nil : [[EHIReservationRentalPriceTotalViewModel alloc] initWithModel:reservation.selectedCarClass
                                                                                                       prepaySelected:reservation.prepaySelected
                                                                                                           paidAmount:nil
                                                                                                         actualAmount:nil
                                                                                                      showOtherOption:NO
                                                                                                               layout:EHIReservationRentalPriceTotalLayoutReview
                                                                                                         isSecretRate:isSecretRate];

    self.confirmationNumber = reservation.confirmationNumber;
    
    // display discount only if not default
    self.discount = reservation.contractDetails ? reservation : nil;
    
    // if the reservation is a one way, display the pickup and return locations in their own sections
    if(reservation.isOneWay) {
        self.pickupSectionModel = reservation.pickupLocation;
        self.returnSectionModel = reservation.returnLocation;
    }
    // otherwise show the roundtrip location in a single section
    else {
        self.pickupReturnSectionModel = reservation.pickupLocation;
    }
    
    // let unauthed users input prefill information if url is provided
    self.assistanceModel = EHIUser.currentUser == nil && reservation.prefillUrl ? [EHIModel placeholder] : nil;
    
    // display any user entered additional info
    self.additionalInfos = (reservation.additionalInfo ?: @[]).select(^(EHIContractAdditionalInfoValue *info) {
        return info.name != nil && info.value != nil;
    }).sortBy(^(EHIContractAdditionalInfoValue *info) {
        return info.sequence;
    });
    
    [self.additionalInfos.firstObject setShouldShowSectionTitle:YES];
    [self.additionalInfos.lastObject setIsLastInSection:YES];
    
    // show the correct banner (if any) based on res state
    self.bannerModel = [EHIInformationBannerViewModel modelWithType:[self bannerTypeForReservation:reservation]];
    
    // capture driver info
    self.driverInfo = reservation.driverInfo;
    // capture optional flight information
    BOOL isAirport = reservation.pickupLocation.type == EHILocationTypeAirport;
    self.airline = isAirport ? reservation.airline : nil;
    // capture delivery and collection info
    self.deliveryCollectionViewModels = [EHIDeliveryCollectionCellViewModel viewModelsForReservation:reservation];
    // create the policies vm once the res comes back
    self.policiesViewModel = [[EHIReservationPoliciesViewModel alloc] initWithModel:reservation];
    self.policiesViewModel.hideFancyDivider = YES;
    
    // build out the sublist models for the the line items/extras
    self.priceSublistModel =  showRentalCostSection ? [[EHIReservationPriceSublistViewModel alloc] initWithCarClass:reservation.selectedCarClass prepay:reservation.prepaySelected] : nil;

    self.scheduleViewModel = [[EHIReservationScheduleCellViewModel alloc] initWithPickupDate:self.pickupDate returnDate:self.returnDate];
    self.scheduleViewModel.showTopDivider = YES;
    
    // display the payment method section if necessary
    BOOL isCorporate = reservation.contractDetails.contractType == EHIContractTypeCorporate;
    BOOL hasPayments = reservation.reservationPayments.count > 0;
    if(hasPayments) {
        self.paymentMethod = reservation.reservationPayments.firstObject;
    } else if(isCorporate) {
        self.paymentMethod = reservation.billingAccount ?: [EHIUserPaymentMethod emptyPaymentMethod];
    }
    
    self.manageReservationModel = [[EHIConfirmationManageReservationViewModel alloc] initWithModel:self.reservation];
    
    self.defaultActionsModel = [[EHIConfirmationActionsViewModel alloc] initWithModel:self.reservation];

    [self updateExtrasWithCarClass:carClass reservation:reservation];
    
    [self computeAppleStoreReview];
}

//
// Helpers
//

- (void)updateExtrasWithCarClass:(EHICarClass *)carClass reservation:(EHIReservation *)reservation
{
    // group extras by status
    NSDictionary *groupedExtras = ([carClass vehicleRateForPrepay:reservation.prepaySelected].extras.selected ?: @[]).groupBy(^(EHICarClassExtra *extra) {
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
    [self.includedExtras.lastObject setLastInSection:YES];
    
    [self.mandatoryExtras.firstObject setShouldShowSectionTitle:YES];
    [self.mandatoryExtras.lastObject setLastInSection:YES];
    
    if (![self.optionalExtras.firstObject isPlaceholder]) {
        [self.optionalExtras.firstObject setShouldShowSectionTitle:YES];
        [self.optionalExtras.lastObject setLastInSection:YES];
    }
}

- (EHIInformationBannerType)bannerTypeForReservation:(EHIReservation *)reservation
{
    if(reservation.driverInfo.loyaltyType == EHIDriverInfoLoyaltyTypeEmeraldClub) {
        return EHIInformationBannerTypeEmeraldConfirmation;
    }
    
    return EHIInformationBannerTypeNone;
}

- (EHIModel *)termsModel
{
    if(!_termsModel) {
        _termsModel = [EHIModel placeholder];
    }
    
    return _termsModel;
}

- (BOOL)canPresentAppReview
{
    return [EHISettings shouldPresentAppStoreRateView];
}

- (BOOL)allowRateAppPopupWithLastDateShown:(NSDate*)lastDate;
{
    
    return !lastDate || [lastDate ehi_daysUntilDate:[NSDate new]] > EHIDaysPerWeek;
}

- (void)computeAppleStoreReview
{
    NSDate *lastPopupPresentedDay = [EHISettings lastDayAppStoreRatePopupRequested];
    if (![self allowRateAppPopupWithLastDateShown:lastPopupPresentedDay]) {
        return;
    }
    
    BOOL presentRate              = self.canPresentAppReview;
    BOOL isLogged                 = [EHIUser currentUser] != nil;
    BOOL canAppReview             = presentRate && isLogged;
    
    BOOL supportsAppStorePopup = [EHIConfirmationInAppReviewViewModel canShowInAppReview];
    BOOL cameFromReviewScreen  = !(self.reservation.currentFlow == EHIReservationBuilderFlowModify) && self.isFromReviewScreen;
    
    if(canAppReview && cameFromReviewScreen) {
        if (supportsAppStorePopup) {
            [EHIConfirmationInAppReviewViewModel requestInAppReview];
            [EHISettings appStoreRatePopupRequested];
            self.appStoreRateModel = nil;
        } else {
            self.appStoreRateModel =  [EHIConfirmationAppStoreRateViewModel new];
        }
    }
}

# pragma mark - Actions

- (void)showQuickPickup
{
    [EHIAnalytics trackAction:EHIAnalyticsResActionAddQuickPickupDetails handler:nil];

    NSString *title = EHILocalizedString(@"reservation_expedited_rental_alert_title", @"To prefill information and expidite your rental, we need to leave the app to go to your web browser", @"Title for expedited rental alert");
    
    EHIAlertViewBuilder *alert = EHIAlertViewBuilder.new
        .title(title)
        .button(EHILocalizedString(@"alert_open_browser_button", @"Open Browser", @"Title for alert 'open browser' button"))
        .cancelButton(nil);
    
    alert.show(^(NSInteger index, BOOL canceled) {
        if(!canceled) {
            [UIApplication ehi_openURL:[NSURL URLWithString:self.reservation.prefillUrl]];
        }
    });
}

- (void)dismissConfirmation
{
    NAVTransitionBuilder *builder = EHIMainRouter.router.transition;
    
    // if from review, dismissing this should pop to the dashboard
    if(self.isFromReviewScreen) {
        builder.root(EHIScreenDashboard).animated(NO);
    }
    
    builder.dismiss.start(self.dismissHandler);
}

- (void (^)())dismissHandler
{
    if(!self.showJoinScreen) {
        return nil;
    }
    
    return ^{
        EHIMainRouter.router.transition.present(EHIScreenConfirmationJoin).object(self.reservation).handler(^(BOOL wantsJoin){
            [EHISettings didShowJoinModal];
            
            if(wantsJoin) {
                EHIMainRouter.router.transition.push(EHIScreenEnrollmentStepOne).start(nil);
            }
        }).start(nil);
    };
}

- (BOOL)showJoinScreen
{
    return EHIUser.currentUser == nil && self.isFromReviewScreen && EHISettings.shouldShowJoinModal;
}

- (void)selectItemAtIndexPath:(NSIndexPath *)indexPath
{
    switch(indexPath.section) {
        case EHIConfirmationSectionPickup:
        case EHIConfirmationSectionReturn:
        case EHIConfirmationSectionPickupReturn:
            [self showLocationForSection:indexPath.section]; break;
        case EHIConfirmationSectionTermsAndConditions:
            [self showTermsAndConitions]; break;
        default:
            break;
    }
}

//
// Helpers
//

- (void)showLocationForSection:(NSUInteger)section
{
    EHILocation *location;
    
    switch(section) {
        case EHIConfirmationSectionPickup:
            location = self.pickupSectionModel; break;
        case EHIConfirmationSectionReturn:
            location = self.returnSectionModel; break;
        case EHIConfirmationSectionPickupReturn:
            location = self.pickupReturnSectionModel; break;
        default:
            return;
    }
    
    // locations from confirm have wrong dates so mark as empty here
    location = [EHILocation modelWithDictionary:@{
        @key(location.uid) : location.uid,
    }];
    
    EHILocationDetailsViewModel *viewModel = [[EHILocationDetailsViewModel alloc] initWithModel:location];
    viewModel.disablesSelection = YES;
    
    // push the location details screen with the correct location model
    self.router.transition
        .push(EHIScreenLocationDetails).object(viewModel).start(nil);
}

- (void)showTermsAndConitions
{
    self.router.transition.present(EHIScreenTermsAndConditions).object(self.reservation).start(nil);
}

# pragma mark - Accessors

- (void)setIsFromReviewScreen:(BOOL)isFromReviewScreen
{
    _isFromReviewScreen = isFromReviewScreen;
    
    [self computeAppleStoreReview];
}

- (BOOL)isLoading
{
    return self.activeRequest != nil;
}

- (void)incrementViewCount
{
    [EHISettings incrementConfirmationViewCount];
}

- (void)presentedAppleStoreRate
{
    self.appStoreRateModel = nil;
    
    [EHISettings presentedAppStoreRate];
}

- (void)promptAppleStoreRate
{
    [EHIToastManager showMessage:EHILocalizedString(@"confirmation_rating_thanks_message", @"Thank you for your feedback", @"")];
    
    dispatch_after_seconds(1.0, ^{
        [UIApplication ehi_promptUrl:[EHISettings environment].iTunesLink];
        [self presentedAppleStoreRate];
    });
}

# pragma mark - Analytics

- (void)updateAnalyticsContext:(EHIAnalyticsContext *)context
{
    [super updateAnalyticsContext:context];
   
    // encode our current reservation, if possible
    [self updateAnalyticsContext:context withReservation:self.reservation];
}

- (void)updateAnalyticsContext:(EHIAnalyticsContext *)context withReservation:(EHIReservation *)reservation
{
    // manually encode the reservation on the confirmation screen, since it could be presented
    // outside the res flow
    [context encodeReservation:reservation];
}

@end
