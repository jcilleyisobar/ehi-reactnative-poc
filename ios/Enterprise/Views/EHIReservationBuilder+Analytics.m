//
//  EHIReservationBuilder+Analytics.m
//  Enterprise
//
//  Created by Ty Cobb on 6/2/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIReservationBuilder+Analytics.h"

#define EHIAnalyticsDateFormat @"yyyyMMdd"
#define EHIAnalyticsTimeFormat @"HH:mm"

NS_ASSUME_NONNULL_BEGIN

@implementation EHIReservationBuilder (Analytics)

- (void)updateAnalyticsContext:(EHIAnalyticsContext *)context
{
    [super updateAnalyticsContext:context];
    
    [self synchronizeLocationsOnContext:context];
    [self synchronizeDateTimeOnContext:context];
    [self synchronizeReservationOnContext:context];
}

# pragma mark - Utilities

- (void)encodeLocation:(EHILocation *)location context:(nullable EHIAnalyticsContext *)context
{
    [context ?: [EHIAnalytics context] encode:[EHILocation class] encodable:location prefix:self.currentLocationPrefix];
}

- (void)encodeLocationSelection:(EHILocation *)location context:(nullable EHIAnalyticsContext *)context
{
    context = context ?: [EHIAnalytics context];
    
    [context setMacroEvent:EHIAnalyticsMacroEventSelectLocation];
    [context encode:[EHILocation class] encodable:location prefix:self.currentLocationPrefix];
}

- (void)encodeClassSelection:(EHICarClass *)carClass context:(nullable EHIAnalyticsContext *)context
{
    context = context ?: [EHIAnalytics context];
    
    [context setMacroEvent:EHIAnalyticsMacroEventSelectClass];
    [context encode:[EHICarClass class] encodable:carClass];
}

- (void)synchronizeLocationsOnContext:(nullable EHIAnalyticsContext *)context
{
    [context ?: [EHIAnalytics context] encodePickupLocation:self.pickupLocation returnLocation:self.returnLocation];
}

- (void)synchronizeDateTimeOnContext:(nullable EHIAnalyticsContext *)context
{
    [context ?: [EHIAnalytics context] encodePickupDate:self.aggregatePickupDate returnDate:self.aggregateReturnDate];
}

- (void)synchronizeReservationOnContext:(nullable EHIAnalyticsContext *)context
{
    [context ?: [EHIAnalytics context] encodeReservation:self.reservation];
}

//
// Helpers
//

- (NSString *)currentLocationPrefix
{
    return self.isPickingOneWayReservation ? EHIAnalyticsDropoffPrefix : EHIAnalyticsPickupPrefix;
}

@end

@implementation EHIAnalyticsContext (Reservation)

- (void)encodePickupLocation:(nullable EHILocation *)pickupLocation returnLocation:(nullable EHILocation *)returnLocation
{
    // use return location as pickup location if we don't have one
    if(!returnLocation) {
        returnLocation = pickupLocation;
    }
   
    // encode both locations, if possible
    [self encode:[EHILocation class] encodable:pickupLocation prefix:EHIAnalyticsPickupPrefix];
    [self encode:[EHILocation class] encodable:returnLocation prefix:EHIAnalyticsDropoffPrefix];
    
    // indicate whether or not this is a one-way
    self[EHIAnalyticsLocOneWayKey]   = pickupLocation ? @((BOOL)![pickupLocation.uid isEqual:returnLocation.uid]) : nil;
    self[EHIAnalyticsLocShortcutKey] = [self inferShortcutForLocation:pickupLocation];
}

- (void)encodePickupDate:(nullable NSDate *)pickupDate returnDate:(nullable NSDate *)returnDate
{
    // encode pickup times
    [self encodeWithPrefix:EHIAnalyticsPickupPrefix handler:^(EHIAnalyticsContext *context) {
        self[EHIAnalyticsResDateKey] = [pickupDate ehi_stringWithFormat:EHIAnalyticsDateFormat];
        self[EHIAnalyticsResTimeKey] = [pickupDate ehi_stringWithFormat:EHIAnalyticsTimeFormat];
    }];
    
    // encode dropoff times
    [self encodeWithPrefix:EHIAnalyticsDropoffPrefix handler:^(EHIAnalyticsContext *context) {
        self[EHIAnalyticsResDateKey] = [returnDate ehi_stringWithFormat:EHIAnalyticsDateFormat];
        self[EHIAnalyticsResTimeKey] = [returnDate ehi_stringWithFormat:EHIAnalyticsTimeFormat];
    }];
    
    // encode rental length info, if possible
    self[EHIAnalyticsResLeadTimeKey]                         = pickupDate ? @([NSDate.ehi_today ehi_daysUntilDate:pickupDate]) : nil;
    self[EHIAnalyticsResLengthKey]                           = returnDate ? @([pickupDate ehi_daysUntilDate:returnDate]) : nil;
    self[EHIAnalyticsFilterReturnBeforePickUpMessageDisplay] = @([returnDate ehi_isBefore:pickupDate]);
}

- (void)encodeReservation:(nullable EHIReservation *)reservation
{
    // encode res-specific properties
    [self encode:[EHIReservation class] encodable:reservation];
    [self encode:[EHICarClass class] encodable:reservation.selectedCarClass];
    
    // also encode the pickup / return location
    [self encodePickupLocation:reservation.pickupLocation returnLocation:reservation.returnLocation];
    [self encodePickupDate:reservation.pickupTime returnDate:reservation.returnTime];
}

//
// Helpers
//

- (nullable NSString *)inferShortcutForLocation:(nullable EHILocation *)location
{
    if(location.isFavorited) {
        return EHIAnalyticsLocShortcutFavorite;
    } else if(location.isRecentActivity) {
        return EHIAnalyticsLocShortcutRecent;
    }
    
    return nil;
}

@end

NS_ASSUME_NONNULL_END
