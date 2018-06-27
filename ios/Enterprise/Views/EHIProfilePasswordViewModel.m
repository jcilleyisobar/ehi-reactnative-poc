
//
//  EHIProfilePasswordViewModel.m
//  Enterprise
//
//  Created by fhu on 5/20/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIProfilePasswordViewModel.h"
#import "EHIServices+User.h"
#import "EHIUserManager.h"
#import "EHIViewModel_Subclass.h"
#import "EHIProfilePasswordRule.h"
#import "EHIProfilePasswordRuleViewModel.h"
#import "EHIToastManager.h"

@interface  EHIProfilePasswordViewModel ()
@property (strong, nonatomic) EHIUserCredentials *credentials;
@property (copy  , nonatomic) NSArray *passwordRequirements;
@property (copy  , nonatomic) NSArray *passwordSection;
@end

@implementation EHIProfilePasswordViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _title = EHILocalizedString(@"profile_password_navigation_title", @"Change Password", @"navigation bar title for Profile password page");

        _updateAlert = EHILocalizedString(@"cp_change_your_password_banner", @"Before we can log you in, please update your password to fit our new requirements.", @"");
        _passwordsDoNotMatch = EHILocalizedString(@"cp_passwords_do_not_match", @"Passwords do not match", @"");
        
        // get password field template and make selective updates
        _passwordNewFieldModel = [EHISigninFieldModel enterprisePlusFields][EHISignInFieldModelTypePassword];
        _passwordNewFieldModel.title = [EHILocalizedString(@"profile_password_new_title", @"NEW PASSWORD", @"") stringByAppendingString:@" *"];
        
        _passwordConfirmFieldModel = [EHISigninFieldModel enterprisePlusFields][EHISignInFieldModelTypePassword];
        _passwordConfirmFieldModel.title = [EHILocalizedString(@"profile_password_confirm_title", @"CONFIRM PASSWORD", @"") stringByAppendingString:@" *"];
        _requiredInfoViewModel = [EHIRequiredInfoViewModel modelForInfoType:EHIRequiredInfoTypeProfile];
        
        [self addPasswordRequirements];
    }
    
    return self;
}

- (void)updateWithModel:(id)model
{
    [super updateWithModel:model];
    
    if ([model isKindOfClass:[EHIUserCredentials class]]) {
        _credentials = model;
        _forceUpdatePassword = YES;
    }
}

- (void)changePassword
{
    self.isLoading = YES;
    
    if (self.forceUpdatePassword) {
        self.credentials.updatedPassword = self.password;
        [[EHIUserManager sharedInstance] authenticateUserWithCredentials:self.credentials handler:^(EHIUser *user, EHIServicesError *error) {
            
            self.credentials.updatedPassword = nil;
            
            if(!error.hasFailed) {
                [self dismissWithSuccess];
            }

            self.isLoading = NO;
        }];
    } else {
        [[EHIUserManager sharedInstance] changePassword:self.password confirmation:self.passwordConfirmation handler:^(EHIServicesError *error) {
            if(!error.hasFailed) {
                [self dismissWithSuccess];
            }
            
            self.isLoading = NO;
        }];
    }
}

- (void)dismissWithSuccess
{
    NSString *successMessage = EHILocalizedString(@"cp_successfully_changed",  @"Password successfully changed", @"Notify the user that the password was successfully changed");
    [EHIToastManager showMessage:successMessage];
    self.router.transition
    .pop(1).start(nil);
}

#pragma mark - Password Requirements

- (void)addPasswordRequirements
{
    EHIProfilePasswordRule *rule1 = [EHIProfilePasswordRule eightCharactersRule];
    EHIProfilePasswordRule *rule2 = [EHIProfilePasswordRule containsLettersRules];
    EHIProfilePasswordRule *rule3 = [EHIProfilePasswordRule containsNumbersRules];
    EHIProfilePasswordRule *rule4 = [EHIProfilePasswordRule forbiddenRules];
    
    self.passwordRequirements = @[rule1, rule2, rule3, rule4];
    
    self.passwordSection = self.passwordRequirements.map(^(EHIProfilePasswordRule *rule, NSInteger index) {
        return [[EHIProfilePasswordRuleViewModel new] initWithModel:rule];
    });
}

#pragma mark - Accessors

- (BOOL)hasValidPassword
{
    return self.passwordRequirements.all(^(EHIProfilePasswordRule *rule, NSInteger index) {
        return [rule passedForPassword:self.password];
    });
}

- (BOOL)hasValidPasswordAndConfirmation
{
    return [self hasValidPassword] && [self.password isEqualToString:self.passwordConfirmation];
}

- (BOOL)shouldShowInlineError
{
    return ![self.password isEqualToString:self.passwordConfirmation]
        && self.passwordConfirmation.length >= 8
        && self.passwordConfirmation.length >= self.password.length;
}

@end
