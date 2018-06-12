//
//  EHIDriverInfoViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 4/8/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservationStepViewModel_Subclass.h"
#import "EHIDriverInfoViewModel.h"
#import "EHIDriverInfoViewModel_Private.h"
#import "EHIUser.h"
#import "EHIPhoneNumberFormatter.h"
#import "EHISettings.h"
#import "EHIFlightDetailsViewModel.h"
#import "EHIToastManager.h"
#import "EHIUserManager.h"
#import "EHISigninViewModel.h"

@interface  EHIDriverInfoViewModel () <EHIUserListener>
@property (copy  , nonatomic) NSString *actionButtonTitle;
@property (copy  , nonatomic) NSString *displayNameTitle;
@property (assign, nonatomic) BOOL showFirstNameError;
@property (assign, nonatomic) BOOL showLastNameError;
@property (assign, nonatomic) BOOL showPhoneError;
@property (assign, nonatomic) BOOL showEmailError;
@property (assign, nonatomic) BOOL isLoading;
@property (assign, nonatomic) BOOL didSignInDuringReservation;
@end

@implementation EHIDriverInfoViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _title             = EHILocalizedString(@"reservation_driver_info_navigation_title", @"Driver Info", @"navigation bar title for driver info screen");
        _signInBannerTitle = EHILocalizedString(@"reservation_driver_info_signin", @"Are you an Enterprise Plus member? Sign in to speed up the process and save your reservation information.", @"");
        _signInButtonTitle = EHILocalizedString(@"login_title", @"Sign In", @"").uppercaseString;
        _nameTitle         = [EHILocalizedString(@"reservation_driver_info_name_title", @"NAME", @"") stringByAppendingString:@" *"];
        _phoneTitle        = [EHILocalizedString(@"reservation_driver_info_phone_title", @"PHONE NUMBER", @"") stringByAppendingString:@" *"];
        _nextTitle         = EHILocalizedString(@"next_button_title", @"Next", @"a button that says next");
        
        _emailTitle         = [EHILocalizedString(@"reservation_driver_info_email_title", @"EMAIL ADDRESS", @"") stringByAppendingString:@" *"];
        _emailHelpTitle     = EHILocalizedString(@"reservation_driver_info_email_help_title", @"We'll use this to send your confirmation email", @"");
        _emailToggleTitle   = EHILocalizedString(@"reservation_driver_info_email_toggle_title", @"Sign up to receive emails from Enterprise", @"");
        _confirmationTitle  = EHILocalizedString(@"email_special_german_opt_in_text", @"You will receive a confirmation email shortly. Please confirm that you would like to opt-in for email specials.", @"");

        _firstNamePlaceholder = [self attributedPlaceholderForString:EHILocalizedString(@"reservation_driver_info_first_name_placeholder", @"First Name", @"")];
        _lastNamePlaceholder  = [self attributedPlaceholderForString:EHILocalizedString(@"reservation_driver_info_last_name_placeholder", @"Last Name", @"")];
        _emailPlaceholder     = [self attributedPlaceholderForString:EHILocalizedString(@"reservation_driver_info_email_placeholder", @"email@address.com", @"")];
        _phonePlaceholder     = [self attributedPlaceholderForString:EHILocalizedString(@"reservation_driver_info_phone_placeholder", @"Enter Phone Number", @"") ];
        _saveToggleTitle      = [self saveToggleTitleAttributedString];
        
        // apply any cached driver info from the builder
        EHIDriverInfo *driverInfo = self.builder.driverInfo;

        _firstName = driverInfo.firstName;
        _lastName  = driverInfo.lastName;
        self.phone = driverInfo.phone.maskedNumber ?: driverInfo.phone.number;
        _email     = driverInfo.maskedEmail ?: driverInfo.email;

        _displayNameTitle = self.isModify ? [NSString stringWithFormat:@"%@ %@", _firstName, _lastName] : [EHIUser currentUser].displayName;
        
        BOOL shouldCheckEmailNotificationsByDefault = [NSLocale ehi_shouldCheckEmailNotificationsByDefault];
        _wantsEmailNotifications = shouldCheckEmailNotificationsByDefault || driverInfo.wantsEmailNotifications == EHIOptionalBooleanTrue;
        _specialOffersOptIn      = _wantsEmailNotifications ? EHIOptionalBooleanTrue : EHIOptionalBooleanNull;

        _requiredFieldsModel = [EHIRequiredInfoViewModel modelForInfoType: EHIRequiredInfoTypeReservation];
        _footnoteModel       = [EHIRequiredInfoFootnoteViewModel initWithType:EHIRequiredInfoFootnoteTypeReservation];
    }
    
    return self;
}


- (void)didBecomeActive
{
    [super  didBecomeActive];

    [[EHIUserManager sharedInstance] addListener:self];
}

# pragma mark - Actions

- (void)toggleEmailNotifications
{
    self.wantsEmailNotifications = !self.wantsEmailNotifications;

    // track the e-mail toggle
    NSString *action = self.wantsEmailNotifications ? EHIAnalyticsActionEmailOptIn : EHIAnalyticsActionEmailOptOut;
    [EHIAnalytics trackAction:action handler:nil];
}

- (void)commitDriverInfo
{
    // validate while surfacing errors
    if([self invalidDriverInfo:YES]) {
        return;
    }
    
    // update whatever we have with required fields
    EHIDriverInfo *driverInfo = [self buildDriverInfoForRequestWithDriverInfo:self.builder.driverInfo];

    // set the driver info on the builder, let it handle caching
    self.builder.driverInfo = driverInfo;
    
    // mirror settings with caching option
    [EHISettings sharedInstance].autoSaveUserInfo = self.shouldSaveDriverInfo;
    
    [EHIAnalytics trackAction:EHIAnalyticsResActionDone handler:nil];
    
    void(^completionBlock)() = ^{
        if(self.isEditing) {
            self.router.transition
            .pop(1).start(nil);
        } else {
            BOOL isMultiTerminal = self.builder.promptsMultiTerminal;
            if(isMultiTerminal) {
                self.router.transition
                .push(EHIScreenReservationFlightDetails).object(@(EHIFlightDetailsStateReview)).start(nil);
            } else {
                self.router.transition
                .push(EHIScreenReservationReview).start(nil);
            }
        }
    };

    if(self.isModify) {
        [self updateDriverInfo:driverInfo handler:completionBlock];
    } else {
        completionBlock();
    }
}

- (void)updateDriverInfo:(EHIDriverInfo *)driverInfo handler:(void(^)())handler
{
    self.isLoading = YES;
    
    EHIReservation *reservation = self.builder.reservation;
    [[EHIServices sharedInstance] modifyDriver:driverInfo airline:reservation.airline reservation:reservation handler:^(EHIReservation *reservation, EHIServicesError *error) {
        self.isLoading = NO;
        if(!error.hasFailed) {
            ehi_call(handler)();
        }
    }];
}

- (void)presentSignIn
{
    EHISigninViewModel *signinViewModel = [EHISigninViewModel new];
    signinViewModel.isReservationFlow = YES;
    self.router.transition.present(EHIScreenMainSignin).object(signinViewModel).start(nil);
}

# pragma mark - Setters

- (void)setFirstName:(NSString *)firstName
{
    _firstName = firstName;
    
    self.showFirstNameError = NO;
}

- (void)setLastName:(NSString *)lastName
{
    _lastName = lastName;
    
    self.showLastNameError = NO;
}

- (void)setPhone:(NSString *)aPhone
{
    NSString *phone;
    EHIFormattedPhone *formattedPhone;
    if(aPhone.ehi_isMasked) {
        phone = aPhone;

        formattedPhone = [EHIFormattedPhone new];
        formattedPhone.originalPhone  = aPhone;
        formattedPhone.formattedPhone = aPhone;
    } else {
        phone = [EHIPhoneNumberFormatter format:aPhone countryCode:[NSLocale ehi_region]];

        formattedPhone = [EHIPhoneNumberFormatter format:aPhone];
    }

    _phone = phone;
    self.formattedPhone = formattedPhone;
    
    self.showPhoneError = NO;
}

- (void)setEmail:(NSString *)email
{
    _email = email;
    
    self.showEmailError = NO;
}

- (void)setWantsEmailNotifications:(BOOL)wantsEmailNotifications
{
    _wantsEmailNotifications = wantsEmailNotifications;

    self.specialOffersOptIn = _wantsEmailNotifications ? EHIOptionalBooleanTrue : EHIOptionalBooleanFalse;
}

# pragma mark - Accessors

- (NSString *)actionButtonTitle
{
    return self.isEditing
        ? EHILocalizedString(@"reservation_driver_info_done_button_title", @"DONE", @"")
        : EHILocalizedString(@"reservation_driver_info_continue_button_title", @"CONTINUE TO REVIEW", @"");
}

- (NSString *)outsideVendorMessage
{
    BOOL isLogged = [EHIUser currentUser] != nil;
    BOOL showIdentityCheckMessage = [self.builder.reservation.pickupLocation shouldShowIdentityCheckWithExternalVendorMessage];
    
    if(showIdentityCheckMessage && !isLogged) {
        return EHILocalizedString(@"reservation_driver_info_uk_identity_message", @"By entering in your details above you acknowledge that we may pass your information to external partners in order to verify your identity.", @"");
    }
    
    return nil;
}

- (BOOL)hideNameContainer
{
    return (self.isAuthenticated && self.didSignInDuringReservation) || self.isModify;
}

- (BOOL)hideSaveInfoContainer
{
    return self.isAuthenticated || self.isModify;
}

- (BOOL)showSignInContainer
{
    return !self.isAuthenticated && !self.isModify;
}

- (BOOL)showOutsideVendor
{
    return self.outsideVendorMessage == nil;
}

- (BOOL)showConfirmationTitle
{
    return [NSLocale ehi_shouldShowDoubleOptInForEmailSpecials] && self.confirmationTitle.length && self.wantsEmailNotifications;
}

- (BOOL)isAuthenticated
{
    return [EHIUser currentUser] != nil;
}

- (BOOL)invalidDriverInfo
{
    return [self invalidDriverInfo:NO];
}

//
// Helpers
//

- (void)loadUserInformation:(EHIUser *)user
{
    self.displayNameTitle = [EHIUser currentUser].displayName;
    self.firstName        = user.firstName;
    self.lastName         = user.lastName;

    EHIPhone *phone = user.contact.phones.firstObject;
    self.phone      = phone.number ? phone.number : phone.maskedNumber;
    self.email      = user.contact.email ? user.contact.email : user.contact.maskedEmail;

    self.wantsEmailNotifications = user.preference.email.specialOffers == EHIOptionalBooleanTrue;
}

- (BOOL)invalidDriverInfo:(BOOL)exposeErrors
{
    BOOL firstNameError   = self.firstName.length == 0;
    BOOL lastNameError    = self.lastName.length == 0;
    BOOL phoneError       = self.phone.length == 0;
    BOOL emailError       = !self.email.ehi_isMasked && ![self.email ehi_validEmail];
    
    // expose errors if needed
    if(exposeErrors) {
        self.showFirstNameError = firstNameError;
        self.showLastNameError  = lastNameError;
        self.showPhoneError     = phoneError;
        self.showEmailError     = emailError;
    }
    
    return firstNameError || lastNameError || phoneError || emailError;
}

- (NSAttributedString *)attributedPlaceholderForString:(NSString *)string
{
    return [EHIAttributedStringBuilder new]
    .text(string).size(18.0).color([UIColor ehi_silverColor]).string;
}

- (NSAttributedString *)saveToggleTitleAttributedString
{
    NSString *string = EHILocalizedString(@"reservation_driver_info_persist_info_title", @"Save this information for\nsubsequent reservations", @"");
    
    return EHIAttributedStringBuilder.new
        .text(string)
        .size(18.0)
        .color([UIColor ehi_blackColor])
        .minimumLineHeight(21.0)
        .string;
}

# pragma mark - EHIUserListener

- (void)manager:(EHIUserManager *)manager didSignInUser:(EHIUser *)user
{
    [self loadUserInformation:user];

    self.didSignInDuringReservation = YES;
    self.builder.reservation.hasToAssociate = YES;

    NSString *successMessage = EHILocalizedString(@"reservation_driver_info_toast", @"Sign in successful.", @"");
    [EHIToastManager showMessage:successMessage];
}

@end
