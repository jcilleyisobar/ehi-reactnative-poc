//
//  EHIEnrollmentStepThreeViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 8/10/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIEnrollmentStepThreeViewModel.h"
#import "EHIProfilePasswordRuleViewModel.h"
#import "EHIProfilePasswordRule.h"
#import "EHIUserProfiles.h"
#import "EHIUserManager.h"
#import "EHIFormattedPhone.h"
#import "EHIServices+User.h"
#import "EHIEnrollmentConfirmationViewModel.h"
#import "EHIEnrollProfile.h"
#import "EHIEnrollmentIssuesViewModel.h"
#import "EHIDataStore.h"
#import "EHIDriverInfo.h"
#import "EHIRequiredInfoFootnoteViewModel.h"

@interface EHIEnrollmentStepThreeViewModel () <EHIFormFieldDelegate, EHIFormFieldTextToggleDelegate, EHIEnrollmentPasswordViewModelDelegate>
@property (strong, nonatomic) EHIUser *user;
@property (strong, nonatomic) NSArray *passwordRequirements;
@property (copy  , nonatomic) NSString *password;
@property (copy  , nonatomic) NSString *passwordConfirmation;
@end

@implementation EHIEnrollmentStepThreeViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        self.step = EHIEnrollmentStepThree;
        [self buildModels];
    }
    
    return self;
}

- (void)buildModels
{
    self.phoneModel = [EHIFormFieldTextViewModel new];
    self.phoneModel.title        = EHILocalizedString(@"enroll_phone_number_title", @"PHONE NUMBER", @"");
    self.phoneModel.isRequired   = YES;
    self.phoneModel.isPhoneField = YES;
    self.phoneModel.sensitive    = YES;
    self.phoneModel.delegate     = self;
    [self.phoneModel validates:EHIFormFieldValidationNotEmptyOrSpaces];
    
    self.emailModel = [EHIFormFieldTextToggleViewModel new];
    self.emailModel.title = EHILocalizedString(@"enroll_email_title", @"EMAIL", @"");
    self.emailModel.toggleAttributesTitle = EHIAttributedStringBuilder.new
        .appendText(EHILocalizedString(@"reservation_driver_info_email_toggle_title", @"Sign up to receive emails from Enterprise", @""))
        .fontStyle(EHIFontStyleLight, 14.0f)
        .space
        .appendText(EHISectionSignString)
        .fontStyle(EHIFontStyleBold, 14.0f)
        .string;

    self.emailModel.isRequired    = YES;
    self.emailModel.isEmailField  = YES;
    self.emailModel.toggleEnabled = [NSLocale ehi_shouldCheckEmailNotificationsByDefault];
    self.emailModel.delegate      = self;
    self.emailModel.returnKeyType = UIReturnKeyNext;
    
    [self.emailModel validates:^BOOL(id input) {
        if([input isKindOfClass:[NSString class]]) {
            return [input ehi_isMasked] || [input ehi_validEmail];
        } else {
            return NO;
        }
    }];
    
    self.createPasswordModel = [[EHIEnrollmentPasswordViewModel alloc] initWithModel:self];
    self.createPasswordModel.type = EHIEnrollmentPasswordTypeCreate;
    
    self.passwordRequirements = @[
        [EHIProfilePasswordRule eightCharactersRule],
        [EHIProfilePasswordRule containsLettersRules],
        [EHIProfilePasswordRule containsNumbersRules],
        [EHIProfilePasswordRule forbiddenRules]
    ];
    
    self.passwordSection = self.passwordRequirements.map(^(EHIProfilePasswordRule *rule) {
        return [[EHIProfilePasswordRuleViewModel new] initWithModel:rule];
    });
    
    self.confirmPasswordModel = [[EHIEnrollmentPasswordViewModel alloc] initWithModel:self];
    self.confirmPasswordModel.type = EHIEnrollmentPasswordTypeConfirmation;
    
    self.joinModel = [EHIFormFieldActionButtonViewModel new];
    self.joinModel.title = EHILocalizedString(@"enroll_join_action", @"JOIN", @"");
    self.joinModel.isFauxDisabled = YES;
    self.joinModel.delegate = self;
    
    self.footnoteModel = [EHIRequiredInfoFootnoteViewModel initWithType:EHIRequiredInfoFootnoteTypeEnrollment];
}

- (void)updateWithModel:(id)model
{
    [super updateWithModel:model];
    
    if([model isKindOfClass:[EHIUser class]]) {
        self.user = model;
    }
}

- (void)setUser:(EHIUser *)user
{
    _user = user;
    
    [self bindUser];
}

# pragma mark - Profile Binding

- (void)bindUser
{
    EHIEnrollProfile *enrollmentProfile = self.enrollmentProfile;
    
    EHIPhone *phone = (EHIPhone *)self.user.contact.phones.firstObject ?: (EHIPhone *)enrollmentProfile.phones.firstObject;
    if(phone) {
        self.phoneModel.inputValue = phone.maskedNumber ?: phone.number;
    }
    
    NSString *email = self.user.contact.maskedEmail ?: self.user.contact.email ?: enrollmentProfile.email;
    if(email) {
        self.emailModel.inputValue = email;
    }
    
    NSString *password = enrollmentProfile.password;
    if(password) {
        self.createPasswordModel.password  = password;
        self.confirmPasswordModel.password = password;
    }
    
    self.confirmPasswordModel.termsRead = enrollmentProfile.terms.acceptDecline;
    
    // fallback to driver info, if nothing was found
    BOOL noProfileMatch = !self.didMatchProfile;
    BOOL noPhoneOrEmail = self.phoneModel.inputValue == nil || self.emailModel.inputValue == nil;
    
    if(noProfileMatch && noPhoneOrEmail) {
        [self bindDriverInfo];
    }
    
    EHIOptionalBoolean specialOffers = [NSLocale ehi_shouldCheckEmailNotificationsByDefault]
        ? EHIOptionalBooleanTrue
        : EHIOptionalBooleanNull;
    
    [self.user updateSpecialOffersOptIn:specialOffers];
}

- (void)bindDriverInfo
{
    // prefill with driver's info, if exists
    [EHIDataStore first:[EHIDriverInfo class] handler:^(EHIDriverInfo *driverInfo){
        self.emailModel.inputValue = driverInfo.email;
        self.phoneModel.inputValue = driverInfo.phone.number;
    }];
}

# pragma mark - EHIFormFieldDelegate

- (void)formField:(EHIFormFieldTextToggleViewModel *)field didChangeToggleValue:(BOOL)toggleEnabled
{
    [EHIAnalytics trackAction:EHIAnalyticsEnrollmentPromotionalEmail handler:nil];
    
    [self validateForm:NO];
    
    EHIOptionalBoolean specialOffers = toggleEnabled ? EHIOptionalBooleanTrue : EHIOptionalBooleanFalse;
    [self.user updateSpecialOffersOptIn:specialOffers];
}

- (void)formFieldViewModelDidChangeValue:(EHIFormFieldViewModel *)viewModel
{
    [self validateForm:NO];
}

- (void)formFieldViewModelButtonTapped:(EHIFormFieldViewModel *)viewModel
{
    BOOL formValid = ![self invalidateModels];
    if(formValid) {
        [EHIAnalytics trackAction:EHIAnalyticsEnrollmentJoin handler:nil];
        
        // update model
        [self.user updateContact:self.createContact];
        [self persistUser:self.user password:self.password readTerms:self.readTerms];
        [self createEnrollProfileWithUser:self.user];
    }
}

- (void)createEnrollProfileWithUser:(EHIUser *)user
{
    NSString *password = self.password;
    BOOL acceptedTerms = self.readTerms;
    EHIEnrollProfile *enrollProfile = [EHIEnrollProfile modelForUser:user password:password acceptedTerms:acceptedTerms];
    
    void (^handler)(EHIUser *, EHIServicesError *) = ^(EHIUser *user, EHIServicesError *error) {
        self.isLoading = NO;
        if(!error.hasFailed) {
            [self showConfirmation:user];
        } else {
            // after consuming the error, its internal message is set to nil, so let's copy it before
            NSString *message = error.message;
            [error consume];
            [self showAlertErrorWithMessage:message completion:^{
                [self showIssues];
            }];
        }
    };
    
    self.isLoading = YES;
    if(self.didMatchProfile) {
        [[EHIServices sharedInstance] cloneEnrollProfile:enrollProfile handler:handler];
    } else {
        [[EHIServices sharedInstance] createEnrollProfile:enrollProfile handler:handler];
    }
}

- (void)showAlertErrorWithMessage:(NSString *)message completion:(void(^)())completion
{
    EHIAlertViewBuilder *alert = EHIAlertViewBuilder.new
    .title(EHILocalizedString(@"alert_service_error_title", @"Error", @"Title for service error alert"))
    .message(message)
    .button(EHILocalizedString(@"standard_ok_text", @"OK", @""));
    
    alert.show(^(NSInteger index, BOOL canceled) {
        ehi_call(completion)();
    });
}

- (EHIUserContactProfile *)createContact
{
    EHIUserContactProfile *model = [EHIUserContactProfile new];
    
    NSString *email = self.emailModel.inputValue;
    model.email       = [email ehi_isMasked] ? nil : email;
    model.maskedEmail = [email ehi_isMasked] ? email : nil;
    
    model.phones      = (NSArray<EHIPhone> *)@[self.phone];
    
    return model;
}

- (EHIPhone *)phone
{
    EHIPhone *phone = self.user.contact.phones.firstObject;
    
    if(!phone) {
        phone          = EHIPhone.new;
        phone.type     = EHIPhoneTypeHome;
        phone.priority = EHIPhonePriorityFirst;
    }
    
    NSString *inputPhone = self.phoneModel.inputValue;
    phone.number = [inputPhone ehi_isMasked] ? phone.number : inputPhone;
    
    return phone;
}

- (void)showConfirmation:(EHIUser *)user
{
    EHIEnrollmentConfirmationViewModel *model = [EHIEnrollmentConfirmationViewModel initWithUsername:user.profiles.basic.loyalty.number password:self.password];
    model.signinFlow = self.signinFlow;
    model.handler    = self.handler;
    
    // if not in the signin flow, confirmation should pop 4 view controllers to land on the dashboard.
    model.stackPop = 4;
    
    self.router.transition.push(EHIScreenEnrollmentConfirmation).object(model).start(nil);
}

- (void)showIssues
{
    NSString *password     = self.password;
    NSString *confirmation = self.passwordConfirmation;
    BOOL readTerms = self.readTerms;
    EHIEnrollmentIssuesViewModel *model = [EHIEnrollmentIssuesViewModel modelWithPassword:password
                                                                             confirmation:confirmation
                                                                                readTerms:readTerms];
    model.signinFlow = self.signinFlow;
    model.handler = self.handler;
    [model updateWithModel:self.user];
    
    self.router.transition.push(EHIScreenEnrollmentIssues).object(model).start(nil);
}

# pragma mark - EHIEnrollmentPasswordViewModelDelegate

- (void)enrollmentPasswordChanged:(EHIEnrollmentPasswordViewModel *)viewModel
{
    if([viewModel isEqual:self.createPasswordModel]){
        self.password = viewModel.password;
    }
    if([viewModel isEqual:self.confirmPasswordModel]){
        self.passwordConfirmation = viewModel.password;
    }
    
    [self runPasswordValidations];
    
    [self validateForm:NO];
}

- (void)enrollmentPasswordToggleReadTerms:(BOOL)readTerms
{
    self.readTerms = readTerms;
    
    [self validateForm:NO];
    
    [EHIAnalytics trackAction:EHIAnalyticsEnrollmentTerms type:EHIAnalyticsActionTypeTap handler:nil];
}

- (void)setReadTerms:(BOOL)readTerms
{
    _readTerms = readTerms;
    
    self.confirmPasswordModel.termsRead = readTerms;
}

# pragma mark - Validation

- (BOOL)invalidateModels
{
    [self validateForm:YES];
    
    NSArray *messages = [self errorMessages];
    BOOL hasError = messages.count > 0;
    
    // highlight password fields
    self.createPasswordModel.showAlert  = ![self hasValidPassword];
    self.confirmPasswordModel.showAlert = ![self hasValidConfirmation];
    
    // format warning message
    NSString *title = EHILocalizedString(@"enroll_field_validation_message", @"Please check next if the fields are valid:", @"");
    NSString *message = (messages ?: @[]).map(^(NSString *message){
        return [NSString stringWithFormat:@"• %@", message];
    }).join(@"\n");
    
    self.warning = hasError ? [NSString stringWithFormat:@"%@\n%@", title, message] : nil;
    
    return hasError;
}

- (BOOL)validateForm:(BOOL)showErrors
{
    BOOL valid = YES;
    
    valid &= [self.phoneModel validate:showErrors];
    valid &= [self.emailModel validate:showErrors];
    valid &= self.hasValidPassword;
    valid &= self.hasValidConfirmation;
    valid &= self.readTerms;

    self.invalidForm = !valid;

    return !valid;
}

- (void)setInvalidForm:(BOOL)invalidForm
{
    _invalidForm = invalidForm;
    
    self.joinModel.isFauxDisabled = invalidForm;
}

- (NSArray *)errorMessages
{
    NSArray *messages = [NSArray array];
    
    BOOL hasError = NO;
    
    hasError = ![self.phoneModel validate:YES];
    if(hasError) {
        messages = [messages ehi_safelyAppend:self.phoneModel.title];
    }
    
    hasError = ![self.emailModel validate:YES];
    if(hasError) {
        messages = [messages ehi_safelyAppend:self.emailModel.title];
    }
    
    if(!self.hasValidPassword) {
        messages = [messages ehi_safelyAppend:self.createPasswordModel.signinModel.title];
    }
    
    if(!self.hasValidConfirmation) {
        messages = [messages ehi_safelyAppend:self.confirmPasswordModel.signinModel.title];
    }
    
    BOOL readTerms = self.readTerms;
    if(!readTerms) {
        messages = [messages ehi_safelyAppend:EHILocalizedString(@"enroll_terms_and_conditions_string", @"Terms & Conditions", @"")];
    }
    
    return messages;
}

- (NSArray *)errorMessagesShowingErrors:(BOOL)showErrors
{
    [self validateForm:showErrors];
    
    // highlight password fields
    self.createPasswordModel.showAlert  = ![self hasValidPassword];
    self.confirmPasswordModel.showAlert = ![self hasValidConfirmation];
    
    return self.errorMessages;
}

# pragma mark - Password

- (void)runPasswordValidations
{
    self.passwordSection.each(^(EHIProfilePasswordRuleViewModel *model){
        [model invalidatePassword:self.password];
    });
    
    BOOL showPasswordsDontMatch;
    
    if (self.passwordConfirmation.length == 0 || self.password.length == 0) {
        showPasswordsDontMatch = NO;
    } else {
        showPasswordsDontMatch = ![self.password hasPrefix:self.passwordConfirmation]
        || !(self.passwordConfirmation.length <= self.password.length);
    }
    
    self.confirmPasswordModel.showPasswordsDontMatch = showPasswordsDontMatch;
}

- (BOOL)hasValidPassword
{
    return self.passwordRequirements.all(^(EHIProfilePasswordRule *rule) {
        return [rule passedForPassword:self.password];
    });
}

- (BOOL)hasValidConfirmation
{
    return [self.password isEqualToString:self.passwordConfirmation];
}

- (BOOL)hasValidPasswordAndConfirmation
{
    return [self hasValidPassword] && [self hasValidConfirmation];
}

- (EHIUserContactProfile *)currentContact
{
    return self.createContact;
}

@end
