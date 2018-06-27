//
//  NSCollections+Utility.m
//  Enterprise
//
//  Created by Ty Cobb on 2/10/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@import Foundation;

#import "NSCollections+Utility.h"

@implementation NSArray (Utility)

+ (NSArray *)ehi_arrayWithCapacity:(NSUInteger)capacity
{
    NSMutableArray *ary = [NSMutableArray arrayWithCapacity:capacity];
    for(int i = 0; i < capacity; i++) {
        [ary addObject:[NSNull null]];
    }
    return ary;
}

- (id)ehi_safelyAccess:(NSInteger)index
{
    id result = nil;
  
    if(index < self.count) {
        result = self[index];
    }
    
    if(result == [NSNull null]) {
        result = nil;
    }
    
    return result;
}

- (instancetype)ehi_safelyAppend:(id)object
{
    NSArray *result = self;
    
    if(object) {
        result = [result arrayByAddingObject:object];
    }
    
    return result;
}

- (NSIndexSet *)ehi_indexSet
{
    NSMutableIndexSet *indices = [NSMutableIndexSet new];
    
    for(NSNumber *number in self) {
        // ensure that we actually are iterating over the right type
        NSAssert([number isKindOfClass:[NSNumber class]], @"only an array of NSNumbers can be converted into an index set");
        [indices addIndex:number.integerValue];
    }
    
    return [indices copy];
}

- (NSSet *)ehi_set
{
    return [[NSSet alloc] initWithArray:self];
}

- (NSMutableSet *)ehi_mutableSet
{
    return [[NSMutableSet alloc] initWithArray:self];
}

@end

@implementation NSMutableArray (Utility)

- (instancetype)ehi_safelyAppend:(id)object
{
    if(object) {
        [self addObject:object];
    }
    
    return self;
}

@end

@implementation NSDictionary (Utility)

- (NSDictionary *)ehi_reverse
{
    NSMutableDictionary *result = [NSMutableDictionary new];
    
    for(id key in self)  {
        id value = self[key];
        result[value] = key;
    }
    
    return [result copy];
}

- (NSDictionary *)ehi_subdictionaryForKeys:(NSArray *)keys
{
    NSMutableDictionary *result = [NSMutableDictionary new];
    
    for(id key in keys) {
        id value = self[key];
        // only add entries that actually have a value
        if(value) {
            result[key] = value;
        }
    }
    
    return [result copy];
}

- (NSDictionary *)ehi_appendKey:(id<NSCopying>)key value:(id)value
{
    if(!key || !value) {
        return self;
    }
   
    NSMutableDictionary *result = [self mutableCopy];
    result[key] = value;
    
    return [result copy];
}

- (NSDictionary *(^)(BOOL(^)(id, id)))ehi_select;
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

@implementation NSMutableDictionary (Utility)

- (void)ehi_nest:(NSString *)key fields:(NSArray *)fields
{
    NSDictionary *subdictionary = [self ehi_remove:fields];
    if(subdictionary.count) {
        self[key] = subdictionary;
    }
}

- (void)ehi_wrap:(NSString *)key
{
    id value = self[key];
    if(value) {
        self[key] = @[ value ];
    }
}

- (void)ehi_transform:(NSString *)key block:(id (^)(id))transform
{
    [self setValue:transform(self[key]) forKey:key];
}

- (void)ehi_transform:(NSString *)key selector:(SEL)selector
{
    IGNORE_PERFORM_SELECTOR_WARNING(
        [self setValue:[self[key] performSelector:selector] forKey:key];
    );
}

- (NSDictionary *)ehi_remove:(NSArray *)keys
{
    NSDictionary *result = [self ehi_subdictionaryForKeys:keys];
    [self removeObjectsForKeys:keys];
    return result;
}

- (id)ehi_removeField:(NSString *)field
{
    id value = self[field];
    [self removeObjectForKey:field];
    return value;
}

@end
