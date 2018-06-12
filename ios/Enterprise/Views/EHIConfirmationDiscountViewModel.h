//
//  EHIConfirmationDiscountViewModel.h
//  Enterprise
//
//  Created by fhu on 6/8/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIContractDiscountViewModel.h"

@interface EHIConfirmationDiscountViewModel : EHIViewModel <MTRReactive>
@property (strong, nonatomic, readonly) EHIContractDiscountViewModel *discountModel;
@end
