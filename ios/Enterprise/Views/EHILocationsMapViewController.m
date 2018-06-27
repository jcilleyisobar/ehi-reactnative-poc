//
//  EHILocationsMapViewController.m
//  Enterprise
//
//  Created by Ty Cobb on 1/30/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILocationsMapViewController.h"
#import "EHILocationsMapViewModel.h"
#import "EHILocationsMapDataSource.h"
#import "EHILocationsMapFallbackCell.h"
#import "EHILocationsMapCalloutView.h"
#import "EHILocationsMapOffbrandBanner.h"
#import "EHILocationsMapListHeaderView.h"
#import "EHILocationAnnotationView.h"
#import "EHILocationsMapListCell.h"
#import "EHIActionButton.h"
#import "EHIListCollectionView.h"
#import "EHIDismissableView.h"
#import "EHIBarButtonItem.h"
#import "EHIRestorableConstraint.h"
#import "EHIMapping.h"
#import "EHILocationsFilterListView.h"
#import "EHILocationFilterWidgetView.h"
#import "EHILocationsMapListActions.h"
#import "EHIBarButtonItemSpinner.h"

typedef NS_ENUM(NSInteger, EHILocationsMapSnapping) {
    EHILocationsMapSnappingNone,
    EHILocationsMapSnappingHidden,
    EHILocationsMapSnappingVisible,
};

@interface EHILocationsMapViewController () <EHIDismissableViewDelegate, EHIListCollectionViewDelegate, EHILocationsFilterListActions, EHILocationFilterWidgetViewActions, EHILocationsMapCalloutViewActions, EHILocationsMapListActions,
    MKMapViewDelegate, UIGestureRecognizerDelegate>

/** The view model backing this view controller */
@property (strong, nonatomic) EHILocationsMapViewModel *viewModel;
/** An object that manages updating the map view with the correct annotations */
@property (strong, nonatomic) EHILocationsMapDataSource *mapDataSource;

@property (strong  , nonatomic) EHIBarButtonItemSpinner *loadingSpinner;

/** The screen rect corresponding to the map viewport */
@property (nonatomic, readonly) CGRect mapViewport;
/** The insets to apply to the map view's bounds when constructing the viewport */
@property (nonatomic, readonly) UIEdgeInsets insetsForMapViewport;

/** @c YES if the content should be inset further during map panning */
@property (assign, nonatomic) BOOL adjustsViewForPanning;
/** The target snapping state, only valid during scrolling after @c -scrollViewWillEndDragging: */
@property (assign, nonatomic) EHILocationsMapSnapping targetSnapping;

/** The height of the locations list header */
@property (nonatomic, readonly) CGFloat listHeaderHeight;
/** The screen height of the list when it is scrolled as far down as possible */
@property (nonatomic, readonly) CGFloat minimumListHeight;
/** The offset to show the top of the list */
@property (nonatomic, readonly) CGPoint minimumVisibleContentOffset;
@property (nonatomic) CGFloat bottomExtraSpace;

/** Container for the callout when tapping on map annotations */
@property (strong, nonatomic) EHIDismissableView *calloutContainer;
/** The map view for displaying the custom annotations */
@property (weak  , nonatomic) IBOutlet MKMapView *mapView;
/** The list view for showing the current locations */
@property (weak  , nonatomic) IBOutlet EHIListCollectionView *collectionView;

/** Indicator for initial load (nearby, city) */
@property (weak  , nonatomic) IBOutlet EHIActivityIndicator *activityIndicator;

@property (weak  , nonatomic) IBOutlet UIView *measurementView;
@property (weak  , nonatomic) EHILocationsMapListHeaderView *mapHeaderCell;
@property (weak  , nonatomic) EHILocationsMapListCell *firstLocationCell;
@property (weak  , nonatomic) IBOutlet EHILocationsFilterListView *filterListView;

/** Container for the banner views */
@property (weak  , nonatomic) IBOutlet UIView *bannerContainer;
/** Banner that appears when displaying offbrand locations */
@property (weak  , nonatomic) IBOutlet EHILocationsMapOffbrandBanner *offbrandBanner;
/** Top spacing for the banner container */
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *bannerTop;

/** The pan gesture to detect when the user is dragging the map */
@property (weak  , nonatomic) IBOutlet UIPanGestureRecognizer *panGesture;
/** The pinch gesture to detect when the user is zooming the map */
@property (weak  , nonatomic) IBOutlet UIPinchGestureRecognizer *pinchGesture;
/** The double-tap gesture to detect when the user attempts to zoom */
@property (weak  , nonatomic) IBOutlet UITapGestureRecognizer *doubleTapGesture;
@end

@implementation EHILocationsMapViewController

- (id)initWithCoder:(NSCoder *)decoder
{
    if(self = [super initWithCoder:decoder]) {
        self.viewModel = [EHILocationsMapViewModel new];
    }
    
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
   
    // become our pan gesture's delegate so that it recognizes properly
    self.panGesture.delegate   = self;
    self.pinchGesture.delegate = self;
    
    // allow the collection view to pass touches through to the map
    self.collectionView.ignoreTouchesOutsideContent = YES;
    
    // disable rotation of the map
    self.mapView.rotateEnabled = NO;
    self.mapView.layer.mask    = [CALayer ehi_layerWithFrame:self.mapView.bounds];
    self.mapView.layer.mask.opaque = YES;
    
    // create the map data source so that annotations can update properly
    self.mapDataSource = [[EHILocationsMapDataSource alloc] initWithMapView:self.mapView];
   
    // configure the collection view sections
    [self.collectionView.sections construct:@{
        @(EHILocationsMapSectionLocations) : [EHILocationsMapListCell class],
        @(EHILocationsMapSectionHeader)    : [EHILocationsMapListHeaderView class],
        @(EHILocationsMapSectionFallback)  : [EHILocationsMapFallbackCell class],
    }];
    
    // dynamically size location cells
    self.collectionView.sections[EHILocationsMapSectionLocations].isDynamicallySized = YES;
    // always show the header
    self.collectionView.sections[EHILocationsMapSectionHeader].isDynamicallySized = YES;
    self.collectionView.sections[EHILocationsMapSectionHeader].model = [EHIModel placeholder];
    
    self.filterListView.viewModel = self.viewModel.filterListModel;
}

- (void)viewWillAppear:(BOOL)animated
{
    // user location should be available on the maps screen
    [EHIUserLocation location].isAvailable = YES;
   
    [super viewWillAppear:animated];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];

    // preserve selection if we are navigating back
    EHILocationAnnotation *currentAnnotation = self.viewModel.currentAnnotation;
    if(currentAnnotation) {
        id<MKAnnotation> annotation = (self.mapView.annotations ?: @[])
            .select(EHILocationAnnotation.class)
            .find(^(EHILocationAnnotation *annotation){
                return [annotation.location isEqual:currentAnnotation.location];
            });
        [self.mapView selectAnnotation:annotation animated:YES];
    }
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
   
    // disable user location when leaving the maps screen
    [EHIUserLocation location].isAvailable = NO;
}

- (void)updateNavigationItem:(UINavigationItem *)item
{
    [super updateNavigationItem:item];
    
    item.rightBarButtonItem = self.loadingSpinner;
}

# pragma mark - Reactions

- (void)registerReactions:(EHILocationsMapViewModel *)model
{
    [super registerReactions:model];
    
    // register any computations more complicated than simple bindings
    [MTRReactor autorun:self action:@selector(updateLocationsList:)];
    [MTRReactor autorun:self action:@selector(animateLoading:)];
    [MTRReactor autorun:self action:@selector(animateMapRegion:)];
    [MTRReactor autorun:self action:@selector(updateBannerType:)];
    [MTRReactor autorun:self action:@selector(showMapFallbackIfNoLocation:)];
    
    // bind to view model properties
    model.bind.map(@{
        source(model.title)                 : dest(self, .title),
        source(model.annotations)           : dest(self, .mapDataSource.annotations),
        source(model.showsUserLocation)     : dest(self, .mapView.showsUserLocation),
        source(model.offbrandLocationsText) : dest(self, .offbrandBanner.title),
        source(model.animateSpinnerLoading) : dest(self, .loadingSpinner.isAnimating),

        // update inset when panning
        source(model.isPanningMap) : ^(NSNumber *isPanningMap) {
            [self setAdjustsViewForPanning:isPanningMap.boolValue animated:YES];
        },
    });
}

- (void)updateLocationsList:(MTRComputation *)computation
{
    // update the locations reactively
    self.collectionView.sections[EHILocationsMapSectionLocations].models = self.viewModel.listModels;
    self.collectionView.sections[EHILocationsMapSectionFallback].model   = self.viewModel.fallback;
    
    [self invalidateContentInsetAnimated:!computation.isFirstRun animations:nil];
}

- (void)showMapFallbackIfNoLocation:(MTRComputation *)computation
{
    if([self.viewModel shouldShowFallback]) {
        __weak typeof(self) welf = self;
        [self.collectionView flushWithCompletion:^{
            [welf snapLocationListToState:EHILocationsMapSnappingVisible animated:YES];
        }];
    }
}

- (void)animateLoading:(MTRComputation *)computation
{
    BOOL animatesPrimarySpinner = self.viewModel.isLoading && !self.viewModel.listModels;
   
    // always update the main indicator (it only animates on first load)
    self.activityIndicator.isAnimating = animatesPrimarySpinner;
}

- (void)animateMapRegion:(MTRComputation *)computation
{
    // don't update it we don't have a region
    if(!self.viewModel.animatedMapRegion) {
        return;
    }
    
    // unbox the region to convert to a map rect
    MKCoordinateRegion region = NSValueUnbox(MKCoordinateRegion, self.viewModel.animatedMapRegion);
   
    // dispatch this animation so that the collection view has time to calculate its content inset
    BOOL isFirstRun = computation.isFirstRun;
    dispatch_async(dispatch_get_main_queue(), ^{
        MKMapRect rect = [self calculateMapRectFromRegion:region];
        [self.mapView setVisibleMapRect:rect animated:!isFirstRun];
    });
}

- (void)updateBannerType:(MTRComputation *)computation
{
    // show/hide the banner container based on type
    BOOL bannerIsVisible = self.viewModel.bannerType != EHILocationsMapBannerTypeNone;
    self.bannerTop.isDisabled = bannerIsVisible;
  
    // show the container if we're becoming visible
    if(bannerIsVisible) {
        self.bannerContainer.hidden = NO;
    }
    
    [self.view setNeedsUpdateConstraints];
    UIView.animate(!computation.isFirstRun).duration(0.2).option(UIViewAnimationOptionBeginFromCurrentState).transform(^{
        [self updateBannerContentViewWithType:self.viewModel.bannerType];
        [self.view layoutIfNeeded];
    }).start(^(BOOL finished) {
        // hide the container if we're becoming invisible
        if(!bannerIsVisible) {
            self.bannerContainer.hidden = YES;
        }
    });
}

//
// Helpers
//

- (MKMapRect)calculateMapRectFromRegion:(MKCoordinateRegion)region
{
    // convert the region to a map rect
    MKMapRect rect = MKMapRectFromCoordinateRegion(region);
    // inset the rect to pad the content nicely
    rect = [self.mapView mapRectThatFits:rect edgePadding:self.insetsForMapViewport];

    return rect;
}

- (void)updateBannerContentViewWithType:(EHILocationsMapBannerType)type
{
    BOOL hide = type != EHILocationsMapBannerTypeOffbrand;
    MASLayoutPriority priority = hide ? MASLayoutPriorityRequired : MASLayoutPriorityDefaultLow;
    
    [self.offbrandBanner mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@0.0).priority(priority);
    }];
}

# pragma mark - UICollectionViewDelegate

- (void)collectionView:(UICollectionView *)collectionView didDequeueCell:(EHICollectionViewCell *)cell atIndexPath:(NSIndexPath *)indexPath
{
    if([indexPath isEqual:self.headerIndexPath]) {
        self.mapHeaderCell = (EHILocationsMapListHeaderView *)cell;
    }
    
    NSIndexPath *firstItemIndexPath = [NSIndexPath indexPathForItem:0 inSection:EHILocationsMapSectionLocations];
    if([indexPath isEqual:firstItemIndexPath]) {
        self.firstLocationCell = (EHILocationsMapListCell *)cell;
    }
}

- (BOOL)collectionView:(UICollectionView *)collectionView shouldSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    return [self.viewModel shouldSelectIndexPath:indexPath];
}

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    // toggle the locations list when tapping the header
    if(indexPath.section == EHILocationsMapSectionHeader) {
        EHILocationsMapSnapping snapping = [self offsetIsInVisibleContent:collectionView.contentOffset]
            ? EHILocationsMapSnappingHidden
            : EHILocationsMapSnappingVisible;
        
        BOOL isCollapsed = snapping == EHILocationsMapSnappingVisible;
        
        [EHIAnalytics trackAction:EHIAnalyticsLocationListHeader handler:^(EHIAnalyticsContext *context) {
            [context setRouterState:EHIScreenLocationsList];
            context[EHIAnalyticsListCollapsed] = @(isCollapsed);
        }];
        
        [self setOverlayForFirstLocationListItem:!isCollapsed];
        
        [self snapLocationListToState:snapping animated:YES];
    }
}

- (void)setOverlayForFirstLocationListItem:(BOOL)overlay
{
    self.firstLocationCell.showOverlay = overlay;
}

# pragma mark - UIScrollViewDelegate

- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    // calculate actual amount the user has scrolled, and mask out that much of the map
    CGFloat adjustedOffset = scrollView.contentOffset.y + scrollView.contentInset.top;

	[self applyProgressFrom:scrollView withAdjustedOffset:adjustedOffset];
	[self applyMapViewMaskWithInset:adjustedOffset];
}

- (void)applyProgressFrom:(UIScrollView *)scrollView withAdjustedOffset:(CGFloat)adjustedOffset
{
    //            ._________________________.           ._________________________.
    //            |                         |           |                         |
    //            |                         |           | LOCATION LIST    FILTER |
    //            |                         |   100% -> |_________________________|
    //            |                         |           |.........................|
    //            |                         |           |.........................|
    //            |                         |           |.........................|
    //            |                         |           |.........................|
    //            |                         |           |.........................|
    //            |                         |           |.........................|
    //            |                         |           |.........................|
    //            |                         |           |.........................|
    //            |                         |           |.....               .....|
    //            |                         |           |.....  CONTENT SIZE .....|
    //            |                         |           |.....               .....|
    //            |                         |           |.........................|
    //            |_________________________|           |.........................|
    //            |                         |           |.........................|
    //            |      LOCATION LIST      |           |.........................|
    //    100% -> |_________________________|           |.........................|
    //            |.........................|           |.........................|
    //            |....                .....|           |.........................|
    //            |....  CONTENT SIZE  .....|           |.........................|
    //            |....                .....|           |.........................|
    //            |.........................|           |.........................|
    //            '''''''''''''''''''''''''''           '''''''''''''''''''''''''''
    
    if(self.adjustsViewForPanning) {
        return;
    }
    
	CGFloat contentHeight = scrollView.contentSize.height;
	CGFloat viewHeight    = CGRectGetHeight(scrollView.frame);
	CGFloat cellHeight    = CGRectGetHeight(self.mapHeaderCell.frame);
	CGFloat total         = MIN(contentHeight, viewHeight) - cellHeight;
	CGFloat progress      = adjustedOffset/total;
	progress = isnan(progress) ? 0.0f : progress;

	self.mapHeaderCell.progress = progress;
    
    dispatch_async(dispatch_get_main_queue(), ^{
        [UIView animateWithDuration:0.10 animations:^{
            self.filterListView.alpha = fabs(progress - 1.0f);
        } completion:nil];
    });
    
    if(progress <= 0) {
        [self setOverlayForFirstLocationListItem:YES];
    } else if(progress > 0.05 || progress <= 0.1) {
        [self setOverlayForFirstLocationListItem:NO];
    }
}

- (void)scrollViewWillEndDragging:(UIScrollView *)scrollView withVelocity:(CGPoint)velocity targetContentOffset:(inout CGPoint *)contentOffset
{
    // get the target snapping state based on target offset
    self.targetSnapping = [self snappingForTargetContentOffset:*contentOffset];
    
    // if snapping is not none
    if(self.targetSnapping) {
        // then snap, as long as the velocity is sufficiently high. otherwise, kill the deceleration
        // to prevent the snap from occurring unnecessarily graudually
        *contentOffset = abs(velocity.y < 0.8f) ? scrollView.contentOffset : [self offsetForSnappingToState:self.targetSnapping];
    }
}

- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate
{
    if(!decelerate) {
        [self resolveTargetSnapping];
    }
}

- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView
{
    [self resolveTargetSnapping];
}

# pragma mark - Snapping

- (void)resolveTargetSnapping
{
    [self snapLocationListToState:self.targetSnapping animated:YES];
    
    [self setTargetSnapping:EHILocationsMapSnappingNone];
}
       
- (EHILocationsMapSnapping)snappingForTargetContentOffset:(CGPoint)contentOffset
{
    // the snapping threshold is halfway between our minimum offset and our totally hidden offset (-ci.t)
    CGFloat visibilityThreshold = (self.minimumVisibleContentOffset.y - self.collectionView.contentInset.top) / 2.0f;
   
    // if we're beyond the threshold, snap. if we're in the content area snap to the top of the list, otherwise hide.
    if(contentOffset.y < visibilityThreshold) {
        return self.collectionView.contentOffset.y > 0.0f ? EHILocationsMapSnappingVisible : EHILocationsMapSnappingHidden;
    }
    // don't snap if we're scrolling into valid content
    else if(contentOffset.y > 0.0f) {
        return EHILocationsMapSnappingNone;
    }
    // otherwise, we're between halfway and 0 so snap to visible
    return EHILocationsMapSnappingVisible;
}

- (CGPoint)offsetForSnappingToState:(EHILocationsMapSnapping)snapping
{
    switch(snapping) {
        case EHILocationsMapSnappingNone: // if none, just use the current offset
            return self.collectionView.contentOffset;
        case EHILocationsMapSnappingHidden: // if the list hidden, snap to the top of the content inset
            return (CGPoint){ .y = -self.collectionView.contentInset.top };
        case EHILocationsMapSnappingVisible: // if visible, use the top of the list
            return self.minimumVisibleContentOffset;
    }
}

- (void)snapLocationListToState:(EHILocationsMapSnapping)snapping animated:(BOOL)animated
{
    if(snapping != EHILocationsMapSnappingNone) {
        CGPoint offset = [self offsetForSnappingToState:snapping];
        
        dispatch_async(dispatch_get_main_queue(), ^{
            [UIView animateWithDuration:0.1f animations:^{
                [self.collectionView setContentOffset:offset animated:animated];
                [self.collectionView layoutIfNeeded];
                [self.view layoutIfNeeded];
            }];
        });
    }
}

//
// Helpers
//

- (CGPoint)minimumVisibleContentOffset
{
    // calulate the difference between the cs.h - ci.t
    CGFloat minimumOffset = self.collectionView.contentSize.height - self.collectionView.contentInset.top - self.listHeaderHeight;
    // if offset is positive, then we have more content than inset, so use 0 instead
    return (CGPoint) { .y = MIN(minimumOffset, 0.0f) };
}

- (BOOL)offsetIsInVisibleContent:(CGPoint)offset
{
    return offset.y >= self.minimumVisibleContentOffset.y;
}

- (NSIndexPath *)headerIndexPath
{
    return [NSIndexPath indexPathForItem:0 inSection:EHILocationsMapSectionHeader];
}

# pragma mark - MKMapViewDelegate

- (MKAnnotationView *)mapView:(MKMapView *)mapView viewForAnnotation:(id<MKAnnotation>)annotation
{
    // if this is the user location, render the default annotation view 
    if([annotation isKindOfClass:[MKUserLocation class]]) {
        return nil;
    }
    
    // attempt to dequeue the view
    NSString *identifier = NSStringFromClass(EHILocationAnnotationView.class);
    EHILocationAnnotationView *view = (EHILocationAnnotationView *)[mapView dequeueReusableAnnotationViewWithIdentifier:identifier];
   
    // if we didn't get a dequeued view, then create one
    if(!view) {
        view = [[EHILocationAnnotationView alloc] initWithAnnotation:annotation reuseIdentifier:identifier];
    }
   
    // and update it with the annotation
    view.annotation = annotation;
    view.layer.zPosition = view.annotation.location.hasConflicts ? 0 : 1000;

    return view;
}

- (void)mapView:(MKMapView *)mapView didSelectAnnotationView:(EHILocationAnnotationView *)view
{
    // if this is not actually our custom annotation (like, it's the user location) don't show the callout
    if(![view isKindOfClass:[EHILocationAnnotationView class]]) {
        return;
    }

    [self collapaseLocationListViewIfNeeded];

    self.calloutContainer = [self mapView:mapView presentCalloutForAnnotationView:view];
    
    CGFloat offset = fabs(view.centerOffset.y);
    CGPoint point = (CGPoint) {
        .x = mapView.center.x,
        .y = mapView.center.y - offset
    };
    
    [self mapView:mapView animateAnnotation:view.annotation toScreenPoint:point];
    [self.viewModel selectMapAnnotation:view.annotation];
}

- (void)mapView:(MKMapView *)mapView regionWillChangeAnimated:(BOOL)animated
{
    // if we're pinching / panning, set the interaction flag
    if(self.isInteractingWithMap) {
        self.viewModel.isPanningMap = YES;
    }
}

- (void)mapView:(MKMapView *)mapView regionDidChangeAnimated:(BOOL)animated
{
    self.viewModel.mapCenter     = mapView.centerCoordinate;
    self.viewModel.visibleRegion = [self coordinateRegionFromRect:self.mapViewport];

    // if the view model though it was panning, turn it off here
    if(self.viewModel.isPanningMap) {
        self.viewModel.isPanningMap = NO;
    }
}

//
// Helpers
//

- (MKCoordinateRegion)coordinateRegionFromRect:(CGRect)rect
{
    return [self.mapView convertRect:rect toRegionFromView:self.mapView];
}

- (void)collapaseLocationListViewIfNeeded
{
    EHILocationsMapSnapping listViewStatus = [self snappingForTargetContentOffset:self.collectionView.contentOffset];
    if(listViewStatus != EHILocationsMapSnappingVisible) {
        return;
    }
    
    [self setAdjustsViewForPanning:self.viewModel.isPanningMap animated:YES];
}

# pragma mark - Callout

- (EHIDismissableView *)mapView:(MKMapView *)mapView presentCalloutForAnnotationView:(EHILocationAnnotationView *)view
{
    // create the container for the callout view
    EHIDismissableView *container = [EHIDismissableView new];
    container.clipsToBounds = YES;

    // create the callout
    EHILocationsMapCalloutView *calloutView = [EHILocationsMapCalloutView ehi_instanceFromNib];
    
    // create custom metrics to position the callout properly
    EHILayoutMetrics *metrics = [[calloutView.class metrics] copy];
    
    EHIViewModel *model = [self.viewModel calloutModelForLocation:view.annotation.location];
    // allow the metrics to size themselves dynamically
    [metrics dynamicSizeForView:calloutView containerSize:self.view.bounds.size model:model];

    // configure the container with the content view
    [container setDelegate:self];
    [container setContentView:calloutView metrics:metrics];
    
    [self.view addSubview:container];
    
    [container mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(self.collectionView).with.offset(-self.minimumListHeight);
        make.leading.equalTo(self.view);
        make.trailing.equalTo(self.view);
    }];
    
    // and present it
    container.isVisible = YES;
    
    return container;
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

# pragma mark - EHILocationsMapCalloutViewActions

- (void)calloutViewDidTapSelect:(EHILocationsMapCalloutView *)sender
{
    [self.viewModel initReservationWithSelectedLocation];
}

- (void)calloutViewDidTapLocationTitle:(EHILocationsMapCalloutView *)sender
{
     [self.viewModel showSelectedLocationDetails];
}

- (void)calloutViewDidTapChangeState:(EHILocationsMapCalloutView *)sender
{
    [UIView animateWithDuration:0.3f animations:^{
        [sender layoutIfNeeded];
    }];
}

# pragma mark - EHILocationsMapListActions

- (void)locationsMapDidTapSelect:(EHILocationsMapListCell *)sender
{
    NSIndexPath *indexPath = [self.collectionView indexPathForCell:sender];
    [self.viewModel initReservationWithSelectedLocationAtIndexPath:indexPath];
    [self.viewModel selectMapAnnotation:nil];
}

- (void)locationsMapDidTapLocationTitle:(EHILocationsMapListCell *)sender
{
    NSIndexPath *indexPath = [self.collectionView indexPathForCell:sender];
    [self.viewModel showSelectedLocationDetailsForItemAtIndexPath:indexPath];
    [self.viewModel selectMapAnnotation:nil];
}

- (void)locationsMapDidTapChangeState:(EHILocationsMapListCell *)sender
{
    [sender setNeedsUpdateConstraints];
    NSIndexPath *indexPath = [self.collectionView indexPathForCell:sender];
    [self.collectionView ehi_revealExpandedCellAtIndexPath:indexPath completion:nil];
}

# pragma mark - EHILocationFilterWidgetViewActions

- (void)locationFilterWidgetTapped:(EHILocationFilterWidgetView *)sender
{
    [self.viewModel closeTip];
    [self.viewModel transitionToFilterScreen];
}

# pragma mark - Layout

- (void)applyMapViewMaskWithInset:(CGFloat)inset
{
    CGRect maskFrame       = self.mapView.frame;
    maskFrame.size.height -= inset - self.listHeaderHeight;
    maskFrame.size.height  = MAX(maskFrame.size.height, 0.0f);
    
    [CALayer ehi_performUnanimated:^{
        self.mapView.layer.mask.frame = maskFrame;
    }];
}

- (void)setAdjustsViewForPanning:(BOOL)adjustsViewForPanning
{
    [self setAdjustsViewForPanning:adjustsViewForPanning animated:NO];
}

- (void)setAdjustsViewForPanning:(BOOL)adjustsViewForPanning animated:(BOOL)animated
{
    _adjustsViewForPanning = adjustsViewForPanning;
    
    // invalidate the inset
    [self invalidateContentInsetAnimated:animated animations:^{
        CGFloat alpha = adjustsViewForPanning ? 0.0f : 1.0f;
        self.bannerContainer.alpha = alpha;
        self.filterListView.alpha  = alpha;
    }];
    
    [self setOverlayForFirstLocationListItem:YES];
}

- (void)invalidateContentInsetAnimated:(BOOL)animated animations:(void(^)(void))animations
{
    __weak typeof(self) welf = self;
    // let's figure out how tall our collection view's content is going to be
    [self.collectionView flushWithCompletion:^{
        // we don't want the animation to trigger a cell dequeue and have its reactions parented by
        // whaterver called this method
        [MTRReactor nonreactive:^{
            [welf.collectionView layoutIfNeeded];

            // show complete list or just peeking header
            CGFloat height  = welf.collectionView.bounds.size.height;
            NSInteger inset = welf.adjustsViewForPanning ? height : height - welf.minimumListHeight;

            UIView.animate(animated).duration(0.25f).transform(^{
                ehi_call(animations)();
                [welf.collectionView setContentInset:(UIEdgeInsets){ .top = inset }];
                [welf.collectionView setContentOffset:(CGPoint){ .y = -inset } animated:animated];
                [welf setOverlayForFirstLocationListItem:YES];
            }).start(nil);
        }];
    }];
}

# pragma mark - Accessors

- (CGFloat)minimumListHeight
{
    return self.listHeaderHeight;
}

- (CGFloat)listHeaderHeight
{
    return self.mapHeaderCell.intrinsicContentSize.height + self.bottomExtraSpace;
}

- (CGRect)mapViewport
{
    return CGRectApplyInsets(self.mapView.bounds, self.insetsForMapViewport);
}

- (CGFloat)bottomExtraSpace
{
    return CGRectGetMaxY(self.collectionView.frame) - CGRectGetMaxY(self.measurementView.frame);
}

- (UIEdgeInsets)insetsForMapViewport
{
    // the map is zoomed out too much by mapRectThatFits, so lets zoom it in a bit and offset for the list
    CGFloat defaultInset = 64.0f;
    CGFloat bottomInset  = defaultInset + self.minimumListHeight;
    
    return (UIEdgeInsets){
        .top = defaultInset, .left = defaultInset, .right = defaultInset, .bottom = bottomInset
    };
}

- (EHIBarButtonItemSpinner *)loadingSpinner
{
    if(!_loadingSpinner) {
        _loadingSpinner = [EHIBarButtonItemSpinner create];
    }
    
    return _loadingSpinner;
}

# pragma mark - EHILocationsFilterListActions

- (void)filterListDidTapOnSection:(NSNumber *)section
{
    [self.viewModel filterTappedInSection:[section integerValue]];
}

- (void)filterListDidTap:(EHILocationsFilterListView *)sender
{
    [self.viewModel transitionToFilterScreen];
}

- (void)filterListDidClearDates
{
    [self.viewModel clearAllFilters];
}

- (void)filterListDidClearFilters
{
    [self.viewModel clearAllFilters];
}

# pragma mark - EHIDismissableViewDelegate

- (void)dismissableViewWillDismiss:(EHIDismissableView *)view
{
    // we should only have one annotation selected
    id<MKAnnotation> annotation = self.mapView.selectedAnnotations.firstObject;
    // dismiss it right away
    [self.mapView deselectAnnotation:annotation animated:YES];
}

- (void)dismissableViewDidDismmiss:(EHIDismissableView *)view
{
    // we're using the existence of a callout view to block presenting a new annotation's callout
    // before dismissing the existing one (i.e. selection requires a two tap sequence, like a modal).
    // however, the map view annotation selection callback comes back extraordinarily slow, like > 0.25s
    // after actual user interaction. this buys us a bit more time.
    
    dispatch_after_seconds(0.3f, ^{
        self.calloutContainer = nil;
    });
}

# pragma mark - Gestures

- (BOOL)isInteractingWithMap
{
    return (self.panGesture.state >= UIGestureRecognizerStateBegan
        && self.panGesture.state <= UIGestureRecognizerStateEnded)
        || (self.pinchGesture.state >= UIGestureRecognizerStateBegan
        && self.panGesture.state <= UIGestureRecognizerStateEnded)
        || self.doubleTapGesture.state == UIGestureRecognizerStateEnded;
    
}

//
// UIGestureRecognizerDelegate
//

- (BOOL)gestureRecognizerShouldBegin:(UIGestureRecognizer *)gestureRecognizer
{
    // remove the mask if we start panning
    [self applyMapViewMaskWithInset:0.0f];
    
    return YES;
}

- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gesture shouldRecognizeSimultaneouslyWithGestureRecognizer:(UIGestureRecognizer *)other
{
    return YES;
}

# pragma mark - Analytics

- (void)prepareToUpdateAnalyticsContext
{
    [EHIAnalytics changeScreen:EHIScreenLocations state:EHIScreenLocationsMap];
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:nil];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenLocationsMap;
}

@end
