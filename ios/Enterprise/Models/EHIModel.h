//
//  EHIModel.h
//  Enterprise
//
//  Created by Ty Cobb on 1/5/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIMappable.h"
#import "EHIComparable.h"
#import "EHINetworkEncodable.h"
#import "EHIEncodableObject.h"
#import "EHICollection.h"

#define EHIAnnotatable(_model) @protocol _model;

@protocol EHIModel <NSObject>

/** Returns the shared collection instance for this class. Pass-through to @c +collection. */
@property (nonatomic, readonly) EHICollection *collection;

/**
 @brief Factory method for creating a data model.
 
 Returns an instance of this class or some subclasss, depending on the model's implementation.
 Returns nil if the parameterized dictionary is nil.
 
 @param dictionary Dictionary of key-value pairs with which to update the model
 @return An instance of this class (or some subclass) populated with the parameterized dictionary
*/

+ (instancetype)modelWithDictionary:(NSDictionary *)dictionary;

/**
 @brief Factory method for creating a collection of data models.
 
 Returns instances of this class or some subclasss(es), depending on the model's implementation.
 Returns nil if the parameterized collection is nil.
 
 @param dictionaries Dictionary of key-value pairs with which to update the model
 @return An instance of this class (or some subclass) populated with the parameterized dictionary
*/

+ (id)modelsWithDictionaries:(id<EHIMappable>)dictionaries;

/**
 @brief Access the metadata assosciated with this class.
 
 The metadata object provides class-instance storage to subclasses of @c EHIModel. Each @c EHIModel
 subclass has its own distinct metadata object that stores that specific class' metadata.
 
 @return This class's metadata
 */

+ (EHICollection *)collection;

/**
 @brief Returns a placeholder instance
 
 Useful when desirable to render a data-driven UI element but there's no data available. This
 object returns @c YES for @c -isPlaceholder.
 
 @return A placeholder model
*/

+ (instancetype)placeholder;

@end

@interface EHIModel : EHIEncodableObject <EHIModel, EHIComparable, EHINetworkEncodable>

/** Unique identifier for models; subclasses should map their IDs to this key */
@property (nonatomic, readonly) id uid;

/** @c YES if the model was created via @c +placeholder */
@property (nonatomic, readonly) BOOL isPlaceholder;

/**
 @brief Updates the model with a dictionary of attributes

 If a value is not in this dictionary, the existing value (if any) remains unchanged. This
 method is a pass-through to @c -updateWithDictionary:forceDeletions: with @c forceDeletions
 as @c NO.
 
 @param dictioanry Dictionary of model attributes
*/

- (void)updateWithDictionary:(NSDictionary *)dictionary;

/**
 @brief Updates the model with the a dictionary of attributes
 
 If the updates contain a @c nil, @c null, or empty (in the case of a collection) value for a particular
 key _and_ the instance has an exisiting value for that key, then the exisiting value by default will 
 @em not be deleted.
 
 However, if @c YES is passed for @c forceDeletions, existing values will be deleted in the 
 aforementioned cases.
 
 @param dictionary     Dictionary of model attributes
 @param forceDeletions @c YES if existing values should be cleared
*/

- (void)updateWithDictionary:(NSDictionary *)dictionary forceDeletions:(BOOL)forceDeletions;

@end
