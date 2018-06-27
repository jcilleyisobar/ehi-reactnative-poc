//
//  NAVRouterDelegate.h
//  Enterprise
//
//  Created by Alex Koller on 12/3/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "NAVTransition.h"

@class NAVRouter;

@protocol NAVRouterDelegate <NSObject> @optional

/**
 @brief Notifies the delegate when a transition is about to be run
 
 The transition details are fully configured but components necessary to perform the
 transition (such as it's UIViewController) are not yet prepared.
 
 @param router  The router about to initiate the transition
 @param updates The transition about to be performed
 */

- (void)router:(NAVRouter *)router willPerformTransition:(NAVTransition *)transition;

/**
 @brief Notifies the delegate when a transition has finished
 
 @param router     The router that initiated the transition
 @param transition The transition that was performed
 */

- (void)router:(NAVRouter *)router didPerformTransition:(NAVTransition *)transition;

@end