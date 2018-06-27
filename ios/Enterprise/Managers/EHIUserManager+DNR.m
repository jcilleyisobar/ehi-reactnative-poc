//
//  EHIUserManager+DNR.m
//  Enterprise
//
//  Created by Ty Cobb on 7/24/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIUserManager+DNR.h"
#import "EHIInfoModalViewModel.h"
#import "EHIConfiguration.h"

typedef NS_ENUM(NSUInteger, EHIDnrModalType) {
    EHIDnrModalTypeContinue,
    EHIDnrModalTypeReturn
};

@implementation EHIUserManager (DNR)

+ (void)attemptToShowContinueDnrModalWithHandler:(void (^)(BOOL shouldContinue))handler
{
    [[self sharedInstance] attemptToShowDnrModalForError:nil type:EHIDnrModalTypeContinue handler:handler];
}

+ (void)attemptToShowReturnDnrModalWithHandler:(void (^)(BOOL))handler
{
    [[self sharedInstance] attemptToShowDnrModalForError:nil type:EHIDnrModalTypeReturn handler:handler];
}

- (void)attemptToShowDnrModalForError:(EHIServicesError *)error type:(EHIDnrModalType)type handler:(void(^)(BOOL shouldContinue))handler
{
    // if we errored or the user isn't on dnr, it's not necessary to show the modal
    if(error || !self.currentUser.isOnDnrList) {
        ehi_call(handler)(YES);
        return;
    }
    
    // otherwise, show the dnr modal
    EHIInfoModalViewModel *model = [EHIInfoModalViewModel new];
    model.headerNibName     = @"EHIDnrWarning";
    model.firstButtonTitle  = EHILocalizedString(@"info_modal_contactus_button", @"CONTACT US", @"");
    model.secondButtonTitle = type == EHIDnrModalTypeContinue
    ? EHILocalizedString(@"info_modal_continue_button", @"CONTINUE", @"")
    : EHILocalizedString(@"info_modal_return_button", @"RETURN", @"");
    
    // capture the model weakly because we want custom dismiss behavior in the action
    __weak typeof(model) wodel = model;
    
    // run the handler once the modal is dismissed
    [model present:^BOOL(NSInteger index, BOOL canceled) {
        if(index == 0) {
            [UIApplication ehi_promptPhoneCall:[EHIConfiguration configuration].dnrNumber.number];
        }
        
        [wodel dismissWithCompletion:^{
            dispatch_async(dispatch_get_main_queue(), ^{
                BOOL shouldContinue = type == EHIDnrModalTypeContinue && index == 1;
                ehi_call(handler)(shouldContinue);
            });
        }];
        
        return NO;
    }];
}

@end
