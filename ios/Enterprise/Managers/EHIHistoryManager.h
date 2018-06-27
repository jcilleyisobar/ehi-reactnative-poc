//
//  EHIHistoryManager.h
//  Enterprise
//
//  Created by Ty Cobb on 5/19/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservation.h"

@interface EHIHistoryManager : NSObject

/** In-memory access to past rentals; not guaranteed to return all valid reservations */
@property (nonatomic, readonly) NSArray *pastReservations;

/** Singleton accessor for the manager instance */
+ (instancetype)sharedInstance;

/** Fetches the abandoned rentals and applies any filtering business rules */
- (void)abandonedRentalsWithHandler:(void(^)(NSArray *rentals))handler;
/** Fetches the past rentals and applies any filtering business rules */
- (void)pastRentalsWithHandler:(void(^)(NSArray *rentals))handler;

/** Saves a new abandoned rental */
- (void)saveAbandonedRental:(EHIReservation *)reservation;
/** Saves a new past rental */
- (void)savePastRental:(EHIReservation *)reservation;

/** Deletes the abandoned rental */
- (void)deleteAbandonedRental:(EHIReservation *)reservation;
/** Deletes the past rental */
- (void)deletePastRental:(EHIReservation *)reservation;
/** Deletes all abandoned and past rentals */
- (void)clearHistory;

@end
