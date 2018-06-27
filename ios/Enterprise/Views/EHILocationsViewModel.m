//
//  EHILocationsViewModel.m
//  Enterprise
//
//  Created by Ty Cobb on 1/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHILocationsViewModel.h"
#import "EHILocationSearchQuery.h"
#import "EHILocationManager.h"
#import "EHIFavoritesManager.h"
#import "EHIDataStore.h"
#import "EHIServices+Location.h"
#import "EHIReservationBuilder+Analytics.h"

@interface EHILocationsViewModel ()
@property (strong, nonatomic) id<EHINetworkCancelable> activeRequest;
@property (strong, nonatomic) NSDictionary *sectionHeaders;
@property (strong, nonatomic) EHILocationSearchQuery *locationQuery;
@property (copy  , nonatomic) NSString *searchPlaceholder;
@property (strong, nonatomic, readonly) EHIReservationBuilder *builder;
@end

@implementation EHILocationsViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        // populate the static outlets
        _sectionHeaders = [EHISectionHeaderModel modelsWithTitles:@[
            [NSNull null],
            [NSNull null],
            EHILocalizedString(@"locations_favorites_header_title", @"FAVORITES", @"Title for 'favorites' search section"),
            EHILocalizedString(@"locations_recents_header_title", @"RECENT ACTIVITY", @"Title for 'recents' search section"),
            [NSNull null],
            [NSNull null],
            EHILocalizedString(@"locations_zipcode_header_title", @"ZIP CODES", @"Title for 'zip codes' search section"),
        ]];
        
        // check the reservation builder to figure out if we are looking for a pickup/return location
        _searchPlaceholder = self.builder.currentSearchType == EHILocationsSearchTypePickup
            ? EHILocalizedString(@"locations_pickup_search_placeholder", @"Enter a pick-up location", @"Placeholder for locations search field when searching for a pickup location")
            : EHILocalizedString(@"locations_return_search_placeholder", @"Enter a return location", @"Placeholder for locations search field when searching for a return location");
        
        // recents header needs to display the clear button
        EHISectionHeaderModel *header = [self headerForSection:EHILocationSectionRecents];
        header.style = EHISectionHeaderStyleAction;
        header.actionButtonTitle = EHILocalizedString(@"locations_clear_section_title", @"CLEAR", @"Title for the 'clear' action on the section header");
    }
    
    return self;
}

- (void)didBecomeActive
{
    [super didBecomeActive];
    
    // refresh the results whenever we're coming on screen
    [self refreshResults];
}

- (void)updateWithModel:(id)model
{
    [super updateWithModel:model];
    
#ifdef TESTS
    [self invalidateResultsWithLocations:model query:nil];
#endif
}

# pragma mark - Services

- (void)fetchLocationsForQuery:(NSString *)query
{
    self.locationQuery = [EHILocationSearchQuery new];
    self.locationQuery.query = query;
    
    // if the builder is active, we are searching for a one way reservation
    self.locationQuery.isOneWay = self.builder.isActive;

    // the request we're about to kick off
    __block id<EHINetworkCancelable> request;
    __weak  typeof(self) welf = self;
    
    // cancel the active request if there is one
    [self.activeRequest cancel];
    
    request = [[EHIServices sharedInstance] fetchLocationsForQuery:self.locationQuery handler:^(EHILocations *locations, EHIServicesError *error) {
        if([welf.activeRequest isEqual:request]) {
            // clear the active request upon completion
            [welf setActiveRequest:nil];
            
            // update the results as long as we didn't error
            if(!error.hasFailed) {
                [welf invalidateResultsWithLocations:locations query:query];
            }
        }
    }];
    
    self.activeRequest = request;
}

# pragma mark - Results 

- (void)clearResults
{
    [self invalidateResultsWithLocations:nil query:nil];
}

- (void)refreshResults
{
    [self invalidateResultsWithLocations:self.locations query:self.failingQuery];
}

- (void)invalidateResultsWithLocations:(EHILocations *)locations query:(NSString *)query
{
    // treat an empty responses as nil for simplicity's sake
    if(!locations.all.count && !locations.cities.count) {
        locations = nil;
    }
    
    // always update the locations and failing query
    self.locations    = locations;
    self.failingQuery = !locations && query ? query : nil;
    
    // if we have a failed query, ensure everything is cleared out
    if(self.failingQuery) {
        self.nearby    = nil;
        self.favorites = nil;
        self.recents   = nil;
        self.cities    = nil;
        self.airports  = nil;
    }
    // if we have no locations or query then clear out our results
    else if(!locations) {
        self.nearby    = [EHIUserLocation location];
        self.favorites = [EHIFavoritesManager sharedInstance].favoriteLocations;
        self.cities    = nil;
        self.airports  = nil;
       
        // pull out whatever recent locations we have from the data store
        [EHIDataStore find:[EHILocation class] handler:^(NSArray *models) {
            if(!self.locations && !self.failingQuery) {
                self.recents = models.first(5).select(^(EHILocation *location) {
                    return ![self.favorites containsObject:location];
                });
            }
        }];
    }
    // otherwise, we should have valid locations
    else {
        self.failingQuery = nil;
        self.nearby       = nil;
        self.favorites    = nil;
        self.recents      = nil;

        self.cities   = locations.cities;
        self.airports = locations.all.each(^(EHILocation *location) {
            location.hidesDetails = YES;
        });
    }
}

# pragma mark -  Setters

- (void)setQuery:(NSString *)query
{
    if(_query != query) {
        _query = query;
        [self didUpdateQuery:query];
    }
}

- (void)setFailingQuery:(NSString *)failingQuery
{
    BOOL hasAlreadyFailed = _failingQuery != nil;
    
    _failingQuery = failingQuery;
    
    // update the analytics context before tracking
    [self invalidateAnalyticsContext];
    // if this is our first failure, fire the zero results action
    if(!hasAlreadyFailed && failingQuery) {
        [EHIAnalytics trackAction:EHIAnalyticsLocActionNoLocations handler:nil];
    }
}

- (void)setActiveRequest:(id<EHINetworkCancelable>)activeRequest
{
    if(_activeRequest != activeRequest) {
        _activeRequest = activeRequest;
        [self didUpdateLoadingState];
    }
}

//
// Helpers
//

- (void)didUpdateQuery:(NSString *)query
{
    // update the analytics context
    [self invalidateAnalyticsContext];
 
    // search if we have a query with more than 2 characters
    if(query.length > 2) {
        [self fetchLocationsForQuery:query];
    }
    // otherwise we have too few characters, so clear the results
    else {
        [self clearResults];
    }
}

- (void)didUpdateLoadingState
{
    id<EHINetworkCancelable> activeRequest = self.activeRequest;
   
    // if we are leaving loading state, leave immediately
    if(!activeRequest) {
        if(self.isLoading) {
            self.isLoading = NO;
        }
    }
    // if we are entering loading state, wait a second before showing activity indicator.
    // prevents activity indicator flashes on quick responses
    else {
        dispatch_after_seconds(1.0f, ^{
            if(self.activeRequest == activeRequest) {
                self.isLoading = YES;
            }
        });
    }
}

# pragma mark - Accessors

- (NSArray *)modelsForSection:(EHILocationSection)section
{
    switch(section) {
        case EHILocationSectionNearby:
            return self.nearby ? @[ self.nearby ] : nil;
        case EHILocationSectionEmptyQuery:
            return self.failingQuery ? @[ self.failingQuery ] : nil;
        case EHILocationSectionFavorites:
            return self.favorites;
        case EHILocationSectionRecents:
            return self.recents;
        case EHILocationSectionAirport:
            return self.airports;
        case EHILocationSectionCity:
            return self.cities;
    }
}

- (EHISectionHeaderModel *)headerForSection:(EHILocationSection)section
{
    return self.sectionHeaders[@(section)];
}

# pragma mark - Selection

- (id<EHIModel>)modelAtIndexPath:(NSIndexPath *)indexPath
{
    switch((EHILocationSection)indexPath.section) {
        case EHILocationSectionFavorites:
            return self.favorites[indexPath.item];
        case EHILocationSectionRecents:
            return self.recents[indexPath.item];
        case EHILocationSectionAirport:
            return self.airports[indexPath.item];
        case EHILocationSectionCity:
            return self.cities[indexPath.item];
        default: return nil;
    }
}

- (void)selectIndexPath:(NSIndexPath *)indexPath
{
    id model = [self modelAtIndexPath:indexPath];
    
    // transition directly into the res flow
    if([self modelAtIndexPathIsLocation:indexPath]) {
        [self selectLocation:model atIndexPath:indexPath];
    }
    // transition to the map with the selected city model
    else if(indexPath.section == EHILocationSectionCity) {
        [self selectCity:model atIndexPath:indexPath];
    }
    // transition to the map with the user's current location
    else if(indexPath.section == EHILocationSectionNearby) {
        [self selectCurrentLocation];
    }
}

//
// Helpers
//

- (BOOL)modelAtIndexPathIsLocation:(NSIndexPath *)indexPath
{
    return indexPath.section == EHILocationSectionAirport
        || indexPath.section == EHILocationSectionFavorites
        || indexPath.section == EHILocationSectionRecents;
}

- (void)selectLocation:(EHILocation *)location atIndexPath:(NSIndexPath *)indexPath
{
    // track selection, temporarily adding search rank / shortcut
    [EHIAnalytics trackAction:EHIAnalyticsLocActionLocation handler:^(EHIAnalyticsContext *context) {
        [self.builder encodeLocation:location context:context];
        
        context.macroEvent = EHIAnalyticsMacroEventSelectLocation;
        context[EHIAnalyticsLocRankKey]     = @(indexPath.item);
        context[EHIAnalyticsLocShortcutKey] = [self analyticsLocationShortcutForSection:indexPath.section];
    }];
    
    [self.builder selectLocation:location];
}

- (void)selectCity:(EHICity *)city atIndexPath:(NSIndexPath *)indexPath
{
    // encode the city into the context
    [EHIAnalytics trackAction:EHIAnalyticsLocActionCity handler:^(EHIAnalyticsContext *context) {
        // temp encode the city
        [context encode:[EHICity class] encodable:city];
        // temp encode the rank
        context[EHIAnalyticsLocRankKey] = @(indexPath.item);
    }];
    
    self.router.transition
        .push(EHIScreenLocationsMap).object(city).start(nil);
}

- (void)selectCurrentLocation
{
    // track nearby call
    [EHIAnalytics trackAction:EHIAnalyticsLocActionNearby handler:^(EHIAnalyticsContext *context) {
        context[EHIAnalyticsLocShortcutKey] = [self analyticsLocationShortcutForSection:EHILocationSectionNearby];
    }];
    
    [[EHIUserLocation location] currentLocationWithHandler:^(CLLocation *location, NSError *error) {
        if(location) {
            self.router.transition
                .push(EHIScreenLocationsMap).object(EHIUserLocation.location).start(nil);
        }
    }];
}

# pragma mark - Actions

- (void)clearRecentActivity
{
    EHIAlertViewBuilder *alertView = EHIAlertViewBuilder.new
        .title(EHILocalizedString(@"locations_confirm_delete_recents_title", @"Clear?", @"Delete confirmation title for recent locations"))
        .message(EHILocalizedString(@"locations_confirm_delete_recents_message", @"Clear all recent activity?", @""))
        .button(EHILocalizedString(@"standard_button_clear", @"Clear", @"Standard clear button title"))
        .cancelButton(nil);
    
    alertView.show(^(NSInteger index, BOOL canceled) {
        if(!canceled) {
            // destroy all locations and nil our our storage
            [EHIDataStore purge:[EHILocation class] handler:nil];
            [self setRecents:nil];
        }
    });
}

# pragma mark - Analytics

- (void)updateAnalyticsContext:(EHIAnalyticsContext *)context
{
    [super updateAnalyticsContext:context];
  
    // encode whatever locations we've got
    [self.builder synchronizeLocationsOnContext:context];
    
    // encode the rest of the view model state
    context[EHIAnalyticsLocQueryKey]  = self.query;
    context[EHIAnalyticsLocZeroKey]   = self.failingQuery ? @YES : nil;
}

//
// Helpers
//

- (NSString *)analyticsLocationShortcutForSection:(EHILocationSection)section
{
    switch(section) {
        case EHILocationSectionNearby:
            return EHIAnalyticsLocShortcutNearby;
        case EHILocationSectionRecents:
            return EHIAnalyticsLocShortcutRecent;
        case EHILocationSectionFavorites:
            return EHIAnalyticsLocShortcutFavorite;
        default: return nil;
    }
}

# pragma mark - Reactive

+ (NSArray *)nonreactiveProperties:(EHILocationsViewModel *)object
{
    return @[
        @key(object.activeRequest),
        @key(object.sectionHeaders),
    ];
}

# pragma mark - Passthrough

- (EHIReservationBuilder *)builder
{
    return [EHIReservationBuilder sharedInstance];
}

@end
