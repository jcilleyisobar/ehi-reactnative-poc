//
//  EHINavigationAnimatable.h
//  Enterprise
//
//  Created by Ty Cobb on 2/12/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@protocol EHINavigationAnimatable <NSObject>

/**
 @brief Called just before animation happens
 
 If the animation needs to add custom views into the hierarchy, they should be added here
 to the parameterzied container.
 
 @param container The containing view for this animation
 */

- (void)prepareToAnimateWithContainer:(UIView *)container;

/**
 @brief Updates the animation with the percent complete
 
 The animation should be able to respond to any value between 0.0-1.0. This method
 may be called multiple times in the animation lifecycle.
 
 @param percentComplete A value in the range 0.0-1.0
*/

- (void)setPercentComplete:(CGFloat)percentComplete;

/**
 @brief Called when this animation completes
 
 Implementers should clean up any necessary custom views, and restore the view to
 the desired end state here if necessary.
*/

- (void)didFinishAnimating;

/**
 @brief Called when the all animations in the transition complete
 
 Implementers can do any further cleanup that needs to happen when the transition is over,
 that couldn't happen when the animation in isolation completed.
*/

- (void)transitionDidComplete;

@end
