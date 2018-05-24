//
//  EHIBinderDestination.h
//  Enterprise
//
//  Created by Ty Cobb on 3/26/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

typedef id(^EHIBinderDestinationTransform)(id);

@interface EHIBinderDestination : NSObject

/**
 Intializes a new destination for the given target/keypath pair.
 
 @param target  The object to set the value on when the binding's source changes
 @param keypath The path set on the target when the binding's source changes
*/

- (instancetype)initWithTarget:(id)target keypath:(NSString *)keypath;

/** Applies any transforms to the value and then sets it on the target */
- (void)updateWithValue:(id)value;

/** Adds a transform to apply to the source value before it's set on the @c target. Only one transform is allowed. */
- (EHIBinderDestination *(^)(EHIBinderDestinationTransform))transform;

@end
