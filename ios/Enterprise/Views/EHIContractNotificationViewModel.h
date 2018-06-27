//
//  EHIContractNotificationViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 5/19/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIContractDetails.h"

typedef NS_ENUM(NSInteger, EHIContractNotificationFlow) {
    EHIContractNotificationFlowDefault,
    EHIContractNotificationFlowModify,
    EHIContractNotificationFlowCancel,
};

@interface EHIContractNotificationViewModel : EHIViewModel <MTRReactive>
@property (copy  , nonatomic, readonly) NSString *title;
@property (strong, nonatomic, readonly) UIColor *backgroundColor;

- (instancetype)initWithContract:(EHIContractDetails *)contract flow:(EHIContractNotificationFlow)flow;

@end
