//
//  EHIRentalsPagingViewModel.h
//  Enterprise
//
//  Created by Ty Cobb on 7/7/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIUserRentals.h"

NS_ASSUME_NONNULL_BEGIN

typedef void(^EHIRentalsPagingHandler)(NSError * __nullable);

@interface EHIRentalsPagingViewModel : EHIViewModel <MTRReactive>

/** Title for the "load more" button */
@property (copy  , nonatomic, readonly) NSString *loadMoreTitle;
/** @c YES if the more rentals are currently being loaded */
@property (assign, nonatomic, readonly) BOOL isLoading;

/** Fetches more rentals and calls back the @c handler when finished */
- (void)loadMoreRentalsWithHandler:(nullable EHIRentalsPagingHandler)handler;

@end

NS_ASSUME_NONNULL_END
