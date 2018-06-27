//
//  EHILocationDetailsAfterHoursModalViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 6/12/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHILocationDetailsAfterHoursModalViewModel.h"

@implementation EHILocationDetailsAfterHoursModalViewModel

- (NSString *)title
{
    return EHILocalizedString(@"after_hours_return_modal_title", @"After hours return", @"");
}

- (NSString *)details
{
    return EHILocalizedString(@"after_hours_return_modal_details", @"Allows you to return your rental even after operating hours.", @"");
}

- (NSString *)secondButtonTitle
{
    return EHILocalizedString(@"modal_default_confirmation_title", @"CLOSE", @"");
}

- (BOOL)hidesActionButton
{
    return YES;
}

- (BOOL)hidesCloseButton
{
    return YES;
}

- (BOOL)needsAutoDismiss
{
    return YES;
}

@end
