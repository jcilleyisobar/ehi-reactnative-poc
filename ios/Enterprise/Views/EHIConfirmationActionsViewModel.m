//
//  EHIConfirmationActionsViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 4/21/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIConfirmationActionsViewModel.h"

@interface EHIConfirmationActionsViewModel ()
@property (assign, nonatomic) BOOL isLoadingModify;
@property (assign, nonatomic) BOOL isLoadingCancel;
@end

@implementation EHIConfirmationActionsViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _returnHomeTitle = EHILocalizedString(@"reservation_confirmation_return_home_action_title", @"RETURN TO HOME", @"");
        _modifyTitle     = EHILocalizedString(@"reservation_confirmation_modify_title", @"MODIFY RESERVATION", @"Title for the confirmation screen modify button");
        _cancelReservationTitle = EHILocalizedString(@"reservation_confirmation_cancel_reservation_action_title", @"CANCEL RESERVATION", @"");
    }
    
    return self;
}

# pragma mark - Actions

- (void)returnToDashboard;
{
    [EHIAnalytics trackAction:EHIAnalyticsResActionReturnHome handler:nil];
}

@end
