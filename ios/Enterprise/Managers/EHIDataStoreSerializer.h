//
//  EHIDataStoreSerializer.h
//  Enterprise
//
//  Created by Ty Cobb on 4/29/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@protocol EHIDataStoreSerializer <NSObject>

/**
 Creates an archive from the parameterized object on the main thread, and asynchronously persists
 it on a background queue.
 
 @param object  The object to write to file
 @param path    The relative path of the file
 @param handler A callback when the write completes
*/

- (void)write:(id<NSCoding>)object toPath:(NSString *)path handler:(void(^)(BOOL successful))handler;

/**
 @brief Unarchives all the objects for the specified path
 
 This operation occurs on a background queue, and calls back the handler with the found
 objects, if any.
 
 @param path    The path to read objects from
 @param handler The handler to callback when the read completes
*/

- (void)readAll:(NSString *)path handler:(void(^)(NSArray *objects))handler;

/**
 @brief Removes the object at the given path
 
 Calls back the block with a flag indiciating if anything was actually removed.
 
 @param path    The path of the object to delete
 @param handler The handler to call back when the deletion finishes
*/

- (void)remove:(NSString *)path handler:(void(^)(BOOL didRemove))handler;

/**
 @brief Creates any necessary directories for the relative path
 
 This method isn't particularly safe. If the relative path has a filename on the end it'll be
 created as a directory. It also runs on the main thread.
 
 @param path The relative path
*/

- (void)ensureDirectoriesToPath:(NSString *)path;

@end
