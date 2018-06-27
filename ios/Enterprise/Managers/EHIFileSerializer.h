//
//  EHIFileSerializer.h
//  Enterprise
//
//  Created by Ty Cobb on 2/23/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIDataStoreSerializer.h"

@interface EHIFileSerializer : NSObject <EHIDataStoreSerializer>

/**
 Shared instance serializer which writes files to the caches directory.
 @return A file serializer instance.
*/

+ (instancetype)serializer;

/**
 Creates a new serializer that writes files to the given search path directory.
 @param directory The search path directory to use as the base path.
 @return A new serializer instance
*/

- (instancetype)initWithDirectory:(NSSearchPathDirectory)directory;

/**
 Unarchives and instantiates the object archived on disk at the relative path on a background queue,
 and calls the handler when unarchiving completes.
 
 @param relativePath The relative path of the file
 @param handler      A callback when the read completes
*/

- (void)read:(NSString *)relativePath withHandler:(void(^)(id object))handler;

@end
