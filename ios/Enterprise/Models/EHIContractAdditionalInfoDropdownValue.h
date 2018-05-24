//
//  EHICorporateAccountInfoDropdownValue.h
//  Enterprise
//
//  Created by fhu on 6/12/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"

@interface EHIContractAdditionalInfoDropdownValue : EHIModel
@property (assign, nonatomic, readonly) NSInteger value;
@property (copy  , nonatomic, readonly) NSString *displayText;
@end

EHIAnnotatable(EHIContractAdditionalInfoDropdownValue);