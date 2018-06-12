//
//  EHIUserLocation.m
//  Enterprise
//
//  Created by Ty Cobb on 2/24/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@import MapKit;

#import "EHIUserLocation.h"
#import "EHILocationManager.h"

@interface EHIUserLocation ()
@property (strong, nonatomic) MKDistanceFormatter *formatter;
@end

@implementation EHIUserLocation

+ (instancetype)location
{
    return [EHILocationManager sharedInstance].userLocation;
}

# pragma mark - Synchronization

- (void)currentLocationWithHandler:(void (^)(CLLocation *, NSError *))handler
{
    // this method return ths location regardless of isAvailable
    if(_currentLocation) {
        ehi_call(handler)(_currentLocation, nil);
    } else {
        [self synchronizeCurrentLocationWithHandler:handler];
    }
}

- (void)synchronizeCurrentLocationWithHandler:(void (^)(CLLocation *, NSError *))handler
{
    [[EHILocationManager sharedInstance] currentLocationWithHandler:handler];
}

# pragma mark - Distance

- (CLLocationDistance)distanceToCoordinate:(CLLocationCoordinate2D)coordinate
{
    // return nil if we don't have a user location
    if(!self.currentLocation) {
        return EHIFloatValueNil;
    }
    
    // get the distnace between the user location and the coordinate
    CLLocation *location = [[CLLocation alloc] initWithLatitude:coordinate.latitude longitude:coordinate.longitude];
    return [self.currentLocation distanceFromLocation:location];
}

- (NSString *)localizedDistanceToCoordinate:(CLLocationCoordinate2D)coordinate
{
    CLLocationDistance distance = [self distanceToCoordinate:coordinate];
    // return the formatted distance, or nil if the distance is nil
    return distance == EHIFloatValueNil ? nil : [self.formatter stringFromDistance:distance];
}

# pragma mark - Accessors

- (CLLocation *)currentLocation
{
    return self.isAvailable ? _currentLocation : nil;
}

- (MKDistanceFormatter *)formatter
{
    if(_formatter) {
        return _formatter;
    }
    
    _formatter = [MKDistanceFormatter new];
    _formatter.unitStyle = MKDistanceFormatterUnitStyleAbbreviated;
    
    return _formatter;
}

# pragma mark - EHIAnalyticsEncodable

+ (void)encodeWithContext:(EHIAnalyticsContext *)context instance:(nullable EHIUserLocation *)instance
{
    context[EHIAnalyticsLocLatitudeKey]  = instance.isAvailable ? @(instance.currentLocation.coordinate.latitude)  : nil;
    context[EHIAnalyticsLocLongitudeKey] = instance.isAvailable ? @(instance.currentLocation.coordinate.longitude) : nil;
}

@end
