//
//  UIView+Animation.h
//  Enterprise
//
//  Created by Ty Cobb on 2/3/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@class EHIAnimationBuilder;

@interface UIView (Animation)

/** 
 @c Begins an animation builder
 
 The parameter to the block is an @c isAnimated parameter, and it should be @c YES
 if the animation should should actually animate.
*/

+ (EHIAnimationBuilder *(^)(BOOL))animate;

@end

@interface EHIAnimationBuilder : NSObject

/** Sets the duration of the future animation */
- (EHIAnimationBuilder *(^)(NSTimeInterval))duration;
/** Sets the delay before the future animation executes */
- (EHIAnimationBuilder *(^)(NSTimeInterval))delay;
/** Delays the animation by dispatching, rather than using @c UIView's built-in animation delay */
- (EHIAnimationBuilder *(^)(NSTimeInterval))wait;
/** Sets spring damping for the future animation; by default the animation doesn't use spring physics */
- (EHIAnimationBuilder *(^)(CGFloat))damping;
/** Sets spring velocity for the future animation; by default the animation doesn't use spring physics */
- (EHIAnimationBuilder *(^)(CGFloat))initialVelocity;
/** Sets the options of the future animation */
- (EHIAnimationBuilder *(^)(UIViewAnimationOptions))options;
/** Combines the options with the exisitng options via bitwise @c or */
- (EHIAnimationBuilder *(^)(UIViewAnimationOptions))option;

/** Adds a block that's run before the animation */
- (EHIAnimationBuilder *(^)(void(^)(void)))before;
/** Sets the animation block of the future animation */
- (EHIAnimationBuilder *(^)(void(^)(void)))transform;

/** Generates another animation to chain onto the end of receiver; block accepts an @c isAnimated parameter */
- (EHIAnimationBuilder *(^)(BOOL))thenAnimate;
/** Chains another animation onto the end of the receiver to run in its completion */
- (EHIAnimationBuilder *(^)(EHIAnimationBuilder *))then;
/** Begins the animation, accepting an optional completion block as a parameter */
- (void(^)(void(^)(BOOL)))start;

@end
