//
//  EHIClassSelectDiscountViewModel.m
//  Enterprise
//
//  Created by Ty Cobb on 5/1/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIClassSelectDiscountViewModel.h"
#import "EHIContractDetails.h"

@implementation EHIClassSelectDiscountViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _discountModel = [[EHIContractDiscountViewModel alloc] initWithFlow:EHIContractDiscoutFlowCarClassSelect];
    }
    
    return self;
}

- (void)updateWithModel:(EHIContractDetails *)model
{
    [super updateWithModel:model];

    if([model isKindOfClass:[EHIContractDetails class]]) {
        [self.discountModel updateWithModel:model];
    }
}

@end
