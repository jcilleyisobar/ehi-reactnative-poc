//
//  NAVTransitionAnimation.h
//  Enterprise
//
//  Created by Alex Koller on 12/1/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "NAVTransitionDestination.h"

@protocol NAVAnimationDelegate;

@interface NAVAnimation : NSObject <NAVTransitionDestination>

/**
 @brief Notifier for animation lifecycle events. Optional
 
 A user may set the delegate of the animation to receive such events
 */

@property (weak, nonatomic) id<NAVAnimationDelegate> delegate;

/**
 @brief Flag indicating whether the animator is visible
 
 Updating this flag will fire @c onPresentation and @c onDismissal events, but will not
 trigger any NAVAnimator callbacks. The animator should update this property after its
 animation is complete.
 */

@property (assign, nonatomic) BOOL isVisible;

/**
 @brief Updates the visible state of the animation
 
 If the isVisible state did not actually change, no animation is run and the completion
 is called immediately.
 
 @param isVisible  Flag indicating whether the animation is visible
 @param animated   Flag indicating whether the transition is animated
 @param completion Callback when the animation completes
 */

- (void)setIsVisible:(BOOL)isVisible animated:(BOOL)animated completion:(void (^)(void))completion;

@end

@interface NAVAnimation (Lifecycle)

/**
 @brief Performs the view updates to resolve the animation
 
 This method is only called if visibility actually changes. Subclasses must call back the completion
 correctly when the animation finishes.
 
 @param isVisible  Flag indicating whether the animation is visible
 @param animated   Flag indicating whether the transition is animated
 @param completion Callback when the animation completes
 */

- (void)updateIsVisible:(BOOL)isVisible animated:(BOOL)animated completion:(void(^)(void))completion;

/**
 @brief Called after the animation finishes
 
 This hook gets called regardless of whether the animation was triggered via a router update or
 externally.
 
 @param isVisible The new animation state
 */

- (void)didFinishAnimationToVisible:(BOOL)isVisible;

@end

@protocol NAVAnimationDelegate <NSObject>

/**
 @brief Notifies the delegate when the animation's state changes
 
 The router uses this internally to synchronize the current URL when animation happen
 outside a routing callback.
 
 @param animation The animation that is updating
 @param isVisible Flag indiciating whether the animation is visible
 */

- (void)animation:(NAVAnimation *)animation didUpdateIsVisible:(BOOL)isVisible;

@end
