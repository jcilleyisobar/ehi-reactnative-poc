//
//  NAVTransitionBuilder.h
//  Enterprise
//
//  Created by Alex Koller on 12/4/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "NAVAnimationTransition.h"

@protocol NAVTransitionBuilderDelegate;

@interface NAVTransitionBuilder : NSObject

@property (weak  , nonatomic) id<NAVTransitionBuilderDelegate> delegate;

- (NAVTransitionBuilder *(^)(BOOL))animated;

/**
 @brief If all stack transitions are combined to a single transition
 
 If yes, creating successive stack transitions via @c -root, @c -push, or @c -pop will
 automatically combine the transitions internally to create a single fluid transition.
 Appending any non-supported combinable transition finalizes any currently built
 multi-step stack transition. The final transition's @c animated of a multi-step stack
 transition will determine the whole transition's @c animated.
 
 Defaults to @c YES.
 */

- (NAVTransitionBuilder *(^)(BOOL))combineStack;

/** An arbitrary object to be passed along in the transition's attributes */
- (NAVTransitionBuilder *(^)(id))object;

/** An arbitrary handler to be passed along in the transition's attributes */
- (NAVTransitionBuilder *(^)(id))handler;

/**
 Stack
 */

/** Pops to root with the given screen. Will reuse and update current root if possible. */
- (NAVTransitionBuilder *(^)(NSString *))root;

/** Pushes the given screen onto the navigation stack */
- (NAVTransitionBuilder *(^)(NSString *))push;

/** Pops the given amount of screens of the navigation stack */
- (NAVTransitionBuilder *(^)(NSUInteger))pop;

/** Navigates to the associated screen in the backstack or pushes it on top if it doesn't exist */
- (NAVTransitionBuilder *(^)(NSString *))resolve;

/**
 Animation
 */

/** Convenience for @code -animateWithOptions(NSString *, NAVAnimationOptionsVisible|NAVAnimationOptionsModal) @endcode */
- (NAVTransitionBuilder *(^)(NSString *))present;

/** Convenience for @code -animateWithOptions(nil, NAVAnimationOptionsHidden|NAVAnimationOptionsModal) @endcode
 
 @note Objects and handlers passed during a dismiss transition will go to the view controller performing the transition
 which may @b not be the ultimately visible view controller.
 */

- (NAVTransitionBuilder *)dismiss;

/** Convenience for @code -animateWithOptions(NSString *, BOOL ? NAVAnimationOptionsVisible : NAVAnimationOptionsHidden) @endcode */
- (NAVTransitionBuilder *(^)(NSString *, BOOL))animate;

/**
 @brief Perform the associated animation transition for the given screen
  
 NAVAnimationOptionsHidden  Default
 NAVAnimationOptionsVisible Bring the given screen into view using an animation.
 NAVAnimationOptionsAsync   When this animation starts, does not wait until finish to queue up 
                            further transitions.
 NAVAnimationOptionsModal   Animate the given screen through standard modal present/dismiss APIs. A
                            screen name is not required when dismissing a modal. If not included, the 
                            router looks for an appropriate animation registered through NAVTransitionFactory.
 */

- (NAVTransitionBuilder *(^)(NSString *, NAVAnimationOptions))animateWithOptions;

/**
 @brief Builds the current transition
 
 Use this method when a single transition is required.
 */

- (NAVTransition *)build;

/**
 @brief Runs all transitions through the delegate
 
 Use this method when running 1 or more transitions associated with the builder. Each
 call to a transition method on this builder (root, push, pop, resolve, present,
 dismiss, animate) marks the beginning of a new transition.
 
 @param block:completion A callback when @b all transitions on this builder complete
 */

- (void (^)(void (^)(void)))start;

@end

@protocol NAVTransitionBuilderDelegate <NSObject>

/**
 @brief Enqueues the transitions on this builder for execution
 
 Enqeueus all transitions created by this builder to be executed. If no transitions are
 currently being performed, the first transition is run immediately.
 */

- (void)enqueueTransitions:(NSArray *)transitions forBuilder:(NAVTransitionBuilder *)transitionBuilder;

@end