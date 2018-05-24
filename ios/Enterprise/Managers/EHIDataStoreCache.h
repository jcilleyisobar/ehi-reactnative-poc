//
//  EHIDataStoreCache.h
//  Enterprise
//
//  Created by Ty Cobb on 3/9/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"

@interface EHIDataStoreCache : NSObject

/** @c YES once the cache has been populated with models */ 
@property (nonatomic, readonly) BOOL isSynchronized;
/** Returns all the objects in the cache */
@property (nonatomic, readonly) NSArray *objects;

/** Updates the cache with the list of models, using their IDs as keys */
- (void)populateWithModels:(NSArray *)models;
/** Updates the object in the cache for the given key */
- (void)setObject:(id)object forKeyedSubscript:(NSString *)key;
/** Empties the cache of all its data */
- (void)reset;

@end
