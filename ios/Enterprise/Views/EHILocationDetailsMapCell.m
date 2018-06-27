//
//  EHILocationDetailsMapCell.m
//  Enterprise
//
//  Created by Ty Cobb on 2/25/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@import MapKit;

#import "EHILocationDetailsMapCell.h"
#import "EHILocationDetailsMapViewModel.h"
#import "EHILocationsMapDataSource.h"
#import "EHILocationAnnotationView.h"

@interface EHILocationDetailsMapCell () <MKMapViewDelegate>
@property (strong, nonatomic) EHILocationDetailsMapViewModel *viewModel;
@property (strong, nonatomic) EHILocationsMapDataSource *mapDataSource;
@property (weak  , nonatomic) IBOutlet MKMapView *mapView;
@property (strong, nonatomic) UITapGestureRecognizer* tapGesture;
@end

@implementation EHILocationDetailsMapCell

- (id)initWithCoder:(NSCoder *)decoder
{
    if(self = [super initWithCoder:decoder]) {
        self.viewModel = [EHILocationDetailsMapViewModel new];
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    // disable scrolling/zooming on the map
    self.mapView.userInteractionEnabled = NO;
    
    //add tap gesture, it'll prompt the user with thrid party map apps
    [self addTapGesture];
    
    // create the map data source so that annotations can update properly
    self.mapDataSource = [[EHILocationsMapDataSource alloc] initWithMapView:self.mapView];
}

# pragma mark - Reactions

- (void)registerReactions:(EHILocationDetailsMapViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.annotations) : dest(self, .mapDataSource.annotations),
        source(model.regionValue) : ^(NSValue *region) {
            /* 
               Setting mapView's region calls through to mapView:viewForAnnotation:
               Consequentially, EHILocationAnnotationView's annotation image reaction
               created on -init sets this reaction as it's parent. Further updates then
               destroy the annotation image reaction and prevent the favorites button
               from updating the image. Async setRegion here to prevent a parent-child relation.
            */
            dispatch_async(dispatch_get_main_queue(), ^{
                // set the region to whatever we've got
                self.mapView.region = region.MKCoordinateRegionValue;
            });
        
            // show the map view if we have an actual region
            UIView.animate(region != nil).transform(^{
                self.mapView.alpha = region ? 1.0f : 0.0f;
            }).start(nil);
        }
    });
}

# pragma mark - MKMapViewDelegate

- (MKAnnotationView *)mapView:(MKMapView *)mapView viewForAnnotation:(id<MKAnnotation>)annotation
{
    // attempt to dequeue the view
    NSString *identifier = NSStringFromClass(EHILocationAnnotationView.class);
    EHILocationAnnotationView *view = (EHILocationAnnotationView *)[mapView dequeueReusableAnnotationViewWithIdentifier:identifier];
    
    // if we didn't get a dequeued view, then create one
    if(!view) {
        view = [[EHILocationAnnotationView alloc] initWithAnnotation:annotation reuseIdentifier:identifier];
    }
    
    // and update it with the annotation
    view.annotation = annotation;
    
    // make it use the selected image
    [view setSelected:YES animated:NO];

    // center the pin whitin the map
    CGFloat offset = fabs(view.centerOffset.y);
    CGPoint point = (CGPoint) {
        .x = mapView.center.x,
        .y = mapView.center.y + offset
    };

    [self mapView:mapView animateAnnotation:view.annotation toScreenPoint:point];
    
    return view;
}

- (void)mapView:(MKMapView *)mapView animateAnnotation:(id<MKAnnotation>)annotation toScreenPoint:(CGPoint)point
{
    CLLocationCoordinate2D destinationCoordinate = [mapView convertPoint:point toCoordinateFromView:mapView];
    CLLocationCoordinate2D annotationCoordinate  = annotation.coordinate;
    CLLocationCoordinate2D centerCoordinate      = mapView.centerCoordinate;
    
    centerCoordinate.latitude  += annotationCoordinate.latitude  - destinationCoordinate.latitude;
    centerCoordinate.longitude += annotationCoordinate.longitude - destinationCoordinate.longitude;
    
    [mapView setCenterCoordinate:centerCoordinate animated:YES];
}

# pragma mark - Actions

- (void)addTapGesture
{
    [self addGestureRecognizer:self.tapGesture];
}

- (UITapGestureRecognizer *)tapGesture
{
    if(!_tapGesture) {
        _tapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(didTapMapItem:)];
    }
    
    return _tapGesture;
}

- (void)didTapMapItem:(UITapGestureRecognizer *)recognizer
{
    [self.viewModel promptMaps];
}

# pragma mark - Layout

+ (EHILayoutMetrics *)defaultMetrics
{
    EHILayoutMetrics *metrics = [super defaultMetrics];
    metrics.fixedSize = (CGSize){ .width = EHILayoutValueNil, .height = 140.0f };
    return metrics;
}

@end
