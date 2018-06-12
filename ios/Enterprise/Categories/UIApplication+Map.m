//
//  UIApplication+Map.m
//  Enterprise
//
//  Created by mplace on 6/17/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@import MapKit;

#import "EHIThirdPartyMapFactory.h"

@implementation UIApplication (Map)

+ (void)ehi_promptDirectionsForLocation:(nullable EHILocation *)location
{
    if(!location) {
        return;
    }
    
    EHIActionSheetBuilder *actionSheet = EHIActionSheetBuilder.new
    .title(EHILocalizedString(@"get_directions_action_title", @"Would you like to be directed to Maps?", @"Title for Get Directions Action"));
    
    __weak __typeof__(self) weakSelf = self;
    actionSheet.buttonWithAction(EHILocalizedString(@"get_directions_apple_maps", @"Apple Maps", @"Open the Apple Maps"), ^{
        [weakSelf ehi_openMapsWithLocation:location];
    });
    
    actionSheet = [self appendThirdPartyMaps:actionSheet location:location];
    
    actionSheet.buttonWithAction(EHILocalizedString(@"get_directions_copy_to_clipboard_button", @"Copy Address", @"Copy Address to Clipboard button for Get Directions alert"), ^{
        [[UIPasteboard generalPasteboard] setString:location.address.formattedAddress ?: @""];

    })
    .cancelButton(EHILocalizedString(@"get_directions_cancel_button", @"Cancel", @"Cancel button for Get Directions alert"));
    
    [actionSheet showExecutingButtonAction];
}

+ (void)ehi_openThirdPartyMap:(nullable id<EHIThirdPartyMapProtocol>)thirdPartMap withLocation:(nullable EHILocation *)location
{
    if (!thirdPartMap && !location) {
        return;
    }
    
    [self ehi_openURL:[thirdPartMap urlWithCoordinate:location.position]];
}

+ (void)ehi_openMapsWithLocation:(nullable EHILocation *)location
{
    if(!location) {
        return;
    }
    
    [[self mapItemForLocation:location] openInMapsWithLaunchOptions:nil];
}

//
// Helper
//

+ (MKMapItem *)mapItemForLocation:(EHILocation *)location
{
    // generate a placemark for the lat/lon
    CLLocationCoordinate2D coordinate = location.position.coordinate;
    MKPlacemark *placemark = [[MKPlacemark alloc] initWithCoordinate:coordinate addressDictionary:nil];
    
    // and create a map item from it
    MKMapItem *destination = [[MKMapItem alloc] initWithPlacemark:placemark];
    destination.name = location.displayName;
    
    return destination;
}

+ (BOOL)supportsThirdPartyMap:(id<EHIThirdPartyMapProtocol>)map
{
    NSURL *mapURLScheme = [NSURL URLWithString:[map scheme]];
    return [[self sharedApplication] canOpenURL:mapURLScheme];
}

+ (EHIActionSheetBuilder *)appendThirdPartyMaps:(EHIActionSheetBuilder *)actionSheet location:(EHILocation *)location
{
    id<EHIThirdPartyMapProtocol> googleMaps = [EHIThirdPartyMapFactory thirdPartyMapWithType:EHIThirdPartyMapTypeGoogleMaps];
    id<EHIThirdPartyMapProtocol> waze = [EHIThirdPartyMapFactory thirdPartyMapWithType:EHIThirdPartyMapTypeWaze];
 
    __weak __typeof__(self) weakSelf = self;
    if ([self supportsThirdPartyMap:googleMaps]) {
        actionSheet.buttonWithAction(googleMaps.name, ^{
            [weakSelf ehi_openThirdPartyMap:googleMaps withLocation:location];
        });
    }
    
    if ([self supportsThirdPartyMap:waze]) {
        actionSheet.buttonWithAction(waze.name, ^{
            [weakSelf ehi_openThirdPartyMap:waze withLocation:location];
        });
    }
    
    return actionSheet;
}

@end
