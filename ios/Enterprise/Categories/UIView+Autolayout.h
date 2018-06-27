//
//  UIView+Autolayout.h
//  Enterprise
//
//  Created by Ty Cobb on 1/27/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@interface UIView (Autolayout)

/**
 @brief Moves the view to a new superview
 
 Any constraints currently attaching this view to its current superview are ported to
 the new superview to maintain its layout.
 
 @param superview The new superview
*/

- (void)ehi_migrateToSuperview:(UIView *)superview;

/**
 @brief Maps the receiever's constraints to the @c view

 Any constraint on the receiver that also involves it will be recreated with the @c view
 in its place.
*/

- (NSArray *)ehi_migrateConstraintsToView:(UIView *)view;

/**
 Removes contstraints from the callee that invole the @c view

 @param view The view to check for in the constraints list
 @return The removed constraints
*/

- (NSArray *)ehi_removeConstraintsInvolvingView:(UIView *)view;

/**
 Finds the first constraint involving the attribute
 
 @param attribute The attribute on the constraint to locate
 @return A constraint with this attribute, or nil
*/

- (NSLayoutConstraint *)ehi_firstConstraintInvolvingAttribute:(NSLayoutAttribute)attribute;

/**
 Animates any changes in constraints performed before hand
 
 Perform any animatable constraint changes before calling this method
 
 @param duration  The duration time of the animation
 */

- (void)ehi_animateConstraintChangeWithDuration:(float)duration completion:(void (^)())completion;

@end
