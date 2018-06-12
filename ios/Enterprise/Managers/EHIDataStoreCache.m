//
//  EHIDataStoreCache.m
//  Enterprise
//
//  Created by Ty Cobb on 3/9/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIDataStoreCache.h"

@interface EHIDataStoreCache ()
@property (assign, nonatomic) BOOL isSynchronized;
@property (nonatomic, readonly) NSMutableArray *orderedKeys;
@property (nonatomic, readonly) NSMutableDictionary *store;
@end

@implementation EHIDataStoreCache

- (instancetype)init
{
    if(self = [super init]) {
        _orderedKeys = [NSMutableArray new];
        _store = [NSMutableDictionary new];
    }
    
    return self;
}

# pragma mark - Mutation

- (void)setObject:(id)object forKeyedSubscript:(NSString *)key
{
    // if we already have this object, we're going to want to update its key position in some way,
    // so lets pull it out of its current position
    if(self.store[key]) {
        [self.orderedKeys removeObject:key];
    }
 
    // update the store with this object (may be nil)
    [self.store setValue:object forKey:key];
    
    // if we actually set an object, add the key to the front of our list
    if(object) {
        [self.orderedKeys insertObject:key atIndex:0];
    }
}

- (void)populateWithModels:(NSArray *)models
{
    self.isSynchronized = YES;
    
    for(EHIModel *model in models) {
        self[model.uid] = model;
    }
}

- (void)reset
{
    [self.orderedKeys removeAllObjects];
    [self.store removeAllObjects];
}

# pragma mark - Accessors

- (NSArray *)objects
{
    return self.orderedKeys.map(^(NSString *key) {
        return self.store[key];
    });
}

@end
