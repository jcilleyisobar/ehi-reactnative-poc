//
//  EHIThirdPartyMapWaze.m
//  Enterprise
//
//  Created by Rafael Ramos on 1/8/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIThirdPartyMapWaze.h"

@implementation EHIThirdPartyMapWaze

- (NSString *)name
{
    return EHILocalizedString(@"get_directions_waze", @"Waze", @"Open Waze, if installed, button title for Get Directions alert");
}

- (NSString *)scheme
{
    return @"waze://";
}

/** ?ll=<lat>,<lon>&navigate=yes
 https://www.waze.com/about/dev
 **/
- (NSString *)coordinatesPlaceholder
{
    return @"?ll=%f,%f&navigate=yes";
}

- (NSURL *)urlWithCoordinate:(EHILocationCoordinate *)coordinate
{
    NSString *coordinatesURL = [NSString stringWithFormat:[self coordinatesPlaceholder], coordinate.latitude, coordinate.longitude];
    NSString *mapURL = [[self scheme] stringByAppendingString:coordinatesURL];
    
    return [NSURL URLWithString:mapURL];
}
@end
