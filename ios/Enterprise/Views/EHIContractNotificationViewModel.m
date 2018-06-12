//
//  EHIContractNotificationBannerViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 5/19/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIContractNotificationViewModel.h"

@interface EHIContractNotificationViewModel ()
@property (assign, nonatomic) EHIContractNotificationFlow flow;
@end

@implementation EHIContractNotificationViewModel

- (instancetype)initWithContract:(EHIContractDetails *)contract flow:(EHIContractNotificationFlow)flow
{
    if(self = [super init]) {
        self.flow = flow;
        [self fetchContractAdministratorName:contract];
    }
    
    return self;
}

//
// Helpers
//

- (void)fetchContractAdministratorName:(EHIContractDetails *)contract
{
    NSString *title = [self notificationTitleForFlow:self.flow];
    title  = [title ehi_applyReplacementMap:@{
                @"contract_name" : contract.name ?: @"",
            }];
    _title = title;
}

- (NSString *)notificationTitleForFlow:(EHIContractNotificationFlow)flow
{
    switch (flow) {
        case EHIContractNotificationFlowDefault: {
            return EHILocalizedString(@"review_contract_notification_banner_title", @ "Upon booking, trip details will be sent to the administrator/s for #{contract_name}", @"");
        }
        case EHIContractNotificationFlowModify: {
            return EHILocalizedString(@"review_contract_notification_banner_modify_title", @"Upon modifying, tip details will be sent to the administrator/s for #{contract_name}", @"");
        }
        case EHIContractNotificationFlowCancel: {
            return EHILocalizedString(@"confirmation_cancel_contract_details", @ "Upon canceling, notification will be sent to the administrator/s for #{contract_name}", @"");
        }
    }
}

- (UIColor *)backgroundColor
{
    switch(self.flow) {
        case EHIContractNotificationFlowDefault:
        case EHIContractNotificationFlowModify:
            return [UIColor ehi_tanColor];
        case EHIContractNotificationFlowCancel:
            return [UIColor clearColor];
    }
}

@end
