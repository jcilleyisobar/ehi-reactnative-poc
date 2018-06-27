//
//  EHICollection.h
//  Enterprise
//
//  Created by Ty Cobb on 1/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@interface EHICollection : NSObject

# pragma mark - Serialization

/** The name for this collection; by default it is the stringified model class */
@property (copy, nonatomic) NSString *name;

/**
 @brief The maximum number of records to store for this collection

 Subclasses may customize this property the impose a limit on how many items may
 be stored from this collection. If the limit is exceeded, the oldest item is
 removed.
 
 The default value is unbounded.
 
 @note This is unimplemented
*/

@property (assign, nonatomic) NSInteger historyLimit;

/**
 @brief @c YES if the history should be stored securely
 
 If this collection should be stored securly, its data is written to an encrypted 
 data store.
 
 @note Presently, only collections with one item can be stored securely
*/

@property (assign, nonatomic) BOOL isSecure;

/**
 @brief @c YES if this collection should skip saving to disk
 
 If this collection should be persisted in memory only, no data will be
 saved to disk during save operations. Find operations will also ignore any
 data on the disk.
*/

@property (assign, nonatomic) BOOL inMemoryOnly;

/** @c YES if the collection has a history limit imposed */
@property (nonatomic, readonly) BOOL hasHistoryLimit;

# pragma mark - Parsing

/**
 @brief The key -> key mappings assosciated with this collection
 
 The mappings are applied to any dictionaries used to construct models of this
 collection type before updating the model.
*/

@property (strong, nonatomic) NSDictionary *mappings;

/** 
 @brief A dictionary of key -> transformer entries for this collection
 
 If an attributes dictionary contains the a key-value pair matching the key of one
 of these transformers, its value will be transformed before setting it on the model.
*/

@property (strong, nonatomic) NSMutableDictionary *transformers;

@end
