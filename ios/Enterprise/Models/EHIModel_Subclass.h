//
//  EHIModel_Subclass.h
//  Enterprise
//
//  Created by Ty Cobb on 1/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"
#import "EHICollection.h"
#import "EHIMapTransformer.h"

@interface EHIModel (Collection)

/**
 @brief Provides the model subclass a hook to customize its collection
 Changes to collection, other than for mappings, should be made here.
 @param collection The collection backing this model type
*/

+ (void)prepareCollection:(EHICollection *)collection;

@end

@interface EHIModel (Initialization)

/**
 @brief Designated initializer for models
 
 Immediately calls @c -updateWithDictionary:forceDeletions: on the model with the @c dictionary
 passed to this method.
 
 @param dictionary The attributes to initialize the model with
 @return A new model instance updated with the @c dictionary
*/

- (instancetype)initWithDictionary:(NSDictionary *)dictionary;

@end

@interface EHIModel (Parsing)

/**
 @brief Allows subclasses to provide a dictionary of key -> key mappings.
 
 The provided mappings that will be automatically applied to any attributes dictionaries. If a class 
 does not define a mapping for a particular key, the system will step up the inheritance chain until 
 one is found.
 
 The value of @c model will always be @c nil, and it exists solely so that subclasses may downcast
 the parameter to take advantage of the @c \@key macro when registering mappings.
 
 @note This method is called and cached once, so it cannot contain dynamic keys.
 @warning You should not call @c super.
 
 @param model A placholder value that is always @c nil
 @return A dictionary of key -> key mappings.
 */

+ (NSDictionary *)mappings:(id)model;

/**
 @brief Factory mechanism for model superclasses to return a customized subclass.
 
 This method is called as part of modelWithDictionary: before allocation to provide a class the opportunity
 to return a subclass based on type information contained within the update dictionary. The cycle will repeat
 on the subclass if one is returned.
 
 The value of @c placeholder will always be @c nil, and it exists solely so that subclasses may downcast
 the parameter to take advantage of the @c \@key macro when registering transformers.
 
 @param dictionary An attributes dictionary.
 @param placeholder A placholder value that is always @c nil
 
 @return The subclass to instantiate or this class.
*/

+ (Class<EHIModel>)subclassToInstantiateForDictionary:(NSDictionary *)dictionary placeholder:(id)placeholder;

/**
 @brief Allows models to process the update attributes before they're applied
 
 The parameterized dictionary is mutable, and updated values should be set on the dictionary (@em not on the
 model) in the general case. Primary use is to perform any necessary remapping (ie. enumerations) and to create
 child models from sub-dictionaries if they can't be parsed automatically.
 
 @param dictionary Dictionary of model attributes
*/

- (void)parseDictionary:(NSMutableDictionary *)dictionary;

@end

@interface EHIModel (Transformers)

/**
 @brief Allows subclasses to register transformers for various values
 
 Transformers can be registered by calling @c +key:registerTransformer: or @c +key:registerMap:. Any
 attributes dictionary that contains the given @c key will have its value passed through the transformer before setting
 it on the model.
 
 @param model A placeholder value that is always @c nil
*/

+ (void)registerTransformers:(id)model;

/** Registers a transformer for values of the given key */
+ (void)key:(NSString *)key registerTransformer:(NSValueTransformer *)transformer;
/** Creates an @c EHIMapTransformer from the map, and registers it the given key */
+ (void)key:(NSString *)key registerMap:(NSDictionary *)map;
/** Creates an @c EHIMapTransformer from the map, and registers it the given key */
+ (void)key:(NSString *)key registerMap:(NSDictionary *)map defaultValue:(id)value;

/** Returns the value transformer asssosciated with the specific property key, if any */
+ (NSValueTransformer *)transformerForKey:(NSString *)key;

@end
