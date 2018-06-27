//
//  EHIEnrollmentConfirmationViewModel.m
//  Enterprise
//
//  Created by Rafael Machado on 8/11/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIEnrollmentConfirmationViewModel.h"
#import "EHIUserManager.h"
#import "EHIRewardsLearnMoreViewModel.h"

@interface EHIEnrollmentConfirmationViewModel ()
@property (copy  , nonatomic) NSString *username;
@property (copy  , nonatomic) NSString *password;
@end

@implementation EHIEnrollmentConfirmationViewModel

+ (instancetype)initWithUsername:(NSString *)username password:(NSString *)password
{
    EHIEnrollmentConfirmationViewModel *model = [EHIEnrollmentConfirmationViewModel new];
    model.username = username;
    model.password = password;
    model.benefitsViewModel = [[EHIBenefitsViewModel alloc] initWithTitle:model.summaryTitle description:model.memberNumber];
    [model performLogin];
    
    return model;
}

- (instancetype)init
{
    if(self = [super init]) {
        _continueTitle = EHILocalizedString(@"enroll_confirmation_continue_action", @"CONTINUE", @"");
    }
    
    return self;
}

- (void)performLogin
{
    [EHIAnalytics trackState:^(EHIAnalyticsContext *state) {
        state.macroEvent = EHIAnalyticsMacroEventEnrollmentComplete;
    }];
    
    NSString *username = self.username;
    NSString *password = self.password;
    BOOL remembersCredentials = [NSLocale ehi_shouldCheckRememberMeByDefault];
    if(username && password) {
        
        EHIUserCredentials *credentials = [EHIUserCredentials modelWithDictionary:@{
            @key(credentials.identification)       : username,
            @key(credentials.password)             : password,
            @key(credentials.remembersCredentials) : @(remembersCredentials),
        }];
        
        [[EHIUserManager sharedInstance] authenticateUserWithCredentials:credentials handler:^(EHIUser *user, EHIServicesError *error){
            [self reset];
            // hide any login errors
            [error consume];
        }];
    }
}

//
// Helpers
//

- (NSAttributedString *)summaryTitle
{
    NSString *title = EHILocalizedString(@"enroll_confirmation_title", @"Welcome to Enterprise Plus!", @"");
    
    return EHIAttributedStringBuilder.new.fontStyle(EHIFontStyleBold, 20.0).lineSpacing(4).text(title).string;
}

- (NSAttributedString *)memberNumber
{
    EHIAttributedStringBuilder *builder = EHIAttributedStringBuilder.new;
    
    NSString *memberTitle = EHILocalizedString(@"enroll_confirmation_member_number", @"Here's your Member Number: #{number}", @"");
    
    NSAttributedString *attributedMemberNumber = EHIAttributedStringBuilder.new.fontStyle(EHIFontStyleLight, 18.0).appendText(self.username ?: @"").string;
    
    return builder.appendText(memberTitle).fontStyle(EHIFontStyleLight, 18.0).replace(@"#{number}", attributedMemberNumber).string;
}

- (NSString *)intro
{
    return EHILocalizedString(@"enroll_confirmation_intro_text", @"Now you can enjoy all the special app features for our Enterprise Plus members:", @"");
}


- (NSString *)bulletOne
{
    return EHILocalizedString(@"enroll_confirmation_bullet_one", @"Manage all your reservations in one place", @"");
}

- (NSString *)bulletTwo
{
    return EHILocalizedString(@"enroll_confirmation_bullet_two", @"Set pick-up and return reminders", @"");
}

- (NSString *)bulletThree
{
    return EHILocalizedString(@"enroll_confirmation_bullet_three", @"Earn points for your rentals", @"");
}

- (NSString *)bulletFour
{
    return EHILocalizedString(@"enroll_confirmation_bullet_four", @"Redeem points towards free days", @"");
}

- (NSString *)learnMore
{
    return EHILocalizedString(@"enroll_confirmation_learn_more", @"Want to learn more?", @"");
}

- (NSString *)learnMoreButton
{
    return EHILocalizedString(@"enroll_confirmation_view_eplus_benefits", @"View all Enterprise Plus benefits", @"");
}

# pragma mark - Actions

- (void)close
{
    [EHIAnalytics trackAction:EHIAnalyticsEnrollmentContinue type:EHIAnalyticsActionTypeTap handler:nil];
    
    if(self.handler) {
        ehi_call(self.handler)();
    } else if(self.signinFlow) {
        self.router.transition.dismiss.start(nil);
    } else {
        self.router.transition.pop(self.stackPop).start(nil);
    }
}

- (void)showBenefits
{
    [EHIAnalytics trackAction:EHIAnalyticsEnrollmentLearnMore type:EHIAnalyticsActionTypeTap handler:nil];
    
    EHIRewardsLearnMoreViewModel *model = [EHIRewardsLearnMoreViewModel new];
    model.layout = EHIRewardsLearnMoreLayoutEnroll;
    self.router.transition.push(EHIScreenAboutEnterprisePlus).object(@YES).start(nil);
}

@end
