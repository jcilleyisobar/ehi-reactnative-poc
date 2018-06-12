//
//  EHIDashboardLayoutAttributes.m
//  Enterprise
//
//  Created by Ty Cobb on 5/29/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIDashboardLayoutAttributes.h"

@implementation EHIDashboardLayoutAttributes

- (id)copyWithZone:(NSZone *)zone
{
    EHIDashboardLayoutAttributes *copy = [super copyWithZone:zone];
    copy.stickyOffset = self.stickyOffset;
    return copy;
}

@end
