//
//  NSBundle+Utility.m
//  Enterprise
//
//  Created by Alex Koller on 6/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "NSBundle+Utility.h"

@implementation NSBundle (Utility)

+ (NSString *)versionShort
{
    return [[[self mainBundle] infoDictionary] objectForKey:@"CFBundleShortVersionString"];
}

@end
