//
//  EHINavigationAnimation.h
//  Enterprise
//
//  Created by Ty Cobb on 1/29/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHINavigationAnimatable.h"
#import "EHINavigationAnimationBuilder.h"

@interface EHINavigationAnimation : NSObject <EHINavigationAnimatable>

/** The block type used by view animation subclasses */
typedef void(^EHINavigationAnimationBlock)(UIView *target, CGFloat percentComplete);

/** @c YES if the animation is becoming visible */
@property (assign, nonatomic) BOOL isEntering;
/** @c YES if the animation is a push transition, @c NO if it's a pop */
@property (assign, nonatomic) BOOL isPush;

/** The target that is passed to the animation blocks */
@property (weak  , nonatomic) UIView *target;
/** @c YES if the target should be temporarily inserted into the animation container during animation */
@property (assign, nonatomic) BOOL shouldProxyTarget;
/** @c YES if the animation should force its target to layout during animation */
@property (assign, nonatomic) BOOL forcesLayout;
/** @c YES if the animation should be applied in reverse order on unwind; defaults to @c YES */
@property (assign, nonatomic) BOOL reversesOrderOnUnwind;
/** A bitmask of animation options to apply to this particular animation */
@property (assign, nonatomic) UIViewAnimationOptions options;
/** An array of @c EHINavigationAnimationBlock that are called during execution */
@property (strong, nonatomic) NSArray *animationBlocks;

/** The amount of time to wait before initiating the animation */
@property (assign, nonatomic) NSTimeInterval delay;
/** The transition-independent duration for this animation */
@property (assign, nonatomic) NSTimeInterval duration;

/** Calculates the actual delay based on the total duration and the animation's internal configuration */
- (NSTimeInterval)delayForTotalDuration:(NSTimeInterval)duration;
/** Calcualtes the end-time for this animation based on the total duration and the animation's interal configuration */
- (NSTimeInterval)terminalTimeForTotalDuration:(NSTimeInterval)duration;

@end

@interface EHINavigationAnimation (Building)

/**
 @brief Sets the animation's target
 
 Any animations passed to the builder are executed on the target when the animation 
 runs. Mutually exclusive with @c proxy.
*/

+ (EHINavigationAnimationBuilder *(^)(UIView *))target;

/**
 @brief Sets the animation's proxy target
 
 This method functions identically to @c target, except that the view is removed from
 its superview and placed into the animation's container during animation. It is restored 
 to its original superview when the animation completes.
*/

+ (EHINavigationAnimationBuilder *(^)(UIView *))proxy;

@end


