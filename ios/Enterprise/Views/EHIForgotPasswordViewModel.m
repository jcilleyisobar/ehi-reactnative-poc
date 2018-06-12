//
//  EHIForgotPasswordViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 1/14/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIForgotPasswordViewModel.h"
#import "EHIUserProfiles.h"
#import "EHIUserManager.h"
#import "EHIToastManager.h"

@interface EHIForgotPasswordViewModel ()
@property (assign, nonatomic) BOOL isLoading;
@end

@implementation EHIForgotPasswordViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _emailAddressTitle = [EHILocalizedString(@"forgot_password_email_address_title", @"Email Address", @"")stringByAppendingString:@" *"];
        _instructionsTitle = EHILocalizedString(@"forgot_password_instructions_message", @"To reset your password, please enter your name and email address.", @"");
        _sentEmailMessage  = EHILocalizedString(@"forgot_password_email_sent_message", @"A PASSWORD RESET EMAIL WILL BE SENT TO YOU", @"");
        _firstNameTitle    = [EHILocalizedString(@"forgot_password_first_name_title", @"First Name", @"") stringByAppendingString:@" *"];
        _lastNameTitle     = [EHILocalizedString(@"forgot_password_last_name_title", @"Last Name", @"") stringByAppendingString:@" *"];
        _submitTitle       = EHILocalizedString(@"forgot_password_submit_button_text", @"SUBMIT", @"");
        _title             = EHILocalizedString(@"forgot_password_navigation_title", @"Forgot Password", @"");

        _requiredInfoModel = [EHIRequiredInfoViewModel modelForInfoType:EHIRequiredInfoTypeForgotPassword];
    }
    
    return self;
}

# pragma mark - Validation

- (BOOL)validateFieldsShowingErrors:(BOOL)showErrors
{
    BOOL hasErrorFirstName = !self.firstName.length;
    BOOL hasErrorLastName  = !self.lastName.length;
    BOOL hasErrorEmail     = !self.emailAddress.ehi_validEmail;
    if(showErrors) {
        self.hasErrorFirstName = hasErrorFirstName;
        self.hasErrorLastName  = hasErrorLastName;
        self.hasErrorEmail     = hasErrorEmail;
    }
    return hasErrorFirstName || hasErrorLastName || hasErrorEmail;
}

- (BOOL)hasErrors
{
    return [self validateFieldsShowingErrors:NO];
}

# pragma mark - Actions

- (void)submit
{
    BOOL hasErrors = [self validateFieldsShowingErrors:YES];
    if(!hasErrors) {
        [EHIAnalytics trackAction:EHIAnalyticsUserActionForgotPassword handler:nil];

        EHIUser *user = [self createUser];
        
        self.isLoading = YES;
        
        [[EHIUserManager sharedInstance] resetPassword:user handler:^(EHIServicesError *error) {
            if(!error.hasFailed) {
                [EHIToastManager showMessage:self.sentEmailMessage];
                [self dismiss];
            }
            self.isLoading = NO;
        }];
    }
}

- (void)dismiss
{
    self.router.transition.dismiss.start(nil);
}

# pragma mark - Setters

- (void)setFirstName:(NSString *)firstName
{
    _firstName = firstName;
    
    self.hasErrorFirstName = NO;
}

- (void)setLastName:(NSString *)lastName
{
    _lastName = lastName;
    
    self.hasErrorLastName = NO;
}

- (void)setEmailAddress:(NSString *)emailAddress
{
    _emailAddress = emailAddress;
    
    self.hasErrorEmail = !emailAddress.ehi_validEmail;
}

//
// Helpers
//

- (EHIUser *)createUser
{
    EHIUser *user;
    NSDictionary *userDictionary = @{
        @"profile" : @{
            @"basic_profile" :@{
                @key(user.profiles.basic.firstName)  : self.firstName ?: @"",
                @key(user.profiles.basic.lastName)   : self.lastName ?: @""
            },
        },
        @"contact_profile" : @{
            @key(user.contact.email) : self.emailAddress ?: @""
        }
     };
    
    return [EHIUser modelWithDictionary:userDictionary];
}

@end
