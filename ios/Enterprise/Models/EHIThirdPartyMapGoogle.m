//
//  EHIThirdPartyMapGoogle.m
//  Enterprise
//
//  Created by Rafael Ramos on 1/8/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIThirdPartyMapGoogle.h"

@implementation EHIThirdPartyMapGoogle

- (NSString *)name
{
    return EHILocalizedString(@"get_directions_google_maps", @"Google Maps", @"Open Google Maps, if installed, button title for Get Directions alert");
}

- (NSString *)scheme
{
    return @"comgooglemaps://";
}

/** center: This is the map viewport center point. Formatted as a comma separated string of latitude,longitude
 https://developers.google.com/maps/documentation/ios-sdk/urlscheme
 **/
- (NSString *)coordinatesPlaceholder
{
    return @"?center=%f,%f&zoom=14&views=traffic";
}

- (NSURL *)urlWithCoordinate:(EHILocationCoordinate *)coordinate
{
    NSString *coordinatesURL = [NSString stringWithFormat:[self coordinatesPlaceholder], coordinate.latitude, coordinate.longitude];
    NSString *mapURL = [[self scheme] stringByAppendingString:coordinatesURL];
    
    return [NSURL URLWithString:mapURL];
}

@end
