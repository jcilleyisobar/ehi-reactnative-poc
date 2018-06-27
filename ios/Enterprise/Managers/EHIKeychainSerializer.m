//
//  EHIKeychainSerializer.m
//  Enterprise
//
//  Created by Ty Cobb on 4/29/15.
//  Copyright (c) 2015 Isobar. All rights reserved.
//

#import "EHIKeychainSerializer.h"
#import "EHISettings.h"

@interface EHIKeychainSerializer ()
@property (nonatomic, readonly) NSOperationQueue *queue;
@end

@implementation EHIKeychainSerializer

+ (instancetype)serializer
{
    static EHIKeychainSerializer *serializer;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        serializer = [EHIKeychainSerializer new];
    });
    
    return serializer;
}

- (instancetype)init
{
    if(self = [super init]) {
        _queue = [NSOperationQueue new];
        _queue.maxConcurrentOperationCount = 1;
    }
    
    return self;
}

+ (void)prepareToLaunch
{
    // clear out the keychain if its the first run of the app
    if([EHISettings sharedInstance].isFirstRun){
        NSArray *secItemClasses = @[
            (__bridge id)kSecClassGenericPassword,
            (__bridge id)kSecClassInternetPassword,
            (__bridge id)kSecClassCertificate,
            (__bridge id)kSecClassKey,
            (__bridge id)kSecClassIdentity
        ];
        
        for(id secItemClass in secItemClasses) {
            NSDictionary *spec = @{(__bridge id)kSecClass: secItemClass};
            SecItemDelete((__bridge CFDictionaryRef)spec);
        }
    }
}

# pragma mark - EHIDataStoreSerializer

- (void)write:(id<NSCoding>)object toPath:(NSString *)path handler:(void(^)(BOOL successful))handler
{
    [self.queue addOperationWithBlock:^{
        [self setObject:object forKey:path];
        dispatch_async(dispatch_get_main_queue(), ^{
            ehi_call(handler)(YES);
        });
    }];
}

- (void)readAll:(NSString *)path handler:(void(^)(NSArray *objects))handler
{
    [self.queue addOperationWithBlock:^{
        id result = [self objectForKey:path];
        dispatch_async(dispatch_get_main_queue(), ^{
            ehi_call(handler)(result ? @[ result ] : nil);
        });
    }];
}

- (void)remove:(NSString *)path handler:(void(^)(BOOL didRemove))handler
{
    [self.queue addOperationWithBlock:^{
        BOOL willDelete = [self objectForKey:path] != nil;
        [self setObject:nil forKey:path];
        dispatch_async(dispatch_get_main_queue(), ^{
            ehi_call(handler)(willDelete);
        });
    }];
}

- (void)ensureDirectoriesToPath:(NSString *)path
{
    // the keychain can't do anything here
}

# pragma mark - Updating

- (void)setObject:(id<NSCoding>)object forKey:(NSString *)key
{
    NSMutableDictionary *query = [self queryForIdentifier:key];
    if(object) {
        [self upsertData:[NSKeyedArchiver archivedDataWithRootObject:object] forQuery:query];
    } else {
        [self deleteDataForQuery:query];
    }
}

- (void)setObject:(id<NSCoding>)object forKeyedSubscript:(NSString *)key
{
    [self setObject:object forKey:key];
}

//
// Helpers
//

- (BOOL)upsertData:(NSData *)data forQuery:(NSMutableDictionary *)query
{
    query[(__bridge id)kSecValueData] = data;
    
    OSStatus status = SecItemAdd((__bridge CFDictionaryRef)query, NULL);
    
    switch(status) {
        case errSecSuccess:
            return YES;
        case errSecDuplicateItem:
            return [self updateData:data forQuery:query];
        default:
            return NO;
    }
}

- (BOOL)updateData:(NSData *)data forQuery:(NSMutableDictionary *)query
{
    [query removeObjectForKey:(__bridge id)kSecValueData];
    NSDictionary *attributes = @{ (__bridge id)kSecValueData : data };
    
    OSStatus status = SecItemUpdate((__bridge CFDictionaryRef)query, (__bridge CFDictionaryRef)attributes);
    return status == errSecSuccess;
}

- (BOOL)deleteDataForQuery:(NSMutableDictionary *)query
{
    OSStatus status = SecItemDelete((__bridge CFDictionaryRef)query);
    return status == errSecSuccess;
}

# pragma mark - Fetching

- (id<NSCoding>)objectForKey:(NSString *)key
{
    NSData *data = [self retrieveDataForKey:key];
    if(!data) {
        return nil;
    }
    
    id<NSCoding> object = nil;
    
    @try {
        object = [NSKeyedUnarchiver unarchiveObjectWithData:data];
    } @catch (NSException *exception) {
        EHIDomainError(EHILogDomainFiles, @"keychain :: %@", exception);
    }
    
    return object;
}

- (id<NSCoding>)objectForKeyedSubscript:(NSString *)key
{
    return [self objectForKey:key];
}

//
// Helpers
//

- (NSData *)retrieveDataForKey:(NSString *)key
{
    NSMutableDictionary *query = [self queryForIdentifier:key];
    query[(__bridge id)kSecReturnData] = (__bridge id)kCFBooleanTrue;
    query[(__bridge id)kSecMatchLimit] = (__bridge id)kSecMatchLimitOne;
    
    CFTypeRef result = NULL;
    OSStatus  status = SecItemCopyMatching((__bridge CFDictionaryRef)query, &result);
    
    return status == errSecSuccess ? (__bridge NSData *)result : nil;
}

# pragma mark - Query

- (NSMutableDictionary *)queryForIdentifier:(NSString *)identifier
{
    NSParameterAssert(identifier);
    
    return [@{
        (__bridge id)kSecAttrService    : [[NSBundle mainBundle] bundleIdentifier],
        (__bridge id)kSecClass          : (__bridge id)kSecClassGenericPassword,
        (__bridge id)kSecAttrAccessible : (__bridge id)kSecAttrAccessibleWhenUnlocked,
        (__bridge id)kSecAttrAccount    : identifier,
        (__bridge id)kSecAttrGeneric    : [identifier dataUsingEncoding:NSUTF8StringEncoding],
    } mutableCopy];
}

@end
