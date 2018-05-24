//
//  EHISurveyViewModel.m
//  Enterprise
//
//  Created by frhoads on 12/7/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHISurveyViewModel.h"
#import "EHIViewModel_Subclass.h"
#import "EHISurvey.h"
#import "EHIUser.h"
#import "EHIPhoneNumberFormatter.h"
#import "EHIToastManager.h"

@implementation EHISurveyViewModel

- (instancetype)init
{
    if(self = [super init]) {
        _title               = EHILocalizedString(@"survey_title", @"Send Survey", @"");
        _greetingsTitle      = EHILocalizedString(@"survey_greetings_message", @"Thank you for helping!", @"");
        _instructionsTitle   = EHILocalizedString(@"survey_instructions_message", @"You will be emailed or text messaged (US & CAN only) the survey from our partner ForeSee. Text messaging rates apply.", @"");
        _sendSurveyTitle     = EHILocalizedString(@"survey_send_survey_button_title", @"SEND SURVEY", @"");
        _surveyPolicyTitle   = EHILocalizedString(@"survey_foresee_policy_button_title", @"Foresee Privacy Policy", @"");
        _isInvalidInput      = YES;
    }
    
    return self;
}

- (void)didBecomeActive
{
    [super didBecomeActive];
    
    [self updateSession];
}

- (void)navigateBack
{
    [EHIAnalytics trackAction:EHIAnalyticsSurveyActionBack handler:nil];
    [super navigateBack];
}

# pragma mark - Accessors

- (void)setCustomerDetail:(NSString *)customerDetail
{
    BOOL isPossiblePhoneNumber = [customerDetail ehi_isPossiblePhoneNumber];
    if(isPossiblePhoneNumber) {
        _customerDetail = [EHIPhoneNumberFormatter format:customerDetail].formattedPhone;
    } else {
        _customerDetail = customerDetail;
    }
    
    BOOL invalidEmail       = ![customerDetail ehi_validEmail];
    BOOL invalidPhoneNumber = ![customerDetail ehi_isPhoneNumber];
    
    self.isInvalidInput = invalidEmail && invalidPhoneNumber;
}

- (NSAttributedString *)customerDetailTitle
{
    NSString *emailPhone  = EHILocalizedString(@"survey_contact_input_title", @"EMAIL OR PHONE", @"");
    NSString *textMessage = EHILocalizedString(@"survey_contact_input_title_details", @"(text message)", @"");
    
    return EHIAttributedStringBuilder.new
            .appendText(emailPhone).fontStyle(EHIFontStyleBold, 14.0f).space
            .appendText(textMessage).fontStyle(EHIFontStyleLight, 12.0f).string;
}

# pragma mark - Actions

- (void)submitContact
{
    if(self.isInvalidInput) {
       [self showErrorMessage];
    } else {
        [EHIAnalytics trackAction:EHIAnalyticsSurveyActionSend handler:nil];

        self.isLoading = YES;
        __weak __typeof(self) welf = self;
        [self.survey requestSurveyWithCustomerDetails:self.customerDetail validation:^(BOOL isInvalid) {
            welf.isLoading = NO;
            if(isInvalid) {
                [welf showErrorMessage];
            } else {
                [welf dismiss];
            }
        }];
    }
}

# pragma mark - Survey Session

- (void)updateSession
{
    EHIUser *user = [EHIUser currentUser];
    EHISurveySession *session = [[EHISurvey sharedInstance] session];
    
    [self updateSessionForUser:user withSession:session];
    [self updateSessionRentalsForUser:user withSession:session];
}

- (void)updateSessionForUser:(EHIUser *)user withSession:(EHISurveySession *)session
{
    NSString *loyaltyId     = user.profiles.basic.loyalty.number;
    NSString *loyaltyTier   = EHILoyaltyTierTitleForTier(user.profiles.basic.loyalty.tier);
    NSString *loyaltyPoints = user.loyaltyPoints;
    NSString *customerAttachedCID = user.corporateContract.contractNumber;
    
    session[EHISurveyLoyaltyIdKey]        = loyaltyId;
    session[EHISurveyLoyaltyTierKey]      = loyaltyTier;
    session[EHISurveyPointsBalance]       = loyaltyPoints;
    session[EHISurveyCustomerAttachedCID] = customerAttachedCID;
}

- (void)updateSessionRentalsForUser:(EHIUser *)user withSession:(EHISurveySession *)session
{
    EHIUserRental *rental = nil;
    NSString *state = EHISurveyRentalStateNoneValue;
    if(user.currentRentals.all.count > 0) {
        state  = EHISurveyRentalStateCurrentValue;
        rental = user.currentRentals.all.find(^(EHIUserRental *rental){
            return rental.isCurrent;
        });
    } else if(user.upcomingRentals.all.count > 0) {
        state  = EHISurveyRentalStateUpcomingValue;
        rental = user.upcomingRentals.all.firstObject;
    }
    session[EHISurveyRentalStateKey]  = state;
    if (![state isEqualToString:EHISurveyRentalStateNoneValue]){
        session[EHISurveyLocationTypeKey] = rental.pickupLocation.type == EHILocationTypeAirport ? EHISurveyLocationAirportValue : EHISurveyLocationHomeCityValue;
        session[EHISurveyRentalLengthKey] = rental.returnDate ? @([rental.pickupDate ehi_daysUntilDate:rental.returnDate]) : nil;
    }
}

- (void)dismiss
{
    [self showSuccessMessage];
    self.router.transition.pop(1).start(nil);
}

- (void)showSuccessMessage
{
    NSString *message = EHILocalizedString(@"survey_success_message", @"We will send you the survey shortly", @"");
    [self showMessage:message];
}

- (void)showErrorMessage
{
    NSString *message = EHILocalizedString(@"survey_invalid_contact_info_message", @"Please enter an email or phone number", @"");
    [self showMessage:message];
}

- (void)showMessage:(NSString *)message
{
    [EHIToastManager showMessage:message];
}

- (void)showSurveyPolicy
{
    [EHIAnalytics trackAction:EHIAnalyticsSurveyActionReadPolicy handler:nil];

    NSURL *url = self.survey.policiesURL;
    self.router.transition.present(EHIScreenWebBrowser).object(url).start(nil);
}

# pragma mark - Passthrough

- (EHISurvey *)survey
{
    return [EHISurvey sharedInstance];
}

@end
