//
//  NAVTransitionControllerActions.h
//  Enterprise
//
//  Created by Alex Koller on 11/16/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "NAVTransition.h"

@protocol NAVTransitionPerformer

/**
 @brief Passthrough to @c -transition:completion:
 
 Should be implemented as a passthrough
 */

- (void)transition:(NAVTransition *)transition;

/**
 @brief Performs the requested transition
 
 @c NAVViewController's implementation runs the correct navigation 
 updates for the given transition. Classes other than @c NAVViewController
 or it's subclasses can use this protocol to mark itself as capable of consuming
 transitions.
 */

- (void)transition:(NAVTransition *)transition completion:(void (^)(void))completion;

@end