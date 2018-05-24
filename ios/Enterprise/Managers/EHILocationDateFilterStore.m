//
//  EHILocationDateFilterStore.m
//  Enterprise
//
//  Created by Rafael Ramos on 5/30/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHILocationDateFilterStore.h"

@implementation EHILocationDateFilterStore

- (void)setPickupTime:(NSDate *)pickupTime
{
    if(pickupTime != nil) {
        _pickupTime = pickupTime;
    }
}

- (void)setReturnTime:(NSDate *)returnTime
{
    if(returnTime != nil) {
        _returnTime = returnTime;
    }
}

- (void)clearReturnTime
{
    _returnTime = nil;
}

- (void)clearPickupTime
{
    _pickupTime = nil;
}

@end
