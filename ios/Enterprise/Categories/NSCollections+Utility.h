//
//  NSCollections+Utility.h
//  Enterprise
//
//  Created by Ty Cobb on 2/10/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@import Foundation;

@interface NSArray (Utility)

/** Returns an array initialized with nil objects */
+ (NSArray *)ehi_arrayWithCapacity:(NSUInteger)capacity;
/** Returns the object at @c index if it exists and is not @c NSNull; otherwise, this method returns @c nil */
- (id)ehi_safelyAccess:(NSInteger)index;
/** Appends the object to the array if it is not @c nil. Otherwise, does nothing. */
- (instancetype)ehi_safelyAppend:(id)object;

/** Retursn a set created from the receiver */
- (NSSet *)ehi_set;
/** Returns a mutable set created from the receiver */
- (NSMutableSet *)ehi_mutableSet;
/** Attempts to convert the receiver into an index set. */
- (NSIndexSet *)ehi_indexSet;

@end

@interface NSDictionary (Utility)

/** Returns the sub-dictionary for the given keys; if the value for a key is @c nil, it is @em not added. */
- (NSDictionary *)ehi_subdictionaryForKeys:(NSArray *)keys;
/** Returns a dictionary with the keys and values reversed */
- (NSDictionary *)ehi_reverse;
/** Returns a new dictionary with the key-value pair appended, if they exist */
- (NSDictionary *)ehi_appendKey:(id<NSCopying>)key value:(id)value;
/** Filters the dictionary to key-value pairs that return @c YES from the block */
- (NSDictionary *(^)(BOOL(^)(id, id)))ehi_select;

@end

@interface NSMutableDictionary (Utility)

/** Nests a sub-dictionary specified by @c fields under the @c key */
- (void)ehi_nest:(NSString *)key fields:(NSArray *)fields;
/** Wraps the value for the key in an array */
- (void)ehi_wrap:(NSString *)key;
/** Updates the value for the specified @c key according to the @c transform block */
- (void)ehi_transform:(NSString *)key block:(id(^)(id))transform;
/** Updates the value for the specified @c key by performing the @c selector on it */
- (void)ehi_transform:(NSString *)key selector:(SEL)selector;
/** Removes the values for the keys specified by @c fields and returns a dictionary of the removed values */
- (NSDictionary *)ehi_remove:(NSArray *)fields;
/** Removes the value for the @c field and returns that value */
- (id)ehi_removeField:(NSString *)field;

@end
