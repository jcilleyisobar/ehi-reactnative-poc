//
//  EHIAirline.m
//  Enterprise
//
//  Created by Alex Koller on 6/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel_Subclass.h"
#import "EHIAirline.h"

@implementation EHIAirline

# pragma mark - Mappings

+ (NSDictionary *)mappings:(EHIAirline *)model
{
    return @{
        @"flight_number" : @key(model.flightNumber),
    };
}

- (BOOL)isOther
{
    return [self.code isEqualToString:EHIAirlineOtherCode];
}

- (BOOL)isWalkIn
{
    return [self.code isEqualToString:EHIAirlineWalkInCode];
}

# pragma mark - EHINetworkEncodable

- (void)encodeWithRequest:(EHINetworkRequest *)request
{
    request[@"code"] = self.code;
    request[@"flight_number"] = self.flightNumber;
}

@end
