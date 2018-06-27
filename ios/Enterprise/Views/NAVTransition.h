//
//  NAVTransition.h
//  Enterprise
//
//  Created by Alex Koller on 11/16/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "NAVAttributes.h"

@protocol NAVTransitionDestination;

typedef NS_ENUM(NSUInteger, NAVTransitionType) {
    NAVTransitionTypeUnknown,
    NAVTransitionTypeRoot,
    NAVTransitionTypePush,
    NAVTransitionTypePop,
    NAVTransitionTypeResolve,
    NAVTransitionTypeReplace,
    NAVTransitionTypeAnimation,
};

@interface NAVTransition : NSObject
/** The type describing how this transition executes */
@property (assign, nonatomic) NAVTransitionType type;
/** Arbitrary data used to further describe transition (pop count, screen, etc.) */
@property (strong, nonatomic) id data;
/** @c YES if this transition should be animated */
@property (assign, nonatomic) BOOL isAnimated;
/** Attributes to be passed along to the destination of this transition */
@property (strong, nonatomic) NAVAttributes *attributes;
/** Block to be called when this transition completes */
@property (strong, nonatomic) void (^completion)(void);
/** @c YES if this is a root, push, pop, resolve, or replace transition */
@property (assign, nonatomic, readonly) BOOL isStack;
/** @c YES if this transition runs asynchronously */
@property (assign, nonatomic, readonly) BOOL isAsync;
/** The visible end result of this transition. @c nil before @c -prepareWithController: */
@property (strong, nonatomic, readonly) id<NAVTransitionDestination> destination;

+ (instancetype)transitionWithType:(NAVTransitionType)type;

@end

@interface NAVTransition (Lifecycle)

/**
 @brief Make all necessary preparations to execute transition
 
 The transition uses the given @c controller to setup all internal varibles
 to eventually perform the transition. If applicable, destination view
 controllers are created and the topmost controller is exposed via the
 @c viewController property.
 
 @param controller The controller with which to prepare this reservation
 */

- (void)prepareWithController:(UIViewController *)controller;

/**
 @brief Execute this transition
 
 @b Must be called after @c -prepareWithController:. The transition
 performs it's associated UI updates using the controller passed to
 in during preparation. The transition calls it's own @c completion
 before finally calling the @c completion block passed in here.
 
 @param completion Block to call after transition completes
 */

- (void)performWithCompletion:(void (^)(void))completion;

@end