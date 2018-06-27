//
//  EHIKeychainSerializer.h
//  Enterprise
//
//  Created by Ty Cobb on 4/29/15.
//  Copyright (c) 2015 Isobar. All rights reserved.
//

#import "EHIDataStoreSerializer.h"

@interface EHIKeychainSerializer : NSObject <EHIDataStoreSerializer>

/** Returns the shared keychain instance */
+ (instancetype)serializer;

/** Bootstraps the user manager, allowing it to perform startup tasks */
+ (void)prepareToLaunch;

/**
 @brief Stores an object in the keychain.
 
 The object is archived using NSKeyedArchiver synchronously before being storage. If the parameterized
 object is nil and the keychain contains an existing value for the key, the value will be deleted.
 
 @param object The object to store in the keychain
 @param key    The key that maps to the stored object
*/

- (void)setObject:(id<NSCoding>)object forKey:(NSString *)key;

/**
 @brief Returns the object stored in the keychain for a given key.
 
 The object will be unarchived using NSKeyedUnarchiver synchronously before it's returned, and if no
 object exists for the parameterized key this method returns nil.
 
 @param key The key corresponding to the object
 @return The object in the keychain matching the parameterized key.
*/

- (id)objectForKey:(NSString *)key;

@end
