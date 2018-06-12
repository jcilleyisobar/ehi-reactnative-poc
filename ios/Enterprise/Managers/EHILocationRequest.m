//
//  EHILocationRequest.m
//  Enterprise
//
//  Created by Ty Cobb on 4/15/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILocationRequest.h"

@implementation EHILocationRequest

- (instancetype)initWithAvailabilityFlag:(BOOL)isAvailabilityRequest handler:(id)handler
{
    if(self = [super init]) {
        _identifier = [NSUUID UUID];
        _handler = [handler copy];
        _isAvailabilityRequest = isAvailabilityRequest;
    }
    
    return self;
}

@end
