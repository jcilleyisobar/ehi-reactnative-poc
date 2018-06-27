//
//  EHISigninViewModel.m
//  Enterprise
//
//  Created by Ty Cobb on 4/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHISigninViewModel.h"
#import "EHISigninFieldModel.h"
#import "EHIReservationBuilder.h"
#import "EHISigninRecoveryType.h"
#import "EHIConfiguration.h"
#import "EHIUserManager+Analytics.h"

@interface EHISigninViewModel ()
@property (copy  , nonatomic) NSString *identification;
@property (copy  , nonatomic) NSString *password;
@property (assign, nonatomic) BOOL isLoading;
@property (assign, nonatomic) BOOL hasValidCredentials;
@property (assign, nonatomic) BOOL remembersCredentials;
@end

@implementation EHISigninViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        // construct static content
        _title                   = EHILocalizedString(@"login_title", @"Sign In", @"Title for the login screen");
        _actionTitle             = EHILocalizedString(@"login_action_title", @"SIGN IN", @"Title for the login button");
		_staySignedInTitle       = EHILocalizedString(@"login_stay_signed_in_label", @"Keep me signed in", @"label for 'keep me signed in' toggle'");
        _forgotUsernameTitle     = EHILocalizedString(@"login_identification_forgot_title", @"Forgot Member Number", @"Title for login forgot identification button");
        _forgotPasswordTitle     = EHILocalizedString(@"login_password_fogot_title", @"FORGOT PASSWORD", @"Title for login forgot password button");
        _partialEnrollmentTitle  = EHILocalizedString(@"login_partial_enrollment_title", @"or need to complete your Enrollment?", @"");
        _joinNowTitle            = EHILocalizedString(@"login_join_now_title", @"JOIN ENTERPRISE PLUS", @"");
        _emeraldClubMembersTitle = [self emeraldTextForUserState];
        _hideArrowImage = [self isLoggedWithEmeraldClub];
        
		_fieldModels = [EHISigninFieldModel enterprisePlusFields];
        
        // default remember me properly
        _remembersCredentials = [NSLocale ehi_shouldCheckRememberMeByDefault];
       
        // validate to the correct initial state
        [self validateCredentials];
    }
    
    return self;
}

- (void)didBecomeActive
{
    [super didBecomeActive];
    
    [self rerunReactions];
}

- (NSString *)emeraldTextForUserState
{
    if(self.isLoggedWithEmeraldClub) {
        return EHILocalizedString(@"menu_emerald_club_sign_out", @"Remove National Emerald Club Account", @"title for Menu Sign Out");
    } else {
        return EHILocalizedString(@"login_emerald_club_members_button_title", @"Emerald Club Members", @"");
    }
}

- (void)rerunReactions
{
    self.emeraldClubMembersTitle = [self emeraldTextForUserState];
    self.hideArrowImage = [self isLoggedWithEmeraldClub];
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
    [[EHIUserManager sharedInstance] promptEmeraldClubWarningIfNecessaryWithHandler:^(BOOL shouldSignIn) {
        if(shouldSignIn) {
            self.isLoading = YES;
            
            EHIUserCredentials *credentials = [EHIUserCredentials modelWithDictionary:@{
#if EHIUserAutofillCredentials
                @key(credentials.identification) : self.identification ?: @"5DR6NMN", // 5KXTJGT // QGB5Z4K
                @key(credentials.password)       : self.password ?: @"enterprise1",
#else
                @key(credentials.identification) : self.identification,
                @key(credentials.password)       : self.password,
#endif
                @key(credentials.remembersCredentials) : @(self.remembersCredentials),
            }];
            
            [[EHIUserManager sharedInstance] authenticateUserWithCredentials:credentials handler:^(EHIUser *user, EHIServicesError *error) {
                self.isLoading = NO;
                
                if(!error.hasFailed) {

                    // track the change
                    [self invalidateAnalyticsContext];
                    [EHIAnalytics trackState:^(EHIAnalyticsContext *context) {
                        [context setRouterScreen:EHIScreenSignin];
                        [context setRouterState:EHIScreenSignin];
                        [context setState:EHIAnalyticsUserStateSuccess silent:NO];
                    }];
                    
                    // dismiss signin after root is optionally replaced underneath and we are active, otherwise we are going to mess up with the navigation stack
                    if(self.isActive) {
                        [self dismiss];
                    }
                    
                    // invoke our handler
                    ehi_call(self.signinHandler)();
                    
                    
                } else {
                    [self rerunReactions];
                }
            }];
        } else {
            [self rerunReactions];
        }
    }];
}

- (void)closeSignin
{
    [self dismiss];
    ehi_call(self.signinHandler)();
}

- (void)dismiss
{
    self.router.transition.dismiss.start(nil);
}

// native implementation will only work with cashew release so this has to wait until 1.7.
- (void)showForgotPasswordScreen
{
    self.router.transition
        .present(EHIScreenForgotPassword).start(nil);
}

- (void)showJoinNowScreen
{
    [EHIAnalytics trackAction:EHIAnalyticsDashActionJoin handler:nil];
    
    __weak typeof(self) welf = self;
    self.router.transition.push(EHIScreenEnrollmentStepOne).object(@YES).handler(^(){
        [welf dismiss];
        welf.router.transition
            .root(EHIScreenDashboard).animated(NO).start(nil);
    }).start(nil);
}

- (void)showRecoveryModalOrWebBrowserWithType:(EHISigninRecoveryType)type
{
    // track the transition to this screen
    [EHIAnalytics trackState:^(EHIAnalyticsContext *context) {
        [context setState:[self analyticsStateForRecoveryType:type] silent:NO];
    }];
    
    switch (type) {
        case EHISigninRecoveryTypePartialEnrollment:
            self.router.transition
                .present(EHIScreenWebBrowser).object(self.activateUrl).start(nil);
            break;
        default:
            self.router.transition
                .present(EHIScreenSigninRecovery).object(@(type)).start(nil);
            break;
    }
}

- (NSURL *)activateUrl
{
    return [EHIConfiguration configuration].activateUrl ? [[NSURL alloc] initWithString:[EHIConfiguration configuration].activateUrl] : nil;
}

- (void)didTapEmeraldClubMembers
{
    if(self.isLoggedWithEmeraldClub) {
        [[EHIUserManager sharedInstance] promptLogoutWithHandler:^(BOOL didLogout){
            [self rerunReactions];
        }];
    } else {
        __weak typeof (self) welf = self;
        self.router.transition.present(EHIScreenSigninEmerald).handler(^(BOOL didSignIn){
            if(didSignIn) {
                welf.router.transition.dismiss.start(nil);
                ehi_call(welf.signinHandler)();
            }
        }).start(nil);
    }
}

- (void)toggleRemembersCredentials
{
    [self setRemembersCredentials:!self.remembersCredentials];
    
    [EHIAnalytics trackAction:self.remembersCredentials ? EHIAnalyticsUserActionRememberOn : EHIAnalyticsUserActionRememberOff handler:nil];
    
    [self invalidateAnalyticsContext];
}

- (BOOL)isLoggedWithEmeraldClub
{
    return [EHIUserManager sharedInstance].isEmeraldUser;
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

- (NSString *)analyticsStateForRecoveryType:(EHISigninRecoveryType)type
{
    switch(type) {
        case EHISigninRecoveryTypeUsername:
            return EHIAnalyticsUserStateForgotEmail;
        case EHISigninRecoveryTypePartialEnrollment:
            return EHIAnalyticsUserStatePartialEnroll;
        case EHISigninRecoveryTypeForgotConfirmation:
            return EHIAnalyticsUserStateForgotConfirmation;
        case EHISignInRecoveryTypeJoinNow:
            return EHIAnalyticsUserActionJoin;
    }
}

@end
