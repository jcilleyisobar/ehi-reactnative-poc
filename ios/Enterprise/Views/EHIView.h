//
//  EHIView.h
//  Enterprise
//
//  Created by Michael Place on 3/10/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIUpdatable.h"
#import "EHILayoutable.h"

@interface EHIView : UIView <EHIUpdatable, EHILayoutable>

/**
 @brief The view model backing this view
 Subclasses should redeclare this property in their class extension to provide their custom subtype.
*/

@property (strong, nonatomic) EHIViewModel *viewModel;

/**
 @brief Setup all the accessibility identifiers
 Subclasses should properly fill the identifiers for itself and subviews, if necessary
 */

- (void)registerAccessibilityIdentifiers;

/**
 @brief One time hook for setting up any reactive bindings
 Subclasses should override this method to bind to properties on its view model.
 @param model The view model to update the interface with
*/

- (void)registerReactions:(id)model;

/**
 @brief Dynamically sizes the view and updates it with the model
 
 The model is bound to the view through normal @c EHIUpdatable data flow, and then
 it is dynamically sized based on the container.

 @param size  The size of the container for this view
 @param model The model to bind to the view
*/

- (void)resizeDynamicallyForContainer:(CGSize)size model:(id)model;

/**
 Calls @c -layoutIfNeeded inside an animation block, to flush any buffered layout changes
 */
- (void)forceLayout;

/**
 @brief @c YES if this view can be automatically replaced from a nib
 
 If @c YES, placeholders for views of this class may be added to other interface builder files,
 and during @c -awakeAfterUsingCoder: an instance from the nib will be unarchived and
 substituted in its place.
 
 Defaults to @c NO.
*/

+ (BOOL)isReplaceable;

@end
