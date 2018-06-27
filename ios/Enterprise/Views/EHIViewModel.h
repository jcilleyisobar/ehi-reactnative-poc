//
//  EHIViewModel.h
//  Enterprise
//
//  Created by Ty Cobb on 1/9/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "Reactor.h"
#import "EHIBinder.h"
#import "EHIAnalyticsUpdater.h"

@interface EHIViewModel : NSObject <MTRReactive>

/** @c YES if the view model is currently "visible" */
@property (assign, nonatomic) BOOL isActive;

/** A stored token from the last time that @c -takeOwnership was called; may be @c nil */
@property (strong, nonatomic) id ownershipToken;

/**
 @brief Designated initializer
 
 Subclasses should implement this method to perform initialization. Calling @c -init
 is a pass-through to this method with @c model as @c nil.
 
 @param model The data model to initialize with
 
 @return A new view model
*/

- (instancetype)initWithModel:(id)model;

/**
 @brief Handles model updates
 Subclasses should implement this to update an existing view model with new data.
 @param model The data model to update with
 */

- (void)updateWithModel:(id)model;

/**
 @brief Handles back navigation
 Default implementation pops one view controller off the stack
*/

- (void)navigateBack;

/**
 @brief Cleans up the view model's dependencies and generates a new ownership token

 The token is stored on the @c ownershipToken proprety. Views may take ownership of the
 view model when they receive it and store the token to verify that they still own it
 at a later point.
 
 This is useful when working with recycled views that might take ownership of a view
 model before its previous owner could be made aware.
 
 This method sets @c isActive to @c NO, and the caller is responsible for updating it
 appropriately.
 
 @return A new ownership token.
*/

- (id)takeOwnership;

@end

@interface EHIViewModel (Bindings)

/**
 @brief Constructs a new binder for this view model
 
 The caller can use the binder add reactive links between a value on the view model
 and one of its output sources.
 
 @return A new binder for this view model
*/

- (EHIBinder *)bind;

@end

@interface EHIViewModel (Analytics) <EHIAnalyticsUpdater>

/**
 @brief Invalidates the current analytics context
 
 Forces the context to be repopulated by calling @c -updateAnalyticsContext: using current
 analytics context.
*/

- (void)invalidateAnalyticsContext;

@end
