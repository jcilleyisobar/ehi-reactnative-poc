//
//  EHIShortcutManager.m
//  Enterprise
//
//  Created by Alex Koller on 9/30/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHIShortcutManager.h"
#import "EHITransitionManager.h"
#import "EHILocationManager.h"
#import "EHILocationDetailsViewModel.h"

@implementation EHIShortcutManager

# pragma mark - Shortcut Handling

+ (void)performActionForShortcutItem:(UIApplicationShortcutItem *)item completion:(void (^)(BOOL))completionHandler
{
    if([item.type isEqualToString:EHIShortcutTypeSearchLocationsKey]) {
        [self handleSearchShortcut];
    } else if([item.type isEqualToString:EHIShortcutTypeNearbyLocationsKey]) {
        [self handleNearbyShortcut];
    } else if ([item.type isEqualToString:EHIShortcutTypeLocationDetailsKey]) {
        [self handleLocationShortcut:item];
    } else if ([item.type isEqualToString:EHIShortcutTypeRentalDetailsKey]) {
        [self handleRentalShortcut:item];
    }
    
    completionHandler(YES);
}

+ (void)handleSearchShortcut
{
    [EHITransitionManager transitionToScreen:EHIScreenLocations asModal:NO];
}

+ (void)handleNearbyShortcut
{
    EHIUserLocation *model = [EHILocationManager sharedInstance].userLocation;
    
    [EHITransitionManager transitionToScreen:EHIScreenLocationsMap object:model asModal:NO];
}

+ (void)handleLocationShortcut:(UIApplicationShortcutItem *)item
{
    EHILocation *model = [EHILocation modelWithDictionary:item.userInfo];
    EHILocationDetailsViewModel *viewModel = [[EHILocationDetailsViewModel alloc] initWithModel:model];
    
    [EHITransitionManager transitionToScreen:EHIScreenLocationDetails object:viewModel asModal:NO];
}

+ (void)handleRentalShortcut:(UIApplicationShortcutItem *)item
{
    EHIUserRental *model = [EHIUserRental modelWithDictionary:item.userInfo];
    
    [EHITransitionManager transitionToScreen:EHIScreenReservation object:model asModal:YES];
}

@end
