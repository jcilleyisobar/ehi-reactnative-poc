//
//  EHIRestorableConstraint.h
//  EHIius
//
//  Created by Ty Cobb on 7/31/14.
//  Copyright (c) 2014 Isobar. All rights reserved.
//

#define EHIRestorableConstant (MAXFLOAT)

@interface EHIRestorableConstraint : NSLayoutConstraint

/**
 @brief The constraint's initial value.
 
 It's generally safer to not rely on using this in calculations (use EHIRestorableConstant to 
 reset to this value, or offsetConstant: instead).
 
 @return The initial value of the constraint
*/

@property (assign, nonatomic, readonly) CGFloat restorableValue;

/**
 @brief Whether the constraint is enabled or not
 
 If @c NO, the constraint's value is set to 0. If @c YES, the constraint is restored to its 
 restorable value.
*/

@property (assign, nonatomic) BOOL isDisabled;

/**
 @brief Updates the constraint's constant value.
 
 Functions identically to the NSLayoutConstraint's setConstant: implementation, except that if the
 value passed is EHIRestorableConstant, the contraints original value will be applied.
 
 @param constant The constant value to set, or EHIRestorableConstant
*/

- (void)setConstant:(CGFloat)constant;


/**
 @brief Updates the constant with a relative offset.
 
 Updates are made relative to the contraints @a initial value, not its current value.
 
 @param constant The offset to adjust the initial value by
*/

- (void)setOffset:(CGFloat)constant;

@end
