//
//  EHINetworkError.m
//  Enterprise
//
//  Created by Ty Cobb on 6/2/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHINetworkError.h"

@implementation NSError (Network)

- (NSString *)message
{
    return self.localizedDescription;
}

@end
