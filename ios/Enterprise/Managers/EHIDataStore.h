//
//  EHIDataStore.h
//  Enterprise
//
//  Created by Ty Cobb on 2/23/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIDataStoreRequest.h"

@interface EHIDataStore : NSObject

/**
 @brief Saves the model to disk
 
 If the save is successful, the handler is called back with @c YES, otherwise, it is
 called with @c NO.

 @param model   The model to save
 @param handler The handler to call when the write completes
*/

+ (void)save:(EHIModel *)model handler:(void(^)(BOOL didSucceed))handler;

/**
 @brief Saves the list of models to disk
 
 If each save is successful, the handler is called back with @c YES, otherwise, it is
 called with @c NO.

 @param models  The models to save
 @param handler The handler to call when the write completes
*/

+ (void)saveAll:(NSArray *)models handler:(void(^)(BOOL didSucceed))handler;

/**
 @brief Finds all the models of the specified type
 
 If no models exist, then the handler will be passed an empty array.
 
 @param klass   The class of models to find
 @param handler The handler to call with the found models, if any
*/

+ (void)find:(Class<EHIModel>)klass handler:(void(^)(NSArray *models))handler;

/**
 @brief Finds the first model of the specified type
 
 If no models exist, the handler is passed @c nil.
 
 @param klass   The class of model to find
 @param handler The handler to call with the found model, if any
*/

+ (void)first:(Class<EHIModel>)klass handler:(void(^)(id result))handler;

/**
 @brief Removes the model from the data store, if it exists
 
 When finished, this method calls back the block with a flag indicating if
 anything was actually removed.
 
 @param model   The model to remove the matching record for
 @param handler The handler to call when the removal finishes
*/

+ (void)remove:(EHIModel *)model handler:(void(^)(BOOL didRemove))handler;

/**
 @brief Removes all the models of the specified class from the data store
 
 When finished, this method calls back the block with a flag indicating if
 anything was actually removed.
 
 @param klass   The class of models to remove
 @param handler The handler to call when the removal finishes
*/

+ (void)purge:(Class<EHIModel>)klass handler:(void(^)(BOOL didRemove))handler;

/**
 @brief Starts a generic @c EHIDataStoreRequest
 
 If the appropriate parameters are not set for the request type, this method throws an 
 exception. The handler is called back in the form of the related specialized operations.
 
 @param request The request to make
 @param handler The handler to call on completion
*/

+ (void)start:(EHIDataStoreRequest *)request handler:(id)handler;

/**
 @brief Finds all the models of the specified type in memory
 
 Will search the in-memory cache for all models of the specified type and return
 them. Any models previously saved to disk and not loaded in memory will be ignored. 
 Models saved to disk can be loaded into memory via an asynchronous @c -find operation
 
 @param klass The class of models to find
 @return The found models, if any
*/

+ (NSArray *)findInMemory:(Class<EHIModel>)klass;

/**
 @brief Checks if the data store has any records for this class
 
 This method runs synchrously, and is reliable regardless of whether or not the data store
 has loaded any data yet.
*/

+ (BOOL)any:(Class<EHIModel>)klass;

@end
