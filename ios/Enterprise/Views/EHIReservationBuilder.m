//
//  EHIReservationBuilder.m
//  Enterprise
//
//  Created by mplace on 3/9/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservationBuilder_Private.h"
#import "EHIInfoModalViewModel.h"

@implementation EHIReservationBuilder

+ (instancetype)sharedInstance
{
    static dispatch_once_t once;
    static EHIReservationBuilder *sharedInstance;
    
    dispatch_once(&once, ^{
        sharedInstance = [EHIReservationBuilder new];
    });
    
    return sharedInstance;
}

- (instancetype)init
{
    if(self = [super init]) {
        _listeners = [NSHashTable weakObjectsHashTable];
    }
    
    return self;
}

# pragma mark - EHIViewModel

- (void)didBecomeActive
{
    [super didBecomeActive];
  
    // begin listening to user events so that we can capture the contract
    [[EHIUserManager sharedInstance] addListener:self];
    
    // if we became active with a pickup location, then save it as recent activity
    if(self.pickupLocation && self.currentFlow == EHIReservationBuilderFlowDefault && [EHISettings sharedInstance].saveSearchHistory) {
        [EHIDataStore save:self.pickupLocation handler:nil];
    }
}

- (void)didResignActive
{
    [super didResignActive];

    // clear out existing data
    [self resetData];
    
    // stop listening for user events
    [[EHIUserManager sharedInstance] removeListener:self];
}

# pragma mark - Reservation Binding

- (void)modifyReservation:(EHIReservation *)reservation handler:(void (^)(EHIServicesError *))handler
{
    __block id<EHINetworkCancelable> request;
    request = [[EHIServices sharedInstance] fetchRentalForConfirmation:reservation.confirmationNumber firstName:reservation.driverInfo.firstName lastName:reservation.driverInfo.lastName handler:^(EHIReservation *modifiableReservation, EHIServicesError *error) {
        // block any canceled requests
        if(self.activeRequest != request) {
            return;
        }
        
        if(!error.hasFailed) {
            
            [[EHIServices sharedInstance] updateAvailableCarClasses:modifiableReservation handler:^(EHIReservation *newReservation, EHIServicesError *error) {
                
                if (!error.hasFailed) {
                    // clear out any old data
                    [self resetData];
                    
                    // update to the modify flow
                    self.currentFlow = EHIReservationBuilderFlowModify;
                    
                    // save old reservation and start modify with new copy
                    self.modifiedReservation = reservation;
                    self.reservation         = newReservation;
                    
                    // update payment method flag to avoid wierd states
                    [self.reservation updateSelectedPaymentOption];
                    
                    // bind the modify reservation's data
                    [self updateWithReservation:self.reservation];
                    
                    // notify the caller that we are done
                    ehi_call(handler)(error);
                    
                    // always start modify at review
                    self.router.transition
                    .push(EHIScreenReservationReview).start(nil);
                }
                else {
                    ehi_call(handler)(error);
                }
            }];
        }
        else {
            ehi_call(handler)(error);
        }
        
    }];
    
    // update the active request
    self.activeRequest = request;
}

- (void)restartReservation:(EHIReservation *)reservation
{
    // update to the restart flow
    self.currentFlow = EHIReservationBuilderFlowRestart;
    
    // bind this reservations data
    [self updateWithReservation:reservation];
   
    // and present the reservation modal
    self.router.transition
        .present(EHIScreenReservation).start(nil);
}

- (void)setReservation:(EHIReservation *)reservation
{
    _reservation = reservation;
    
    // the corporate code on our reservation should override anything else
    self.discount = reservation.contractDetails;
    
    // determine default state for redemption points visibility while ignoring setter
    _hidePoints = ![self defaultPointVisibility];
    
    // default state for prepay
    self.creditCardAdded = NO;
    
    // synchronize current flows
    reservation.currentFlow = self.currentFlow;
}

- (void)updateWithReservation:(EHIReservation *)reservation
{
    // update our internal state with the reservation, but don't store it
    self.pickupLocation = reservation.pickupLocation;
    
    // only set a return location if it's not the same as our pickup location
    if(![reservation.returnLocation isEqual:reservation.pickupLocation]) {
        self.returnLocation = reservation.returnLocation;
    }
    
    self.pickupDate   = reservation.pickupTime;
    self.returnDate   = reservation.returnTime;
    
    self.pickupTime   = [reservation.pickupTime ehi_time];
    self.returnTime   = [reservation.returnTime ehi_time];

    self.discount     = reservation.contractDetails;
    self.discountCode = reservation.contractDetails.uid;
    
    // save grafted on properties
    self.driverInfo     = reservation.driverInfo;
    self.airline        = reservation.airline;
    self.additionalInfo = [reservation.additionalInfo mutableCopy];
}

- (void)resetData
{
    self.currentFlow = EHIReservationBuilderFlowDefault;
    self.reservation = nil;
    self.modifiedReservation = nil;
    self.isEmeraldReservation = NO;
    self.reservationIsModified = NO;
    
    // locations
    self.pickupLocation = nil;
    self.returnLocation = nil;
    
    // date-time
    self.pickupDate = nil;
    self.pickupTime = nil;
    self.returnDate = nil;
    self.returnTime = nil;
    
    // user info
    self.discount       = nil;
    self.discountCode   = nil;
    self.paymentMethod  = nil;
    self.additionalInfo = nil;
    self.driverInfo     = nil;
    self.airline        = nil;
    
    // pin auth
    self.pinAuth        = nil;
    
    // travel purpose
    self.travelPurposeSelectedPreRates = NO;
    self.travelPurpose = EHIReservationTravelPurposeNone;
    
    // cancel any active request
    self.activeRequest = nil;
}

# pragma mark - Selection

- (void)selectLocation:(EHILocation *)location
{
    // show an alert if this is an off-brand location
    if(location && !location.isOnBrand) {
        [self showExternalRentalAlertForLocation:location];
        return;
    }

    // select the location based on our current search type
    if(self.currentSearchType == EHILocationsSearchTypePickup) {
        [self selectPickupLocation:location];
    } else {
        [self selectReturnLocation:location];
    }
}

- (void)selectPickupLocation:(EHILocation *)location
{
    self.pickupLocation = location;
    
    // if our pickup location matches our return location, nil out the return location
    if([self.returnLocation.uid isEqualToString:location.uid]) {
        self.returnLocation = nil;
    }
   
    // update the analytics context locations
    [self synchronizeLocationsOnContext:nil];
    
    // show the reservation modal if we are not active
    if(location && !self.isActive) {
        self.router.transition
            .present(EHIScreenReservation).start(nil);
    }
    // otherwise pop back to the itinerary view (root)
    else {
        self.router.transition
            .root(EHIScreenReservationItinerary).start(nil);
    }
}

- (void)selectReturnLocation:(EHILocation *)location
{
    self.returnLocation = location;
   
    // update the analytics context locations
    [self synchronizeLocationsOnContext:nil];
    [EHIAnalytics trackAction:EHIAnalyticsLocActionLocation handler:nil];
    
    // and if we have a actual location, let's try and pop back to the itinerary view (root)
    if(location) {
        self.router.transition
            .root(EHIScreenReservationItinerary).start(nil);
    }
}

//
// Helpers
//

- (void)showExternalRentalAlertForLocation:(EHILocation *)location
{
    NSString *title = EHILocalizedString(@"reservation_offbrand_alert_title", @"To view #{brand} rentals we need to leave the app to go to your web browser", @"Title for offbrand reservation alert");
    title = [title ehi_applyReplacementMap:@{
        @"brand" : location.brandTitle ?: @"",
    }];
    
    EHIAlertViewBuilder *alert = EHIAlertViewBuilder.new
        .title(title)
        .button(EHILocalizedString(@"alert_open_browser_button", @"Open Browser", @"Title for alert 'open browser' button"))
        .cancelButton(nil);
    
    alert.show(^(NSInteger index, BOOL canceled) {
        if(!canceled) {
            [UIApplication ehi_openURL:[NSURL URLWithString:location.brandUrl]];
        }
    });
}

- (EHILocationsSearchType)currentSearchType
{
    if(!self.isActive) {
        return EHILocationsSearchTypePickup;
    } else {
        return self.searchTypeOverride;
    }
}

# pragma mark - Requests

- (void)setActiveRequest:(id<EHINetworkCancelable>)activeInitiateRequest
{
    // cancel any previous request
    [_activeRequest cancel];
    _activeRequest = activeInitiateRequest;
}

# pragma mark - Cancelation

- (void)cancelReservation
{
    [EHIAnalytics trackState:^(EHIAnalyticsContext *context) {
        context.state = EHIAnalyticsResStateAbandon;
    }];
    
    NSString *alertMessage = self.isModifyingReservation
        ? EHILocalizedString(@"reservation_cancel_confirm_message", @"Would you like to discard your updates?", @"")
        : EHILocalizedString(@"reservation_cancel_discard_confirm_message", @"Discard information and exit the reservation?", @"");
    
    NSString *alertButtonString = self.isModifyingReservation
        ? EHILocalizedString(@"reservation_cancel_confirm_button", @"Discard Update", @"")
        : EHILocalizedString(@"reservation_cancel_discard_confirm_button", @"Discard", @"");
    
    NSString *cancelButtonString = self.isModifyingReservation
        ? EHILocalizedString(@"reservation_cancel_return_button", @"Return to Modify", @"")
        : EHILocalizedString(@"reservation_cancel_discard_return_button", @"Return", @"");
    
    EHIAlertViewBuilder *alertView = EHIAlertViewBuilder.new
        .message(alertMessage)
        .cancelButton(cancelButtonString)
        .button(alertButtonString);
    
    alertView.show(^(NSInteger index, BOOL canceled) {
        if(!canceled) {
            [self didCancelReservation];
        }
    });
}

//
// Helpers
//

- (void)didCancelReservation
{
    // save this reservation as abandoned
    if(self.reservation.uid && [EHISettings sharedInstance].saveSearchHistory) {
        [[EHIHistoryManager sharedInstance] saveAbandonedRental:self.targetSavingReservation];
    }

    [EHIAnalytics trackAction:EHIAnalyticsResActionConfirm handler:^(EHIAnalyticsContext *context) {
        context.state = EHIAnalyticsResStateAbandon;
    }];
    
    if(self.isModifyingReservation) {
        self.router.transition
            .resolve(EHIScreenConfirmation)
            .object(self.reservation)
            .start(nil);
    } else {
        // dismiss the reservation modal
        EHIMainRouter.router.transition
            .root(EHIScreenDashboard)
            .animated(NO)
            .dismiss
            .start(nil);
    }
}

- (EHIReservation *)targetSavingReservation
{
    // save locations object from Solr
    EHIReservation *reservation = self.reservation.copy;
    reservation.pickupLocation  = self.pickupLocation;
    reservation.returnLocation  = self.returnLocation;
    
    return reservation;
}

# pragma mark - Driver Info

- (void)setDriverInfo:(EHIDriverInfo *)driverInfo
{
    _driverInfo = driverInfo;
 
    // manage caching if not logged in
    if(driverInfo && ![EHIUser currentUser]) {
        // cache if requested
        if(driverInfo.shouldSerialize) {
            [EHIDataStore save:driverInfo.deepCopy handler:nil];
        }
        // otherwise, delete
        else {
            // skip if in modify, user can't change the flag in this flow
            if(!self.isModifyingReservation) {
                [EHIDataStore purge:[EHIDriverInfo class] handler:nil];
            }
        }
    }
}

- (void)loadDriverInfo
{
    EHIUser *user = [EHIUser currentUser];
    
    // if we're logged in
    if(user) {
        // purge any stored information
        [EHIDataStore purge:[EHIDriverInfo class] handler:nil];
        // and create whatever we can from the user
        self.driverInfo = [self driverInfoFromUser:user];
    }
    else {
        [EHIDataStore first:[EHIDriverInfo class] handler:^(EHIDriverInfo *driverInfo) {
            self.driverInfo = driverInfo ?: [self driverInfoFromUser:user];
        }];
    }
}

- (EHIDriverInfo *)driverInfoFromUser:(EHIUser *)user
{
    EHIUserProfiles *profiles = user.profiles;
    
    // create basic info
    EHIDriverInfo *driverInfo = [EHIDriverInfo new];
    driverInfo.firstName   = profiles.basic.firstName;
    driverInfo.lastName    = profiles.basic.lastName;
    driverInfo.email       = user.contact.email;
    driverInfo.maskedEmail = user.contact.maskedEmail;
    
    // set the phone number if it exists
    driverInfo.phone = [user.contact.phones firstObject];
    
    // use profile setting or default based on locale
    driverInfo.wantsEmailNotifications = user == nil && ![NSLocale ehi_shouldCheckEmailNotificationsByDefault] ? EHIOptionalBooleanNull : user.preference.email.specialOffers;
    
    // caching based on settings
    driverInfo.shouldSerialize = [[EHISettings sharedInstance] autoSaveUserInfo];
    
    return driverInfo;
}

# pragma mark - Additional Information

- (NSMutableArray<EHIContractAdditionalInfoValue> *)additionalInfo
{
    if(!_additionalInfo) {
        _additionalInfo = (NSMutableArray<EHIContractAdditionalInfoValue> *)[NSMutableArray new];
    }
    
    return _additionalInfo;
}

- (EHIContractAdditionalInfoValue *)additionalInfoForKey:(NSString *)key
{
    return self.additionalInfo.find(^(EHIContractAdditionalInfoValue *info) {
        return [info.uid isEqualToString:key];
    });
}

- (void)setAdditionalInfo:(id)info forKey:(NSString *)key
{
    EHIContractAdditionalInfoValue *existingInfo = [self additionalInfoForKey:key];
    
    // update
    if(info) {
        // create model if needed
        if(!existingInfo) {
            existingInfo = [EHIContractAdditionalInfoValue modelWithDictionary:@{
                @key(existingInfo.uid) : key
            }];
            
            [self.additionalInfo addObject:existingInfo];
        }

        existingInfo.value = info;
    }
    // delete
    else if(existingInfo) {
        [self.additionalInfo removeObject:existingInfo];
    }
}

- (void)resetPreRateData
{
    self.travelPurpose = EHIReservationTravelPurposeNone;
    self.pinAuth       = nil;
    
    [self resetAdditionalData];
}

-(void)resetAdditionalData
{
    self.additionalInfo = nil;
}

-(EHIPrice *)originalPricePrepaySelected:(BOOL)prepay
{
    return [self.modifiedReservation.selectedCarClass priceContextForPrepay:prepay].viewPrice;
}

- (EHILocation *)modifiedReservationPickupLocation
{
    return self.modifiedReservation.pickupLocation;
}

# pragma mark - Edit Reservation

- (void)editInfoForReservationStep:(EHIReservationStep)step
{
    // determine the correct screen for the reservation step
    NSString *screen = [self screenForReservationStep:step];
    // begin navigation to that screen
    [self showEditReservationAlertForScreen:screen];
}

- (NSString *)screenForReservationStep:(EHIReservationStep)step
{
    switch (step) {
        case EHIReservationStepLocation:
        case EHIReservationStepItinerary:
            return EHIScreenReservationItinerary;
        case EHIReservationStepClassSelect:
            return EHIScreenReservationClassSelect;
        case EHIReservationStepExtras:
            return EHIScreenReservationExtras;
        case EHIReservationStepReview:
            return EHIScreenReservationReview;
        case EHIReservationStepDriverInfo:
            return EHIScreenReservationDriverInfo;
        default:
            return nil;
    }
}

- (void)showEditReservationAlertForScreen:(NSString *)screen
{
    // just continue to edit when in modify flow
    if(self.isModifyingReservation) {
        self.router.transition
            .push(screen).start(nil);

        return;
    }
    
    EHIAlertViewBuilder *alert = EHIAlertViewBuilder.new
        .title(EHILocalizedString(@"reservation_edit_confirmation_alert_title", @"Are you sure? Editing will restart the reservation process from the point of the edit.", @"Title for the edit reservation confirmation alert"))
        .button(EHILocalizedString(@"alert_continue_button_title", @"Continue", @"Title for alert 'Continue' button"))
        .cancelButton(nil);
    
    // prompt the user to confirm the navigation to the destination screen
    alert.show(^(NSInteger index, BOOL canceled) {
        // track the user's response
        NSString *action = canceled ? EHIAnalyticsResActionChangeCancel : EHIAnalyticsResActionChangeAccept;
        [EHIAnalytics trackAction:action handler:nil];
        
        if(!canceled) {
            self.paymentMethod = nil;
            self.router.transition
                .resolve(screen).start(nil);
        }
    });
}

# pragma mark - Reservation Construction

- (void)setPickupDate:(NSDate *)pickupDate
{
    _pickupDate = pickupDate;
   
    if(pickupDate && self.currentFlow != EHIReservationBuilderFlowLocationSearch) {
        self.currentSchedulingStep = EHIReservationSchedulingStepReturnDate;
    }
}

- (void)setReturnDate:(NSDate *)returnDate
{
    _returnDate = returnDate;
    
    if(returnDate && self.currentFlow != EHIReservationBuilderFlowLocationSearch) {
        self.currentSchedulingStep = self.pickupTime == nil
            ? EHIReservationSchedulingStepPickupTime
            : EHIReservationSchedulingStepReturnTime;
    }
}

- (void)setPickupTime:(NSDate *)pickupTime
{
    _pickupTime = pickupTime;
    
    if(pickupTime) {
        // if we already have a return time selected, this must be an edit and we are done
        if(self.returnTime) {
            self.currentSchedulingStep = EHIReservationSchedulingStepStepComplete;
        }
        // otherwise progress naturally to the return time selection step
        else {
            self.currentSchedulingStep = EHIReservationSchedulingStepReturnTime;
        }
    }
}

- (void)setReturnTime:(NSDate *)returnTime
{
    _returnTime = returnTime;
    
    if(returnTime) {
        self.currentSchedulingStep = EHIReservationSchedulingStepStepComplete;
    }
}

- (void)setPaymentMethod:(EHIUserPaymentMethod *)paymentMethod
{
    _paymentMethod   = paymentMethod;
    _creditCardAdded = _paymentMethod.paymentType == EHIUserPaymentTypeCard;
}

# pragma mark - Scheduling Step

- (void)transitionBackToSchedulingStep:(EHIReservationSchedulingStep)step
{
    // if we haven't reached this step yet, then bail
    if(![self canTransitionToSchedulingStep:step]) {
        return;
    }
   
    // otherwise, update the step and transition
    self.currentSchedulingStep = step;
    self.router.transition
        .resolve([self screenForSchedulingStep:step]).start(nil);
}

- (void)setCurrentSchedulingStep:(EHIReservationSchedulingStep)step
{
    // if the step is farther along than we have data for, don't switch
    if(![self canTransitionToSchedulingStep:step]) {
        return;
    }
   
    _currentSchedulingStep = step;

    // cascade through and nil out dates if those steps are selected, as they need to be
    // chosen as a pair
    switch(step) {
        case EHIReservationSchedulingStepPickupDate:
            self.pickupDate = nil;
        case EHIReservationSchedulingStepReturnDate:
            self.returnDate = nil;
        default: break;
    }
}

- (void)showToastForCurrentSchedulingStep
{
    [EHIToastManager showMessage:self.toastText];
}

- (BOOL)canTransitionToSchedulingStep:(EHIReservationSchedulingStep)step
{
    return step <= [self computedSchedulingStep];
}

//
// Helpers
//

- (NSString *)screenForSchedulingStep:(EHIReservationSchedulingStep)step
{
    switch(step) {
        case EHIReservationSchedulingStepPickupDate:
        case EHIReservationSchedulingStepReturnDate:
            return EHIScreenReservationCalendar;
        case EHIReservationSchedulingStepPickupTime:
        case EHIReservationSchedulingStepReturnTime:
            return EHIScreenReservationTimeSelect;
        default: return nil;
    }
}

- (EHIReservationSchedulingStep)computedSchedulingStep
{
    if(!self.pickupDate) {
        return EHIReservationSchedulingStepPickupDate;
    } else if(!self.pickupTime) {
        return EHIReservationSchedulingStepPickupTime;
    } else if(!self.returnDate) {
        return EHIReservationSchedulingStepReturnDate;
    } else if(!self.returnTime) {
        return EHIReservationSchedulingStepReturnTime;
    } else {
        return EHIReservationSchedulingStepStepComplete;
    }
}

- (NSString *)toastText
{
    switch(self.currentSchedulingStep) {
        case EHIReservationSchedulingStepPickupDate:
            return EHILocalizedString(@"reservation_pickup_date_toast", @"Please select a pick-up date below", @"Toast message when user taps on pickup date");
        case EHIReservationSchedulingStepReturnDate:
            return EHILocalizedString(@"reservation_return_date_toast", @"Please select a return date below", @"Toast message when user taps on return date");
        case EHIReservationSchedulingStepPickupTime:
            return EHILocalizedString(@"reservation_pickup_time_toast", @"Please select a pick-up time below", @"Toast message when the user taps on pickup time");
        case EHIReservationSchedulingStepReturnTime:
            return EHILocalizedString(@"reservation_return_time_toast", @"Please select a return time below", @"Toast message when the user taps on return time");
        default: return nil;
    }
}

# pragma mark - On Request

- (void)promptOnRequestSelectionWithHandler:(void (^)(BOOL shouldContinue))handler
{
    EHIInfoModalViewModel *model = [EHIInfoModalViewModel new];
    model.details = EHILocalizedString(@"on_request_status_message_dialog_title", @"One of our customer service reps will call you", @"");
    model.firstButtonTitle  = EHILocalizedString(@"info_modal_continue_button", @"CONTINUE", @"");
    model.secondButtonTitle = EHILocalizedString(@"info_modal_return_button", @"RETURN", @"");
    
    __weak typeof(model) wodel = model;
    [model present:^BOOL(NSInteger index, BOOL canceled) {
        
        [wodel dismissWithCompletion:^{
            ehi_call(handler)(index == 0);
        }];
        
        return NO;
    }];
}

# pragma mark - Constructing Reservation Attributes

- (NSDate *)aggregatePickupDate
{
    return [NSDate ehi_dateFromDate:self.pickupDate timeDate:self.pickupTime];
}

- (NSDate *)aggregateReturnDate
{
    return [NSDate ehi_dateFromDate:self.returnDate timeDate:self.returnTime];
}

- (BOOL)allowsOneWayReservation
{
    // modify doesn't return the flag, so assume we can and let services throw an error if needed
    if(self.isModifyingReservation) {
        return YES;
    }
    
    return self.pickupLocation.allowsOneWay;
}

- (BOOL)canInitiateReservation
{
#if EHIReservationMock
    return YES;
#else
    return self.pickupLocation
        && self.pickupDate && self.returnDate
        && self.pickupDate && self.returnTime;
#endif
}

- (BOOL)canModifyLocation
{
    if(self.isModifyingReservation) {
        return !self.reservation.blockModifyPickupLocation;
    }
    
    return YES;
}

- (BOOL)isOneWayReservation
{
    return self.returnLocation != nil;
}

- (BOOL)isPickingOneWayReservation
{
    return self.isOneWayReservation ?: self.currentSearchType == EHILocationsSearchTypeReturn;
}

- (BOOL)isModifyingReservation
{
    return self.currentFlow == EHIReservationBuilderFlowModify;
}

- (void)setCurrentFlow:(EHIReservationBuilderFlow)currentFlow
{
    _currentFlow = currentFlow;
    
    if(_currentFlow == EHIReservationBuilderFlowLocationSearch) {
        self.currentSchedulingStep = [self computedSchedulingStep];
    }
    
    self.reservation.currentFlow = currentFlow;
}

# pragma mark - Active Reservation Attributes

- (NSArray *)carClasses
{
    return self.reservation.carClasses ?: @[];
}

- (NSArray *)carClassesFilters
{
    return self.reservation.carClassesFilters ?: @[];
}

- (EHICarClass *)selectedCarClass
{
    return self.reservation.selectedCarClass;
}

- (NSArray *)selectedExtras
{
    return [self.reservation.selectedCarClass vehicleRateForPrepay:self.reservation.prepaySelected].extras.selected ?: @[];
}

- (NSArray *)selectedLineItems
{
    return [self.reservation.selectedCarClass vehicleRateForPrepay:self.reservation.prepaySelected].priceSummary.lineItems ?: @[];
}

- (id<EHIPriceContext>)totalPrice
{
    return [self.reservation.selectedCarClass vehicleRateForPrepay:self.reservation.prepaySelected].priceSummary;
}

- (NSInteger)maxRedemptionDays
{
    return self.selectedCarClass.maxRedemptionDays;
}

- (BOOL)allowsVehicleUpgrade
{
    // hide upgrades in modify when a CID is attached
    // maybe backend should add this logic when they set `upgrade_vehicle_possible` instead in the future?!
    BOOL hideUpgradesInModify = self.isModifyingReservation && self.reservation.discountCode.length > 0;
    return self.pointsUsed == 0 && self.reservation.allowsVehicleUpgrade && !hideUpgradesInModify;
}

- (BOOL)hasUpgradedVehicles
{
    return self.reservation.upgradeCarClassDetails.count != 0;
}

- (BOOL)isEuropeanUnion
{
    return self.reservation.isEuropeanUnion;
}

- (BOOL)reservationWasPrepay
{
    return (self.modifiedReservation?: self.reservation).prepaySelected;
}

# pragma mark - Redemption

- (BOOL)hideRedemption
{
    return [EHIUser currentUser] == nil
        || [EHIUserManager sharedInstance].isEmeraldUser
        || self.reservation.selectedPaymentOption == EHIReservationPaymentOptionPayNow
        || self.reservation.hasToAssociate;
}

- (NSInteger)daysRedeemed
{
    return self.reservation.selectedCarClass.daysToRedeem;
}

- (NSInteger)pointsUsed
{
    return self.reservation.selectedCarClass.pointsUsed;
}

- (BOOL)promptsMultiTerminal
{
    return self.reservation.pickupLocation.promptsForFlightInfo;
}

- (void)setHidePoints:(BOOL)hidePoints
{
    if (!self.isRedemptionAllowed) {
        [self showRedemptionNotAllowedModalWithTitle:EHILocalizedString(@"currently_dont_allow_points_unsupported", @"Points are only applicable at participating locations. Points cannot be redeemed at certain locations.", @"")];
        return;
    }
    
    // save setting
    [EHISettings sharedInstance].redemptionHidePoints = hidePoints;
    
    _hidePoints = hidePoints;
}

- (void)showRedemptionNotAllowedModalWithTitle:(NSString *)title
{
    EHIInfoModalViewModel *model = [EHIInfoModalViewModel new];
    model.title = title;
    model.hidesCloseButton = YES;
    
    [model present:nil];
}

- (BOOL)defaultPointVisibility
{
    if(self.hideRedemption || !self.isRedemptionAllowed || self.hasNoRedeemableCar) {
        return NO;
    }
    
    // otherwise, just use the internal setting
    return ![EHISettings sharedInstance].redemptionHidePoints;
}

//
// Helpers
//

- (BOOL)isRedemptionAllowed
{
    return (self.carClasses ?: @[]).any(^(EHICarClass *carClass) {
        return carClass.isRedemptionAllowed;
    });
}

- (BOOL)hasNoRedeemableCar
{
    return (self.carClasses ?: @[]).all(^(EHICarClass *carClass) {
        return carClass.maxRedemptionDays == 0;
    });
}

# pragma mark - EHIUserManager

- (void)manager:(EHIUserManager *)manager didChangeAuthenticationForUser:(EHIUser *)user
{
    // if we don't have a discount yet, then use whatever the default is
    if(!self.discount) {
        self.discount = [self defaultDiscount];
    }
    self.isEmeraldReservation = manager.isEmeraldUser;
}

- (BOOL)isEmeraldReservation
{
    //we are caching isEmeraldReservation on the res builder because the user manager auto-logouts EC users
    //we also want to check our reservation's driverInfo as we also get this information back from the reservation object
    return _isEmeraldReservation || self.reservation.driverInfo.loyaltyType == EHIDriverInfoLoyaltyTypeEmeraldClub;
}

- (EHIContractDetails *)defaultDiscount
{
    return [EHIUser currentUser].corporateContract;
}

# pragma mark - MTRReactive

+ (NSArray *)nonreactiveProperties:(EHIReservationBuilder *)builder
{
    return @[
        @key(builder.isReady),
        @key(builder.currentFlow),
        @key(builder.modifiedReservation),
    ];
}

@end

