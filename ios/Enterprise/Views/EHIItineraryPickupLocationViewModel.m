//
//  EHIItineraryPickupLocationViewModel.m
//  Enterprise
//
//  Created by mplace on 3/4/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIItineraryPickupLocationViewModel.h"
#import "EHIViewModel_Subclass.h"
#import "EHIReservationBuilder.h"
#import "EHIPlaceholder.h"
#import "EHILocation.h"
#import "EHIPickupLocationLockedModalViewModel.h"
#import "EHILocationIconProvider.h"

@interface EHIItineraryPickupLocationViewModel () <EHIReservationBuilderReadinessListener>
@property (nonatomic, readonly) EHIReservationBuilder *builder;
@end

@implementation EHIItineraryPickupLocationViewModel

- (void)didBecomeActive
{
    [super didBecomeActive];

    [self.builder waitForReadiness:self];
}

# pragma mark - Reactions

- (void)builderIsReady:(EHIReservationBuilder *)builder
{
    [MTRReactor autorun:self action:@selector(updatePickupLocation:)];
}

- (void)updatePickupLocation:(MTRComputation *)computation
{
    self.title              = self.builder.pickupLocation.displayName;
    self.shouldHideIcon     = self.iconImageName == nil;
    self.shouldHideLockIcon = self.canChangePickupLocation;
}

# pragma mark - Actions

- (void)searchForPickupLocation
{
    if(self.canChangePickupLocation) {
        // let the builder know what kind of search we are about to perform
        self.builder.searchTypeOverride = EHILocationsSearchTypePickup;
        
        // navigate to the locations search screen
        self.router.transition
        .push(EHIScreenLocations).start(nil);
    } else {
        [self showLockModal];
    }
}

- (void)showLockModal
{
    [EHIAnalytics trackAction:EHIAnalyticsLocationLockedModalActionLock handler:nil];
    
    [[EHIPickupLocationLockedModalViewModel new] present];
}

# pragma mark - Passthrough

- (EHIReservationBuilder *)builder
{
    return [EHIReservationBuilder sharedInstance];
}

# pragma mark - Accessors

- (NSString *)iconImageName
{
    return [EHILocationIconProvider iconForLocation:self.builder.pickupLocation];
}

//
// Helpers
//

- (BOOL)canChangePickupLocation
{
    return self.builder.canModifyLocation;
}

@end
