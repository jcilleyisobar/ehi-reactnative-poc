//
//  EHIHistoryManager.m
//  Enterprise
//
//  Created by Ty Cobb on 5/19/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIHistoryManager.h"
#import "EHIFavoritesManager.h"
#import "EHIDataStore.h"

@interface EHIHistoryManager ()
@property (strong, nonatomic) EHICollection *abandonded;
@property (strong, nonatomic) EHICollection *past;
@end

@implementation EHIHistoryManager

+ (instancetype)sharedInstance
{
    static EHIHistoryManager *sharedInstance;
    
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedInstance = [self new];
    });
    
    return sharedInstance;
}

# pragma mark - Synchronous Access

- (NSArray *)pastReservations
{
    __block NSArray *result = nil;
    
    // if this method is able to return synchronously, we'll return a valid list
    [self pastRentalsWithHandler:^(NSArray *rentals) {
        result = rentals;
    }];
    
    return result;
}

# pragma mark - Fetching

- (void)abandonedRentalsWithHandler:(void(^)(NSArray *))handler
{
    [self findReservations:self.abandonded handler:^(NSArray *reservations) {
        // partition the reservations based on whether or not they're in the future
        NSArray *partitions = reservations.partition(^(EHIReservation *reservation) {
            return [reservation.pickupTime ehi_isAfter:[NSDate date]];
        });
        
        // delete all past rentals
        [self deleteReservations:partitions.lastObject inCollection:self.abandonded];
        
        // and call the handler with the future reservations
        NSArray *futureReservations = partitions.firstObject;
        ehi_call(handler)(futureReservations.first(3));
    }];
}

- (void)pastRentalsWithHandler:(void (^)(NSArray *))handler
{
    [self findReservations:self.past handler:^(NSArray *reservations) {
        ehi_call(handler)(reservations.select(^(EHIReservation *reservation) {
            return !reservation.pickupLocation.isFavorited;
        }));
    }];
}

//
// Helpers
//

- (void)findReservations:(EHICollection *)collection handler:(void(^)(NSArray *reservations))handler
{
    [EHIDataStore start:[EHIDataStoreRequest find:collection] handler:handler];
}

# pragma mark - Saving

- (void)saveAbandonedRental:(EHIReservation *)reservation
{
    // copy the res
    reservation = reservation.copy;
    
    // and save it
    [self saveReservation:reservation collection:self.abandonded];
}

- (void)savePastRental:(EHIReservation *)reservation
{
    // if this was a one-way, we'll start with the return location next time
    EHILocation *location = reservation.returnLocation ?: reservation.pickupLocation;
    // santize any entries that duplicate this location
    [self removePastRentalsWithLocation:location];
   
    // create a copy of this reservation
    reservation = reservation.copy;
    
    // update its data, clearing out anything unsaved (basically everything)
    reservation.pickupLocation = location;
    reservation.returnLocation = nil;
    reservation.pickupTime = nil;
    reservation.returnTime = nil;
   
    // and save it
    [self saveReservation:reservation collection:self.past];
}

//
// Helpers
//

- (void)removePastRentalsWithLocation:(EHILocation *)location
{
     // see if we already have a res matching this pickup location
    EHIReservation *reservation = (self.pastReservations ?: @[]).find(^(EHIReservation *reservation) {
        return [reservation.pickupLocation isEqual:location];
    });
   
    // if so, delete it
    if(reservation) {
        [self deleteReservation:reservation inCollection:self.past];
    }   
}

- (void)saveReservation:(EHIReservation *)reservation collection:(EHICollection *)collection
{
    EHIDataStoreRequest *request = [EHIDataStoreRequest save:reservation];
    request.collection = collection;
    
    [EHIDataStore start:request handler:nil];
}

# pragma mark - Deleting

- (void)deleteAbandonedRental:(EHIReservation *)reservation
{
    [self deleteReservation:reservation inCollection:self.abandonded];
}

- (void)deletePastRental:(EHIReservation *)reservation
{
    [self deleteReservation:reservation inCollection:self.past];
}

- (void)clearHistory
{
    EHIDataStoreRequest *deleteAbandoned = [EHIDataStoreRequest purge:self.abandonded];
    [EHIDataStore start:deleteAbandoned handler:nil];
    
    EHIDataStoreRequest *deletePast = [EHIDataStoreRequest purge:self.past];
    [EHIDataStore start:deletePast handler:nil];
}

//
// Helpers
//

- (void)deleteReservations:(NSArray *)reservations inCollection:(EHICollection *)collection
{
    reservations.each(^(EHIReservation *reservation) {
        [self deleteReservation:reservation inCollection:collection];
    });
}

- (void)deleteReservation:(EHIReservation *)reservation inCollection:(EHICollection *)collection
{
    EHIDataStoreRequest *request = [EHIDataStoreRequest remove:reservation];
    request.collection = collection;
    
    [EHIDataStore start:request handler:nil];
}

# pragma mark - Collections

- (EHICollection *)abandonded
{
    if(!_abandonded) {
        _abandonded = [EHICollection new];
        _abandonded.name = @"abandoned-rentals";
    }
   
    return _abandonded;
}

- (EHICollection *)past
{
    if(!_past) {
        _past = [EHICollection new];
        _past.name = @"past-rentals";
        _past.historyLimit = 3;
    }
    
    return _past;
}

@end
