//
//  EHIEmeraldClubSignInViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 6/2/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIEmeraldClubSignInViewModel.h"
#import "EHIViewModel_Subclass.h"
#import "EHIUserCredentials.h"
#import "EHIUserManager.h"
#import "EHIUserManager+Analytics.h"
#import "EHIConfiguration.h"
#import "EHIWebBrowserViewModel.h"
#import "EHIToastManager.h"

@interface EHIEmeraldClubSignInViewModel ()
@property (copy  , nonatomic) NSString *identification;
@property (copy  , nonatomic) NSString *password;
@property (assign, nonatomic) BOOL isLoading;
@property (assign, nonatomic) BOOL hasValidCredentials;
@property (assign, nonatomic) BOOL remembersCredentials;
@end

@implementation EHIEmeraldClubSignInViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _title = EHILocalizedString(@"emerald_login_title", @"Emerald Club", @"");
        _headerInfoText      = EHILocalizedString(@"emerald_login_add_account_title", @"Add your Emerald Club account", @"");
        _subheaderInfoText   = EHILocalizedString(@"emerald_login_add_account_subtitle", @"You can add your Emerald Club Account to this reservation to receive your corporate benefits and credits towards tier status", @"");
        _staySignedInTitle   = EHILocalizedString(@"emerald_login_remember_credentials_title", @"Remember Details", @"");
        _addAccountTitle     = EHILocalizedString(@"emerald_login_add_account_button_title", @"ADD ACCOUNT", @"");
        _forgotPasswordTitle = EHILocalizedString(@"emerald_login_forgot_password_button_title", @"Forgot Password or User Name", @"");
        
        _fieldModels = [EHISigninFieldModel emeraldClubFields];
        
        // validate to the correct initial state
        [self validateCredentials];
    }
    
    return self;
}

- (void)didResignActive
{
    [super didResignActive];
    
    // prevent retain cycles
    self.signinHandler = nil;
}

- (void)setUsername:(NSString *)username
{
    [self setValue:username forFieldWithType:EHISigninFieldTypeEmail];
}

- (void)setLayout:(EHISigninLayout)layout
{
    _layout = layout;
    
    switch (layout) {
        case EHISigninLayoutEnrollment: {
            _headerInfoText = EHILocalizedString(@"enroll_account_exists_title", @"Looks like you have an existing account. Please enter your password.", @"");
            _subheaderInfoText  = nil;
            break;
        }
        default: break;
    }
}

# pragma mark - Fields

- (void)setValue:(NSString *)value forFieldWithType:(EHISigninFieldType)type
{
    switch(type) {
        case EHISigninFieldTypeEmail:
            self.identification = value; break;
        case EHISigninFieldTypePassword:
            self.password = value; break;
    }
}

//
// Setters
//

- (void)setIdentification:(NSString *)identification
{
    _identification = identification;
    
    [self validateCredentials];
}

- (void)setPassword:(NSString *)password
{
    _password = password;
    
    [self validateCredentials];
}

//
// Validators
//

- (void)validateCredentials
{
#if EHIUserAutofillCredentials
    BOOL isValid = YES;
#else
    BOOL hasEmail    = self.identification.length != 0;
    BOOL hasPassword = self.password.length != 0;
    
    BOOL isValid = hasEmail && hasPassword;
#endif
    
    if(self.hasValidCredentials != isValid) {
        self.hasValidCredentials = isValid;
    }
}

# pragma mark - Actions

- (void)initiateSignin
{
    self.isLoading = YES;
    
    EHIUserCredentials *credentials = [self createEmeraldCredentials];
    
    [[EHIUserManager sharedInstance] authenticateUserWithCredentials:credentials handler:^(EHIUser *user, EHIServicesError *error) {
        self.isLoading = NO;
        
        if(!error.hasFailed) {
            
            [self showSuccessMessage];
            
            // track the change
            [self invalidateAnalyticsContext];
            [EHIAnalytics trackState:^(EHIAnalyticsContext *context) {
                [context setRouterScreen:EHIScreenSigninEmerald];
                [context setRouterState:EHIScreenSigninEmerald];
                [context setState:EHIAnalyticsUserStateSuccess silent:NO];
            }];
            
            // dismiss signin after root is optionally replaced underneath and we are active, otherwise we are going to mess up with the navigation stack
            if(self.isActive) {
                self.router.transition.dismiss.start(nil);
            }
            
            ehi_call(self.signinHandler)(YES);
        }
    }];
}

- (void)showSuccessMessage
{
    NSString *message = EHILocalizedString(@"reservation_emerald_sign_in_confirmation_toast_message", @"", @"");
    [EHIToastManager showMessage:message];
}

- (void)cancelSignIn
{
    self.router.transition.dismiss.start(nil);
    ehi_call(self.signinHandler)(NO);
}

- (void)toggleRemembersCredentials
{
    [self setRemembersCredentials:!self.remembersCredentials];
    
    [EHIAnalytics trackAction:self.remembersCredentials ? EHIAnalyticsUserActionRememberOn : EHIAnalyticsUserActionRememberOff handler:nil];

    [self invalidateAnalyticsContext];
}

- (void)didTapForgotPassword
{
    [EHIAnalytics trackAction:EHIAnalyticsECUserActionForgotPasswordUsername handler:nil];

    NSString *forgotPassword = [EHIConfiguration configuration].nationalForgotPasswordUrl;
    NSURL *forgotPasswordURL = [NSURL URLWithString:forgotPassword];
    EHIWebBrowserViewModel *model = [[EHIWebBrowserViewModel alloc] initWithUrl:forgotPasswordURL body:nil];
    
    self.router.transition.present(EHIScreenWebBrowser).object(model).start(nil);
}

//
// Helpers
//

- (EHIUserCredentials *)createEmeraldCredentials
{
    // generate user credentials
    EHIUserCredentials *credentials = [EHIUserCredentials modelWithDictionary:@{
#if EHIUserAutofillCredentials
           @key(credentials.identification) : self.identification ?: @"ISOBAR2",   // @"mncr40, "@"mncr13", @"user011"
           @key(credentials.password)       : self.password ?: @"isobar2",
#else
           @key(credentials.identification) : self.identification,
           @key(credentials.password)       : self.password,
#endif
           @key(credentials.remembersCredentials) : @(self.remembersCredentials),
           @key(credentials.isEmeraldCredentials) : @(YES),
     }];
    return credentials;
}

# pragma mark - Analytics

- (void)updateAnalyticsContext:(EHIAnalyticsContext *)context
{
    [super updateAnalyticsContext:context];
    
    // encode credential saving flag
    context[EHIAnalyticsUserKeepSigninKey] = @(self.remembersCredentials);
    // encode the "sign-in" dictionary
    [[EHIUserManager sharedInstance] updateAnalyticsContext:context];
}

@end
