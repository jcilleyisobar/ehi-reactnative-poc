//
//  EHIAnalyticsContext+Serialization.h
//  Enterprise
//
//  Created by Ty Cobb on 6/1/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIAnalyticsContext.h"

NS_ASSUME_NONNULL_BEGIN

@interface EHIAnalyticsContext (Serialization)

/** Generates a path joining the screen, state, and action (provided they exist) */
- (nullable NSString *)path;

/**
 @brief Converts the context into a dictionary
 
 The dictionary may contain nested dictionaries, if this context contained nested contexts.
 Any non-encodable values will have @c description called on them before being added to the
 dictionary.
*/

- (NSDictionary *)dictionaryRepresentation;

/**
 @brief Serializes a generic attributes dictionary
 
 The serialization process stringifies and formats the attributes according to the specified
 analytics format.
*/

+ (NSDictionary *)serializeAttributes:(NSDictionary *)attributes;

/** Serializes a specific value to meet analytics data specifications */
+ (id)serializeValue:(id)value;

@end

NS_ASSUME_NONNULL_END
