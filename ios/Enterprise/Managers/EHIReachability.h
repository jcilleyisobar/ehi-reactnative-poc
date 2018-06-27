//
//  EHIReachability.h
//  Enterprise
//
//  Created by Ty Cobb on 6/15/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

NS_ASSUME_NONNULL_BEGIN

@protocol EHIReachabilityListener;

@interface EHIReachability : NSObject

/** @c YES if the network is currently reachable */
@property (nonatomic, readonly) BOOL isReachable;
/** @c YES if the reachability status is unknown */
@property (nonatomic, readonly) BOOL isReachabilityUnknown;

/** Returns the shared reachability manager */
+ (instancetype)sharedInstance;

/**
 @brief Adds a listener for reachability changes
 
 If the @c listener has already been added, this method does nothing.

 Listeners are not retained. Upon registration, @c -reachability:didChange: is 
 called immediately with the current state.
*/

- (void)addListener:(id<EHIReachabilityListener>)listener;

/** 
 @brief Attempts to re-connect to the server

 The listeners are notified regardless of whether or not the retry was successful.
*/

- (void)retry;

@end

@protocol EHIReachabilityListener <NSObject>
/** Called to notify the listener of reachability updates */
- (void)reachability:(EHIReachability *)reachability didChange:(BOOL)isReachable;
@end

NS_ASSUME_NONNULL_END
