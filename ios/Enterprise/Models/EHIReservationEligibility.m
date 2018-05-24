//
//  EHIReservationEligibility.m
//  Enterprise
//
//  Created by Alex Koller on 8/12/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel_Subclass.h"
#import "EHIReservationEligibility.h"

@implementation EHIReservationEligibility

+ (NSDictionary *)mappings:(EHIReservationEligibility *)model
{
    return @{
        @"create_reservation"    : @key(model.canCreate),
        @"modify_reservation"    : @key(model.canModify),
        @"cancel_reservation"    : @key(model.canCancel),
        @"view_base_reservation" : @key(model.canViewBase),
        @"view_full_reservation" : @key(model.canViewFull),
        @"blocked_reasons"       : @key(model.blockedReasons),
    };
}

@end
