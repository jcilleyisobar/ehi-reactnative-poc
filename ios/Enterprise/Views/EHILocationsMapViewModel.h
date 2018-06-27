//
//  EHILocationsMapViewModel.h
//  Enterprise
//
//  Created by Ty Cobb on 1/30/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIUserLocation.h"
#import "EHICity.h"
#import "EHILocationAnnotation.h"
#import "EHISectionHeaderModel.h"
#import "NSValue+MapKit.h"
#import "EHILocationsFilterListViewModel.h"
#import "EHILocationInteractorViewModel.h"

typedef NS_ENUM(NSInteger, EHILocationsMapSection) {
    EHILocationsMapSectionHeader,
    EHILocationsMapSectionLocations,
    EHILocationsMapSectionFallback
};

typedef NS_ENUM(NSInteger, EHILocationsMapBannerType) {
    EHILocationsMapBannerTypeNone,
    EHILocationsMapBannerTypeFilter,
    EHILocationsMapBannerTypeOffbrand,
};

@interface EHILocationsMapViewModel : EHILocationInteractorViewModel <MTRReactive>

/** The city backing this view model; may be nil */
@property (strong, nonatomic) EHICity *city;
/** The nearby location backing this view model; may be nil */
@property (strong, nonatomic) EHIUserLocation *nearbyLocation;
/** The current selected annotation */
@property (strong, nonatomic, readonly) EHILocationAnnotation *currentAnnotation;

/** The type of the currently visible banner */
@property (assign, nonatomic) EHILocationsMapBannerType bannerType;
/** @c YES if filtering is currently allowed */
@property (assign, nonatomic, readonly) BOOL filtersAllowed;
/** The filters that are currently applied to search results */
@property (copy  , nonatomic, readonly) NSArray *activeFilters;

/** The center coordinate of the map view; non-reactive */
@property (assign, nonatomic) CLLocationCoordinate2D mapCenter;
/** The current viewport of the map; the view should update this property as the map moves; non-reactive */
@property (assign, nonatomic) MKCoordinateRegion visibleRegion;
/** @c YES if the user is panning the map; the view should set this */
@property (assign, nonatomic) BOOL isPanningMap;
/** @c YES if the view model is fetching new locations */
@property (assign, nonatomic) BOOL isLoading;

/** An array of @c EHILocation should be visible in the view's list */
@property (copy  , nonatomic) NSArray *listModels;
/** Placeholder data object set when there are no locations in the list */
@property (strong, nonatomic) EHIModel *fallback;
/** An array of @c EHILocationAnnotation that should be visible on the view's map */
@property (copy  , nonatomic, readonly) NSArray *annotations;
/** A boxed @c MKCoordinateRegion that fits the annotations */
@property (copy  , nonatomic, readonly) NSValue *animatedMapRegion;
/** @c YES if the user's location should be shown on the map */
@property (assign, nonatomic, readonly) BOOL showsUserLocation;
/** @c YES if a request is being performed */
@property (assign, nonatomic) BOOL animateSpinnerLoading;

/** Title for the screen */
@property (copy  , nonatomic, readonly) NSString *title;
/** Title for the scroll action button */
@property (copy  , nonatomic, readonly) NSString *scrollToTopTitle;
/** Message to display in the offbrand locations banner */
@property (copy  , nonatomic, readonly) NSString *offbrandLocationsText;

@property (strong, nonatomic, readonly) EHILocationsFilterListViewModel *filterListModel;

- (EHIViewModel *)calloutModelForLocation:(EHILocation *)location;
/** Present the location details using the location that is currently presented by the callout view */
- (void)showSelectedLocationDetails;
- (void)showSelectedLocationDetailsForItemAtIndexPath:(NSIndexPath *)indexPath;
/** Create a reservation using the location that is currently presented by the callout view */
- (void)initReservationWithSelectedLocation;
- (void)initReservationWithSelectedLocationAtIndexPath:(NSIndexPath *)indexPath;
/** Will navigate to the location filter screen */
- (void)transitionToFilterScreen;
/** Clear the active filters */
- (void)clearAllFilters;
/** Return YES if should show fallback cell on the collection view */
- (BOOL)shouldShowFallback;
/** Determines whether an index path is selectbale */
- (BOOL)shouldSelectIndexPath:(NSIndexPath *)indexPath;
/** Selects the map annotation, probably just firing a tracking call */
- (void)selectMapAnnotation:(EHILocationAnnotation *)annotation;
/** Will return the location associated with the index path */
- (EHILocation *)modelForIndexPath:(NSIndexPath *)indexPath;

- (void)filterTappedInSection:(EHIDateTimeComponentSection)section;

- (void)closeTip;

@end
