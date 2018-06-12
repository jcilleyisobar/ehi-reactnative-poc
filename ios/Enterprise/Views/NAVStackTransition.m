//
//  NAVStackTransition.m
//  Enterprise
//
//  Created by Alex Koller on 12/4/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "NAVTransition_Subclass.h"
#import "NAVStackTransition.h"
#import "NAVTransitionFactory.h"

@interface NAVStackTransition ()
/** The navigation controller driving this stack transition */
@property (weak  , nonatomic) UINavigationController *navigationController;
/** The destination of stack transitions is always a view controller */
@property (strong, nonatomic) NAVViewController *destination;
/** Only for @c NAVTransitionTypeReplace. The new stack controllers after replacement */
@property (strong, nonatomic) NSArray *replaceViewControllers;
@end

@implementation NAVStackTransition

# pragma mark - NAVTransition

- (void)prepareWithController:(UIViewController *)controller
{
    self.navigationController = [self navigationControllerFromController:controller];
    
    switch(self.type) {
        case NAVTransitionTypeRoot:
            self.destination = [self prepareRootController]; break;
        case NAVTransitionTypePush:
            self.destination = [self preparePushController]; break;
        case NAVTransitionTypePop:
            self.destination = [self preparePopController]; break;
        case NAVTransitionTypeResolve:
            self.destination = [self prepareResolveController]; break;
        case NAVTransitionTypeReplace:
            self.destination = [self prepareReplaceController]; break;
        default:
            break;
    }
}

- (void)performWithCompletion:(void (^)(void))completion
{
    // call our own completion before returning
    void (^wrappedCompletion)() = ^{
        ehi_call(self.completion)();
        ehi_call(completion)();
    };
    
    switch(self.type) {
        case NAVTransitionTypeRoot:
            [self performRootWithCompletion:wrappedCompletion]; break;
        case NAVTransitionTypePush:
            [self performPushWithCompletion:wrappedCompletion]; break;
        case NAVTransitionTypePop:
            [self performPopWithCompletion:wrappedCompletion]; break;
        case NAVTransitionTypeResolve:
            [self performResolveWithCompletion:wrappedCompletion]; break;
        case NAVTransitionTypeReplace:
            [self performReplaceWithCompletion:wrappedCompletion]; break;
        default:
            NSAssert(false, @"Cannot perform transition of given type");
    }
}

//
// Helpers
//

- (UINavigationController *)navigationControllerFromController:(UIViewController *)controller
{
    // see if our delegate is a nav controller
    if([controller isKindOfClass:UINavigationController.class]) {
        return (UINavigationController *)controller;
    }
    
    // see if we can find a navigation controller from the given controller
    id navigationController = [controller performSelector:@selector(navigationController)];
    if([navigationController isKindOfClass:UINavigationController.class]) {
        return navigationController;
    }
    else {
        NSAssert(false, @"No navigation controller for view controller");
        return nil;
    }
}

# pragma mark - Preparation

- (NAVViewController *)prepareRootController
{
    NAVViewController *currentController = [self.navigationController.viewControllers firstObject];
    
    // don't create new if already exists at root navigation
    if(currentController.class == [NAVTransitionFactory classForTransition:self]) {
        [currentController updateWithAttributes:self.attributes];
        return currentController;
    }
    
    return [NAVTransitionFactory controllerForTransition:self];
}

- (NAVViewController *)preparePushController
{
    return [NAVTransitionFactory controllerForTransition:self];
}

- (NAVViewController *)preparePopController
{
    // find our destination controller
    NSInteger popCount       = [self.data integerValue];
    NSArray *viewControllers = self.navigationController.viewControllers;
    NSInteger index          = viewControllers.count - 1 - popCount;
    
    // pass appropriate attributes
    NAVViewController *controller = viewControllers[MAX(index, 0)];
    [controller updateWithAttributes:self.attributes];
    
    return controller;
}

- (NAVViewController *)prepareResolveController
{
    NAVViewController *controller = self.navigationController.viewControllers.find(^(NAVViewController *controller) {
        return [[controller.class screenName] isEqualToString:self.data];
    });
    
    if(controller) {
        [controller updateWithAttributes:self.attributes];
        return controller;
    }
    
    return [NAVTransitionFactory controllerForTransition:self];
}

- (NAVViewController *)prepareReplaceController
{
    NSArray *transitions = self.data;
    NSArray *controllers = self.navigationController.viewControllers;
    
    // apply transitions
    self.replaceViewControllers = transitions.inject(controllers, ^(NSMutableArray *controllers, NAVTransition *transition) {
        switch(transition.type) {
            case NAVTransitionTypeRoot:
                [controllers removeAllObjects];
            case NAVTransitionTypePush:
                [controllers addObject:[NAVTransitionFactory controllerForTransition:transition]]; break;
            case NAVTransitionTypePop:
                [controllers removeLastObject]; break;
            default:
                break;
        }
        
        return controllers;
    });

    return self.replaceViewControllers.lastObject;
}

# pragma mark - Performance

- (void)performRootWithCompletion:(void (^)(void))completion
{
    [self performTransaction:^{
        [self.navigationController setViewControllers:@[self.destination] animated:self.isAnimated];
    } completion:completion];
}

- (void)performPushWithCompletion:(void (^)(void))completion
{
    [self performTransaction:^{
        [self.navigationController pushViewController:self.destination animated:self.isAnimated];
    } completion:completion];
}

- (void)performPopWithCompletion:(void (^)(void))completion
{
    [self performTransaction:^{
        [self.navigationController popToViewController:self.destination animated:self.isAnimated];
    } completion:completion];
}

- (void)performResolveWithCompletion:(void (^)(void))completion
{
    // if our new top controller is in the stack, pop to it
    if([self.navigationController.viewControllers containsObject:self.destination]) {
        [self performTransaction:^{
            [self.navigationController popToViewController:self.destination animated:self.isAnimated];
        } completion:completion];
    }
    // otherwise, push it
    else {
        [self performTransaction:^{
            [self.navigationController pushViewController:self.destination animated:self.isAnimated];
        } completion:completion];
    }
}

- (void)performReplaceWithCompletion:(void (^)(void))completion
{
    [self performTransaction:^{
        [self.navigationController setViewControllers:self.replaceViewControllers animated:self.isAnimated];
    } completion:completion];
}

//
// Helpers
//

- (void)performTransaction:(void (^)(void))transaction completion:(void (^)(void))completion
{
    // run the transition with a completion
    BOOL completeAsynchronously = self.isAnimated;
    
    [CATransaction begin];
    [CATransaction setCompletionBlock:^{
        // sequential animated navigation controller updates fail unless we give it a frame
        optionally_dispatch_async(completeAsynchronously, dispatch_get_main_queue(), ^{
            ehi_call(completion)();
        });
    }];
    
    transaction();
    
    [CATransaction commit];
}

@end
