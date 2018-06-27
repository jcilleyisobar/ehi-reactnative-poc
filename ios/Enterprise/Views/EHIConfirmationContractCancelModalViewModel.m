//
//  EHIConfirmationContractCancelModalViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 5/20/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIConfirmationContractCancelModalViewModel.h"

@implementation EHIConfirmationContractCancelModalViewModel

- (instancetype)initWithContract:(EHIContractDetails *)contract
{
    if(self = [super init]) {
        _contractModel = [[EHIContractNotificationViewModel alloc] initWithContract:contract flow:EHIContractNotificationFlowCancel];
    }
    
    return self;
}

# pragma mark - Info Modal

- (NSString *)detailsNibName
{
    return @"EHIConfirmationContractCancelModal";
}

- (NSString *)firstButtonTitle
{
    return EHILocalizedString(@"confirmation_cancel_contract_yes_button_title", @"YES, CANCEL", @"");
}

- (NSString *)secondButtonTitle
{
    return EHILocalizedString(@"confirmation_cancel_contract_no_button_title", @"NO",@"");
}

- (NSString *)title
{
    return EHILocalizedString(@"confirmation_cancel_contract_title", @"Cancel Reservation", @"");
}

- (NSString *)subtitle
{
    return EHILocalizedString(@"confirmation_cancel_contract_subtitle", @"Do you want to cancel this reservation?", @"");
}


@end
