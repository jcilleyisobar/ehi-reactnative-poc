//
//  EHIReservationBuilder+Analytics.h
//  Enterprise
//
//  Created by Ty Cobb on 6/2/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservationBuilder.h"

NS_ASSUME_NONNULL_BEGIN

@interface EHIReservationBuilder (Analytics)

/**
 @brief Updates the analytics context with the location
 
 The location is encoded using the appropriate pickup/dropoff prefix, depending on the
 resevation builder's current state.
*/

- (void)encodeLocation:(EHILocation *)location context:(nullable EHIAnalyticsContext *)context;

/**
 @brief Updates the analytics context with the location
 
 Also registers the location selection macro event.
 
 The location is encoded using the appropriate pickup/dropoff prefix, depending on the
 resevation builder's current state.
*/

- (void)encodeLocationSelection:(EHILocation *)location context:(nullable EHIAnalyticsContext *)context;

/**
 @brief Updates the analytics context with the car class
 
 Also registers the class select macro event.
*/

- (void)encodeClassSelection:(EHICarClass *)carClass context:(nullable EHIAnalyticsContext *)context;

/**
 @brief Updates the analytics context with the current locations

 If no @c context is specified, the current context is used. See @c -encodePickupLocation:returnLocaiton:
 on @c EHIAnalyticsContext for more information.
 
 @param context The context to update, or @c nil.
*/

- (void)synchronizeLocationsOnContext:(nullable EHIAnalyticsContext *)context;

/**
 @brief Updates the analytics context with the current dates / times
 
 If no @c context is specified, the current context is used. See @c -encodePickupTime:returnTime:
 on @c EHIAnalyticsContext for more information.

 @param context The context to update, or @c nil
*/

- (void)synchronizeDateTimeOnContext:(nullable EHIAnalyticsContext *)context;

/**
 @brief Updates the analytics context with any information specific to the reservation
 
 If no @c context is specified, the current context is used. See @c -encodeReservation:
 on @c EHIAnalyticsContext for more information.
 
 @param context The context to update, or @c nil
*/

- (void)synchronizeReservationOnContext:(nullable EHIAnalyticsContext *)context;

@end

@interface EHIAnalyticsContext (Reservation)

/**
 @brief Updates the analytics context with the parameterized locations
 
 Any previous location data is destroyed and replaced by the builders current pickup
 and return locations.
*/

- (void)encodePickupLocation:(nullable EHILocation *)pickupLocation returnLocation:(nullable EHILocation *)returnLocation;

/**
 @brief Updates the analytics context with the dates / times
 
 Any previous date / time data is destroyed and replaced by the parameterized pickup 
 and return times.
*/

- (void)encodePickupDate:(nullable NSDate *)pickupDate returnDate:(nullable NSDate *)returnDate;

/**
 @brief Updates the receiver context with any information specific to the reservation
 
 Any previous data is destroyed and replaced by the reservation's current state. This does not 
 incoporate any data from the location / date-time encoding methods.
*/

- (void)encodeReservation:(nullable EHIReservation *)reservation;

@end

NS_ASSUME_NONNULL_END
