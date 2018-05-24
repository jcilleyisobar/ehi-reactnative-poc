//
//  EHILocationDetailsViewModel.m
//  Enterprise
//
//  Created by Pawel Bragoszewski on 12.02.2015.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHILocationDetailsViewModel.h"
#import "EHIServices+Location.h"
#import "EHIReservationBuilder+Analytics.h"
#import "NAVViewController.h"
#import "EHILocationDetailsConflictViewModel.h"
#import "EHILocationDetailsInfoViewModel.h"

@interface EHILocationDetailsViewModel ()
@property (strong, nonatomic) EHILocation *location;
@property (copy  , nonatomic) NSArray *hours;
@property (copy  , nonatomic) NSArray *policies;
@property (copy  , nonatomic) NSDictionary *sectionHeaders;
@property (strong, nonatomic) EHILocationDetailsConflictViewModel *conflictsModel;
@property (strong, nonatomic) EHILocationDetailsInfoViewModel *infoModel;
@property (assign, nonatomic) BOOL isLoading;
@property (nonatomic, readonly) EHIReservationBuilder *builder;
@end

@implementation EHILocationDetailsViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _title = EHILocalizedString(@"location_details_title", @"Location Details", @"Title for the 'Location Details' screen");
        
        // generate the fixed section headers
        _sectionHeaders = [EHISectionHeaderModel modelsWithTitles:@[
            [NSNull null],
            [NSNull null],
            EHILocalizedString(@"location_details_info_header", @"LOCATION DETAILS", @"Title for the location details 'info' section"),
            EHILocalizedString(@"location_details_hours_header", @"HOURS THIS WEEK", @"Title for the location details 'hours' section"),
            EHILocalizedString(@"location_details_pickup_header", @"NEED A PICKUP?", @"Title for the location details 'pickup' section"),
            EHILocalizedString(@"location_details_policies_header", @"POLICY INFORMATION", @"Title for the location details 'policies' section"),
        ]];
        
        // TODO(R): There's gotta be a better way
        // check if we're currently rooted at the dashboard
        UINavigationController *navigationController = [UIApplication sharedApplication].keyWindow.rootViewController.navigationController;
        NAVViewController *rootController = navigationController.viewControllers.firstObject;
        BOOL isFromDashboard = [[rootController.class screenName] isEqualToString:EHIScreenDashboard];
        
        // if this is from the dashboard flow, or if the res builder is active, show "select location"
        _actionTitle = isFromDashboard || [EHIReservationBuilder sharedInstance].isActive
            ? EHILocalizedString(@"location_details_select_location_title", @"SELECT LOCATION", @"Title for the 'select location' button")
            : EHILocalizedString(@"location_details_start_reservation_title", @"START RESERVATION", @"Title for the 'start reservation' button");
    }
    
    return self;
}

- (void)updateWithModel:(EHILocation *)model
{
    [super updateWithModel:model];
    
    if([model isKindOfClass:[EHILocation class]]) {
        [self updateWithLocation:model];
    }
}

- (void)updateWithLocation:(EHILocation *)location
{
    self.location = location;
    self.policies = [self policiesFromLocation:location];
    self.hours    = [self hoursFromLocation:location];
    self.conflictsModel = [self shouldShowConflictsForLocation:location] ? [[EHILocationDetailsConflictViewModel alloc] initWithModel:location] : nil;
    self.infoModel = [[EHILocationDetailsInfoViewModel alloc] initWithModel:location];
    self.infoModel.isOneWay = self.builder.isPickingOneWayReservation;
}

- (BOOL)shouldShowConflictsForLocation:(EHILocation *)location
{
    if(!location.hasPickupConflicts && (location.hasDropoffConflicts && location.hasAfterHours)) {
        return NO;
    }
    
    return location.hasConflicts;
}

- (void)didInitialize
{
    [super didInitialize];
   
    // allow quick access via app shortcut
    [UIApplication addLocationShortcut:self.location];
    
    // we're going to assume we have recent enough cache for the details if we have policies
    if(self.location.policies.count > 0) {
        return;
    }
    
    __block EHILocation *updatedLocation;
    self.isLoading = YES;
    
    dispatch_group_t group = dispatch_group_create();
    
    dispatch_group_enter(group);
    [[EHIServices sharedInstance] updateDetailsForLocation:self.location handler:^(EHILocation *location, EHIServicesError *error) {
        updatedLocation = location;
        dispatch_group_leave(group);
    }];
    
    dispatch_group_enter(group);
    NSDate *today        = [NSDate ehi_today];
    NSDate *oneWeekAfter = [[NSDate ehi_today] ehi_addDays:7];
    [[EHIServices sharedInstance] updateHoursForLocation:self.location fromDate:today toDate:oneWeekAfter handler:^(EHILocation *location, EHIServicesError *error) {
        if (!error.hasFailed) {
            updatedLocation.hours = location.hours;
        }
        dispatch_group_leave(group);
    }];
    
    dispatch_group_notify(group, dispatch_get_main_queue(), ^{
        self.isLoading = NO;
        [self updateWithModel:updatedLocation];
    });
}

# pragma mark - Filters

- (NSArray *)hoursFromLocation:(EHILocation *)location
{
    if(location.hours.days.count <= 0) {
        return nil;
    }
    
    // get the endpoint for this week
    NSDate *start = [NSDate ehi_today];
    NSDate *end = [start ehi_addDays:6];
 
    // get all the available days for this week
    EHILocationTimes *timesKey;
    NSArray *availableDays = location.hours.days
    .map(^(NSString *key, EHILocationDay *day){
        [day.standardTimes updateWithDictionary:@{ @key(timesKey.date): key }];
        return day.standardTimes;
    }).select(^(EHILocationTimes *day) {
        NSDate *date = [day.date isKindOfClass:NSString.class]
            ? [(NSString *)day.date ehi_date]
            : day.date;
        return [date ehi_isBetweenDate:start andDate:end];
    }) ?: @[];
    
    // make sure we always have 7 days from today on, otherwise fill in "unavailable" days
    NSMutableArray *days = [NSMutableArray new];
    for (int i = 0; i < 7; i++) {
        NSDate *date = [start ehi_addDays:i];
        EHILocationTimes *availableDay = availableDays.find(^(EHILocationTimes *day) {
            return [day.date ehi_isEqual:date granularity:NSCalendarUnitDay];
        });
        
        if (!availableDay) {
            availableDay = [EHILocationTimes modelWithDictionary:@{@key(availableDay.date) : date.ehi_string}];
        }
        
        // when location is closed, SLOR returns nil, so lets add dummy data
        BOOL needsDummySlice = availableDay.isClosedAllDay && availableDay.slices.firstObject == nil;
        if(needsDummySlice) {
            EHILocationTimesSlice *dummy = [EHILocationTimesSlice new];
            dummy.times = availableDay;
            availableDay.slices = (NSArray<EHILocationTimesSlice> *)@[dummy];
        }
        
        [days addObject:availableDay];
    }
    
    return days.flatMap(^(EHILocationTimes *day) {
        return day.slices;
    });
}

- (NSArray *)policiesFromLocation:(EHILocation *)location
{
    // if we don't have policies yet, return nothing
    if(!location.policies) {
        return nil;
    }
   
    // otherwise, return at least the first three
    const NSInteger policiesDisplayed = 3;
    NSArray *result = location.policies.first(policiesDisplayed);
    
    // if we have more than that, add a placeholder for a link to view all
    if(location.policies.count > policiesDisplayed) {
        result = [result ehi_safelyAppend:[EHILocationPolicy placeholder]];
    }
    
    return result;
}

# pragma mark - Interaction Hooks

- (void)showPolicyAtIndexPath:(NSIndexPath *)indexPath
{
    if(indexPath.section == EHILocationDetailsSectionPolicies) {
        id policyOrPolicies = [self selectPolicyAtIndexPath:indexPath];
        
        // drill into the policy list
        if([policyOrPolicies isEqual:self.allPolicies]) {
            self.router.transition
            .push(EHIScreenPolicies).object(policyOrPolicies).start(nil);
        }
        // drill into the detail page for this policy
        else {
            self.router.transition
            .push(EHIScreenPolicyDetail).object(policyOrPolicies).start(nil);
        }
    }
}

- (id)selectPolicyAtIndexPath:(NSIndexPath *)indexPath
{
    NSAssert(indexPath.section == EHILocationDetailsSectionPolicies, @"Must pass an index path for the policy section");
    EHILocationPolicy *policy = self.policies[indexPath.item];
   
    // if this was our placeholder, return all the policies
    if(policy.isPlaceholder) {
        return self.allPolicies;
    }
    
    return policy;
}

- (void)selectLocation
{
    [EHIAnalytics trackAction:EHIAnalyticsLocActionLocation handler:^(EHIAnalyticsContext *context) {
        context.macroEvent = EHIAnalyticsMacroEventSelectLocation;
    }];
    
    // from map, we should compute dates before passing the location to the builder
    ehi_call(self.computeDatesBlock)();
    
    [self.builder selectLocation:self.location];
}

# pragma mark - Accessors

- (BOOL)isOnBrand
{
    return self.location.isOnBrand;
}

- (EHISectionHeaderModel *)headerForSection:(EHILocationDetailsSection)section
{
    return self.sectionHeaders[@(section)];
}

- (NSArray *)allPolicies
{
    return self.location.policies;
}

- (EHILocation *)pickupLocation
{
    return self.location.offersPickup ? self.location : nil;
}

- (EHIReservationBuilder *)builder
{
    return [EHIReservationBuilder sharedInstance];
}

# pragma mark - Analytics

- (void)updateAnalyticsContext:(EHIAnalyticsContext *)context
{
    [super updateAnalyticsContext:context];

    // synchronize any already selected locations
    [self.builder synchronizeLocationsOnContext:context];
    // encode this location as if it were selected
    [self.builder encodeLocation:self.location context:context];
}

# pragma mark - MTRReactive

+ (NSArray *)nonreactiveProperties:(EHILocationDetailsViewModel *)model
{
    return @[
        @key(model.sectionHeaders),
    ];
}

@end
