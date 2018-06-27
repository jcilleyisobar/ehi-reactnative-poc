//
//  EHIServices+Rentals.h
//  Enterprise
//
//  Created by Ty Cobb on 7/6/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIServices.h"
#import "EHIUser.h"
#import "EHIUserRentals.h"
#import "EHIUserRental.h"

NS_ASSUME_NONNULL_BEGIN

typedef void(^EHIUserRentalsHandler)(EHIUserRentals *rentals, EHIServicesError *error);

@interface EHIServices (Rentals)
- (nullable id<EHINetworkCancelable>)fetchUpcomingRentalsWithHandler:(nullable EHIUserRentalsHandler)handler;
- (nullable id<EHINetworkCancelable>)fetchCurrentRentalsWithHandler:(nullable EHIUserRentalsHandler)handler;
- (nullable id<EHINetworkCancelable>)fetchPastRentalsWithHandler:(nullable EHIUserRentalsHandler)handler;
- (nullable id<EHINetworkCancelable>)fetchInvoiceDetails:(nullable EHIUserRental*)rental handler:(void(^)(EHIUserRental *, EHIServicesError *))handler;
@end

NS_ASSUME_NONNULL_END
