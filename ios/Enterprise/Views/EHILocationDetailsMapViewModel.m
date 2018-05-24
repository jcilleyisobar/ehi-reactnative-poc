//
//  EHILocationDetailsMapViewModel.m
//  Enterprise
//
//  Created by Ty Cobb on 3/2/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@import MapKit;

#import "EHILocationDetailsMapViewModel.h"
#import "EHILocationAnnotation.h"
#import "EHILocation.h"
#import "EHIMapping.h"
#import "UIApplication+Map.h"

@interface EHILocationDetailsMapViewModel ()
@property (strong, nonatomic) EHILocationAnnotation *annotation;
@property (strong, nonatomic) NSValue *regionValue;
@end

@implementation EHILocationDetailsMapViewModel

- (void)updateWithModel:(EHILocation *)model
{
    [super updateWithModel:model];
 
    // only update the annotation as necessary
    if(self.annotation.location != model) {
        self.annotation = [self annotationFromLocation:model];
    }
    
    self.regionValue = [self regionValueFromLocation:model];
}

# pragma mark - Filters

- (EHILocationAnnotation *)annotationFromLocation:(EHILocation *)location
{
    return location.position ? [[EHILocationAnnotation alloc] initWithLocation:location] : nil;
}

- (NSValue *)regionValueFromLocation:(EHILocation *)location
{
    EHILocationCoordinate *position = location.position;
    if(!position) {
        return nil;
    }
    
    // create a region from a .1.2km x 1.2km box
    MKCoordinateRegion region = MKCoordinateRegionMakeWithDistance(
        position.coordinate,
        1200.0, 1200.0
    );

    return NSValueBox(MKCoordinateRegion, region);
}

# pragma mark - Accessors

- (NSArray *)annotations
{
    return self.annotation ? @[ self.annotation ] : nil;
}

# pragma mark - Actions

- (void)promptMaps
{
    [UIApplication ehi_promptDirectionsForLocation:self.annotation.location];
}

@end
