//
//  EHITimePickerViewModel.m
//  Enterprise
//
//  Created by mplace on 3/26/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHITimePickerViewModel.h"
#import "EHITimePickerTime.h"
#import "EHIInfoModalViewModel.h"
#import "EHIReservationBuilder.h"
#import "EHIReservationBuilder+Analytics.h"
#import "EHIServices+Location.h"
#import "EHILocationsMapViewModel.h"

typedef NS_ENUM(NSUInteger, EHITimePickerInfoType) {
    EHITimePickerInfoTypeNone,
    EHITimePickerInfoTypeClosed,
    EHITimePickerInfoTypeLastOpenTime,
    EHITimePickerInfoTypeAfterHours,
};

@interface EHITimePickerViewModel () <EHIReservationBuilderReadinessListener>
@property (nonatomic, readonly) EHIReservationBuilder *builder;
@property (copy  , nonatomic) NSArray *pickupTimes;
@property (copy  , nonatomic) NSArray *returnTimes;
@property (assign, nonatomic) EHITimePickerInfoType infoType;
@end

@implementation EHITimePickerViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _title = EHILocalizedString(@"time_picker_screen_title", @"Pick-up & Return Times", @"Title for the time picker screen");
        _selectionButtonTitle = EHILocalizedString(@"time_picker_button_title", @"SELECT", @"Title for the time picker selection button");
        _needLocationOpenLabelTitle = EHILocalizedString(@"ldt_location_closed_location_message_title", @"NEED A LOCATION OPEN AT THIS TIME?", @"");
        _searchForLocationsButtonTitle = EHILocalizedString(@"ldt_location_closed_location_button_title", @"FIND OPEN LOCATIONS", @"");
    }
    
    return self;
}

# pragma mark - EHIViewModel

- (void)didBecomeActive
{
    [super didBecomeActive];
    
    // default info button state
    self.infoType = EHITimePickerInfoTypeNone;
    
    [self.builder waitForReadiness:self];
}

- (void)didInitialize
{
    [super didInitialize];

    // fetch the hours for our pickup and return location/dates
    [self fetchHours];
}

- (void)fetchHours
{
    EHIReservationBuilder *builder = self.builder;
    
    // set the loading state
    self.isLoading = YES;
    dispatch_group_t group = dispatch_group_create();
    
    // fetch the pickup location hours
    dispatch_group_enter(group);
    [[EHIServices sharedInstance] fetchHoursForLocation:builder.pickupLocation date:builder.pickupDate handler:^(EHILocationDay *day, EHIServicesError *error) {
        [error consume];
        dispatch_group_leave(group);
        self.pickupTimes = [self generateTimesForDay:day isReturnDate:NO];
    }];
    
    // fetch the return location hours
    EHILocation *returnLocation = builder.returnLocation ?: builder.pickupLocation;
    
    dispatch_group_enter(group);
    [[EHIServices sharedInstance] fetchHoursForLocation:returnLocation date:builder.returnDate handler:^(EHILocationDay *day, EHIServicesError *error) {
        [error consume];
        dispatch_group_leave(group);
        self.returnTimes = [self generateTimesForDay:day isReturnDate:YES];
    }];
    
    // stop loading and update the UI
    dispatch_group_notify(group, dispatch_get_main_queue(), ^{
        self.isLoading = NO;
        
        // set the times based on the current scheduling step
        [self updateTimesForCurrentFlow];
    });
}

//
// Helper
//

- (NSArray *)generateTimesForDay:(EHILocationDay *)day isReturnDate:(BOOL)isReturnDate
{
    EHILocationTimes *standardHours = day.standardTimes;
    EHILocationTimes *dropTimes = day.dropTimes;
    
    // the first valid time is midnight
    NSDate *firstValidTime = [NSDate ehi_today];
    // the last valid time is 11:30 of the next day
    NSDate *lastValidTime = [firstValidTime ehi_addDays:1];
    // generate an offset for each half hour
    NSArray *times = @(0).upTo(([firstValidTime ehi_hoursUntilDate:lastValidTime] * 2) - 1);
    
    // map the offsets into times on the half hour
    times = times.map(^(NSNumber *offset) {
        NSDate *date = [firstValidTime ehi_addMinutes:offset.integerValue * 30];
        EHITimePickerTime *time = [[EHITimePickerTime alloc] initWithDate:date];
        
        if(standardHours != nil) {
            // check if the time is within our regularly open hours
            time.isClosed     = ![standardHours isOpenForDate:date];
            // check if the time is during close but available for drop offs
            if(dropTimes != nil) {
                time.isAfterHours = isReturnDate && time.isClosed && (dropTimes.isOpenAllDay || [dropTimes isOpenForDate:date]);
            }
            // determine if the time is the last closed time before open
            time.isOpenTime   = [standardHours doesOpenAtDate:date];
            // determine if the time is the closing time for a time slice
            time.isCloseTime  = [standardHours doesCloseAtDate:date];
        } else {
            time.isClosed = NO;
        }
        
        return time;
    });
    
    return times;
}

# pragma mark - Reactions

- (void)builderIsReady:(EHIReservationBuilder *)builder
{
    [MTRReactor autorun:self action:@selector(updateCurrentSchedulingStep:)];
}

- (void)updateCurrentSchedulingStep:(MTRComputation *)computation
{
    if(self.builder.currentSchedulingStep == EHIReservationSchedulingStepStepComplete) {
        // pop back to the itinerary screen after setting the return time
        [MTRReactor nonreactive:^{
            self.router.transition
            .pop(2).start(nil);
        }];
    }
}

- (void)updateTimesForCurrentFlow
{
    switch(self.builder.currentSchedulingStep) {
        case EHIReservationSchedulingStepPickupTime:
            self.times = self.pickupTimes;
            break;
        case EHIReservationSchedulingStepReturnTime:
            self.times = self.returnTimes;
            break;
        default: break;
    }
}

# pragma mark - Actions

- (BOOL)shouldSelectTimeAtIndexPath:(NSIndexPath *)indexPath
{
    EHITimePickerTime *time = [self timeAtIndexPath:indexPath];
    return !time.isClosed || (self.isPickingReturnTime && time.isAfterHours);
}

- (void)selectTimeAtIndexPath:(NSIndexPath *)indexPath
{
    EHITimePickerTime *time = [self timeAtIndexPath:indexPath];
    
    // update the builder with the selected time, depending on the current schedule step
    if(self.builder.currentSchedulingStep == EHIReservationSchedulingStepPickupTime) {
        self.builder.pickupTime = time.date;
    } else if(self.builder.currentSchedulingStep == EHIReservationSchedulingStepReturnTime) {
        self.builder.returnTime = time.date;
    }
    
    [self updateTimesForCurrentFlow];
}

- (void)triggerInfoAction
{
    // create the view model for this modal
    EHIInfoModalViewModel *model = [EHIInfoModalViewModel new];

    if(self.infoType == EHITimePickerInfoTypeAfterHours) {
        model.title   = EHILocalizedString(@"time_selection_after_hours_info_title", @"After Hours", @"Title for the after hours modal");
        model.details = EHILocalizedString(@"time_selection_after_hours_details", @"After Hours Drop Offs allows you to drop off and self return your rental even though it is outside normal business hours.", @"Details for the time selection after hours modal");
    }
    
    if(self.isLastPickupTime) {
        model.title   = EHILocalizedString(@"time_selection_pickup_close_time_info_title", @"Last Pickup", @"");
        model.details = EHILocalizedString(@"time_selection_pickup_close_time_details", @"Selected pickup time is the same as location close time. Please pickup your vehicle on or before selected time.", @"");
    }

    [model present:nil];
}

- (void)showLocationsMap
{
    EHILocationFilterQuery *filterQuery = self.filterQuery;
 
    BOOL isRoundTrip = self.isRoundTrip;
    if(!isRoundTrip) {
        if(self.isPickingReturnTime && self.builder.searchTypeOverride == EHILocationsSearchTypePickup) {
            filterQuery.oneWayNeedsDropoffData = YES;
        }
        if(!self.isPickingReturnTime && self.builder.searchTypeOverride == EHILocationsSearchTypeReturn) {
            filterQuery.oneWayNeedsPickupData = YES;
        }
    }
    
    EHILocationsMapViewModel *model = [[EHILocationsMapViewModel alloc] initWithModel:filterQuery];
    
    EHILocation *location = !isRoundTrip && self.isPickingReturnTime ? self.builder.returnLocation : self.builder.pickupLocation;
    model.location = location;
    
    [self trackDidTapSelectOpenLocations];

    self.router
        .transition
        .push(EHIScreenLocationsMap)
        .object(model)
        .start(nil);
}

- (EHILocationFilterQuery *)filterQuery
{
    EHILocationFilterDateQuery *datesFilter = EHILocationFilterDateQuery.new;
    
    datesFilter.pickupDate = self.builder.pickupDate;
    datesFilter.returnDate = self.builder.returnDate;
    datesFilter.pickupTime = self.builder.pickupTime;
    datesFilter.returnTime = self.builder.returnTime;
    
    NSDate *time = [self timeAtIndexPath:self.indexPathForCurrentTime].date;

    if(self.isPickingReturnTime) {
        datesFilter.returnTime = time;
    } else {
        datesFilter.pickupTime = time;
    }
    
    EHILocationFilterQuery *query = EHILocationFilterQuery.new;
    query.datesFilter = datesFilter;
    
    return query;
}

- (BOOL)isRoundTrip
{
    return self.builder.returnLocation == nil || [self.builder.pickupLocation.uid isEqualToString:self.builder.returnLocation.uid];
}

# pragma mark - Selectable Time

- (void)setIndexPathForCurrentTime:(NSIndexPath *)indexPath
{
    _indexPathForCurrentTime = indexPath;

    // grab the time based on the index we are scrolling over
    EHITimePickerTime *time = [self timeAtIndexPath:indexPath];
    
    // choose correct info to display
    if(time.isAfterHours) {
        self.infoType = EHITimePickerInfoTypeAfterHours;
    } else if(time.isCloseTime) {
        self.infoType = EHITimePickerInfoTypeLastOpenTime;
    } else if(time.isClosed) {
        self.infoType = EHITimePickerInfoTypeClosed;
    } else {
        self.infoType = EHITimePickerInfoTypeNone;
    }
    
    self.currentTimeIsClosed = time.isClosed;
}

- (void)setInfoType:(EHITimePickerInfoType)infoType
{
    _infoType = infoType;
    
    // update out state so that the UI can react
    BOOL isLastPickup           = self.isLastPickupTime;
    BOOL isAfterHours           = infoType == EHITimePickerInfoTypeAfterHours;
    self.infoButtonIsHidden     = !(isLastPickup || isAfterHours);
    self.infoButtonIsSelectable = isLastPickup || isAfterHours;

    self.isLastReturnTime = infoType == EHITimePickerInfoTypeLastOpenTime && self.isPickingReturnTime;
    
    // update the button specifics
    self.infoButtonType  = isAfterHours || isLastPickup ? EHIButtonTypeInfo : EHIButtonTypeNone;
    self.selectionButtonTitle = EHILocalizedString(@"time_picker_button_title", @"SELECT", @"");
}

- (NSString *)infoButtonTitle
{
    if(self.infoType == EHITimePickerInfoTypeAfterHours) {
        return EHILocalizedString(@"time_selection_location_after_hours_title", @"After Hours Return", @"title for a button that informs the user that the location supports after hours returns.");
    } else if(self.isLastPickupTime) {
        return EHILocalizedString(@"time_selection_location_pickup_close_time_title", @"Last Pick Up Time", @"");
    } else {
        return nil;
    }
}

- (NSString *)lastReturnTimeTitle
{
    return EHILocalizedString(@"time_selection_return_close_time_info_title", @"Last Return Time", @"");
}

- (NSString *)lastReturnTimeText
{
    return EHILocalizedString(@"time_selection_return_close_time_details", @"", @"");
}

# pragma mark - Accessors

- (EHITimePickerTime *)timeAtIndexPath:(NSIndexPath *)indexPath
{
    return indexPath.item < self.times.count ? self.times[indexPath.item] : nil;
}

- (BOOL)isPickingReturnTime
{
    return self.builder.currentSchedulingStep == EHIReservationSchedulingStepReturnTime;
}

- (BOOL)isLastPickupTime
{
    return self.infoType == EHITimePickerInfoTypeLastOpenTime
        && self.builder.currentSchedulingStep == EHIReservationSchedulingStepPickupTime;
}

- (BOOL)currentTimeIsSelectable
{
    return [self shouldSelectTimeAtIndexPath:self.indexPathForCurrentTime];
}

- (NSIndexPath *)initialIndexPath
{
    // return the center index (12pm)
    NSInteger item = self.times.count / 2;
    return [NSIndexPath indexPathForItem:item inSection:0];
}

- (EHIReservationBuilder *)builder
{
    return [EHIReservationBuilder sharedInstance];
}

# pragma mark - Subclassing Hooks

- (void)navigateBack
{
    self.router.transition
        .pop(2).start(nil);
}

# pragma mark - Analytics

- (void)updateAnalyticsContext:(EHIAnalyticsContext *)context
{
    [super updateAnalyticsContext:context];
   
    [self.builder synchronizeLocationsOnContext:context];
    [self.builder synchronizeDateTimeOnContext:context];
}

- (void)trackDidTapSelectOpenLocations
{
    [EHIAnalytics trackAction:EHIAnalyticsSearchOpenLocations handler:^(EHIAnalyticsContext *context) {
        [context encode:[EHILocationFilterQuery class] encodable:self.filterQuery];
    }];
}

@end
