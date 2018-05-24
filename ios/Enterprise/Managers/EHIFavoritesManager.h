//
//  EHIFavoritesManager.h
//  Enterprise
//
//  Created by Ty Cobb on 2/24/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILocation.h"

@interface EHIFavoritesManager : NSObject

/** The list of currently favorited locations */
@property (nonatomic, readonly) NSArray *favoriteLocations;

/** Singleton accessor for favorite locations */
+ (instancetype)sharedInstance;
/** Bootstraps the favorites manager, allowing it to perform startup tasks */
+ (void)prepareToLaunch;

/** Updates whether a location is favorited or not; reactive */
- (void)updateLocation:(EHILocation *)location isFavorited:(BOOL)isFavorited;
/** @c YES if the location is currently favorited; reactive */
- (BOOL)locationIsFavorited:(EHILocation *)location;

@end
