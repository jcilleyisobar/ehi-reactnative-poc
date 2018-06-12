//
//  EHILabel.h
//  Enterprise
//
//  Created by mplace on 2/12/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@interface EHILabel : UILabel

/*
 @brief Adjusts automatic @c preferredMaxLayoutWidth calculation to be relative to
 the @c superview instead of @c self.
 
 Labels that require their bounds to shrink to accommomdate additional content on 
 the left or right side should define the amount of space needed in this property.
 Instead of using the shrunk size, @c preferredMaxLayoutWidth calculations will be
 done relative to the @c superview with the given insets.
 
 Default is @c UIEdgeInsetsZero which disables calculations relative to parent.
 */
@property (assign, nonatomic) UIEdgeInsets insetsForPreferredWidthRelativeToParent;

/** @c YES if the label should have a strikethrough applied */
@property (assign, nonatomic) BOOL appliesStrikethrough;

/** @c YES if the label should opt out of auto shrink */
@property (assign, nonatomic) BOOL disablesAutoShrink;

/** @c YES if the label content should be copyable **/
@property (assign, nonatomic) BOOL copyable;

@end
