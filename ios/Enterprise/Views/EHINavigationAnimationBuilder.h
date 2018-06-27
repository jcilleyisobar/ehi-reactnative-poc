//
//  EHINavigationAnimationBuilder.h
//  Enterprise
//
//  Created by Ty Cobb on 2/12/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@class EHINavigationAnimation;

@interface EHINavigationAnimationBuilder : NSObject

/** 
 @brief Initializes a new animation builder for the view
 
 @param target      The view to run the animations on
 @param shouldProxy @c YES if the view should be inserted into the container hierarchy during animation
*/

- (instancetype)initWithTarget:(UIView *)target shouldProxy:(BOOL)shouldProxy;

/** Generates an @c EHINavigationAnimation from the current builder state */
- (EHINavigationAnimation *)build;

/** 
 @brief Sets the duration for this animation independent of the overall transition 
 
 A duration is a required element of the animation, so any custom animations should set this value.
*/
- (EHINavigationAnimationBuilder *(^)(NSTimeInterval))duration;
/** Delays the animation by a fixed amount during the transition */
- (EHINavigationAnimationBuilder *(^)(NSTimeInterval))delay;
/** Options to apply to the animation */
- (EHINavigationAnimationBuilder *(^)(UIViewAnimationOptions))options;
/** Forces the animation target to layout during animation */
- (EHINavigationAnimationBuilder *(^)(BOOL))forcesLayout;
/** Indicates whether the aniamtion should be applied in the reverse order (completing delay) on unwind */
- (EHINavigationAnimationBuilder *(^)(BOOL))reversesOrderOnUnwind;

/**
 @brief Animates the view's transform using the vector range

 The translation is applied to the view's layer, and the transform is set to apply
 perspective by default.
*/
- (EHINavigationAnimationBuilder *(^)(EHIFloatRangeVector))translationRange;
/** Pass-through to @c translationRange with the location for each range set to @c 0 */
- (EHINavigationAnimationBuilder *(^)(EHIFloatVector))translation;
/** Pass-through to @c translationRange with the location for each range set to @c 0, and then reversed */
- (EHINavigationAnimationBuilder *(^)(EHIFloatVector))reverseTranslation;

/**
 @brief Animates the view's alpha

 The alpha range is applied to the view according to the animation percent.
*/
- (EHINavigationAnimationBuilder *(^)(EHIFloatRange))alphaRange;
/** Pass-through to @c alphaRange with the location for the range set to @c 0 */
- (EHINavigationAnimationBuilder *(^)(CGFloat))alpha;
/** Pass-through to @c alphaRange with the locaiton for the range set to @c 0, and then reversed */
- (EHINavigationAnimationBuilder *(^)(CGFloat))reverseAlpha;

/**
 *brief Animates the view's background color
 
 Interpolates between the RGBA values using @c UIColor's getRed:green:blue:alpha:
*/
- (EHINavigationAnimationBuilder *(^)(UIColor *fromColor, UIColor *toColor))colorRange;
/** Pass-through to @c colorRange with @c fromColor set to white */
- (EHINavigationAnimationBuilder *(^)(UIColor *color))color;

/**
 @brief Animates the view to a new frame

 The frame itself may not necessarily animated, but various transforms may be applied to
 the view to achieve the desired effect.
*/
- (EHINavigationAnimationBuilder *(^)(CGRect))frame;
/** Pass-through to @c frame, reversing all the created animation ranges */
- (EHINavigationAnimationBuilder *(^)(CGRect))reverseFrame;

/**
 @brief Adds an arbitrary block to be called during the animation

 The block is bassed the @c target
*/
- (EHINavigationAnimationBuilder *(^)(void(^)(id, CGFloat)))block;

@end

@interface EHINavigationAnimationBuilder (Special)

/**
 @brief Generates a navigation animation that isn't properly reversible
 
 The navigation bar doesn't behave properly on reverse, so we're going to create a custom animation
 for it that ignores the navigation transition's standard behavior.
 
 This animation respects @c delay, but ignores other properties.
*/

- (EHINavigationAnimationBuilder *)autoreversingNavigationBar;

@end
