//
//  EHIDebugMapViewController.m
//  Enterprise
//
//  Created by Alex Koller on 12/10/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHIDebugMapViewController.h"
#import "EHIDebugMapViewModel.h"
#import "EHIGeofenceManager.h"

@interface EHIDebugMapViewController () <MKMapViewDelegate>
@property (strong, nonatomic) EHIDebugMapViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet MKMapView *mapView;
@end

@implementation EHIDebugMapViewController

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder] ) {
        self.viewModel = [EHIDebugMapViewModel new];
    }
    
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.mapView.delegate = self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIDebugMapViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateRegion:)];
    [MTRReactor autorun:self action:@selector(invalidateUserLocation:)];
    [MTRReactor autorun:self action:@selector(invalidateOverlays:)];
    
    model.bind.map(@{
        source(model.title) : dest(self, .title),
        source(model.isMockingLocations) : ^(NSNumber *mockingLocations){
            self.mapView.showsUserLocation = ![mockingLocations boolValue];
        },
    });
}

- (void)invalidateRegion:(MTRComputation *)computation
{
    MKCoordinateRegion region = NSValueUnbox(MKCoordinateRegion, self.viewModel.region);
    
    [self.mapView setRegion:region animated:!computation.isFirstRun];
}

- (void)invalidateUserLocation:(MTRComputation *)computation
{
    [self.mapView removeAnnotations:self.mapView.annotations];
    
    [self.mapView addAnnotation:self.viewModel.mockUserLocation];
}

- (void)invalidateOverlays:(MTRComputation *)computation
{
    // remove old overlays
    [self.mapView removeOverlays:self.mapView.overlays];
    
    // add new
    [self.mapView addOverlays:self.viewModel.overlays];
}

# pragma mark - MKMapViewDelegate

- (MKAnnotationView *)mapView:(MKMapView *)mapView viewForAnnotation:(id<MKAnnotation>)annotation
{
    // if this is the user location, render the default annotation view
    if([annotation isKindOfClass:[MKUserLocation class]]) {
        return nil;
    }
    
    // attempt to dequeue the view
    NSString *identifier = NSStringFromClass(MKPinAnnotationView.class);
    MKPinAnnotationView *view = (MKPinAnnotationView *)[mapView dequeueReusableAnnotationViewWithIdentifier:identifier];
    
    // if we didn't get a dequeued view, then create one
    if(!view) {
        view = [[MKPinAnnotationView alloc] initWithAnnotation:annotation reuseIdentifier:identifier];
    }
    
    // and update it with the annotation
    view.annotation = annotation;
    
    return view;
}

- (MKOverlayRenderer *)mapView:(MKMapView *)mapView rendererForOverlay:(id<MKOverlay>)overlay
{
    MKCircleRenderer *renderer = [[MKCircleRenderer alloc] initWithOverlay:overlay];
    renderer.lineWidth   = 1.0;
    renderer.strokeColor = [UIColor redColor];
    renderer.fillColor   = [[UIColor redColor] colorWithAlphaComponent:0.05];
    
    return renderer;
}

# pragma mark - Actions

- (void)didTapTestButton:(id)sender
{
    [self.viewModel testLocations];
}

# pragma mark - EHIViewController

- (void)updateNavigationItem:(UINavigationItem *)item
{
    [super updateNavigationItem:item];
    
    item.rightBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"Test" style:UIBarButtonItemStyleDone target:self action:@selector(didTapTestButton:)];
    item.rightBarButtonItem.tintColor = [UIColor whiteColor];
    
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenDebugMap;
}

@end
