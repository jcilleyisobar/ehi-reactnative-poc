//
//  EHIReachability.m
//  Enterprise
//
//  Created by Ty Cobb on 6/15/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import <AFNetworking/AFNetworkReachabilityManager.h>
#import "EHIReachability.h"
#import "EHIConfiguration.h"

NS_ASSUME_NONNULL_BEGIN

@interface EHIReachability ()
@property (copy  , nonatomic) NSHashTable *listeners;
@property (assign, nonatomic) BOOL isRetrying;
@property (nonatomic, readonly) BOOL networkIsReachable;
@end

@implementation EHIReachability

+ (instancetype)sharedInstance
{
    static EHIReachability *sharedInstance;
    
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedInstance = [self new];
    });
    
    return sharedInstance;
}

- (instancetype)init
{
    if(self = [super init]) {
        _listeners = [NSHashTable weakObjectsHashTable];
    
        // start listening for AFNetorkings reachability changes
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceiveReachabilityNotification:) name:AFNetworkingReachabilityDidChangeNotification object:nil];
        [[AFNetworkReachabilityManager sharedManager] startMonitoring];
    }
    
    return self;
}

- (void)retry
{
    if(self.isRetrying) {
        return;
    }
    
    self.isRetrying = YES;
    
    // attempt to fetch the config feed and notify listeners of any change
    [[EHIConfiguration configuration] onReady:^(BOOL isReady) {
        [self setIsRetrying:NO];
        [self notifyListeners];
    }];
}

# pragma mark - Listeners

- (void)addListener:(id<EHIReachabilityListener>)listener
{
    if([self.listeners containsObject:listener]) {
        return;
    }
    
    [self.listeners addObject:listener];
    [listener reachability:self didChange:self.isReachable];
}

# pragma mark - Callbacks

- (void)didReceiveReachabilityNotification:(NSNotification *)notification
{
    // if we're reachable or have no network, there's nothing to do so notify listeners
    if(self.isReachable || !self.networkIsReachable) {
        [self notifyListeners];
    }
    // otherwise, fetch the config feed if necessary and then notify listners
    else {
        [self retry];
    }
}

- (void)notifyListeners
{
    NSArray *listeners = [self.listeners allObjects];
    for(id<EHIReachabilityListener> listener in listeners) {
        [listener reachability:self didChange:self.isReachable];
    }
}

# pragma mark - Accessors

- (BOOL)isReachable
{
    return self.networkIsReachable && [EHIConfiguration configuration].isReady;
}

- (BOOL)isReachabilityUnknown
{
    return [AFNetworkReachabilityManager sharedManager].networkReachabilityStatus == AFNetworkReachabilityStatusUnknown;
}

- (BOOL)networkIsReachable
{
    return [AFNetworkReachabilityManager sharedManager].isReachable;
}

@end

NS_ASSUME_NONNULL_END
