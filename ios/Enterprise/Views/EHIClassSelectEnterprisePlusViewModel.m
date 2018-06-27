//
//  EHIClassSelectEnterprisePlusViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 4/10/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIClassSelectEnterprisePlusViewModel.h"

@implementation EHIClassSelectEnterprisePlusViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _title = EHILocalizedString(@"reservation_class_select_enterprise_plus_prompt", @"We've made it simple:\nDollars equal points.", @"text prompting user to learn more about enterprise plus on car class select screen");
    }
    return self;
}



@end
