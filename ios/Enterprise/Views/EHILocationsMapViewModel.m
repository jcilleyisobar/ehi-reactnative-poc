//
//  EHILocationsMapViewModel.m
//  Enterprise
//
//  Created by Ty Cobb on 1/30/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHILocationsMapViewModel.h"
#import "EHIInfoModalViewModel.h"
#import "EHILocationManager.h"
#import "EHIMapping.h"
#import "EHIReservationBuilder_Private.h"
#import "EHIReservationBuilder+Analytics.h"
#import "EHIServices+Location.h"
#import "EHILocationDetailsViewModel.h"
#import "EHILocationsMapListViewModel.h"
#import "EHIReservationRouter.h"
#import "EHILocationsSearchNoResultModal.h"

#define EHILocationsSearchDelay (0.5)

@interface EHILocationsMapViewModel ()
@property (copy     , nonatomic) NSString *title;
@property (copy     , nonatomic) NSArray *annotations;
@property (copy     , nonatomic) NSValue *animatedMapRegion;
@property (copy     , nonatomic) NSArray *locations;
@property (assign   , nonatomic) CLLocationDistance searchRadius;
@property (copy     , nonatomic) NSArray *activeFilters;
@property (strong   , nonatomic) EHILocationFilterDateQuery *dateQuery;
@property (strong   , nonatomic) id<EHINetworkCancelable> lastRequest;
@property (assign   , nonatomic) BOOL hasReorientedMap;
@property (assign   , nonatomic) BOOL filtersAllowed;
@property (assign   , nonatomic) BOOL isFirstLocationSearch;
@property (assign   , nonatomic) BOOL isNearbySearch;
@property (assign   , nonatomic) BOOL isShowingNoLocationModal;
@property (strong   , nonatomic) EHICalendarDateTimeInteractor *interactor;
@property (strong   , nonatomic) EHILocationsFilterListViewModel *filterListModel;
@property (nonatomic, readonly) EHISearchRegion nearbyRegion;
@property (nonatomic, readonly) EHIReservationBuilder *builder;
@end

@implementation EHILocationsMapViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _showsUserLocation     = [EHILocationManager sharedInstance].locationsAvailable;
        _isFirstLocationSearch = YES;
    }
    
    return self;
}

- (void)didResignActive
{
    [super didResignActive];
    
    // we don't want to animate in on our way back
    self.animatedMapRegion = nil;
}

- (void)didBecomeActive
{
    [super didBecomeActive];
    
    BOOL shouldShowNoLocationModal = self.shouldShowNoLocationModal;
    if(shouldShowNoLocationModal) {
        [self showNoLocationsModal];
    }
}

# pragma mark - Location Selection

- (BOOL)shouldSelectIndexPath:(NSIndexPath *)indexPath
{
    return indexPath.section == EHILocationsMapSectionHeader || indexPath.section == EHILocationsMapSectionLocations;
}

- (void)selectMapAnnotation:(EHILocationAnnotation *)annotation
{
    _currentAnnotation = annotation;
    
    if(annotation) {
        [EHIAnalytics trackAction:EHIAnalyticsLocActionPin handler:^(EHIAnalyticsContext *context) {
            [self.builder encodeLocationSelection:annotation.location context:context];
        }];
    }
}

//
// Helpers
//

- (EHILocation *)modelForIndexPath:(NSIndexPath *)indexPath;
{
    return self.locations[indexPath.row];
}

- (void)filterTappedInSection:(EHIDateTimeComponentSection)section
{
    EHICalendarData *data = [self calendarDataForSection:section];
    __weak __typeof(self) welf = self;
    [self.interactor handleChangesInSection:section with:data completion:^(NSDate *pickupValue, NSDate *returnValue) {
		[welf updateDateQueryInSection:section pickupValue:pickupValue returnValue:returnValue];
	}];
}

- (void)updateDateQueryInSection:(EHIDateTimeComponentSection)section pickupValue:(NSDate *)pickupValue returnValue:(NSDate *)returnValue
{
    switch(section) {
        case EHIDateTimeComponentSectionPickupDate:
        case EHIDateTimeComponentSectionReturnDate: {
            self.dateQuery.pickupDate = pickupValue;
            self.dateQuery.returnDate = returnValue;
            break;
        }
        case EHIDateTimeComponentSectionPickupTime:
        case EHIDateTimeComponentSectionReturnTime: {
            self.dateQuery.pickupTime = pickupValue;
            self.dateQuery.returnTime = returnValue;
            break;
        }
    }
    
    [self refetchLocations];
}

- (EHICalendarData *)calendarDataForSection:(EHIDateTimeComponentSection)section
{
    EHICalendarData *data = [EHICalendarData new];
    switch(section) {
        case EHIDateTimeComponentSectionPickupDate:
        case EHIDateTimeComponentSectionReturnDate: {
            data.pickupDate = self.dateQuery.pickupDate;
            data.returnDate = self.dateQuery.returnDate;
            break;
        }
        case EHIDateTimeComponentSectionPickupTime:
        case EHIDateTimeComponentSectionReturnTime: {
            data.pickupTime = self.dateQuery.pickupTime;
            data.returnTime = self.dateQuery.returnTime;
            break;
        }
    }

	return data;
}

- (void)closeTip
{
    [self.filterListModel closeTip];
}

# pragma mark - Filters

- (void)setFilterQuery:(EHILocationFilterQuery *)filterQuery
{
    [super setFilterQuery:filterQuery];
    
    // update our locations
    self.locations     = filterQuery.locations;
    self.activeFilters = filterQuery.activeFilters;
    
    [self.filterListModel updateActiveFilters:self.activeFilters];
    
    if(filterQuery.datesFilter) {
        self.dateQuery = filterQuery.datesFilter;
        [self assembleWithProvider:filterQuery];
    }

    // update the visible banner type
    self.bannerType = filterQuery.activeFilters.count ? EHILocationsMapBannerTypeFilter : EHILocationsMapBannerTypeNone;
}

- (EHILocationFilterQuery *)buildFilterQuery
{
    EHILocationFilterQuery *filterQuery = super.filterQuery ?: EHILocationFilterQuery.new;
 
    // update the filter from our current state
    filterQuery.locations    = self.locations;
    filterQuery.region       = self.nearbyRegion;
    filterQuery.datesFilter  = self.dateQuery;
    filterQuery.locationType = self.queryType;
   
    return filterQuery;
}

- (EHILocationFilterDateQuery *)dateQuery
{
    if(!_dateQuery) {
        _dateQuery = [EHILocationFilterDateQuery new];
    }
    
    return _dateQuery;
}

- (void)clearAllFilters
{
    [self clearAllFiltersTrackingEvent:YES];
}

- (void)clearAllFiltersTrackingEvent:(BOOL)tracking
{
    if(tracking) {
        [EHIAnalytics trackAction:EHIAnalyticsActionClearAllFilters handler:nil];
    }
    
    [self.filterListModel clearFiltersQuery];
    [self.filterListModel clearDateQuery];
    self.filterQuery = [EHILocationFilterQuery new];
    self.dateQuery   = [EHILocationFilterDateQuery new];
    
    [self refetchLocations];
}

- (void)refetchLocations
{
    _lastRequest = nil;
    
    [self kickoffNearbyLocationSearch];
}

- (BOOL)shouldShowFallback
{
    return !self.isLoading && self.locations.count == 0 && !self.isShowingNoLocationModal;
}

- (void)transitionToFilterScreen
{
    self.router.transition
        .push(EHIScreenLocationFilter).object([self buildFilterQuery]).start(nil);
}

- (void)showSelectedLocationDetails
{
    [self showSelectedLocationDetailsForItemAtIndexPath:nil];
}

- (void)showSelectedLocationDetailsForItemAtIndexPath:(NSIndexPath *)indexPath
{
    EHILocation *location = indexPath ? [self modelForIndexPath:indexPath] : self.currentAnnotation.location;
    EHILocationDetailsViewModel *viewModel = [[EHILocationDetailsViewModel alloc] initWithModel:location];
    
    __weak typeof(self) welf = self;
    viewModel.computeDatesBlock = ^{
        [welf computeDatesForLocation:location];
    };
    
    self.router.transition
        .push(EHIScreenLocationDetails).object(viewModel).start(nil);
}

- (void)initReservationWithSelectedLocation
{
    [self initReservationWithSelectedLocationAtIndexPath:nil];
}

- (void)initReservationWithSelectedLocationAtIndexPath:(NSIndexPath *)indexPath
{
    EHILocation *location = indexPath ? [self modelForIndexPath:indexPath] : self.currentAnnotation.location;
    
    [self computeDatesForLocation:location];
    
    if(indexPath) {
        [self trackLocation:location selectedFromListAtIndexPath:indexPath];
    } else {
        [self trackLocation:location];
    }
    
    // then select the location
    [self.builder selectLocation:location];
}

- (void)computeDatesForLocation:(EHILocation *)location
{
    self.location = location;
    
    EHIReservationBuilderFlow currentFlow = self.builder.currentFlow;
    
    // mark it as location search, to skip the checks over other flows
    self.builder.currentFlow  = EHIReservationBuilderFlowLocationSearch;
    
    if(self.shouldSendPickupDate) {
        self.builder.pickupDate = self.filterQuery.pickupDate;
    }
    
    if(self.shouldSendPickupTime) {
        self.builder.pickupTime = self.canSendPickupTime ? self.filterQuery.pickupTime : nil;
    }
    
    if(self.shouldSendDropoffDate) {
        self.builder.returnDate = self.canSendDropoffDate ? self.filterQuery.returnDate : nil;
    }
    
    if(self.shouldSendDropoffTime) {
        self.builder.returnTime = self.canSendDropoffTime ? self.filterQuery.returnTime : nil;
    }
    
    if(self.shouldWipeDropoffData) {
        self.builder.returnDate = nil;
        self.builder.returnTime = nil;
    }
    
    if(self.queryType == EHILocationFilterQueryLocationTypeDropoffOneWay && self.builder.pickupDate == nil) {
        self.builder.pickupDate = self.filterQuery.pickupDate;
        self.builder.pickupTime = self.canSendPickupTime ? self.filterQuery.pickupTime : nil;
        self.builder.returnDate = self.canSendDropoffDate ? self.filterQuery.returnDate : nil;
        self.builder.returnTime = self.canSendDropoffTime ? self.filterQuery.returnTime : nil;
    }
    
    BOOL isValidDates = [self.builder.pickupDate ehi_isBefore:self.builder.returnDate];
    if(!isValidDates) {
        self.builder.returnDate = nil;
        self.builder.returnTime = nil;
    }
    
    self.builder.currentFlow = currentFlow;
}

# pragma mark - City Updating

- (void)updateWithModel:(id)model
{
    [super updateWithModel:model];
    
    void (^filterListFillBlock)() = ^{
        self.filterQuery.datesFilter.pickupDate = self.builder.pickupDate;
        self.filterQuery.datesFilter.returnDate = self.builder.returnDate;
        self.filterQuery.datesFilter.pickupTime = self.builder.pickupTime;
        self.filterQuery.datesFilter.returnTime = self.builder.returnTime;
        
        [self assembleWithProvider:self.filterQuery];
    };
    
    if([model isKindOfClass:[EHILocation class]]) {
        filterListFillBlock();
        self.location = model;
    }
    // if we have a city, then update with that single city
    if([model isKindOfClass:[EHICity class]]) {
        filterListFillBlock();
        self.city = model;
    }
    // if we have a user location, let's see if we have the current location
    else if([model isKindOfClass:[EHIUserLocation class]]) {
        filterListFillBlock();
        self.nearbyLocation = model;
    }
    // if we have a filter query, update our locations
    else if([model isKindOfClass:[EHILocationFilterQuery class]]) {
        self.filterQuery = model;
    }
}

- (void)setLocation:(EHILocation *)location
{
    [super setLocation:location];
    
    self.title = location.localizedName;
    self.animatedMapRegion = [self defaultRegionForCoordinate:location.position.coordinate];
    
    // fetch the locations for the city
    [self kickoffSearchAtCoordinate:location.position.coordinate];
}

- (void)setCity:(EHICity *)city
{
    _city = city;
    
    self.title = city.formattedName;
    self.animatedMapRegion = [self defaultRegionForCoordinate:city.position.coordinate];
    
    // fetch the locations for the city
    [self kickoffSearchAtCoordinate:city.position.coordinate];
}

- (void)setNearbyLocation:(EHIUserLocation *)nearbyLocation
{
    _nearbyLocation = nearbyLocation;
   
    self.title = EHILocalizedString(@"locations_nearby_title", @"Nearby Locations", @"Title for the 'Nearby Locations' search");
    self.isNearbySearch = YES;
    
    [nearbyLocation currentLocationWithHandler:^(CLLocation *location, NSError *error) {
        // center the map on the location before we do anything
        self.animatedMapRegion = [self defaultRegionForCoordinate:location.coordinate];
        // fetch locations nearby this coordinate
        [self kickoffSearchAtCoordinate:location.coordinate];
    }];
}

//
// Setters
//

- (void)setLocations:(NSArray *)locations
{
    _locations = locations;
   
    self.fallback = locations.count ? nil : [EHIModel placeholder];
    self.annotations = [self annotationsFromLocations:locations];
    self.listModels  = (locations ?: @[]).map(^(EHILocation *location){
        EHILocationsMapListViewModel *model = [[EHILocationsMapListViewModel alloc] initWithModel:location];
        model.layout      = EHILocationsMapListLayoutList;
        model.isOneWay    = self.builder.isPickingOneWayReservation;
        model.filterQuery = self.filterQuery;
        
        return model;
    });
}

- (BOOL)shouldShowNoLocationModal
{
    BOOL isEmpty      = self.locations != nil && self.locations.count == 0;
    BOOL isFiltering  = self.filterQuery != nil && !self.filterQuery.isEmpty;
    
    return isEmpty && isFiltering;
}

# pragma mark - Nearby Search

- (void)setIsPanningMap:(BOOL)isPanningMap
{
    _isPanningMap = isPanningMap;
    
    // once the user pans, we'll mark the map as reoriented
    self.hasReorientedMap = YES;
}

- (void)setHasReorientedMap:(BOOL)hasReorientedMap
{
    _hasReorientedMap = hasReorientedMap;
   
    // cancel any previously scheduled search call
    [NSObject cancelPreviousPerformRequestsWithTarget:self selector:@selector(kickoffNearbyLocationSearch) object:nil];
    
    // after re-orienting kickoff a delayed location search as long as we're not panning
    if(hasReorientedMap) {
        if(self.isPanningMap) {
            [self willFetchLocations];
        } else {
            [self performSelector:@selector(kickoffNearbyLocationSearch) withObject:nil afterDelay:EHILocationsSearchDelay];
        }
    }
}

- (void)kickoffNearbyLocationSearch
{
    // fire off a search at the map center, using the visible radius
    [self fetchLocationsForRegion:self.nearbyRegion completion:nil];
}

- (EHISearchRegion)nearbyRegion
{
    return (EHISearchRegion) {
        .center = self.visibleRegion.center,
        .radius = MKCoordinateRegionDiagonalInMeters(self.visibleRegion) / 2.0,
    };
}

# pragma mark - Searching

- (void)kickoffSearchAtCoordinate:(CLLocationCoordinate2D)coordinate
{
    // fire off a request centered at this coordinate
    [self fetchLocationsForRegion:(EHISearchRegion){ .center = coordinate } completion:nil];
}

- (void)fetchLocationsForRegion:(EHISearchRegion)region completion:(void(^)(BOOL))completion
{    
    // capture the current request
    __block id<EHINetworkCancelable> request;
    
    [self willFetchLocations];
    
    __weak typeof(self) welf = self;
    self.animateSpinnerLoading = YES;
    request = [[EHIServices sharedInstance] fetchLocationsForRegion:region filters:self.filterQuery handler:^(EHISpatialLocations *query, EHIServicesError *error) {
        welf.animateSpinnerLoading = NO;
        if(request != welf.lastRequest) {
            ehi_call(completion)(NO);
            return;
        }
       
        // destroy the query results if we got an error
        if(error.hasFailed) {
            query = nil;
        }
        
        [welf didFetchLocationsWithQuery:query];
        
        // update the animated region if we weren't specifying the radius (nearby)
        NSValue *animatedRegion = region.radius ? nil : [welf regionForCenter:region.center radius:query.radius * 1000.0];
        if(animatedRegion) {
            welf.animatedMapRegion = animatedRegion;
        }
        
        // finish loading, nil out the map center so that the modify location button hides
        welf.lastRequest = nil;
        welf.hasReorientedMap = NO;
        
        // call the completion for this request if we have one
        ehi_call(completion)(YES);
        
        // flag that first search is over
        welf.isFirstLocationSearch = NO;
    }];
    
    self.lastRequest = request;
}

- (void)willFetchLocations
{
    // if we don't have filters, don't allow any until we get results back
    if(!self.activeFilters && self.filtersAllowed) {
        self.filtersAllowed = NO;
    }
}

- (void)didFetchLocationsWithQuery:(EHISpatialLocations *)query
{
    // mark locations as nearby, if needed
    if(self.isNearbySearch) {
        (query.locations ?: @[]).each(^(EHILocation *location) {
            location.isNearbyLocation = YES;
        });
    }
    
    // update the locations
    self.locations    = query.locations;
    self.searchRadius = query.radius * CLLocationDistanceKilometersToMeters;
    
    // on first search, alert user of offbrand locations, if any
    if(self.isFirstLocationSearch && query.hasOffbrandLocations) {
        [self showDriveAllianceModal];
    }

    // if we don't have filters, update whether they're available
    if(!self.activeFilters) {
        self.filtersAllowed = !query.hasOffbrandLocations;
        self.bannerType = query.hasOffbrandLocations ? EHILocationsMapBannerTypeOffbrand : EHILocationsMapBannerTypeNone;
    }
}

- (void)setLastRequest:(id<EHINetworkCancelable>)lastRequest
{
    if(_lastRequest == lastRequest) {
        return;
    }
   
    [_lastRequest cancel];
    _lastRequest   = lastRequest;
    self.isLoading = lastRequest != nil;
}

//
// Helpers
//

- (EHIViewModel *)calloutModelForLocation:(EHILocation *)location
{
    EHILocation *loc = (self.locations ?: @[]).find(^(EHILocation *loc){
        return [loc.uid isEqualToString:location.uid];
    });
    
    EHILocationsMapListViewModel *model = [[EHILocationsMapListViewModel alloc] initWithModel:loc];
    model.filterQuery = self.filterQuery;
    
    return model;
}

- (void)showNoLocationsModal
{
    self.isShowingNoLocationModal = YES;
    
    EHILocationsSearchNoResultModal *modal = [[EHILocationsSearchNoResultModal alloc] initWithFilterQuery:[self buildFilterQuery]];
    
    __weak typeof(self) welf = self;
    [modal presentWithCompletion:^(BOOL wantsToEdit) {
        welf.isShowingNoLocationModal = NO;
        if(wantsToEdit) {
            [welf transitionToFilterScreen];
        } else {
            [welf clearAllFiltersTrackingEvent:NO];
        }
    }];
}

- (void)showDriveAllianceModal
{
    // show drive alliance modal
    EHIInfoModalViewModel *infoViewModal = [EHIInfoModalViewModel new];
    infoViewModal.title = EHILocalizedString(@"info_modal_drive_alliance_title", @"We're sorry, but there aren't any Enterprise locations in this area yet.", @"title for drive alliance info modal");
    infoViewModal.detailsNibName = @"EHILocationsDriveAllianceView";
    infoViewModal.secondButtonTitle = EHILocalizedString(@"standard_ok_text", @"OK", @"");
    
    [infoViewModal present:nil];
}

# pragma mark - Annotations

- (NSArray *)annotationsFromLocations:(NSArray *)locations
{
    if(!locations) {
        return nil;
    }

    NSArray *annotations = locations.select(^(EHILocation *location){
        return location.position != nil;
    }).map(^(EHILocation *location) {
        location.pickupDate  = self.dateQuery.pickupDate;
        location.dropOffDate = self.dateQuery.returnDate;
        return [[EHILocationAnnotation alloc] initWithLocation:location];
    });
    
    return [self offsetOverlappingAnnotations:annotations];
}

- (NSArray *)offsetOverlappingAnnotations:(NSArray *)annotations
{
    // the threshold in geo-coord degrees below which we'll move our pins
    const CLLocationDegrees distanceThreshold = 0.003;
    
    for(EHILocationAnnotation *annotation in annotations) {
        // we're going to do a naive n^2 pass through our annotations to move them apart
        for(EHILocationAnnotation *other in annotations) {
            // if we hit ourselves, then we should stop shifting up. this allows the last view to shift up
            // the correct number of times
            if(annotation == other) {
                break;
            }
            
            // capture the location's raw coordinates
            CLLocationCoordinate2D coordinate      = annotation.coordinate;
            CLLocationCoordinate2D otherCoordinate = other.coordinate;
            
            // get the distance to see if we exceed our threshold
            CLLocationDegrees distance = CLLocationCoordinate2DDistance(coordinate, otherCoordinate);
            if(distance < distanceThreshold) {
                // accumulate an offset for each overlapping pin hit
                annotation.offset = CLLocationCoordinate2DOffset(annotation.offset, -distanceThreshold, 0.0f);
            }
        }
    }
    
    return annotations;
}

# pragma mark - Regions

- (NSValue *)regionForCenter:(CLLocationCoordinate2D)center radius:(CLLocationDistance)radius
{
    return NSValueBox(MKCoordinateRegion, MKCoordinateRegionMakeWithDistance(center, radius, radius));
}

- (NSValue *)defaultRegionForCoordinate:(CLLocationCoordinate2D)coordinate
{
    return NSValueBox(MKCoordinateRegion, MKCoordinateRegionMakeWithDistance(
        coordinate,
        15.0 * CLLocationDistanceMilesToMeters,
        15.0 * CLLocationDistanceMilesToMeters
    ));
}

# pragma mark - Accessors

- (NSString *)scrollToTopTitle
{
    return EHILocalizedString(@"locations_scrolls_title", @"RETURN TO TOP", @"Title for locations map 'Return to Top' button");
}

- (NSString *)offbrandLocationsText
{
    return EHILocalizedString(@"locations_offbrand_message", @"We didn't find any Enterprise locations. You can still rent through our Drive Alliance partners Alamo Rent a Car and National Car Rental!", @"Message for the offbrand locations banner");
}

- (EHIReservationBuilder *)builder
{
    return [EHIReservationBuilder sharedInstance];
}

- (EHILocationsFilterListViewModel *)filterListModel
{
	if(!_filterListModel) {
		_filterListModel = [EHILocationsFilterListViewModel new];
        _filterListModel.isFromLDT   = [self.router isKindOfClass:EHIReservationRouter.class];
        _filterListModel.isFiltering = self.filterQuery.isFilteringByDates;

        if(_filterListModel.showFilterTip) {
            [EHISettings didShowLocationsMapFilterTip];
        }
	}

	return _filterListModel;
}

# pragma mark - EHILocationInteractorViewModel

- (id<EHIDateTimeUpdatableProtocol>)updatable
{
	return self.filterListModel;
}

- (EHISingleDateCalendarFlow)flow
{
    return EHISingleDateCalendarFlowLocationsMap;
}

- (BOOL)isSelectingPickupLocation
{
    return self.builder.searchTypeOverride == EHILocationsSearchTypePickup;
}

- (BOOL)hasDropoffLocation
{
    return self.builder.returnLocation != nil;
}

# pragma mark - Analytics

- (void)updateAnalyticsContext:(EHIAnalyticsContext *)context
{
    [super updateAnalyticsContext:context];
    
    // encode whatever locations we've got
    [self.builder synchronizeLocationsOnContext:context];
    
    // encode the city, if it exists
    [context encode:[EHICity class] encodable:self.city];
    // encode the user location, if it exists
    [context encode:[EHIUserLocation class] encodable:self.nearbyLocation prefix:EHIAnalyticsCurrentPrefix];
    [context encode:[EHILocationFilterQuery class] encodable:self.filterQuery];
}

- (void)trackLocation:(EHILocation *)location selectedFromListAtIndexPath:(NSIndexPath *)indexPath
{
    // track the selection action
    [EHIAnalytics trackAction:EHIAnalyticsLocActionLocation handler:^(EHIAnalyticsContext *context) {
        [self.builder encodeLocation:location context:context];
        [context setRouterState:EHIScreenLocationsList];
        context.macroEvent = EHIAnalyticsMacroEventSelectLocation;
        context[EHIAnalyticsLocConflict] = @(location.hasConflicts);
        context[EHIAnalyticsLocRankKey] = @(indexPath.item);
    }];
}

- (void)trackLocation:(EHILocation *)location
{
    // track the selection
    [EHIAnalytics trackAction:EHIAnalyticsLocActionModal handler:^(EHIAnalyticsContext *context) {
        [context setRouterState:EHIScreenLocationsMap];
        context[EHIAnalyticsLocConflict] = @(location.hasConflicts);
        [self.builder encodeLocationSelection:location context:context];
    }];
}

# pragma mark - MTRReactive

+ (NSArray *)nonreactiveProperties:(EHILocationsMapViewModel *)model
{
    return @[
        @key(model.city),
        @key(model.nearbyLocation),
        @key(model.lastRequest),
        @key(model.mapCenter),
        @key(model.visibleRegion),
        @key(model.searchRadius),
        @key(model.showsUserLocation),
        @key(model.hasReorientedMap),
    ];
}

@end
