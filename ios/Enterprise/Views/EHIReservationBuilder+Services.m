//
//  EHIReservationBuilder+Services.m
//  Enterprise
//
//  Created by Ty Cobb on 6/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservationBuilder_Private.h"
#import "EHIServices+User.h"
#import "EHIWebBrowserViewModel.h"
#import "EHIServices+Payment.h"
#import "EHIPaymentGateway.h"
#import "EHICreditCardPanguiResponse.h"

@implementation EHIReservationBuilder (Services)

# pragma mark - Initiate

- (void)initiateReservationWithHandler:(void (^)(EHIServicesError *))completion
{
#if EHIReservationMock
    // use mock data to create the reservation
    EHIReservation *reservation = [self prepareMockReservationForInitiateRequest];
#else
    // use the data that we gathered to create the reservation
    EHIReservation *reservation = [self prepareReservationForInitiateRequest];
#endif
    reservation.additionalInfo = self.additionalInfo;
    
    // load whatever driver info we can muster, if needed
    if(!self.isModifyingReservation) {
        [self loadDriverInfo];
    }
    
    __block id<EHINetworkCancelable> request;
    __weak __typeof(self) welf = self;
    request = [[EHIServices sharedInstance] initiateReservation:reservation handler:^(EHIReservation *reservation, EHIServicesError *error) {
        // block any canceled requests
        if(welf.activeRequest != request) {
            return;
        }
        
        welf.activeRequest = nil;
        
        // update our internal reservation with the service response
        if(!error.hasFailed) {
            welf.reservation = reservation;
        }
        
        ehi_call(completion)(error);
    }];
    
    // update the active request
    self.activeRequest = request;
}

//
// Helpers
//

- (EHIReservation *)prepareReservationForInitiateRequest
{
    // format attributes according to service specification
    NSDictionary *attributes = @{
        @key(self.reservation.pickupLocationId) : self.pickupLocation.uid ?: @"",
        // if we dont have a return location specified, assume pickup as the return location
        @key(self.reservation.returnLocationId) : self.returnLocation.uid ?: self.pickupLocation.uid ?: @"",
        // format dates according to service specification
        @key(self.reservation.pickupTime)       : self.aggregatePickupDate.ehi_dateTimeString ?: @"",
        @key(self.reservation.returnTime)       : self.aggregateReturnDate.ehi_dateTimeString ?: @"",
        @key(self.reservation.renterAge)        : @(self.renterAge.value),
    };
    
    NSString *fallbackDiscount = [EHIUser currentUser] != nil ? self.discount.uid : nil;
    
    // always prioritize manually input discount code
    NSString *discountCode = self.discountCode ?: fallbackDiscount;
    
    // apply optional parameters
    if(discountCode) {
        attributes = [attributes ehi_appendKey:@key(self.reservation.discountCode) value:discountCode.uppercaseString];
    }
    
    if(self.travelPurpose != EHIReservationTravelPurposeNone) {
        attributes = [attributes ehi_appendKey:@key(self.reservation.travelPurpose) value:@(self.travelPurpose)];
    }
    
    if(self.pinAuth.length > 0) {
        attributes = [attributes ehi_appendKey:@key(self.reservation.pinAuth) value:self.pinAuth];
    }
    
    // and generate a reservation from the attribtues
    return [EHIReservation modelWithDictionary:[attributes copy]];
}

- (EHIReservation *)prepareMockReservationForInitiateRequest
{
    // otherwise use dummy data for now
    EHIReservation *reservation = [EHIReservation modelWithDictionary:@{
        @key(self.reservation.pickupLocationId) : @"1018965",
        @key(self.reservation.returnLocationId) : @"1018965",
        @key(self.reservation.pickupTime)       : @"2015-04-28T10:25",
        @key(self.reservation.returnTime)       : @"2015-04-29T12:25",
        @key(self.reservation.renterAge)        : @(25),
    }];
    
    // give the builder a pickup date/time and return date/time
    self.pickupDate = reservation.pickupTime;
    self.returnDate = reservation.returnTime;
    self.pickupTime = reservation.pickupTime;
    self.returnTime = reservation.returnTime;
    
    return reservation;
}

# pragma mark - Prepay

- (void)submitCreditCard:(EHICreditCard *)creditCard handler:(void (^)(id response, EHIServicesError *))handler
{
    __weak __typeof(self) welf = self;
    EHIPaymentGateway *manager = [EHIPaymentGateway new];
    self.activeRequest = [manager submitCreditCard:creditCard token:self.reservation.uid handler:^(id response, EHIServicesError *error) {
        welf.activeRequest = nil;
        [welf promptCardSubmissionError:error];
        ehi_call(handler)(response, error);
    }];
}

- (void)check3dsDataWithHandler:(void (^)(BOOL, NSString *, EHIServicesError *))handler
{
    __block id<EHINetworkCancelable> request;
    __weak __typeof(self) welf = self;
    request = [[EHIServices sharedInstance] check3dsDataForReservation:self.reservation handler:^(EHI3DSData *ehi3dsData, EHIServicesError *error) {
        // block any canceled requests
        if(welf.activeRequest != request) {
            return;
        }
        
        welf.activeRequest = nil;
        
        if(!error.hasFailed && ehi3dsData.isSupported) {
            EHIWebBrowserViewModel *viewModel = [[EHIWebBrowserViewModel new] initWithUrl:ehi3dsData.url body:ehi3dsData.body];
            welf.router.transition.present(EHIScreenWebBrowser).object(viewModel).handler(^(NSString *validationData) {
                ehi_call(handler)(YES, validationData, validationData ? nil : [EHIServicesError servicesErrorFailure]);
            }).start(nil);
        } else {
            ehi_call(handler)(NO, nil, error);
        }
    }];
    
    self.activeRequest = request;
}

//
// Helpers
//

- (void)promptCardSubmissionError:(EHIServicesError *)error
{
    NSString *message = nil;
    
    if(error.code == EHINetworkStatusCodeBadRequest) {
        message = EHILocalizedString(@"alert_service_farepayment_bad_input", @"The credit card information you entered is invalid.", @"");
    } else if(error.code == EHINetworkStatusCodeNotFound) {
        message = EHILocalizedString(@"alert_service_farepayment_bad_submission_key", @"Your submission info is invalid.", @"");
    } else if(error.code == EHINetworkStatusCodeConflict) {
        message = EHILocalizedString(@"alert_service_farepayment_duplicate_submission", @"You have already submitted a valid card.", @"");
    } else if(error.code == EHINetworkStatusCodeInternalServerError) {
        message = EHILocalizedString(@"alert_service_farepayment_unexpected_error", @"There was an unexpected error with your payment. Please try again.", @"");
    } else if(error.code == NSURLErrorCancelled) {
        message = EHILocalizedString(@"alert_service_farepayment_unexpected_error", @"There was an unexpected error with your payment. Please try again.", @"");
    }
    
    // pop our own custom error for farepayment
    if(message) {
        [error consume];
        
        EHIAlertViewBuilder.new
            .title(EHILocalizedString(@"alert_service_error_title", @"Error", @"Title for service error alert"))
            .message(message)
            .button(EHILocalizedString(@"alert_service_error_okay", @"Okay", @"Title for the service error alert confirmation button"))
            .show(nil);
    }
}

# pragma mark - Modify

- (void)modifyLocationDateTimeWithHandler:(EHIReservationHandler)handler
{
    [self.reservation updateWithDictionary:@{
        @key(self.reservation.pickupLocationId) : self.pickupLocation.uid ?: @"",
        @key(self.reservation.returnLocationId) : self.returnLocation.uid ?: self.pickupLocation.uid ?: @"",
        @key(self.reservation.pickupTime)       : self.aggregatePickupDate.ehi_dateTimeString ?: @"",
        @key(self.reservation.returnTime)       : self.aggregateReturnDate.ehi_dateTimeString ?: @"",
    }];
    
    [[EHIServices sharedInstance] modifyDateAndLocation:self.reservation handler:^(EHIReservation *newReservation, EHIServicesError* error) {
        // since the endpoint above does not return an updated car class list, we have to call the endpoint updateCarClasses to have a valid list
        if(!error.hasFailed) {
            [[EHIServices sharedInstance] updateAvailableCarClasses:newReservation handler:handler];
        } else {
            ehi_call(handler)(newReservation, error);
        }
    }];
}

# pragma mark - Commit

- (void)commitReservationWith3DSCheck:(BOOL)check3DS handler:(void (^)(EHIServicesError *))handler
{
    if (check3DS) {
        // first check if credit card needs verification through 3DS
        [self check3dsDataWithHandler:^(BOOL supports3ds, NSString *validationData, EHIServicesError *error) {
            if (!error.hasFailed) {
                self.reservation.creditCard3dsValidation = validationData;
                
                [self commitOrModifyReservationWithHandler:handler];
            }
            else {
                self.reservation.creditCard3dsValidation = nil;
                
                ehi_call(handler)(error);
            }
        }];
    } else {
        [self commitOrModifyReservationWithHandler:handler];
    }
}

- (void)commitOrModifyReservationWithHandler:(void (^)(EHIServicesError *))handler
{
    [self prepareReservationForCommitRequest];

    __block id<EHINetworkCancelable> request;

    __weak __typeof(self) welf = self;

    EHIReservationHandler commitHandler = ^(EHIReservation *reservation, EHIServicesError *error) {
        // block any canceled requests
        if(welf.activeRequest != request) {
            return;
        }
        
        welf.activeRequest = nil;
        
        if(!error.hasFailed) {
            [welf didCommitReservation];
        }

        ehi_call(handler)(error);
    };

    if(self.isModifyingReservation) {
        request = [self commitModifyWithHandler:commitHandler];
    } else {
        if(self.reservation.hasToAssociate) {
            dispatch_group_t group = dispatch_group_create();

            dispatch_group_enter(group);
            request = [[EHIServices sharedInstance] associateReservation:self.reservation withUser:[EHIUser currentUser] handler:^(EHIReservation *newReservation, EHIServicesError *error) {
                [error consume];
                dispatch_group_leave(group);
            }];

            dispatch_group_notify(group, dispatch_get_main_queue(), ^{
                [[EHIServices sharedInstance] commitReservation:self.reservation handler:commitHandler];
            });
        } else {
            request = [[EHIServices sharedInstance] commitReservation:self.reservation handler:commitHandler];
        }
    }
    
    self.activeRequest = request;
}

- (id<EHINetworkCancelable>)commitModifyWithHandler:(EHIReservationHandler)commitHandler
{
    // batch pre-commit calls
    __block BOOL success   = YES;
    dispatch_group_t group = dispatch_group_create();
    
    // handle service calls together
    EHIReservation *reservation           = self.reservation;
    EHINetworkCancelableGroup *cancelable = [EHINetworkCancelableGroup new];
    EHIReservationHandler sharedHandler   = ^(EHIReservation *reservation, EHIServicesError *error) {
        dispatch_group_leave(group);
        success &= !error.hasFailed;
    };
    
    if (reservation.additionalInfo.count > 0){
        dispatch_group_enter(group);
        [cancelable addCancelable:[[EHIServices sharedInstance] modifyAdditionalInfo:reservation.additionalInfo reservation:reservation handler:sharedHandler]];
    }
    
    dispatch_group_notify(group, dispatch_get_main_queue(), ^{
        if(success) {
            [cancelable addCancelable:[[EHIServices sharedInstance] commitModifyReservation:reservation handler:commitHandler]];
        } else {
            ehi_call(commitHandler)(nil, [EHIServicesError servicesErrorFailure]);
        }
    });
    
    return cancelable;
}

- (void)retrieveCommitedReservation:(EHIReservation *)reservation handler:(void(^)(void))handler
{
    NSString *confirmation = reservation.confirmationNumber;
    NSString *firstName = reservation.driverInfo.firstName;
    NSString *lastName = reservation.driverInfo.lastName;
    
    [[EHIServices sharedInstance] fetchRentalForConfirmation:confirmation firstName:firstName lastName:lastName handler:^(EHIReservation *retrievedReservation, EHIServicesError *error) {
        if(!error.hasFailed) {
            // if we don't get back a prefill url, graft the one from the commit reservation if it exists
            if(!retrievedReservation.prefillUrl && reservation.prefillUrl) {
                [retrievedReservation updateWithDictionary:@{
                    @key(reservation.prefillUrl) : reservation.prefillUrl,
                }];
            }

            self.reservation = retrievedReservation;
        }
        
        ehi_call(handler)();
    }];
}

//
// Helpers
//

- (void)prepareReservationForCommitRequest
{
    self.reservation.driverInfo     = self.driverInfo;
    self.reservation.airline        = self.airline;
    self.reservation.travelPurpose  = self.travelPurpose;
    self.reservation.paymentMethod  = self.paymentMethod;
    self.reservation.additionalInfo = self.additionalInfo;
}

- (void)didCommitReservation
{
    [self updateProfilePreferencesIfNeded];
    [self trackSuccessfulBookingOfReservation:self.reservation];

    // refresh profile, points may have been used or refunded in modify flow
    [[EHIUserManager sharedInstance] refreshUserWithHandler:nil];
    
    // save this as a past rental
    if([EHISettings sharedInstance].saveSearchHistory) {
        [[EHIHistoryManager sharedInstance] savePastRental:self.targetSavingReservation];
    }
    
    // update cached rentals
    [[EHIUserManager sharedInstance] refreshCurrentAndUpcomingRentalsWithHandler:nil];
}

- (void)updateProfilePreferencesIfNeded
{
    EHIUser *user = [EHIUser currentUser];
    BOOL isLogged = user != nil;
    if(isLogged) {
        BOOL wantsEmailNotifications = self.driverInfo.wantsEmailNotifications == EHIOptionalBooleanTrue;
        BOOL isSpecialOffersAssigned = user.preference.email.specialOffers == EHIOptionalBooleanTrue;
        BOOL shouldUpdate  = wantsEmailNotifications && wantsEmailNotifications != isSpecialOffersAssigned;

        id specialOffers = [EHIOptionalBooleanTransformer() reverseTransformedValue: @(self.driverInfo.wantsEmailNotifications)];
        
        if(shouldUpdate) {
            EHIUserPreferencesProfile *prefs = user.preference;

            EHIUser *newUser = user.deepCopy;
            [newUser updateWithDictionary:@{
                @key(newUser.preference) : @{
                    @key(prefs.email.rentalReceipts) : @(prefs.email.rentalReceipts),
                    @key(prefs.email.specialOffers)  : specialOffers,
                    @key(prefs.email.partnerOffers)  : @(prefs.email.partnerOffers)
                }
             }];
            
            [[EHIServices sharedInstance] updateUser:user withUser:newUser
                                                           handler:^(EHIUser *user, EHIServicesError *error){
                // dont raise alerts
                [error consume];
            }];
        }
    }
}


- (void)trackSuccessfulBookingOfReservation:(EHIReservation *)reservation
{
    // capture the value of the previous reservation
    NSInteger originalValue = self.modifiedReservation.customerValue;
    NSInteger updatedValue  = reservation.customerValue;
    
    [EHIAnalytics trackState:^(EHIAnalyticsContext *context) {
        [context setState:EHIAnalyticsResStateSuccessful silent:NO];
        
        // apply custom macro / value for this event
        context.macroEvent    = EHIAnalyticsMacroEventConfirmation;
        context.customerValue = updatedValue - originalValue;
        
        // also send the confirmation number
        context[EHIAnalyticsResConfNumberKey] = reservation.confirmationNumber;
    }];
}


# pragma mark - Cancellation

- (void)cancelReservation:(EHIReservation *)reservation handler:(void(^)(EHIServicesError *))handler
{
    __block id<EHINetworkCancelable> request;
    __weak __typeof(self) welf = self;
    request = [[EHIServices sharedInstance] cancelReservation:reservation handler:^(EHIServicesError *error) {
        // block any canceled requests
        if(welf.activeRequest != request) {
            return;
        }
        
        welf.activeRequest = nil;
        
        // refresh errors after canceling if we didn't error
        if(!error.hasFailed) {
            [[EHIUserManager sharedInstance] refreshCurrentAndUpcomingRentalsWithHandler:nil];
            
            // refresh user if reservation had points
            if(reservation.selectedCarClass.pointsUsed) {
                [[EHIUserManager sharedInstance] refreshUserWithHandler:nil];
            }
        }
        
        ehi_call(handler)(error);
    }];
    
    self.activeRequest = request;
}


@end
