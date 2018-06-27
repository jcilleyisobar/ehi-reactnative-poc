//
//  EHIBinder.h
//  Enterprise
//
//  Created by Ty Cobb on 1/28/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIBinderDestination.h"

#define depend(_code) (void)_code
#define source(_path) @key(_path)
#define dest(_callee, _path) [[EHIBinderDestination alloc] initWithTarget:_callee keypath:@keypath( _callee _path )]

@interface EHIBinder : NSObject

/**
 @brief Constructs a binder for the given source object

 The binder is used to add bindings (in the form of computations) between a value
 on the source object and some destination.
 
 @param source The source object to pull data from
 
 @return A new binder for this source
*/

- (instancetype)initWithSource:(id)source;

/**
 @brief Creates bindings from a map of source -> destination
 See @c -pair for more thorough documentation creating bindings
*/

- (EHIBinder *(^)(NSDictionary *))map;

/**
 @brief Creates a binding from the a source, destination pair
 
 Whenever the source value changes, the destination will be set accordingly.
 
 @b Sources are values on the view model and should be created via the @c source macro.
 However, they're just string keys and are used to access the value on the binder's
 source object.
 
 @b Destinations may have two forms:
 
 1. If created via the @c destination macro, in which case they are arrays containing 
 the destination object and the key to set on the destination.
 
 2. Otherwise, a block that receives the source value as a parmaeter any time it changes
*/

- (EHIBinder *(^)(NSString *source, id destination))pair;

@end
