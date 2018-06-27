//
//  EHIToast.m
//  Enterprise
//
//  Created by Alex Koller on 4/7/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIToast.h"

const NSTimeInterval EHIToastDurationShort = 2.5;
const NSTimeInterval EHIToastDurationLong  = 3.5;

@implementation EHIToast

- (instancetype)init
{
    if(self = [super init]) {
        _duration = EHIToastDurationShort;
    }
    
    return self;
}

@end
