//
//  EHIFormattedPhone.m
//  Enterprise
//
//  Created by mplace on 8/31/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIFormattedPhone.h"

@implementation EHIFormattedPhone

+ (instancetype)modelWithPhone:(NSString *)phone
{
    EHIFormattedPhone *model = [EHIFormattedPhone new];
    model.originalPhone = phone;
    
    return model;
}

@end
