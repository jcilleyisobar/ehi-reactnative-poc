//
//  EHISigninRouterManager.m
//  Enterprise
//
//  Created by Rafael Machado on 8/6/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHISigninRouterManager.h"

@implementation EHISigninRouterManager

+ (instancetype)sharedInstance
{
    static dispatch_once_t once;
    static EHISigninRouterManager *sharedInstance;
    
    dispatch_once(&once, ^{
        sharedInstance = [EHISigninRouterManager new];
    });
    
    return sharedInstance;
}

@end
