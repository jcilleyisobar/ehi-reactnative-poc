//
//  EHIReviewAdditionalInfoItemViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 8/2/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIContractAdditionalInfo.h"

@interface EHIReviewAdditionalInfoItemViewModel : EHIViewModel <MTRReactive>

- (instancetype)initWithAdditionalInfo:(EHIContractAdditionalInfo *)info;

@property (copy, nonatomic, readonly) NSAttributedString *title;
@property (copy, nonatomic, readonly) NSAttributedString *value;
@end
