//
//  EHIFormFieldAdditionalInfoViewModel.h
//  Enterprise
//
//  Created by fhu on 6/9/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIFormFieldViewModel.h"
#import "EHIContractAdditionalInfo.h"

@interface EHIFormFieldViewModel (Generator)

+ (EHIFormFieldViewModel *)viewModelForCorporateCodeInfo:(EHIContractAdditionalInfo *)info value:(NSString *)value;

@end