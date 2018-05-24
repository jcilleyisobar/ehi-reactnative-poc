//
//  NAVAnimationTransition.m
//  Enterprise
//
//  Created by Alex Koller on 12/4/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "NAVTransition_Subclass.h"
#import "NAVAnimationTransition.h"
#import "NAVTransitionFactory.h"

@interface NAVAnimationTransition ()
@property (weak  , nonatomic) NAVViewController *presentingViewController;
@property (assign, nonatomic, readonly) BOOL isVisible;
@property (assign, nonatomic, readonly) BOOL isModal;
@end

@implementation NAVAnimationTransition

# pragma mark - NAVTransition

- (void)prepareWithController:(NAVViewController *)controller
{
    // find controller managing transition and our destination controller
    if(self.isModal) {
        self.presentingViewController = [self presentingViewControllerForViewController:controller];
        
        if(self.isVisible) {
            self.destination = [self preparePresentedController];
        } else {
            self.destination = [self preparePoppingController];
        }
    }
    // find our destination animation
    else {
        self.destination = [self prepareAnimation];
    }
}

- (void)performWithCompletion:(void (^)(void))completion
{
    NSAssert(self.type == NAVTransitionTypeAnimation, @"Cannot perform transntion of given type");
    
    // call our own completion before returning
    void (^wrappedCompletion)() = ^{
        ehi_call(self.completion)();
        ehi_call(completion)();
    };
    
    // run animation associated with this transition
    if(self.isModal) {
        [self performModaTransitionWithCompletion:wrappedCompletion];
    } else {
        [self performAnimationWithCompletion:wrappedCompletion];
    }

}

//
// Helpers
//

- (NAVViewController *)presentingViewControllerForViewController:(UIViewController *)controller
{
    // get top modal
    while(controller.presentedViewController) {
        controller = controller.presentedViewController;
    }
    
    // when hiding, controller presenting top most controller controls transition
    if(!self.isVisible) {
        controller = controller.presentingViewController;
    }
    
    return (NAVViewController *)controller;
}

- (void)performModaTransitionWithCompletion:(void (^)(void))completion
{
    if(self.isVisible) {
        [self performPresentWithCompletion:completion];
    } else {
        [self performDismissWithCompletion:completion];
    }
}

# pragma mark - Preparation

- (id<NAVTransitionDestination>)preparePresentedController
{
    return [NAVTransitionFactory controllerForTransition:self];
}

- (id<NAVTransitionDestination>)preparePoppingController
{
    // presenting controller will be new visible controller after dismiss
    [self.presentingViewController updateWithAttributes:self.attributes];
    
    return self.presentingViewController;
}

- (id<NAVTransitionDestination>)prepareAnimation
{
    NAVAnimation *animation = [NAVTransitionFactory animationForTransition:self];
    [animation updateWithAttributes:self.attributes];
    
    return animation;
}

# pragma mark - Performance

- (void)performPresentWithCompletion:(void (^)(void))completion
{
    // our destination should be a new view controller
    NAVViewController *controller = (NAVViewController *)self.destination;
    
    [self.presentingViewController presentViewController:controller animated:self.isAnimated completion:completion];
}

- (void)performDismissWithCompletion:(void (^)(void))completion
{
    [self.presentingViewController dismissViewControllerAnimated:self.isAnimated completion:completion];
}

- (void)performAnimationWithCompletion:(void (^)(void))completion
{
    // our destination is an animation
    NAVAnimation *animation = self.destination;
    
    [animation setIsVisible:self.isVisible animated:self.isAnimated completion:completion];
}

# pragma mark - Accessors

- (BOOL)isVisible
{
    return self.options & NAVAnimationOptionsVisible;
}

- (BOOL)isAsync
{
    return self.options & NAVAnimationOptionsAsync;
}

- (BOOL)isModal
{
    return self.options & NAVAnimationOptionsModal;
}

@end
