//
//  EHIRentalsViewModel.h
//  Enterprise
//
//  Created by fhu on 4/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIRentalsMode.h"

typedef NS_ENUM(NSUInteger, EHIRentalsSection) {
    EHIRentalsSectionLoading,
    EHIRentalsSectionFallback,
    EHIRentalsSectionPastRental,
    EHIRentalsSectionUpcomingRental,
    EHIRentalsSectionPaging,
    EHIRentalsSectionFooter,
};

@class EHIRentalsFallbackViewModel;
@class EHIRentalsFooterViewModel;

@interface EHIRentalsViewModel : EHIViewModel <MTRReactive>

@property (copy  , nonatomic, readonly) NSString *title;
@property (copy  , nonatomic, readonly) NSArray *segmentedControlItems;
@property (strong, nonatomic, readonly) EHIRentalsFallbackViewModel *fallbackViewModel;
@property (strong, nonatomic, readonly) EHIModel *pagingModel;
@property (strong, nonatomic, readonly) EHIRentalsFooterViewModel *footerViewModel;
@property (assign, nonatomic, readonly) BOOL isLoading;

@property (copy  , nonatomic, readonly) NSArray *pastRentals;
@property (copy  , nonatomic, readonly) NSArray *upcomingRentals;
@property (assign, nonatomic, readonly) BOOL shouldHideUnauth;
@property (assign, nonatomic, readonly) EHIRentalsMode mode;

/** Refreshes the rentals, re-fetching from services */
- (void)refreshRentals;
/** Invalidates the rentals using the current mode cached rentals */
- (void)invalidateVisibleRentals;
/** Switches the active mode of the rentals screen, re-rendering the rentals content */
- (void)switchToMode:(EHIRentalsMode)mode;

/** @c YES if the rental at the @c indexPath supports selection */
- (BOOL)shouldSelectRentalAtIndexPath:(NSIndexPath *)indexPath;
/** Runs the selection action for the rental specified by @c indexPath */
- (void)selectRentalAtIndexPath:(NSIndexPath *)indexPath;

@end
