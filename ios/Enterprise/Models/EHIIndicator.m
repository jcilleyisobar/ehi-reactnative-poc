//
//  EHIIndicator.m
//  Enterprise
//
//  Created by mplace on 7/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIIndicator.h"
#import "EHIModel_Subclass.h"

@implementation EHIIndicator

+ (void)registerTransformers:(EHIIndicator *)model
{
    [super registerTransformers:model];
    
    [self key:@key(model.code) registerMap:@{
        @"TRN" : @(EHIIndicatorCodeRail),
        @"POC" : @(EHIIndicatorCodePortOfCall),
        @"MTR" : @(EHIIndicatorCodeMotorcycle),
        @"ECX" : @(EHIIndicatorCodeExotics),
    } defaultValue:@(EHIIndicatorCodeUnknown)];
}

@end
