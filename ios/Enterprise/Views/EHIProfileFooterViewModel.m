//
//  EHIProfileFooterViewModel.m
//  Enterprise
//
//  Created by fhu on 5/20/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIProfileFooterViewModel.h"
#import "EHIViewModel_Subclass.h"

@implementation EHIProfileFooterViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _changePasswordTitle = EHILocalizedString(@"profile_change_password_button", @"CHANGE PASSWORD", @"");
    }
    
    return self;
}

- (void)changePassword
{
    [EHIAnalytics trackAction:EHIAnalyticsProfileActionChangePassword handler:nil];
    
    self.router.transition
        .push(EHIScreenProfilePassword).start(nil);
}

@end
