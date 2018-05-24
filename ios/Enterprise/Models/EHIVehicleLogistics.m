//
//  EHIVehicleLogistics.m
//  Enterprise
//
//  Created by Alex Koller on 6/11/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIVehicleLogistics.h"

@implementation EHIVehicleLogistics

# pragma mark - Mappings

+ (NSDictionary *)mappings:(EHIVehicleLogistics *)model
{
    return @{
        @"delivery_info"     : @key(model.deliveryInfo),
        @"collection_info"   : @key(model.collectionInfo),
    };
}

# pragma mark - Computed

- (BOOL)hasSameDeliveryAndCollection
{
    return [self.deliveryInfo isEqual:self.collectionInfo];
}

@end
