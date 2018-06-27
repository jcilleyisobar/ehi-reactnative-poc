//
//  EHILocationsMapDataSource.m
//  Enterprise
//
//  Created by Ty Cobb on 2/6/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILocationsMapDataSource.h"
#import "EHILocationAnnotation.h"

@interface EHILocationsMapDataSource ()
@property (weak, nonatomic) MKMapView *mapView;
@end

@implementation EHILocationsMapDataSource

- (instancetype)initWithMapView:(MKMapView *)mapView
{
    if(self = [super init]) {
        _mapView = mapView;
    }
    
    return self;
}

- (void)setAnnotations:(NSArray *)annotations
{
    NSArray *sorted = annotations.sort;
    
    NSArray *currentAnnotations = self.mapView.annotations;
    [self.mapView removeAnnotations:currentAnnotations];
    [self.mapView addAnnotations:sorted];
    
    // update our storage
    _annotations = sorted;
}

@end
