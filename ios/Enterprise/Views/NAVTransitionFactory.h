//
//  NAVTransitionControllerFactory.h
//  Enterprise
//
//  Created by Alex Koller on 11/16/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "NAVViewController.h"
#import "NAVTransition.h"
#import "NAVAnimation.h"

@interface NAVTransitionFactory : NSObject

/**
 @brief Constructs a view controller for a given route
 
 The parameterized route will contain the view controller's class as its destination,
 which the factory should use to instantiate the view controller.
 
 The router's behavior is undefined if this method returns nil.
 
 @param route The route for this view controller
 @return A UIViewController instance
 */

+ (NAVViewController *)controllerForTransition:(NAVTransition *)transition;

/**
 @brief Constructs a view controller for a given route
 
 The parameterized route will contain the view controller's class as its destination,
 which the factory should use to instantiate the view controller.
 
 The router's behavior is undefined if this method returns nil.
 
 @param route The route for this view controller
 @return A UIViewController instance
 */

+ (Class<NAVViewController>)classForTransition:(NAVTransition *)transition;

/**
 @brief Locates the animation for a given route
 
 By default, the route will contain the animation as its destination, so the implementer
 may just return that animation. This provides the implementer the ability to return an
 animation dynamically for the route.
 
 @param route The route for the animation
 @return A NAVAnimation instance
 */

+ (NAVAnimation *)animationForTransition:(NAVTransition *)transition;

+ (void)registerAnimation:(NAVAnimation *)animation forScreen:(NSString *)screen;

@end
