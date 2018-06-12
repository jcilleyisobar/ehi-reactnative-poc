//
//  EHIItineraryViewModel.m
//  Enterprise
//
//  Created by Ty Cobb on 2/23/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservationStepViewModel_Subclass.h"
#import "EHIItineraryViewModel.h"
#import "EHISigninViewModel.h"
#import "EHIInfoModalViewModel.h"
#import "EHIReservationRouter.h"
#import "EHIToastManager.h"
#import "EHIUserManager+DNR.h"
#import "EHIReservationBuilder+Analytics.h"
#import "EHIServices+User.h"
#import "EHIServices+Reservation.h"
#import "EHIServices+Contracts.h"
#import "EHIContractDetails.h"
#import "EHIAdditionalInformationViewModel.h"

@interface EHIItineraryViewModel () <EHIReservationBuilderReadinessListener>
@property (assign, nonatomic) BOOL isReadyToContinue;
@property (assign, nonatomic) BOOL isLoading;
@property (assign, nonatomic) BOOL isInitiating;
@property (assign, nonatomic) BOOL isOneWay;
@property (copy  , nonatomic) NSString *pickupHeaderFallbackTitle;
@property (copy  , nonatomic) NSString *pickupHeaderTitle;
@property (copy  , nonatomic) NSString *title;
@property (copy  , nonatomic) NSString *actionButtonTitle;
@property (assign, nonatomic) BOOL isAskingForAdditionalInfo;
@end

@implementation EHIItineraryViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _actionButtonTitle = EHILocalizedString(@"reservation_itinerary_action_button", @"CONTINUE", @"Reservation itinerary 'CONTINUE' title");
        _returnHeaderTitle = EHILocalizedString(@"reservation_location_selection_return_header_title", @"RETURN LOCATION", @"header title for a section that allows user to select return location");
    }
    
    return self;
}

- (void)didBecomeActive
{
    [super didBecomeActive];
    
    if(!self.isModify) {
        self.isLoading = YES;
        [[EHIUserManager sharedInstance] attemptEmeraldAutoAuthentcateWithHandler:^(EHIUser *user, EHIServicesError *error) {
            self.isLoading = NO;
        }];
        
        if (!self.isAskingForAdditionalInfo) {
            // clean up any current pre-rate data
            [self.builder resetPreRateData];
        }
    }
    
    // wait for builder readiness to add reactions
    [self.builder waitForReadiness:self];
}

# pragma mark - Reactions

- (void)builderIsReady:(EHIReservationBuilder *)builder
{
    self.title = self.isModify
        ? EHILocalizedString(@"reservation_navigation_in_modify_title_key", @"Modify Rental", @"navigation bar title for initial reservation screen")
        : EHILocalizedString(@"reservation_navigation_title_key", @"New Rental", @"navigation bar title for modify reservation screen");
    
    [MTRReactor autorun:self action:@selector(invalidatePickupHeader:)];
    
    builder.bind.map(@{
        source(builder.canInitiateReservation) : dest(self, .isReadyToContinue),
    });
}

- (void)invalidatePickupHeader:(MTRComputation *)computation
{
    BOOL isOneWay = self.builder.isOneWayReservation;
    
    self.isOneWay = isOneWay;
    self.pickupHeaderTitle = isOneWay
        ? EHILocalizedString(@"reservation_location_selection_pickup_header_title", @"PICK-UP LOCATION", @"header title for a section that allows user to select pickup location")
        : EHILocalizedString(@"reservation_location_selection_pickup_header_fallback_title", @"LOCATION", @"fallback header title for a section that allows user to select pickup location");
}

# pragma mark - Services
 
- (void)commitItinerary
{
    [EHIAnalytics trackAction:EHIAnalyticsResActionContinue handler:^(EHIAnalyticsContext *context) {
        context.macroEvent = EHIAnalyticsMacroEventDateTime;
    }];
    
    self.isInitiating = YES;
   
    if (self.isModify) {
        [self modifyReservation];
    } else {
        [self initiateReservation];
    }
}

- (void)modifyReservation
{
    [self.builder modifyLocationDateTimeWithHandler:^(EHIReservation *reservation, EHIServicesError *error) {
        self.isInitiating = NO;
        
        if (!error.hasFailed) {
            [self pushToClassSelect];
        }
    }];
}

- (void)initiateReservation
{
    // kickoff the intiate request
    [self.builder initiateReservationWithHandler:^(EHIServicesError *error) {
        self.isInitiating = NO;
        
        if(!error.hasFailed) {
            // transition to class select
            [EHIUserManager attemptToShowContinueDnrModalWithHandler:^(BOOL shouldContinue) {
                if(shouldContinue) {
                    [self pushToClassSelect];
                }
            }];
        } else {
            [self handleInitiateError:error];
        }
    }];
}

- (void)pushToClassSelect
{
    self.router.transition
        .push(EHIScreenReservationClassSelect).start(nil);
}

# pragma mark - Corp Code Errors

- (void)handleInitiateError:(EHIServicesError *)error
{
    if([error hasErrorCode:EHIServicesErrorCodeLoginSystemError]) {
        [self handleLoginSystemError:error];
    } else if ([error hasErrorCode:EHIServicesErrorCodePinRequired]) {
        [self handlePinRequiredError:error];
    } else if ([error hasErrorCode:EHIServicesErrorCodeTravelPurposeNotSpecified]) {
        [self handleTravelPurposeError:error];
    } else if ([error hasErrorCode:EHIServicesErrorCodeBusinessLeisureNotOnProfileError] ||
               [error hasErrorCode:EHIServicesErrorCodeContractNotOnProfile]) {
        [self handleMismatchedAccountError:error];
    } else if ([error hasErrorCode:EHIServicesErrorCodeAdditionalInfoRequired]) {
        [self handleAdditionalInfoError:error];
    }
}

- (void)handleAdditionalInfoError:(EHIServicesError *)error
{
    [error consume];

    self.isInitiating = YES;
    self.isAskingForAdditionalInfo = YES;
    
    EHIAdditionalInformationViewModel *model = [[EHIAdditionalInformationViewModel alloc] initWithFlow:EHIAdditionalInformationFlowDefault];
    __weak typeof(self) welf = self;
    self.router.transition.present(EHIScreenReservationAdditionalInfo).object(model).handler(^(BOOL submitted, EHIServicesError *error){
        [welf handlePreRateFlowError:error submitted:submitted];
    }).start(nil);
}

- (void)handlePinRequiredError:(EHIServicesError *)error
{
    [error consume];
    
    self.isInitiating = YES;
    self.isAskingForAdditionalInfo = YES;

    __weak typeof(self) welf = self;
    self.router.transition.present(EHIScreenReservationPinAuthentication).handler(^(BOOL submitted, EHIServicesError *error) {
        [welf handlePreRateFlowError:error submitted:submitted];
    }).start(nil);
}

- (void)handlePreRateFlowError:(EHIServicesError *)error submitted:(BOOL)submitted
{
    [error consume];
    
    __weak typeof(self) welf = self;
    self.router.transition
        .dismiss.start(^{
            welf.isInitiating = NO;
            welf.isAskingForAdditionalInfo = NO;

            if (submitted) {
                if(error.hasFailed) {
                    [welf handleInitiateError:error];
                }
                else {
                    [welf commitItinerary];
                }
            }
        });
}

- (void)handleMismatchedAccountError:(EHIServicesError *)error
{
    [error consume];
    
    [EHIAnalytics trackAction:EHIAnalyticsCorpFlowActionProfile handler:nil];
    
    EHIInfoModalViewModel *model = [EHIInfoModalViewModel new];
    model.details          = EHILocalizedString(@"info_modal_mismatch_account_details", @"Please contact your travel administrator to get it attached to your E+ profile.", @"detail for mismatch account details info modal");
    model.firstButtonTitle = EHILocalizedString(@"standard_close_button", @"CLOSE", @"");
    model.hidesCloseButton = YES;
    model.headerNibName    = @"EHIItineraryWarning";
   
    [model present:nil];
}

- (void)handleLoginSystemError:(EHIServicesError *)error
{
    [error consume];
    
    [EHIAnalytics trackAction:EHIAnalyticsCorpFlowActionSignin handler:nil];
    
    EHISigninViewModel *model = [EHISigninViewModel new];
    model.headerInfoText = EHILocalizedString(@"sign_in_corp_flow_header_login_error", @"To use this code, you must be logged in to the app. Please log in.", @"sign in header for when user is not logged in");
    
    self.router.transition
        .present(EHIScreenMainSignin).object(model).handler(^{
            [self commitItinerary];
        }).start(nil);
}

- (void)handleTravelPurposeError:(EHIServicesError *)error
{
    [error consume];
    
    [EHIAnalytics trackAction:EHIAnalyticsCorpFlowActionPurpose handler:nil];
    
    EHIInfoModalViewModel *model = [EHIInfoModalViewModel new];
    model.title             = EHILocalizedString(@"info_modal_travel_purpose_title", @"What's the purpose of your rental: business or leisure?", @"title for info modal asking for travel purpose");
    model.firstButtonTitle  = EHILocalizedString(@"info_modal_travel_purpose_first_button", @"BUSINESS", @"title of first button in info modal asking for travel purpose");
    model.secondButtonTitle = EHILocalizedString(@"info_modal_travel_purpose_second_button", @"LEISURE", @"title of second button in info modal asking for travel purpose");

    [model present:^(NSInteger index, BOOL canceled) {
        if(!canceled) {
            self.builder.travelPurpose = index == 0 ? EHIReservationTravelPurposeBusiness : EHIReservationTravelPurposeLeisure;
            self.builder.travelPurposeSelectedPreRates = YES;
            [self commitItinerary];
        }
        
        return YES;
    }];
}

# pragma mark - Passthrough

- (BOOL)isReadyToContinue
{
    return [EHIReservationBuilder sharedInstance].canInitiateReservation;
}

# pragma mark - Analytics

- (void)updateAnalyticsContext:(EHIAnalyticsContext *)context
{
    [super updateAnalyticsContext:context];
    
    [self.builder synchronizeLocationsOnContext:context];
    [self.builder synchronizeDateTimeOnContext:context];
}

@end
