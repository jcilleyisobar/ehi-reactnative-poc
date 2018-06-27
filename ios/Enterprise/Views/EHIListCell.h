//
//  EHIListCell.h
//  Enterprise
//
//  Created by Ty Cobb on 1/21/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIUpdatable.h"
#import "EHILayoutable.h"
#import "EHISizeable.h"
#import "EHIDisposable.h"
#import "EHIMainRouter.h"

@protocol EHIListCell <EHILayoutable, EHISizeable, EHIUpdatable>

/**
 @brief A string reuse identifier to dequeue cells of this class
 
 The default implemenation returns the stringified class name. Subclassed may override this 
 method to provide custom behavior (but probably shouldn't).
 
 @return A reuse identifier
*/

+ (NSString *)identifier;


/**
 @brief Hook for superclass to act as a factory for subclasses
 
 Provides a cell class the ability to inspect the data that will be bound to the deuqued cell
 and return a custom subclass to dequeue instead. If no subclass is desired, return yourself.
 
 If this method is implemented, and this cell supports dynamic sizing, you @em must also implement
 @c potentialSubclasses.
 
 @param model The model that to be bound to this cell.
 @return A subclass of this superclass, or itself.
*/

+ (Class<EHIListCell>)subclassForModel:(id)model;

/**
 @brief One time hook for setting up any reactive bindings
 
 Subclasses should override this method to bind to properties on its view model.
 
 @param model The view model to update the interface with
*/

- (void)registerReactions:(id)model;

/**
 @brief Called on the list cell when the managing view stops displaying it
 
 Cells can use this hook to terminate any operations that need to be cleaned up before
 @c -prepareForReuse would be called.
*/

- (void)didEndDisplaying;

/**
 @brief A disposable wrapper for a reference instance of this cell
 
 The disposable is a reference counting mechansim. Views that are using cells of this type should
 increment the @c reference's @c referenceCount, and should decrement it when they no longer require
 its services.
*/

+ (EHIDisposable *)reference;

/**
 @brief Updates the reference count of the disposable @c reference
 
 If this class has any @c potentialSubclasses, their reference counts are updated using the same
 @c delta value.
 
 @param delta The value to modify the reference count by
*/

+ (void)modifyReferents:(NSInteger)delta;

@optional

/**
 @brief Returns a list of all potential subclasses
 
 If this method is implemented, @c -subclassForModel should be implemented as well. This method
 is @em not cached, and it should not be used directly or in @c -subclassForModel:.
*/

+ (NSArray *)potentialSubclasses;

/**
 @brief A default @c kind string to use for this view
 
 Subclasses may override this method to return a custom string, and consumers of this
 class can use that in place of their own custom @c kind if desired.
 
 Defaults to @c nil.
*/

+ (NSString *)kind;

@end
