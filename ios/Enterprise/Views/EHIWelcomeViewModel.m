//
//  EHIWelcomeViewModel.m
//  Enterprise
//
//  Created by Pawel Bragoszewski on 09.03.2015.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIWelcomeViewModel.h"

@implementation EHIWelcomeViewModel

- (NSString *)signinTitle
{
    return EHILocalizedString(@"login_action_title", @"SIGN IN", @"");
}

- (NSString *)joinTitle
{
    return EHILocalizedString(@"enroll_join_action", @"Join", @"");
}

- (NSAttributedString *)continueTitle
{
    NSString *localizedString = EHILocalizedString(@"welcome_screen_action_continue", @"CONTINUE AS GUEST", @"");

    return [NSAttributedString
            attributedStringWithString:localizedString
            font:[UIFont ehi_fontWithStyle:EHIFontStyleBold size:16.0f]
            color:[UIColor ehi_greenColor]];
}

# pragma mark - Actions

- (void)selectSignIn
{
    [EHIAnalytics trackAction:EHIAnalyticsWelcomeActionSignin handler:nil];
}

- (void)selectSkip
{
    [EHIAnalytics trackAction:EHIAnalyticsWelcomeActionSkip handler:nil];
}

- (void)selectJoin
{
    [EHIAnalytics trackAction:EHIAnalyticsWelcomeActionJoin handler:nil];
}

@end
