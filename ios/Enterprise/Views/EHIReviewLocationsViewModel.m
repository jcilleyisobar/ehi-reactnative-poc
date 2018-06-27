//
//  EHIReviewLocationsViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 4/2/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReviewLocationsViewModel.h"
#import "EHIReservationBuilder.h"
#import "EHIViewModel_Subclass.h"
#import "EHILocation.h"
#import "EHIPickupLocationLockedModalViewModel.h"

@interface EHIReviewLocationsViewModel () <EHIReservationBuilderReadinessListener>
@property (nonatomic, readonly) EHIReservationBuilder *builder;
@end

@implementation EHIReviewLocationsViewModel

- (void)updateWithModel:(id)model
{
    [super updateWithModel:model];
    
    // did become active (which registers our reactions) doesn't fire until after dynamic sizing takes place
    [self invalidateRentalDuration:nil];
    [self invalidatePickupHeader:nil];
}

- (void)didBecomeActive
{
    [super didBecomeActive];
   
    [self.builder waitForReadiness:self];
}

# pragma mark - Reactions

- (void)builderIsReady:(EHIReservationBuilder *)builder
{
    [MTRReactor autorun:self action:@selector(invalidateRentalDuration:)];
    [MTRReactor autorun:self action:@selector(invalidatePickupHeader:)];
}

- (void)invalidatePickupHeader:(MTRComputation *)computation
{
    BOOL isOneWay = self.builder.isOneWayReservation;
    
    self.pickupSectionTitle = isOneWay
        ? EHILocalizedString(@"reservation_location_selection_pickup_header_title", @"PICK-UP LOCATION", @"header title for a section that allows user to select pickup location")
        : EHILocalizedString(@"reservation_location_selection_pickup_header_fallback_title", @"LOCATION", @"fallback header title for a section that allows user to select pickup location");

    self.returnSectionTitle = EHILocalizedString(@"reservation_location_selection_return_header_title", @"RETURN LOCATION", @"header title for a section that allows user to select return location");
}

- (void)invalidateRentalDuration:(MTRComputation *)computation
{
    EHILocation *pickupLocation = self.builder.pickupLocation;
    self.pickupTitle = pickupLocation.displayName;
    self.pickupIconImageName = [self imageNameForLocationType:pickupLocation.type];
    
    EHILocation *returnLocation = self.builder.returnLocation;
    self.returnTitle = returnLocation.displayName;
    self.returnIconImageName = [self imageNameForLocationType:returnLocation.type];
    
    self.showsReturn        = self.builder.isOneWayReservation;
    self.shouldHideLockIcon = self.builder.canModifyLocation;
}

//
// Helpers
//
- (NSString *)imageNameForLocationType:(EHILocationType)type
{
    switch (type) {
        case EHILocationTypeAirport:
            return @"icon_airport_gray";
        default:
            return nil;
    }
}

# pragma mark - Actions

- (void)selectPickupLocation
{
    [self editLocationIfAllowed];
}

- (void)selectReturnLocation
{
    [self editLocationIfAllowed];
}

- (void)editLocationIfAllowed
{
    if(self.canChangePickupLocation) {
        [self.builder editInfoForReservationStep:EHIReservationStepLocation];
    } else {
        [self showLockModal];
    }
}

- (void)showLockModal
{
    [[EHIPickupLocationLockedModalViewModel new] present];
}

# pragma mark - Passthrough

- (EHIReservationBuilder *)builder
{
    return [EHIReservationBuilder sharedInstance];
}

//
// Helpers
//

- (BOOL)canChangePickupLocation
{
    return self.builder.canModifyLocation;
}


@end
