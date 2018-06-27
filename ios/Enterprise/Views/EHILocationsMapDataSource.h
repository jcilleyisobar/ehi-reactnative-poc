//
//  EHILocationsMapDataSource.h
//  Enterprise
//
//  Created by Ty Cobb on 2/6/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@import MapKit;

/**
 Given a list of annotations, this object manages updating a map view so that
 it only displays annotations from the most recent list.
*/

@interface EHILocationsMapDataSource : NSObject

/** The map view's currently visible annotations */
@property (copy, nonatomic) NSArray *annotations;

/**
 Initializes a data source for a specific map view.
 
 @param mapView The map view to manage data for; held weakly
 @return A new @c EHILocationsMapDataSource instance
*/

- (instancetype)initWithMapView:(MKMapView *)mapView;

@end
