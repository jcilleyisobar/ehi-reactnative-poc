//
//  EHIMapping.h
//  Enterprise
//
//  Created by Ty Cobb on 2/4/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@import MapKit;

static const CLLocationCoordinate2D CLLocationCoordinate2DZero = (CLLocationCoordinate2D){ .latitude = 0.0, .longitude = 0.0 };
static const CLLocationDistance CLLocationDistanceMilesToMeters = 1609.34;
static const CLLocationDistance CLLocationDistanceMilesToKilometers = CLLocationDistanceMilesToMeters / 1000.0;
static const CLLocationDistance CLLocationDistanceKilometersToMeters = 1000.0;

// approximates
static const CLLocationDistance CLLocationDistanceDegreesToMeters = 111200;

NS_INLINE BOOL CLLocationCoordinate2DIsZero(CLLocationCoordinate2D coordinate)
{
    return coordinate.latitude == 0.0 && coordinate.longitude == 0.0;
}

NS_INLINE CLLocationDegrees CLLocationCoordinate2DDistance(CLLocationCoordinate2D coordinate, CLLocationCoordinate2D other)
{
    return sqrt(
        pow(coordinate.latitude  - other.latitude,  2.0) +
        pow(coordinate.longitude - other.longitude, 2.0)
    );
}

NS_INLINE CLLocationCoordinate2D CLLocationCoordinate2DOffset(CLLocationCoordinate2D coordinate, CLLocationDegrees latittude, CLLocationDegrees longitude)
{
    coordinate.latitude  += latittude;
    coordinate.longitude += longitude;
    
    return coordinate;
}

NS_INLINE BOOL MKCoordinateRegionContains(MKCoordinateRegion region, CLLocationCoordinate2D coordinate)
{
    EHIFloatRange latitudeRange  = EHIFloatRangeFromCenter(region.center.latitude, region.span.latitudeDelta);
    EHIFloatRange longitudeRange = EHIFloatRangeFromCenter(region.center.longitude, region.span.longitudeDelta);
    
    return EHIFloatRangeContains(latitudeRange, coordinate.latitude)
        && EHIFloatRangeContains(longitudeRange, coordinate.longitude);
}

NS_INLINE CLLocationDegrees MKCoordinateRegionGetMinLongitude(MKCoordinateRegion region)
{
    return region.center.longitude - region.span.longitudeDelta / 2.0;
}

NS_INLINE CLLocationDegrees MKCoordinateRegionGetMaxLongtiude(MKCoordinateRegion region)
{
    return region.center.longitude + region.span.longitudeDelta / 2.0;
}

NS_INLINE CLLocationDegrees MKCoordinateRegionGetMinLatitude(MKCoordinateRegion region)
{
    return region.center.latitude - region.span.latitudeDelta / 2.0;
}

NS_INLINE CLLocationDegrees MKCoordinateRegionGetMaxLatitude(MKCoordinateRegion region)
{
    return region.center.latitude + region.span.latitudeDelta / 2.0;
}

NS_INLINE CLLocationDistance MKCoordinateRegionWidthInMeters(MKCoordinateRegion region)
{
    CLLocation *minimum = [[CLLocation alloc] initWithLatitude:region.center.latitude longitude:MKCoordinateRegionGetMinLongitude(region)];
    CLLocation *maximum = [[CLLocation alloc] initWithLatitude:region.center.latitude longitude:MKCoordinateRegionGetMaxLongtiude(region)];
    
    return [minimum distanceFromLocation:maximum];
}

NS_INLINE CLLocationDegrees MKCoordinateRegionDiagonalInMeters(MKCoordinateRegion region)
{
    CLLocation *topLeft     = [[CLLocation alloc] initWithLatitude:MKCoordinateRegionGetMinLatitude(region) longitude:MKCoordinateRegionGetMinLongitude(region)];
    CLLocation *bottomRight = [[CLLocation alloc] initWithLatitude:MKCoordinateRegionGetMaxLatitude(region) longitude:MKCoordinateRegionGetMaxLongtiude(region)];
    
    return [topLeft distanceFromLocation:bottomRight];
}

NS_INLINE MKMapRect MKMapRectFromCoordinates(CLLocationCoordinate2D topLeft, CLLocationCoordinate2D bottomRight)
{
    // conver the coordinates to map points
    MKMapPoint tl = MKMapPointForCoordinate(topLeft);
    MKMapPoint br = MKMapPointForCoordinate(bottomRight);
   
    // construct the rect from those points
    MKMapRect rect = {
        .origin.x    = MIN(tl.x, br.x),
        .origin.y    = MIN(tl.y, br.y),
        .size.width  = ABS(tl.x - br.x),
        .size.height = ABS(tl.y - br.y)
    };
    
    return rect;
}

NS_INLINE MKMapRect MKMapRectFromCoordinateRegion(MKCoordinateRegion region)
{
    CLLocationCoordinate2D topLeft = (CLLocationCoordinate2D) {
        .latitude  = region.center.latitude  + region.span.latitudeDelta  / 2.0,
        .longitude = region.center.longitude - region.span.longitudeDelta / 2.0
    };
   
    CLLocationCoordinate2D bottomRight = (CLLocationCoordinate2D) {
        .latitude  = region.center.latitude  - region.span.latitudeDelta  / 2.0,
        .longitude = region.center.longitude + region.span.longitudeDelta / 2.0
    };
    
    return MKMapRectFromCoordinates(topLeft, bottomRight);
}
