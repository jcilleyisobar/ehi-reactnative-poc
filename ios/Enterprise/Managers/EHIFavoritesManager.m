//
//  EHIFavoritesManager.m
//  Enterprise
//
//  Created by Ty Cobb on 2/24/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "MTRDependency.h"
#import "EHIFavoritesManager.h"
#import "EHIDataStore.h"

@interface EHIFavoritesManager ()
@property (strong, nonatomic) NSMutableSet *locationsSet;
@property (strong, nonatomic) NSMapTable *locationDependencies;
@property (strong, nonatomic) EHICollection *collection;
@end

@implementation EHIFavoritesManager

+ (instancetype)sharedInstance
{
    static EHIFavoritesManager *sharedInstance;
    
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedInstance = [self new];
    });
    
    return sharedInstance;
}

- (instancetype)init
{
    if(self = [super init]) {
        // store our keys (locations) weakly, values (dependencies) strongly
        _locationDependencies = [NSMapTable weakToStrongObjectsMapTable];
        
        // create a custom collection for favorites so that they can be saved independently from recents
        _collection = [EHICollection new];
        _collection.name = @"favorites";
    }
    
    return self;
}

# pragma mark - Launch 

+ (void)prepareToLaunch
{
    [[self sharedInstance] populateFavorites];
}

- (void)populateFavorites
{
    // get a generic frequest
    EHIDataStoreRequest *request = [self requestWithType:EHIDataStoreRequestTypeFind];
    // and pull out all the favorites
    [EHIDataStore start:request handler:^(NSArray *locations) {
        self.locationsSet = locations.ehi_mutableSet;
    }];
}

# pragma mark - Updating Hooks

- (void)updateLocation:(EHILocation *)location isFavorited:(BOOL)isFavorited
{
    if(isFavorited) {
        [self.locationsSet addObject:location];
        [self startRequestWithType:EHIDataStoreRequestTypeSave location:location];
        [[self dependencyForLocation:location] changed];
    } else {
        [self.locationsSet removeObject:location];
        [self startRequestWithType:EHIDataStoreRequestTypeDelete location:location];
        [[self dependencyForLocation:location] changed];
    }
}

- (BOOL)locationIsFavorited:(EHILocation *)location
{
    [[self lazyDependencyForLocation:location] depend];
    return [self.locationsSet containsObject:location];
}

//
// Helpers
//

- (EHIDataStoreRequest *)requestWithType:(EHIDataStoreRequestType)type
{
    // build up the request with the custom collection name
    EHIDataStoreRequest *request = [EHIDataStoreRequest new];
    request.type  = type;
    request.collection = self.collection;
    
    return request;
}

- (void)startRequestWithType:(EHIDataStoreRequestType)type location:(EHILocation *)location;
{
    // build up the request with the custom collection name
    EHIDataStoreRequest *request = [self requestWithType:type];
    request.model = location;
    
    // and fire it off
    [EHIDataStore start:request handler:nil];
}

# pragma mark - Dependencies

- (MTRDependency *)lazyDependencyForLocation:(EHILocation *)location
{
    MTRDependency *dependency = [self dependencyForLocation:location];
    
    if(!dependency) {
        dependency = [MTRDependency new];
        [self.locationDependencies setObject:dependency forKey:location];
    }
    
    return dependency;
}

- (MTRDependency *)dependencyForLocation:(EHILocation *)location
{
    return [self.locationDependencies objectForKey:location];
}

# pragma mark - Accessors

- (NSArray *)favoriteLocations
{
    return [self.locationsSet allObjects];
}

@end
