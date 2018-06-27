//
//  EHIProfileAuthenticateViewModel.m
//  Enterprise
//
//  Created by fhu on 5/14/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIProfileAuthenticationViewModel.h"
#import "EHIUserManager.h"
#import "EHISigninRecoveryType.h"
#import "EHIViewModel_Subclass.h"
#import "EHISigninFieldModel.h"
#import "EHIConfiguration.h"

@implementation EHIProfileAuthenticationViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _passwordFieldModel = [EHISigninFieldModel enterprisePlusFields][EHISignInFieldModelTypePassword];
        
        _titleText          = EHILocalizedString(@"rewards_provide_password_header", @"We care about keeping your info safe.", @"");
        _subtitleText       = EHILocalizedString(@"rewards_provide_password_label", @"Please verify your password to view your profile.", @"");
        _forgotPasswordText = EHILocalizedString(@"login_password_fogot_title", @"FORGOT PASSWORD", @"Title for login forgot password button");
        _continueButtonText = EHILocalizedString(@"rewards_continue_button_label", @"CONTINUE", @"");
        _cancelButtonText   = EHILocalizedString(@"standard_button_cancel", @"CANCEL", @"").uppercaseString;
    }
    
    return self;
}

# pragma mark - Actions

- (void)authenticateWithHandler:(void (^)(NSError *))handler
{
    self.isLoading = YES;
    
    EHIUserCredentials *credentials = [EHIUserCredentials modelWithDictionary:@{
        @key(credentials.identification)        : [EHIUser currentUser].profiles.basic.loyalty.number ?: @"",
        @key(credentials.password)              : self.password ?: @"",
        @key(credentials.remembersCredentials)  : @([EHIUserManager sharedInstance].credentials.remembersCredentials),
    }];
    
    [[EHIUserManager sharedInstance] authenticateUserWithCredentials:credentials handler:^(EHIUser *user, EHIServicesError *error) {
        self.isLoading = NO;
        ehi_call(handler)(error.internalError);
    }];
}


// native implementation will only work with cashew release so this has to wait until 1.7.
- (void)forgotPassword
{
    NSURL *passwordRecoveryUrl = [EHIConfiguration configuration].forgotPasswordUrl ? [[NSURL alloc] initWithString:[EHIConfiguration configuration].forgotPasswordUrl] : nil;
    self.router.transition
        .present(EHIScreenWebBrowser).object(passwordRecoveryUrl).start(nil);
}

- (void)cancel
{
    self.router.transition.root(EHIScreenDashboard).start(nil);
}

# pragma mark - Setters

- (void)setPassword:(NSString *)password
{
    _password = password.length ? password : nil;
}

# pragma mark - Accessors

- (BOOL)hasValidCredentials
{
    return self.password != nil;
}

@end
