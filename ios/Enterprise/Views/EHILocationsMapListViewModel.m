//
//  EHILocationsMapListViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 5/29/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHILocationsMapListViewModel.h"
#import "EHILocationDetailsAfterHoursModalViewModel.h"
#import "EHILocationConflictDataProvider.h"
#import "EHIViewModel_Subclass.h"
#import "EHIReservationBuilder+Analytics.h"

@interface EHILocationsMapListViewModel ()
@property (strong, nonatomic) EHILocation *location;
@property (strong, nonatomic) EHILocationConflictDataProvider *conflictProvider;
@property (copy  , nonatomic) NSAttributedString *title;
@property (copy  , nonatomic) NSString *subtitle;
@property (assign, nonatomic) EHILocationsMapListStyle style;
@property (assign, nonatomic) BOOL isExpanded;
@property (assign, nonatomic) BOOL hasConflicts;
@end

@implementation EHILocationsMapListViewModel

- (instancetype)initWithModel:(EHILocation *)model
{
    if(self = [super initWithModel:model]) {
        self.location = model;
    }
    
    return self;
}

- (void)updateWithModel:(EHILocation *)model
{
    [super updateWithModel:model];

    self.location = model;
}

- (void)setLocation:(EHILocation *)location
{
    _location = location;

	self.conflictProvider = EHILocationConflictDataProvider.new.location(location).oneWay(self.isOneWay);

    __weak typeof(self) welf = self;
    self.conflictProvider.afterHoursBlock = ^{
        [welf showAfterHoursModal];
    };

    self.title    = [self titleForLocation:location];
    self.subtitle = location.address.formattedAddress ?: @"";
    self.style    = location.hasConflicts ? EHILocationsMapListStyleInvalid : EHILocationsMapListStyleValid;
}

- (void)showAfterHoursModal
{
    [self trackAction:EHIAnalyticsLocActionAboutAfterHours];
    
    EHILocationDetailsAfterHoursModalViewModel *modal = [EHILocationDetailsAfterHoursModalViewModel new];
    [modal present:^BOOL(NSInteger index, BOOL canceled) {
        return YES;
    }];
}

- (NSAttributedString *)titleForLocation:(EHILocation *)location
{
    EHIAttributedStringBuilder *title = EHIAttributedStringBuilder.new
    .text(location.displayName ?: @"").fontStyle(EHIFontStyleRegular, 23.0f).color(UIColor.ehi_greenColor);
    
    if(location.type == EHILocationTypeAirport && location.airportCode) {
        title.space.appendText(location.airportCode).fontStyle(EHIFontStyleLight, 18.0f).color(UIColor.ehi_blackColor);
    }
    
    return title.string;
}

- (void)changeState
{
    self.isExpanded = !self.isExpanded;

    NSString *action = self.isExpanded ? EHIAnalyticsLocActionShowHours : EHIAnalyticsLocActionHideHours;
    [self trackAction:action];
}

# pragma mark - Accessors

- (NSAttributedString *)afterHoursTitle
{
    return self.conflictProvider.afterHours;
}

- (NSAttributedString *)conflictTitle
{
    if(self.style != EHILocationsMapListStyleInvalid) {
        return nil;
    }
    
    NSString *state = self.isExpanded
        ? EHILocalizedString(@"locations_map_hide_hours", @"HIDE HOURS", @"")
        : EHILocalizedString(@"locations_map_view_hours", @"VIEW HOURS", @"");

    return EHIAttributedStringBuilder.new
        .appendText(self.conflictProvider.title)
        .appendText(@" - ")
        .appendText(state)
        .color([UIColor ehi_greenColor])
        .string;
}

- (NSString *)openHoursTitle
{
    if(self.style != EHILocationsMapListStyleInvalid) {
        return nil;
    }
    
	return EHILocalizedString(@"locations_map_location_hours_operation", @"Location's hours of operation on:", @"");
}

- (NSString *)openHours
{
    return self.conflictProvider.openHours;
}

- (NSString *)flexibleTravelTitle
{
    return self.style == EHILocationsMapListStyleInvalid
        ? EHILocalizedString(@"locations_map_flexible_travel_button", @"Flexible Travel?", @"")
        : nil;
}

- (BOOL)shouldShowDetails
{
    return self.conflictProvider.afterHours != nil;
}

- (BOOL)hasConflicts
{
    return self.style == EHILocationsMapListStyleInvalid;
}

//
// Helpers
//

- (void)trackAction:(NSString *)action
{
    [EHIAnalytics trackAction:action handler:^(EHIAnalyticsContext *context) {
        [context encode:[EHILocation class] encodable:self.location];
        [context setRouterState:self.currentAnalyticsState];
        [context encode:self.filterQuery.class encodable:self.filterQuery];
        context[EHIAnalyticsLocConflict] = self.location.isAllDayClosedForDropoff || self.location.isAllDayClosedForPickup
            ? EHIAnalyticsLocClosed : EHIAnalyticsLocNone;
    }];
}

- (NSString *)currentAnalyticsState
{
    return self.layout == EHILocationsMapListLayoutList ? EHIScreenLocationsList : EHIScreenLocationsMap;
}

@end
