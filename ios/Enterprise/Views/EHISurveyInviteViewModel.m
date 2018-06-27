//
//  EHISurveyInviteViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 12/5/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHISurveyInviteViewModel.h"
#import "EHIViewModel_Subclass.h"

@implementation EHISurveyInviteViewModel

- (NSString *)title
{
    return EHILocalizedString(@"survey_invite_title", @"We want to make the app better", @"");
}

- (NSString *)details
{
    return EHILocalizedString(@"survey_invite_subtitle", @"Have ideas about how we can improve our app? Let us know.", @"");
}

- (NSString *)firstButtonTitle
{
    return EHILocalizedString(@"survey_invite_accept_button_title", @"YES, I'LL HELP", @"");
}

- (NSString *)secondButtonTitle
{
    return EHILocalizedString(@"survey_invite_decline_button_title", @"NO, THANKS", @"");
}

- (void)didBecomeActive
{
    [EHIAnalytics changeScreen:EHIScreenSurveyInvite state:EHIScreenSurveyInvite];
    [EHIAnalytics trackState:nil];
}

- (void)present:(EHIInfoModalAction)action
{
    [super present:^BOOL(NSInteger index, BOOL canceled) {
        return action(index, canceled);
    }];
}

@end
