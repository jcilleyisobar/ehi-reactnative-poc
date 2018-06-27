//
//  EHIReachabilityViewModel.h
//  Enterprise
//
//  Created by Ty Cobb on 6/15/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface EHIReachabilityViewModel : EHIViewModel <MTRReactive>

/** The title for the unreachable state modal */
@property (copy  , nonatomic, readonly) NSString *title;
/** The explanatory text for the unreachable state modal */
@property (copy  , nonatomic, readonly) NSString *details;
/** Title for the retry button */
@property (copy  , nonatomic, readonly) NSString *retryTitle;
/** @c YES if the splash screen should be shown */
@property (assign, nonatomic, readonly) BOOL showsSplash;
/** @c YES if attempting to re-establish connection */
@property (assign, nonatomic, readonly) BOOL isLoading;
/** @c YES if reachability is struggling to make a connection */
@property (assign, nonatomic, readonly) BOOL isDelayed;

/** Attempts to re-establish connection to services */
- (void)retryConnection;

@end

NS_ASSUME_NONNULL_END
