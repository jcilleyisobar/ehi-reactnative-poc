//
//  EHIUserRentals.h
//  Enterprise
//
//  Created by Ty Cobb on 7/6/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIUserRental.h"

NS_ASSUME_NONNULL_BEGIN

#define EHIUserRentalsPageSize (5)

@interface EHIUserRentals : EHIModel

@property (copy  , nonatomic, readonly) NSArray<EHIUserRental> *all;
@property (assign, nonatomic, readonly) NSInteger pagingOffset;
@property (assign, nonatomic, readonly) BOOL hasMoreAvailable;

// computed properties
@property (strong, nonatomic, nullable, readonly) EHIUserRental *firstRental;
@property (assign, nonatomic, readonly) NSInteger count;

/** Appends the user rentals data in the @c dictionary to the receiver as a new page; returns @c self for chaining */
- (EHIUserRentals *)appendPage:(NSDictionary *)dictionary;
/** Sorts the rentals by date; returns @c self for chaining */
- (EHIUserRentals *)sort;
/** Sorts the rentals by date in descending order; returns @c self for chaining */
- (EHIUserRentals *)reverseSort;
/** Marks all the rentals as current rentals; returns @c self for chaining */
- (EHIUserRentals *)markAsCurrent;

@end

NS_ASSUME_NONNULL_END
