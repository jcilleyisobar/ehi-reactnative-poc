//
//  EHIItineraryReturnLocationViewModel.m
//  Enterprise
//
//  Created by mplace on 3/11/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIItineraryReturnLocationViewModel.h"
#import "EHIPickupLocationLockedModalViewModel.h"
#import "EHILocationIconProvider.h"

@interface EHIItineraryReturnLocationViewModel () <EHIReservationBuilderReadinessListener>
@property (nonatomic, readonly) EHIReservationBuilder *builder;
@end

@implementation EHIItineraryReturnLocationViewModel

- (instancetype)initWithModel:(id)model
{
    if (self = [super initWithModel:model]) {
        _alternateReturnLocationButtonTitle = EHILocalizedString(@"update_return_location_key", @"Return to a different location", @"Title prompting user to update the return location");
    }
    
    return self;
}

- (void)didBecomeActive
{
    [super didBecomeActive];

    [self.builder waitForReadiness:self];
}

# pragma mark - Reactions

- (void)builderIsReady:(EHIReservationBuilder *)builder
{
    [MTRReactor autorun:self action:@selector(invalidateReturnLocation:)];
}

- (void)invalidateReturnLocation:(MTRComputation *)computation
{
    // update whether or now we're showing the location
    EHILocation *location    = self.builder.returnLocation;
    self.showsReturnLocation = self.builder.isOneWayReservation;
    self.shouldHideIcon      = self.iconImageName == nil;

    self.returnLocationTitle = location.displayName;
}

# pragma mark - Public Methods

- (void)findReturnLocation
{
    [EHIAnalytics trackAction:EHIAnalyticsResActionSelectReturn handler:nil];
    
    if(!self.canModifyLocation) {
        [self showLockModal];
    } else if(!self.builder.allowsOneWayReservation) {
        [self showOneWayReservationAlert];
    } else {
        // let the builder know what kind of search we are about to perform
        self.builder.searchTypeOverride = EHILocationsSearchTypeReturn;
        
        self.router.transition
            .push(EHIScreenLocations).start(nil);
    }
}

- (void)showLockModal
{
    [EHIAnalytics trackAction:EHIAnalyticsLocationLockedModalActionLock handler:nil];
    
    [[EHIPickupLocationLockedModalViewModel new] present];
}

- (void)showOneWayReservationAlert
{
    EHIAlertViewBuilder *alert = EHIAlertViewBuilder.new
        .title(EHILocalizedString(@"alert_one_way_reservation_text", @"Your pick up location does not allow for one-way reservations.", @"Title for one way reservation alert"))
        .button(EHILocalizedString(@"standard_button_gotit", @"Got it", @"Title for alert 'one way reservation' button"));
        
    alert.show(nil);
}

- (void)clearReturnLocation
{
    [EHIAnalytics trackAction:EHIAnalyticsResActionDeleteReturn handler:nil];

    // tell builder we're selecting return and wipe it
    [self.builder setSearchTypeOverride:EHILocationsSearchTypeReturn];
    [self.builder selectLocation:nil];
}

# pragma mark - Accessors

- (BOOL)shouldHideLock
{
    return self.canModifyLocation;
}

- (BOOL)canModifyLocation
{
    return self.builder.canModifyLocation;
}

- (EHIReservationBuilder *)builder
{
    return [EHIReservationBuilder sharedInstance];
}

- (NSString *)iconImageName
{
    return [EHILocationIconProvider iconForLocation:self.builder.returnLocation];
}

@end
