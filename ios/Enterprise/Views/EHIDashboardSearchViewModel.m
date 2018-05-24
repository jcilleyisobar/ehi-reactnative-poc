//
//  EHIDashboardSearchViewModel.m
//  Enterprise
//
//  Created by Ty Cobb on 1/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIDashboardSearchViewModel.h"
#import "EHIUserLocation.h"

@implementation EHIDashboardSearchViewModel

- (instancetype)init
{
    if(self = [super init]) {
        _title = EHILocalizedString(@"dashboard_search_title", @"START A RESERVATION", @"Title for the dashboard search bar");
        _placeholder = EHILocalizedString(@"dashboard_search_placeholder", @"Enter a pick-up location", @"Placeholder for dashboard search bar");
    }
    
    return self;
}

# pragma mark - Actions

- (void)searchNearby
{
    // track the tap regardless of whether or not the user accepts
    [EHIAnalytics trackAction:EHIAnalyticsDashActionNearby handler:nil];
    
    // drill into the map after fetching the user location
    [[EHIUserLocation location] currentLocationWithHandler:^(CLLocation *location, NSError *error) {
        if(location) {
            // transition to the map screen with the user's location
            self.router.transition
                .push(EHIScreenLocationsMap).object(EHIUserLocation.location).start(nil);
        }
    }];
}

- (void)searchLocations
{
    [EHIAnalytics trackAction:EHIAnalyticsDashActionSearch handler:nil];
    
    // transition to location search screen
    self.router.transition
        .push(EHIScreenLocations).start(nil);
    
}

@end
