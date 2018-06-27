//
//  EHIConfirmationContractCancelModalViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 5/20/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIInfoModalViewModel.h"
#import "EHIContractNotificationViewModel.h"
#import "EHIContractDetails.h"

@interface EHIConfirmationContractCancelModalViewModel : EHIInfoModalViewModel <MTRReactive>
@property (copy  , nonatomic) NSString *subtitle;
@property (strong, nonatomic) EHIContractNotificationViewModel *contractModel;

- (instancetype)initWithContract:(EHIContractDetails *)contract;

@end
