//
//  EHIDataStore.m
//  Enterprise
//
//  Created by Ty Cobb on 2/23/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIDataStore.h"
#import "EHIDataStoreRecord.h"
#import "EHIDataStoreCache.h"
#import "EHIFileSerializer.h"
#import "EHIKeychainSerializer.h"

@interface EHIDataStore ()
@property (strong, nonatomic) NSMutableDictionary *caches;
@end

@implementation EHIDataStore

+ (instancetype)sharedInstance
{
    static EHIDataStore *sharedInstance;
    
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedInstance = [self new];
    });
    
    return sharedInstance;
}

- (instancetype)init
{
    if(self = [super init]) {
        _caches = [NSMutableDictionary new];
    }
    
    return self;
}

# pragma mark - Synchronous Operations

+ (NSArray *)findInMemory:(Class<EHIModel>)klass
{
    // generate the find request
    EHIDataStoreRequest *request = [EHIDataStoreRequest find:[klass collection]];
    // return whatever is available in the in-memory cache
    EHIDataStoreCache *cache = [[self sharedInstance] cacheForRequest:request];
    return [[self sharedInstance] cache:cache resultForRequest:request];
}

+ (BOOL)any:(Class<EHIModel>)klass
{
    return [[NSUserDefaults standardUserDefaults] integerForKey:[klass collection].name] != 0;
}

# pragma mark - Specialized Operations

+ (void)find:(Class<EHIModel>)klass handler:(void (^)(NSArray *))handler
{
    EHIDataStoreRequest *request = [EHIDataStoreRequest find:[klass collection]];
    [[self sharedInstance] start:request handler:handler];
}

+ (void)first:(Class<EHIModel>)klass handler:(void (^)(id))handler
{
    EHIDataStoreRequest *request = [EHIDataStoreRequest first:[klass collection]];
    [[self sharedInstance] start:request handler:handler];
}

+ (void)save:(EHIModel *)model handler:(void (^)(BOOL))handler
{
    if(!model) {
        EHIDomainDebug(EHILogDomainFiles, @"attmempted to save a nil model");
        return;
    }
   
    // start the write for this model
    EHIDataStoreRequest *request = [EHIDataStoreRequest save:model];
    [[self sharedInstance] start:request handler:handler];
}

+ (void)saveAll:(NSArray *)models handler:(void(^)(BOOL didSucceed))handler
{
    dispatch_group_t group = dispatch_group_create();
    __block BOOL success = YES;
    
    for(EHIModel *model in models) {
        dispatch_group_enter(group);
        
        [self save:model handler:^(BOOL didSucceed) {
            success &= didSucceed;
            dispatch_group_leave(group);
        }];
    }
    
    dispatch_group_notify(group, dispatch_get_main_queue(), ^{
        ehi_call(handler)(success);
    });
}

+ (void)remove:(EHIModel *)model handler:(void (^)(BOOL))handler
{
    if(!model) {
        EHIDomainDebug(EHILogDomainFiles, @"attempted to remove a nil model");
        return;
    }
   
    // start the delete for this model
    EHIDataStoreRequest *request = [EHIDataStoreRequest remove:model];
    [[self sharedInstance] start:request handler:handler];
}

+ (void)purge:(Class<EHIModel>)klass handler:(void (^)(BOOL))handler
{
    // create a delete request for the class' collection
    EHIDataStoreRequest *request = [EHIDataStoreRequest purge:[klass collection]];
    [[self sharedInstance] start:request handler:handler];
}

# pragma mark - Requests

+ (void)start:(EHIDataStoreRequest *)request handler:(id)handler
{
    // dispatch to the instance method
    [[self sharedInstance] start:request handler:handler];
}

- (void)start:(EHIDataStoreRequest *)request handler:(id)handler
{
    NSParameterAssert(request.collection);
   
    // ensure we've created the correct directories for this request
    [self ensurePathingForRequest:request];
  
    // branch to the correct operation type
    if(request.isRead) {
        [self startRead:request handler:handler];
    } else {
        [self startWrite:request handler:handler];
    }
}

//
// Helpers
//

- (void)startRead:(EHIDataStoreRequest *)request handler:(void(^)(id))handler
{
    EHIDataStoreCache *cache = [self cacheForRequest:request];
    
    // if this is cached, just return the result
    if(cache.isSynchronized) {
        ehi_call(handler)([self cache:cache resultForRequest:request]);
    }
    // otherwise, read the items from the filesystem
    else {
        [[self serializerForRequest:request] readAll:request.collection.name handler:^(NSArray *records) {
            // if we haven't synchronzied already, then do so
            if(!cache.isSynchronized) {
                EHIDataStoreRecord *record;
                // apply default sorting to the records and pull out the models
                NSArray *models = (records ?: @[]).sort.pluck(@key(record.model));
                // cache the models if necessary
                [[self cacheForRequest:request] populateWithModels:models];
            }
           
            ehi_call(handler)([self cache:cache resultForRequest:request]);
        }];
    }
}

- (void)startWrite:(EHIDataStoreRequest *)request handler:(void(^)(BOOL))handler
{
    void(^localHandler)(BOOL) = ^(BOOL didUpdate) {
        // write the number of items in this collection to user defaults so we can fetch
        // it synchronously at any time
        EHIDataStoreCache *cache = [self cacheForRequest:request];
        [[NSUserDefaults standardUserDefaults] setObject:@(cache.objects.count) forKey:request.collection.name];
        
        // and call the base handler
        ehi_call(handler)(didUpdate);
    };
    
    // perform the correct file operation
    switch(request.type) {
        case EHIDataStoreRequestTypeSave:
            [self startSave:request handler:localHandler]; break;
        case EHIDataStoreRequestTypeDelete:
            [self startDelete:request handler:localHandler]; break;
        default: NSAssert(false, @"this is not a valid write request type");
    }
}

- (void)startSave:(EHIDataStoreRequest *)request handler:(void(^)(BOOL))handler
{
    NSAssert(request.model.uid, @"save request: %@ requires a model", request);
    
    // update the cache entry for this model
    EHIDataStoreCache *cache = [self cacheForRequest:request];
    cache[request.model.uid] = request.model;
    
    // if requested, skip saving to disk
    if(request.collection.inMemoryOnly) {
        ehi_call(handler)(YES);
    }
    // otherwise, save to long-term storage
    else {
        // create a record from this model
        EHIDataStoreRecord *record = [[EHIDataStoreRecord alloc] initWithModel:request.model];
        
        // and write it to disk
        NSString *filepath = [self filepathForRequest:request];
        [[self serializerForRequest:request] write:record toPath:filepath handler:handler];
    }
    
    // and then enforce the limit on this collection
    [self limit:request.collection handler:nil];
}

- (void)startDelete:(EHIDataStoreRequest *)request handler:(void(^)(BOOL))handler
{
    NSAssert(request.model.uid || (!request.model && request.collection), @"delete request: %@ requires a model", request);
    
    // update the cache
    EHIDataStoreCache *cache = [self cacheForRequest:request];
    if(request.model) {
        cache[request.model.uid] = nil;
    } else if(request.collection) {
        [cache reset];
    }
   
    // remove the file(s)
    if(!request.collection.inMemoryOnly) {
        NSString *filepath = [self filepathForRequest:request];
        [[self serializerForRequest:request] remove:filepath handler:handler];
    }
}

# pragma mark - Internal

- (void)limit:(EHICollection *)collection handler:(void (^)(BOOL))handler
{
    // don't do anything if this collection is unlimited
    if(!collection.hasHistoryLimit) {
        return;
    }
    
    EHIDataStoreRequest *request = [EHIDataStoreRequest find:collection];
    [self start:request handler:^(NSArray *models) {
        NSInteger deletionCount = models.count - collection.historyLimit;
        
        // if we have no records to remove, call back the handler
        if(deletionCount <= 0) {
            ehi_call(handler)(NO);
            return;
        }
        
        // otherwise, delete the last n records
        NSArray *modelsToRemove = models.last(deletionCount);
        __block BOOL didRemoveAnyModel = NO;
        
        // callback the handler after deleting each model
        dispatch_group_t group = dispatch_group_create();
        for(EHIModel *model in modelsToRemove) {
            // create the delete request in the correct collection
            EHIDataStoreRequest *request = [EHIDataStoreRequest remove:model];
            request.collection = collection;
            
            dispatch_group_enter(group);
            [self start:request handler:^(BOOL didRemove) {
                didRemoveAnyModel |= didRemove;
                dispatch_group_leave(group);
            }];
        }
        
        dispatch_group_notify(group, dispatch_get_main_queue(), ^{
            ehi_call(handler)(didRemoveAnyModel);
        });
    }];
}

# pragma mark - Request Generation


# pragma mark - Cache

- (EHIDataStoreCache *)cacheForRequest:(EHIDataStoreRequest *)request
{
    return self.caches[request.collection.name];
}

- (id)cache:(EHIDataStoreCache *)cache resultForRequest:(EHIDataStoreRequest *)request
{
    // only return the result if we have a populable and populated cache
    if(!cache.isSynchronized && !request.collection.inMemoryOnly) {
        return nil;
    }
    
    // return the correct value
    switch(request.type) {
        case EHIDataStoreRequestTypeFind:
            return cache.objects;
        case EHIDataStoreRequestTypeFirst:
            return cache.objects.firstObject;
        default: return nil;
    }
}

# pragma mark - Helpers

- (void)ensurePathingForRequest:(EHIDataStoreRequest *)request
{
    if(!self.caches[request.collection.name]) {
        // if not, ensure we do and add it to our collections
        [[self serializerForRequest:request] ensureDirectoriesToPath:request.collection.name];
        [self.caches setObject:[EHIDataStoreCache new] forKey:request.collection.name];
    }
}

- (NSString *)filepathForRequest:(EHIDataStoreRequest *)request
{
    // start with the collection name
    NSString *filepath = request.collection.name;
    
    // secure requests only support one item at the moment, so they'll sidestep the normal
    // logic and use the collection name as the path
    if(request.collection.isSecure) {
        return filepath;
    }
   
    // append the model id if this is an individual item request and it's not secure.
    if(request.model) {
        filepath = [filepath stringByAppendingFormat:@"/%@", request.model.uid];
    }
    // if this is a request for an entire collection of items, glob the directory
    else {
        filepath = [filepath stringByAppendingString:@"/*"];
    }
    
    return filepath;
}

# pragma mark - Accessors

- (id<EHIDataStoreSerializer>)serializerForRequest:(EHIDataStoreRequest *)request
{
    if(request.collection.isSecure) {
        return [EHIKeychainSerializer serializer];
    } else {
        return [EHIFileSerializer serializer];
    }
}

@end
