//
//  EHIConfigurationHandler.m
//  Enterprise
//
//  Created by Ty Cobb on 6/19/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIConfigurationHandler.h"

NS_ASSUME_NONNULL_BEGIN

@implementation EHIConfigurationHandler

- (instancetype)initWithBlock:(EHIConfigurationCallback)block
{
    if(self = [super init]) {
        _block = block;
    }
    
    return self;
}

@end

NS_ASSUME_NONNULL_END
