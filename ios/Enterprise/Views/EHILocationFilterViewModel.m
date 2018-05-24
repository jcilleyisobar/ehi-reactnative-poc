//
//  EHILocationFilterViewModel.m
//  Enterprise
//
//  Created by mplace on 2/25/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILocationFilterViewModel.h"
#import "EHIViewModel_Subclass.h"
#import "EHIServices+Location.h"
#import "EHIDateTimeComponentFilterViewModel.h"
#import "EHIReservationBuilder.h"

@interface EHILocationFilterViewModel ()
@property (strong, nonatomic) EHILocationFilterDateQuery *dateQuery;
@property (copy  , nonatomic) NSArray *locations;
@property (assign, nonatomic) EHISearchRegion region;
@end

@implementation EHILocationFilterViewModel

- (void)updateWithModel:(EHILocationFilterQuery *)model
{
    [super updateWithModel:model];
    
    if([model isKindOfClass:[EHILocationFilterQuery class]]) {
        [self assembleWithProvider:model];
        self.locationTypeFilters  = model.locationTypeFilters  ? [[NSArray alloc] initWithArray:model.locationTypeFilters copyItems:YES]  : [EHIFilters locationTypeFilters];
        self.miscellaneousFilters = model.miscellaneousFilters ? [[NSArray alloc] initWithArray:model.miscellaneousFilters copyItems:YES] : [EHIFilters locationMiscellaneousFilters];
        self.dateQuery            = model.datesFilter ?: [EHILocationFilterDateQuery new];

		// update initial state location state
        self.locations = model.locations;
        self.region    = model.region;
    }
}

# pragma mark - Actions

- (void)applyFilters
{
    // fire the tracking action before popping
    [EHIAnalytics trackAction:EHIAnalyticsActionApplyFilter handler:^(EHIAnalyticsContext *context) {
        [context encode:[EHILocationFilterQuery class] encodable:self.filterQuery];
    }];
    
    // navigate back to the map screen with the filter query
    self.router.transition
        .pop(1).object(self.filterQuery).start(nil);
}

- (void)cancelFiltering
{
    // navigate back
    self.router.transition
        .pop(1).object(nil).start(nil);
}

- (void)didTapOnSection:(EHIDateTimeComponentSection)section
{
	EHICalendarData *data = [self calendarDataForSection:section];
	__weak __typeof(self) welf = self;
	[self.interactor handleChangesInSection:section with:data completion:^(NSDate *pickupValue, NSDate *returnValue) {
		[welf updateDateQueryInSection:section pickupValue:pickupValue returnValue:returnValue];
	}];
}

// TODO: Move it to the base class when we have a proper way to store the data
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
    
    [self invalidateLocationsCompletion:nil];
}

# pragma mark - Actions

- (void)didTapOnCleanSection:(EHIDateTimeComponentSection)section
{
    [self trackSectionClear:section];

	switch(section) {
        case EHIDateTimeComponentSectionPickupDate: {
            [self.updatable setDate:nil inSection:EHIDateTimeComponentSectionPickupDate];
            [self.updatable setDate:nil inSection:EHIDateTimeComponentSectionPickupTime];
            self.dateQuery.pickupTime = nil;
            self.dateQuery.pickupDate = nil;
            break;
        }
        case EHIDateTimeComponentSectionReturnDate: {
            [self.updatable setDate:nil inSection:EHIDateTimeComponentSectionReturnDate];
            [self.updatable setDate:nil inSection:EHIDateTimeComponentSectionReturnTime];
            self.dateQuery.returnTime = nil;
            self.dateQuery.returnDate = nil;
            break;
        }
        case EHIDateTimeComponentSectionPickupTime: {
            [self.updatable setDate:nil inSection:EHIDateTimeComponentSectionPickupTime];
			self.dateQuery.pickupTime = nil;
            break;
        }
        case EHIDateTimeComponentSectionReturnTime: {
            [self.updatable setDate:nil inSection:EHIDateTimeComponentSectionReturnTime];
			self.dateQuery.returnTime = nil;
            break;
        }
        default: break;
	}

    [self invalidateLocationsCompletion:nil];
    [self triggerContentRefresh];
}

- (void)trackSectionClear:(EHIDateTimeComponentSection)section
{
    NSString *action = nil;
    switch(section) {
        case EHIDateTimeComponentSectionPickupDate:
        case EHIDateTimeComponentSectionReturnDate: {
            action = EHIAnalyticsActionClearDate;
            break;
        }
        case EHIDateTimeComponentSectionPickupTime:
        case EHIDateTimeComponentSectionReturnTime: {
            action = EHIAnalyticsActionClearTime;
            break;
        }
    }

    [EHIAnalytics trackAction:action handler:^(EHIAnalyticsContext *context) {
        context.macroEvent = EHIAnalyticsMacroEventLocationClearDateTime;
    }];
}

- (void)clearDateSection
{
    [self.updatable setDate:nil inSection:EHIDateTimeComponentSectionPickupDate];
    [self.updatable setDate:nil inSection:EHIDateTimeComponentSectionPickupTime];
    [self.updatable setDate:nil inSection:EHIDateTimeComponentSectionReturnDate];
    [self.updatable setDate:nil inSection:EHIDateTimeComponentSectionReturnTime];
    
    self.dateQuery = [EHILocationFilterDateQuery new];
    
    [self triggerContentRefresh];
}

- (void)triggerContentRefresh
{
    self.shouldRefreshContent = !self.shouldRefreshContent;
}

# pragma mark - Filters

- (void)clearFilters
{
    [EHIAnalytics trackAction:EHIAnalyticsActionResetFilter handler:nil];
    
    // reload a fresh copy of the filters and consequently trigger the reaction
    self.locationTypeFilters  = [EHIFilters locationTypeFilters];
    self.miscellaneousFilters = [EHIFilters locationMiscellaneousFilters];
    [self clearDateSection];
    
    // reload the locations
    [self invalidateLocationsCompletion:nil];
}

- (void)selectFilterAtIndexPath:(NSIndexPath *)indexPath
{
    NSArray *filters = [self filtersForSection:indexPath.section];
    EHIFilters *filter = filters[indexPath.row];
    
    filter.isActive = !filter.isActive;
    [self invalidateLocationsCompletion:^(NSError *error) {
        if(error) {
            filter.isActive = NO;
        }
    }];
}

- (NSArray *)filtersForSection:(EHILocationFilterSection)section
{
    switch (section) {
        case EHILocationFilterSectionOpenDuringTravel:
            return nil;
        case EHILocationFilterSectionLocationType:
            return self.locationTypeFilters;
        case EHILocationFilterSectionMiscellaneous:
            return self.miscellaneousFilters;
    }
}

- (void)invalidateLocationsCompletion:(void (^)(NSError *error))completion
{
    [[EHIServices sharedInstance] fetchLocationsForRegion:self.region filters:self.filterQuery handler:^(EHISpatialLocations *spatial, EHIServicesError *error) {
        if(!error.hasFailed) {
            self.locations = spatial.locations;
        }
        
        ehi_call(completion)(error.internalError);
    }];
}

# pragma mark - Accessors

- (NSString *)title
{
    return EHILocalizedString(@"filter_view_navigation_title", @"Filters", @"");
}

- (NSString *)applyFilterButtonTitle
{
    return EHILocalizedString(@"location_filter_apply_filters_button_title_prefix", @"Apply Filters", @"");
}

- (EHISectionHeaderModel *)headerModelForSection:(EHILocationFilterSection)section
{
    NSString *title;
    switch(section) {
        case EHILocationFilterSectionOpenDuringTravel:
            title = EHILocalizedString(@"filter_details_days_section_title", @"OPEN DURING YOUR TRAVEL", @"");
            break;
        case EHILocationFilterSectionLocationType:
            title = EHILocalizedString(@"location_filter_location_type_header_title", @"Location Type", @"");
            break;
        case EHILocationFilterSectionMiscellaneous:
            title = EHILocalizedString(@"location_filter_miscellaneous_header_title", @"Looking for something else?", @"");
            break;
    }
    
    return [EHISectionHeaderModel modelWithTitle:title];
}

- (EHILocationFilterQuery *)filterQuery
{
    EHILocationFilterQuery *query = [EHILocationFilterQuery new];
    
    query.locations    = self.locations;
    query.region       = self.region;
    query.datesFilter  = self.dateQuery;
    query.locationType = self.queryType;
    query.locationTypeFilters  = self.locationTypeFilters;
    query.miscellaneousFilters = self.miscellaneousFilters;
    
    return query;
}

- (EHIDateTimeComponentViewModel *)dateTimeFilter
{
    if(!_dateTimeFilter) {
        _dateTimeFilter = [EHIDateTimeComponentFilterViewModel new];
    }
    
    return _dateTimeFilter;
}

- (EHIReservationBuilder *)builder
{
    return [EHIReservationBuilder sharedInstance];
}

# pragma mark - EHILocationCalendarViewModel

- (id<EHIDateTimeUpdatableProtocol>)updatable
{
	return self.dateTimeFilter;
}

- (EHISingleDateCalendarFlow)flow
{
    return EHISingleDateCalendarFlowLocationsFilter;
}

- (BOOL)isSelectingPickupLocation
{
    return self.builder.searchTypeOverride == EHILocationsSearchTypePickup;
}

- (BOOL)hasDropoffLocation
{
    return self.builder.returnLocation != nil;
}

# pragma mark - MTRReactive

+ (NSArray *)nonreactiveProperties:(EHILocationFilterViewModel *)model
{
    return @[
        @key(model.region),
    ];
}

@end
