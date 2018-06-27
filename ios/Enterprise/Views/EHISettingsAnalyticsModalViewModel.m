//
//  EHISettingsAnalyticsModalViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 21/05/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHISettingsAnalyticsModalViewModel.h"

@implementation EHISettingsAnalyticsModalViewModel

- (NSString *)title
{
    return EHILocalizedString(@"right_to_be_forgotten_modal_title", @"Are You Sure You Want To Clear Your National Mobile Analytics & App Usage Historical Data?", @"");
}

- (NSString *)details
{
    return EHILocalizedString(@"right_to_be_forgotten_modal_summary", @"By selecting \"Yes\" all your historical data will be cleared and no longer tracked.", @"");
}

- (NSString *)firstButtonTitle
{
    return EHILocalizedString(@"right_to_be_forgotten_modal_confirm", @"Yes", @"");
}

- (NSString *)secondButtonTitle
{
    return EHILocalizedString(@"right_to_be_forgotten_modal_cancel", @"No", @"");
}

- (BOOL)hidesCloseButton
{
    return YES;
}

- (void)present:(EHIInfoModalAction)action
{
    [super present:^BOOL(NSInteger index, BOOL canceled) {
        if(index == 0) {
            [EHIAnalytics trackAction:EHIAnalyticsSettingsActionCleanHistoricalDataYes handler:nil];
        } else {
            [EHIAnalytics trackAction:EHIAnalyticsSettingsActionCleanHistoricalDataNo handler:nil];
        }
        
        return action(index, canceled);
    }];
}

@end
