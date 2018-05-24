//
//  NSCollections+Functional.m
//  Enterprise
//
//  Created by Ty Cobb on 1/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "NSCollections+Functional.h"

@implementation NSArray (Functional)

- (instancetype)map:(id(^)(id))block
{
    return self.map(block);
}

- (NSArray *(^)(NSComparator))ehi_sort
{
    return ^(NSComparator comparator) {
        return [self sortedArrayUsingComparator:comparator];
    };
}

- (NSArray *(^)(NSUInteger))ehi_withoutIndex
{
    return ^(NSUInteger index) {
        return self.without(self[index]);
    };
}

- (NSDictionary *(^)(id))ehi_keyBy
{
    return ^(id keygen) {
        id<NSCopying>(^enumerator)(id);
        
        // if this is not a string, assume it's the block
        if(![keygen isKindOfClass:[NSString class]]) {
            enumerator = keygen;
        }
        // otherwise generate a block using the string keygen
        else {
            enumerator = ^(id object) {
                return [object valueForKeyPath:keygen];
            };
        }
        
        NSMutableDictionary *result = [[NSMutableDictionary alloc] initWithCapacity:self.count];
        
        for(id value in self) {
            // grab the key for each value
            id<NSCopying> key = enumerator(value);
            // and map it to the value if it exists
            if(key) {
                result[key] = value;
            }
        }
        
        return [result copy];
    };
}

- (NSString *(^)(NSString *))ehi_compressJoin
{
    return ^(NSString *separator) {
        return self
            .select(^(id object) { return object != [NSNull null]; })
            .pluck(@key(self.description))
            .select(^(NSString *string) { return string.length != 0; })
            .join(separator);
    };
}

@end

@implementation NSMutableArray (Functional)

- (NSArray *(^)(BOOL(^)(id)))ehi_remove
{
    return ^(BOOL(^predicate)(id)) {
        NSMutableIndexSet *indices = [NSMutableIndexSet new];
        
        // find the index of each object matching the predicate
        NSUInteger index = 0;
        for(id object in self) {
            if(predicate(object)) {
                [indices addIndex:index];
            }
            
            index++;
        }
      
        // pull the matched objects out and then remove them
        NSArray *objects = [self objectsAtIndexes:indices];
        [self removeObjectsAtIndexes:indices];
        
        return objects;
    };
}

@end

@implementation NSSet (Functional)

- (NSArray *(^)(id(^)(id)))map
{
    return ^(id(^mapper)(id)) {
        return self.allObjects.map(mapper);
    };
}

@end

@implementation NSDictionary (Functional)

- (instancetype)map:(id(^)(id))block
{
    NSMutableDictionary *result = [[NSMutableDictionary alloc] initWithCapacity:self.count];

    for(NSString *key in self) {
        id value = block(self[key]);
        if(value) {
            result[key] = value;
        }
    }
    
    return result.copy;
}

- (NSDictionary *(^)(void(^)(id, id)))each
{
    return ^(void(^enumerator)(id, id)) {
        for(id key in self) {
            enumerator(key, self[key]);
        }
        
        return self;
    };
}

- (NSDictionary *(^)(BOOL(^)(id, id)))select
{
    return ^(BOOL(^filter)(id, id)) {
        NSMutableDictionary *result = [NSMutableDictionary new];
        
        for(id key in self) {
            id value = self[key];
            if(filter(key, value)) {
                result[key] = value;
            }
        }
        
        return [result copy];
    };
}

@end

@implementation NSIndexSet (Functional)

- (NSArray *(^)(id (^)(NSUInteger)))map
{
    return ^(id(^block)(NSUInteger)) {
        NSMutableArray *result = [[NSMutableArray alloc] initWithCapacity:self.count];
       
        [self enumerateIndexesUsingBlock:^(NSUInteger index, BOOL *stop) {
            id value = block(index);
            if(value) {
                [result addObject:value];
            }
        }];
        
        return [result copy];
    };
}

@end
