//
//  EHIServices+Rentals.m
//  Enterprise
//
//  Created by Ty Cobb on 7/6/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIServices_Private.h"
#import "EHIServices+Rentals.h"
#import "EHIUserRental.h"
#import "EHIUserManager.h"
#import "EHICarClassPriceSummary.h"

NS_ASSUME_NONNULL_BEGIN

#define EHITimeframeForUpcomingTrips 359
#define EHITimeframeForPastRentals -360

@implementation EHIServices (Rentals)

- (nullable id<EHINetworkCancelable>)fetchUpcomingRentalsWithHandler:(nullable EHIUserRentalsHandler)handler
{
    // use the user's rentals object, or create a new one if necessary
    EHIUserRentals *rentals = EHIUser.currentUser.upcomingRentals;
    if(!rentals) {
        rentals = [EHIUserRentals new];
    }
    
    // generate the request
#if TESTS
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeNone get:@"mock://upcoming_rentals.json"];
#else
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeGBORental get:@"trips/%@/%@/%@/upcoming", kEHIServicesBrandPathKey, kEHIServicesChannelPathKey, EHIUser.currentUser.loyaltyNumber];
#endif
    
    [request parameters:^(EHINetworkRequest *request) {
        request[@"recordCount"]       = @(EHIUserRentalsPageSize);
        request[@"startRecordNumber"] = @(rentals.pagingOffset);
        request[@"searchEndDate"]     = [[NSDate date] ehi_addDays:EHITimeframeForUpcomingTrips].ehi_dateTimeString;
        request[@"preWriteTicketRequested"] = @(rentals == nil);
    }];
    
    return [self startRequest:request parseAsynchronously:YES withBlock:^(NSDictionary *responseData) {
        // append the rentals and the sort the list
        return [[rentals appendPage:responseData] sort];
    } handler:handler];
}

- (nullable id<EHINetworkCancelable>)fetchCurrentRentalsWithHandler:(nullable EHIUserRentalsHandler)handler
{
    EHIUserRentals *rentals = [EHIUserRentals new];
#if TESTS
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeNone get:@"mock://current_rentals.json"];
#else
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeGBORental get:@"trips/%@/%@/%@/current", kEHIServicesBrandPathKey, kEHIServicesChannelPathKey, EHIUser.currentUser.loyaltyNumber];
#endif
    
    return [self startRequest:request parseAsynchronously:YES withBlock:^(NSDictionary *responseData) {
        return [[rentals appendPage:responseData] markAsCurrent];
    } handler:handler];
}

- (nullable id<EHINetworkCancelable>)fetchPastRentalsWithHandler:(nullable EHIUserRentalsHandler)handler
{
#if TESTS
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeNone get:@"mock://past_rentals.json"];
#else
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeGBORental post:@"trips/%@/%@/past", kEHIServicesBrandPathKey, kEHIServicesChannelPathKey];
#endif

    [request body:^(EHINetworkRequest *request) {
        request[@"membership_number"] = EHIUser.currentUser.loyaltyNumber ?: @"";
        request[@"from_date"] = [[NSDate ehi_today] ehi_addDays:EHITimeframeForPastRentals].ehi_dateTimeString;
        request[@"to_date"]   = [NSDate ehi_today].ehi_dateTimeString;
    }];

    EHIUserRentals *rentals = [EHIUserRentals new];
    return [self startRequest:request parseAsynchronously:YES withBlock:^(NSDictionary *responseData) {
        return [rentals appendPage:responseData];
    } handler:handler];
}

- (nullable id<EHINetworkCancelable>)fetchInvoiceDetails:(nullable EHIUserRental*)rental handler:(void(^)(EHIUserRental *, EHIServicesError *))handler
{
#if TESTS
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeNone get:@"mock://invoice.json"];
#else
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeGBORental get:@"/trips/%@/%@/past/invoice", kEHIServicesBrandPathKey, kEHIServicesChannelPathKey];
#endif
    
    [request parameters:^(EHINetworkRequest *request) {
        request[@"invoiceNumber"] = rental.invoiceNumber ?: @"";
    }];

//    Last name is mandatory for unauthenticated users.
//    if([EHIUserManager sharedInstance].currentUser == nil) {
//        [request parameters:^(EHINetworkRequest *request) {
//            request[@"lastName"] = rental.lastName ?: @"";
//        }];
//    }
    
    return [self startRequest:request handler:^(NSDictionary *responseData, EHIServicesError *error) {
        NSDictionary *data = responseData[@"past_trip_detail"];
        EHIUserRental *rental = [EHIUserRental modelWithDictionary:data];
        ehi_call(handler)(rental, error);
    }];
}

@end

NS_ASSUME_NONNULL_END
