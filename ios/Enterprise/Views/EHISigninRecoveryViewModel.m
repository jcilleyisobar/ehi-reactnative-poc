//
//  EHISigninRecoveryViewModel.m
//  Enterprise
//
//  Created by Michael Place on 5/7/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHISigninRecoveryViewModel.h"
#import "EHIViewModel_Subclass.h"
#import "EHIConfiguration.h"

@interface EHISigninRecoveryViewModel ()
@property (assign, nonatomic) EHISigninRecoveryType type;
@end

@implementation EHISigninRecoveryViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _cancelButtonTitle = EHILocalizedString(@"signin_member_info_recovery_cancel_button_title", @"CANCEL", @"cancel button title for the member info recovery modal dialogue");
    }
    
    return self;
}

- (void)updateWithModel:(NSNumber *)model
{
    [super updateWithModel:model];
    
    self.type = model.integerValue;
}

# pragma mark - Type

- (void)setType:(EHISigninRecoveryType)type
{
    _type = type;
    
    self.title   = [self titleForType:self.type];
    self.details = [self attributedDetailsForType:self.type];
    self.actionButtonTitle = [self actionButtonTitleForType:self.type];
}

- (NSString *)titleForType:(EHISigninRecoveryType)type
{
    switch(type) {
        case EHISigninRecoveryTypeUsername:
            return EHILocalizedString(@"signin_member_number_recovery_modal_title", @"Forgot Member Number?", @"");
        case EHISigninRecoveryTypePartialEnrollment:
            return EHILocalizedString(@"signin_partial_enrollment_recovery_modal_title", @"Enrolled at branch?", @"");
        case EHISigninRecoveryTypeForgotConfirmation:
            return EHILocalizedString(@"rentals_lookup_forgot_confirmation_text", @"Forgot your confirmation number?", @"title for info modal asking for confirmation number");
        default:
            return @"";
    }
}

- (NSString *)detailsForType:(EHISigninRecoveryType)type
{
    switch(type) {
        case EHISigninRecoveryTypePartialEnrollment:
            return EHILocalizedString(@"signin_partial_enrollment_recovery_modal_details_text", @"You're almost there! In order to finish creating your account, you need to create your password at Enterprise.com.", @"");
        case EHISigninRecoveryTypeForgotConfirmation:
            return self.detailsForForgotConfirmation;
        case EHISigninRecoveryTypeUsername: {
            // just in case we don't get a contact number from config
            NSString *format = EHILocalizedString(@"signin_member_number_recovery_modal_details_text", @"If you have forgotten your member number or need personal assistance call #{phone_number} Monday through Friday, 8am to 5pm Central Time.", @"");
            return [format ehi_applyReplacementMap:@{
                @"phone_number" : self.eplusSupportNumber ?: @"",
            }];
        }
        default:
            return @"";
    }
}

- (NSString *)actionButtonTitleForType:(EHISigninRecoveryType)type
{
    switch (type) {
        case EHISigninRecoveryTypeUsername:
            return EHILocalizedString(@"signin_member_number_recovery_action_button_title", @"CONTACT US", @"");
        case EHISigninRecoveryTypeForgotConfirmation:
            return EHILocalizedString(@"signin_password_recovery_action_button_title", @"OKAY", @"");
        case EHISigninRecoveryTypePartialEnrollment:
            return EHILocalizedString(@"signin_partial_enrollment_action_button_title", @"LET'S GO", @"");
        default:
            return @"";
    }
}

- (NSAttributedString *)attributedDetailsForType:(EHISigninRecoveryType)type
{
    NSString *details = [self detailsForType:type];
    return EHIAttributedStringBuilder.new
        .lineSpacing(2.0f).fontStyle(EHIFontStyleLight, 14.f).text(details).string;
}

# pragma mark - Actions

- (void)performAction
{
    switch(self.type) {
        case EHISigninRecoveryTypeUsername:
            [self callEplusSupport]; break;
        case EHISigninRecoveryTypePartialEnrollment:
            [self redirectToActivationUrl]; break;
        case EHISigninRecoveryTypeForgotConfirmation:
            [self callConfirmationNumber]; break;
        default:
            return;
    }
}

//
// Helpers
//

- (NSString *)detailsForForgotConfirmation
{
    NSString *details = EHILocalizedString(@"info_modal_forgot_confirmation_details", @"We're here to help. If you have forgotten your confirmation number, or need further personal assistance, please call #{phone_number} Monday-Friday, between 8 a.m. and 5 p.m. Central Time", @"");
    
    return [details ehi_applyReplacementMap:@{
        @"phone_number" : self.numberForForgotConfirmation ?: @"",
    }];
}

- (NSString *)numberForForgotConfirmation
{
    return [EHIConfiguration configuration].primarySupportPhone.number;
}

- (void)callConfirmationNumber
{
    [UIApplication ehi_promptPhoneCall:self.numberForForgotConfirmation];
    [self dismiss];
}

- (void)callEplusSupport
{
    [UIApplication ehi_promptPhoneCall:self.eplusSupportNumber];
    [self dismiss];
}

- (void)redirectToActivationUrl
{
    __weak typeof(self) welf = self;
	self.router.transition.dismiss.start(^{
		dispatch_async(dispatch_get_main_queue(), ^{
			welf.router.transition.present(EHIScreenWebBrowser).object(welf.activateUrl).start(nil);
		});
	});
}

# pragma mark - Accessors

- (NSString *)eplusSupportNumber
{
    return [EHIConfiguration configuration].eplusPhone.number;
}

- (NSURL *)activateUrl
{
    return [EHIConfiguration configuration].activateUrl ? [[NSURL alloc] initWithString:[EHIConfiguration configuration].activateUrl] : nil;
}

# pragma mark - Navigation

- (void)dismiss
{
    self.router.transition.dismiss.start(nil);
}

@end
