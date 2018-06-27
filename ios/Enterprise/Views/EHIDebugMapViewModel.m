//
//  EHIDebugMapViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 12/10/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIDebugMapViewModel.h"
#import "EHIGeofenceManager.h"
#import "EHINotificationManager+Private.h"
#import "EHIMapping.h"
#import "EHIDebugLocationManager.h"
#import "EHIUser.h"
#import "EHIToastManager.h"

#define EHIChicagoCoordinate (CLLocationCoordinate2DMake(41.8631225, -87.7675238))

@interface EHIDebugMapViewModel () <EHIDebugLocationManagerListener>
@property (strong, nonatomic, readonly) EHIDebugLocationManager *locationManager;
@end

@implementation EHIDebugMapViewModel

- (void)didBecomeActive
{
    [super didBecomeActive];
    
    [self.locationManager addListener:self];
    
    self.title = self.locationManager.isUpdatingLocation ? @"Mock Enabled" : @"Mock Disabled";
    self.isMockingLocations = self.locationManager.isUpdatingLocation;
    
    self.region   = [self regionForCenter:EHIChicagoCoordinate];
    self.overlays = [self constructOverlays];
}

//
// Helpers
//

- (NSArray *)constructOverlays
{
    // get notification regions
    NSArray *pendingNotifications = [EHINotificationManager sharedInstance].pendingNotifications ?: @[];
    NSArray *notificationRegions = pendingNotifications
    .map(^(UNNotificationRequest *request){
        return request.trigger;
    })
    .select(^(UNNotificationTrigger *trigger) {
        return [trigger isKindOfClass:UNLocationNotificationTrigger.class];
    })
    .map(^(UNLocationNotificationTrigger *trigger){
        return trigger.region;
    });
    
    // map geofence regions and notification regions
    return @[].concat([EHIGeofenceManager sharedInstance].monitoredRegions).concat(notificationRegions).map(^(CLCircularRegion *region) {
        return [MKCircle circleWithCenterCoordinate:region.center radius:region.radius];
    });
}

# pragma marm - Regions

- (NSValue *)regionForCenter:(CLLocationCoordinate2D)center
{
    CLLocationDistance radius = 30.0 * CLLocationDistanceMilesToMeters;
    return NSValueBox(MKCoordinateRegion, MKCoordinateRegionMakeWithDistance(center, radius, radius));
}


# pragma mark - EHIDebugLocationManagerListener

- (void)debugLocationManager:(EHIDebugLocationManager *)manager didUpdateLocation:(CLLocation *)location
{
    if(!self.mockUserLocation) {
        self.mockUserLocation = [EHIMockUserAnnotation new];
    }
    
    self.mockUserLocation.coordinate = location.coordinate;
}

# pragma mark - Actions

- (void)testLocations
{
    EHIAlertViewBuilder *alert = EHIAlertViewBuilder.new.message(@"What would you like to do?")
        .button(@"Mock Chicago")
        .button(@"Mock London")
        .button(@"Mock First Current")
        .button(@"Mock First Upcoming")
        .button(@"Stop Mock")
        .cancelButton(nil);
    
    alert.show(^(NSUInteger index, BOOL canceled) {
        switch(index) {
            case 4:
                [self enableMockLocations:NO]; break;
            case 5:
                break;
            default:
                [self startMockingWithRouteType:index]; break;
        }
        
        // notify the user when the notification is going to trigger
        if(index == 2 || index == 3) {
            BOOL wantsCurrentRental = index == 2;
            [self showTriggerDateUsingCurrentRental:wantsCurrentRental];
        }
    });
}

- (void)showTriggerDateUsingCurrentRental:(BOOL)useCurrentRental
{
    NSDate *rentalDate = nil;
    rentalDate = useCurrentRental
        ? [[EHIUser currentUser].currentRentals.all.firstObject returnDate]
        : [[EHIUser currentUser].upcomingRentals.all.firstObject pickupDate];
    
    if(rentalDate) {
        NSString *message = [NSString stringWithFormat:@"A notification for this rental will trigger around %@", [rentalDate ehi_stringWithFormat:@"E, yyyy-MM-dd HH:mm"]];
        [EHIToastManager showMessage:message];
    }
}

# pragma mark - Mocking

- (void)startMockingWithRouteType:(EHIDebugRouteType)type
{
    [self.locationManager setRouteType:type];
    
    if(self.locationManager.currentRoute.count == 0) {
        [self promptNoLocations];
        return;
    }
    
    [self enableMockLocations:YES];
    
    // jump map to new mock location
    CLLocation *firstLocation = [self.locationManager.currentRoute firstObject];
    self.region = [self regionForCenter:firstLocation.coordinate];
}

- (void)enableMockLocations:(BOOL)enable
{
    self.title = enable ? @"Mock Enabled" : @"Mock Disabled";
    self.isMockingLocations = enable;
    
    if(enable) {
        [self.locationManager startUpdatingLocation];
    } else {
        [self.locationManager stopUpdatingLocation];
        [self setMockUserLocation:nil];
    }
}

//
// Helpers
//

- (void)promptNoLocations
{
    EHIAlertViewBuilder.new
        .title(@"No Locations")
        .message(@"There are no mock locations for the selected route")
        .cancelButton(nil)
        .show(nil);
}

# pragma mark - Accessors

- (EHIDebugLocationManager *)locationManager
{
    return [EHIDebugLocationManager sharedInstance];
}

@end
