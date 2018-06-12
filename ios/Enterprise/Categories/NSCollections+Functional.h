//
//  NSCollections+Functional
//  Enterprise
//
//  Created by Ty Cobb on 1/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIMappable.h"

@interface NSArray (Functional) <EHIMappable>

/**
 @brief Sorts the receiver according to the block
 This is a pass-through to `sortedArrayUsingComparator`.
*/

- (NSArray *(^)(NSComparator))ehi_sort;

/**
 @brief Removes the object at the given index
 
 @return An array with the object at the given index removed.
 */
- (NSArray *(^)(NSUInteger))ehi_withoutIndex;

/**
 @brief Creates a dictionary by keying each object by the result of a block
 
 This method functions similarly to YOLOKit's @c groupBy, but it is intended for 
 one-to-one relationships. If two objects return the same key, the last object
 replaces the previous in the resulting map.
 
 The parameter to the function should either be an enumerator block of the form
 @c id<NSCopying>(^)(id) or an @c NSString keypath for the value to key by.

 If the @c key is nil, the pair is not added to the dictionary.
*/

- (NSDictionary *(^)(id))ehi_keyBy;

/**
 @brief Generates a string by joining all the elements on the receiver
 
 Before joining, @c description is called on each element and any resulting strings
 that are 0-length are filtered out.
 
 @param block:separator The separator joining the strings
*/

- (NSString *(^)(NSString *))ehi_compressJoin;

@end

@interface NSMutableArray (Functional)

/**
 @brief Removes objects from the receiver matching the block
    
 This method returns a block that can be passed a predicate block that returns @c YES if 
 the element should be removed from the receiver.
 
 @return An array containing the removed elements.
*/

- (NSArray *(^)(BOOL(^)(id)))ehi_remove;

@end

@interface NSSet (Functional)

/**
 @brief Maps the set into an array given using the given block
 
 If the block returns nil for some mapping, it will not be added to the resultant array.
 Thus, this is more of a combination map/filter operation.
*/

- (NSArray *(^)(id(^)(id)))map;

@end

@interface NSDictionary (Functional) <EHIMappable>

/**
 @brief Enumerates all the entries in the receiver
 
 Returns the receiver once the enumeration completes. Passes the the (key, value) entry
 to enumeration block.
*/

- (NSDictionary *(^)(void(^)(id, id)))each;

/**
 @brief Filters the entries in the receiver according to the block
 
 Passes the (key, value) entry to the block, and it the block returns @c YES adds it
 to the result.
*/

- (NSDictionary *(^)(BOOL(^)(id, id)))select;

@end

@interface NSIndexSet (Functional)

/**
 @brief Maps all the indices in an index set into an array of objects
 
 This method returns a block that accepts a mapping block that receives an index and
 returns an object. If the mapping block returns @c nil, nothing is added to the result.
*/

- (NSArray *(^)(id(^)(NSUInteger)))map;

@end
