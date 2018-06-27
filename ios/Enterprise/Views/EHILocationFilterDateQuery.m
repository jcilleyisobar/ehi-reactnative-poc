//
//  EHILocationFilterDateQuery.m
//  Enterprise
//
//  Created by Rafael Ramos on 5/12/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHILocationFilterDateQuery.h"

@implementation EHILocationFilterDateQuery

- (BOOL)hasOnlyReturn
{
    return self.pickupTime == nil
        && self.pickupTime == nil
        && (self.returnDate != nil || self.pickupTime != nil);
}

@end
