//
//  EHILocationCoordinate.m
//  Enterprise
//
//  Created by Ty Cobb on 1/20/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILocationCoordinate.h"
#import "EHIModel_Subclass.h"

@implementation EHILocationCoordinate

# pragma mark - Accessors

- (CLLocationCoordinate2D)coordinate
{
    return (CLLocationCoordinate2D){
        .latitude = self.latitude,
        .longitude = self.longitude
    };
}

# pragma mark - EHIModel

+ (NSDictionary *)mappings:(EHILocationCoordinate *)model
{
    return @{
        @"latitude_coordinate"  : @key(model.latitude),
        @"longitude_coordinate" : @key(model.longitude),
    };
}

@end
