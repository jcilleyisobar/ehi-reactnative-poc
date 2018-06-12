//
//  EHIGeofenceNote.m
//  Enterprise
//
//  Created by Alex Koller on 12/10/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHIModel_Subclass.h"
#import "EHIGeonotification.h"
#import "EHIUserRental.h"
#import "EHINotificationInterfaces.h"

@interface EHIGeonotification ()
@property (assign, nonatomic) EHIGeonotificationType type;
@property (strong, nonatomic) CLRegion *region;
@property (copy  , nonatomic) NSString *message;
@property (strong, nonatomic) NSDate *latestFireDate;
@property (copy  , nonatomic) NSDictionary *userInfo;
@end

@implementation EHIGeonotification

+ (instancetype)geonotificationForAfterHoursRental:(EHIUserRental *)rental
{
    NSString *message = EHILocalizedString(@"notifications_after_hours_alert_message", @"Welcome back. It looks like you're returning your vehicle after hours. Follow our return instructions to return your vehicle safely.", @"");
    
    EHIGeonotification *geonotification = [EHIGeonotification new];
    geonotification.type           = EHIGeonotificationTypeAfterHours;
    geonotification.region         = [self regionForLocation:rental.returnLocation radius:EHIGeofencingReturnRadius];
    geonotification.message        = message;
    geonotification.latestFireDate = rental.returnDate;
    geonotification.userInfo       = [UNNotificationRequest userInfoForRental:rental];
    
    return geonotification;
}

+ (instancetype)geonotificationForWayfindingRental:(EHIUserRental *)rental
{
    NSString *message = EHILocalizedString(@"notifications_wayfinding_alert_message", @"Welcome to #{name}. Do you need help finding our local Enterprise Branch?", @"");
    message = [message ehi_applyReplacementMap:@{
        @"name" : rental.pickupLocation.displayName ?: @"",
    }];
    
    EHIGeonotification *geonotification = [EHIGeonotification new];
    geonotification.type           = EHIGeonotificationTypeWayfinding;
    geonotification.region         = [self regionForLocation:rental.pickupLocation radius:EHIGeofencingPickupRadius];
    geonotification.message        = message;
    geonotification.latestFireDate = rental.pickupDate;
    geonotification.userInfo       = [UNNotificationRequest userInfoForRental:rental];
    
    return geonotification;
}

//
// Helpers
//

+ (CLCircularRegion *)regionForLocation:(EHILocation *)location radius:(NSInteger)radius
{
    NSString *identifier          = location.uid;
    CLLocationCoordinate2D center = location.position.coordinate;
    
    CLCircularRegion *region = [[CLCircularRegion alloc] initWithCenter:center radius:radius identifier:identifier];
    region.notifyOnExit  = NO;
    
    return region;
}

# pragma mark - EHIModel

- (void)parseDictionary:(NSMutableDictionary *)dictionary
{
    [super parseDictionary:dictionary];
    
    // create a dummy uid if necessary
    if(!self.uid) {
        dictionary[@key(self.uid)] = [NSUUID UUID].UUIDString;
    }
}

# pragma mark - Accessors

- (BOOL)shouldFire
{
    // ensure now is some time 2 hours before fire date
    BOOL isTwoHoursBefore = [[NSDate date] ehi_hoursUntilDate:self.latestFireDate] < 2;
    BOOL isBeforeFireDate = [[NSDate date] ehi_isBefore:self.latestFireDate];

    return isTwoHoursBefore && isBeforeFireDate;
}

@end
