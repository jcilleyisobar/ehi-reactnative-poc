//
//  EHILocationsViewModel.h
//  Enterprise
//
//  Created by Ty Cobb on 1/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHILocations.h"
#import "EHISectionHeaderModel.h"
#import "EHIUserLocation.h"
#import "EHIReservationBuilder.h"

typedef NS_ENUM(NSInteger, EHILocationSection) {
    EHILocationSectionNearby,
    EHILocationSectionEmptyQuery,
    EHILocationSectionFavorites,
    EHILocationSectionRecents,
    EHILocationSectionAirport,
    EHILocationSectionCity,
};

@interface EHILocationsViewModel : EHIViewModel <MTRReactive>

// data sources that drives business logic
@property (strong, nonatomic) EHILocations *locations;

// query
@property (copy  , nonatomic) NSString *query;
@property (assign, nonatomic) BOOL isLoading;

// section models 
@property (strong, nonatomic) EHIUserLocation *nearby;
@property (copy  , nonatomic) NSArray *favorites;
@property (copy  , nonatomic) NSArray *recents;
@property (copy  , nonatomic) NSArray *cities;
@property (copy  , nonatomic) NSArray *airports;
@property (copy  , nonatomic) NSString *failingQuery;

// non-reactive outlets
@property (copy  , nonatomic, readonly) NSString *searchPlaceholder;

/** Returns the models for the given section; reactive */
- (NSArray *)modelsForSection:(EHILocationSection)section;
/** Returns the header model for the given section */
- (EHISectionHeaderModel *)headerForSection:(EHILocationSection)section;
/** Will handle and navigation and model passing required for the selection */
- (void)selectIndexPath:(NSIndexPath *)indexPath;
/** Prompts the user to clear recent activity, and then does so if they accept */
- (void)clearRecentActivity;

@end
