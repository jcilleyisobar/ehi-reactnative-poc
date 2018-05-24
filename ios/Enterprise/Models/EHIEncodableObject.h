//
//  EHIModel.h
//  Enterprise
//
//  Created by Ty Cobb on 1/5/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

extern BOOL class_hasInstanceVariableForPropertyName(Class klass, const char *name);

@interface EHIEncodableObject : NSObject <NSCoding, NSCopying>

/**
 @brief Returns an array of encodable keys
 
 Subclasses need only implement this method if they need a specific key whitelist, otherwise
 the keys will be determined greedily at runtime.
 
 @param object A placeholder value; always @c nil
 @return An array of encodable keys
*/

+ (NSArray *)encodableKeys:(id)object;

/**
 @brief Returns an array of unencodable keys
 
 Subclasses need only return keys here that have an assosciated @c ivar and would otherwise
 be encoded.
 
 This method is passed a placeholder value that will always be nil, but that subclasses can
 redeclare to take advantage of the @c @key macro.
 
 @param object A placeholder value; always @c nil
 @return An array of un-encodable keys
*/

+ (NSArray *)unencodableKeys:(id)object;

/**
 @brief Passthrough to @c -deepCopy: passing in @c NO for @c ignoreKeyFilters
 
 @return A deep copy of this object
*/

- (id)deepCopy;

/**
 @brief Returns a deep copy of this object
 
 This method makes the assumption that all properties of this object conform to NSCopying.
 Filtering supplied through @c encodableKeys and @c unencodableKeys can be optionally ignored
 by supplying @c YES for @c ignoreKeyFilters
 
 @param  ignoreKeyFilters If whitelists/blacklist filters should be ignored
 @return A deep copy of this object
*/

- (id)deepCopy:(BOOL)ignoreKeyFilters;

@end


@interface NSDictionary (EHIEncodableObject)

/**
 @brief Builds a dictionary from an encodable object

 This is a pass-through to @c -dictionaryWithEncodableObject:recurseDepth: with the @c depth
 parameter unbounded.
 
 @param object The object to enocde into a dictionary
 @return A dictionary representing this object
*/

+ (NSDictionary *)dictionaryWithEncodableObject:(EHIEncodableObject *)object;

/**
 @brief Builds a dictionary from an encodable object
 
 If the any of this objects properties also sublcass @c CTEncodable object, they will be encoded
 in the dictionary recursively.
 
 @param object The object to enocde into a dictionary
 @return A dictionary representing this object
*/

+ (NSDictionary *)dictionaryWithEncodableObject:(EHIEncodableObject *)object recurseDepth:(NSInteger)depth;

@end
