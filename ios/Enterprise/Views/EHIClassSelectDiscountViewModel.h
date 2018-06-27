//
//  EHIClassSelectDiscountViewModel.h
//  Enterprise
//
//  Created by Ty Cobb on 5/1/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIContractDiscountViewModel.h"

@interface EHIClassSelectDiscountViewModel : EHIViewModel <MTRReactive>
@property (strong, nonatomic, readonly) EHIContractDiscountViewModel *discountModel;
@end
