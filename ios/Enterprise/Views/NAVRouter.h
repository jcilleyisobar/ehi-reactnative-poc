//
//  NAVRouter.h
//  Enterprise
//
//  Created by Ty Cobb on 6/25/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "NAVRouterDelegate.h"
#import "NAVTransitionPerformer.h"
#import "NAVTransitionBuilder.h"

@interface NAVRouter : NSObject

/**
 @brief Sends events concerning the router's update lifecycle. Optional
 
 A user may set the delegate of the router to receive such events
 */

@property (weak, nonatomic) id<NAVRouterDelegate> delegate;

/**
 @brief Object capable of consuming transitions. Required
 
 The transition performer is responsible for consuming @c NAVTransition objects
 and performing the necessary setup and exeuction of the transitions.
 */

@property (weak, nonatomic) id<NAVTransitionPerformer> transitionPerformer;

/**
 @brief Per-class shared instance.
 */

+ (instancetype)router;

/**
 @brief Returns a new transition builder that can be used to run a routing transition
 
 The builder contains a variety of chainable methods for customizing the routing desintation,
 transition attributes, and data passed to routed views.
 
 @see @c NAVTransitionBuilder for a complete list of what's available.
 
 @return A new NAVTransitionBuilder to construct the transition
 */

- (NAVTransitionBuilder *)transition;

@end