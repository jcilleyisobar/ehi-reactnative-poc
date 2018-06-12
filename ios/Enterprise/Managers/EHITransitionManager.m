//
//  EHITransitionManager.m
//  Enterprise
//
//  Created by Alex Koller on 11/30/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHITransitionManager.h"
#import "EHIViewController.h"

@implementation EHITransitionManager

# pragma mark - Transitioning

+ (void)transitionToScreen:(NSString *)screen asModal:(BOOL)modal
{
    [self transitionToScreen:screen object:nil asModal:modal];
}

+ (void)transitionToScreen:(NSString *)screen object:(id)object asModal:(BOOL)modal
{
    void (^completion)(void) = ^{
        // hide the menu
        NAVTransitionBuilder *transition = EHIMainRouter.router.transition.animateWithOptions(EHIScreenMenu, NAVAnimationOptionsHidden).animated(NO);
        
        // present our screen
        if(modal) {
            transition.root(EHIScreenDashboard).animated(NO)
            .present(screen).object(object).start(nil);
        }
        // otherwise, set as root
        else {
            transition.root(screen).object(object).animated(NO).start(nil);
        }
    };

    UIViewController *rootViewController = [UIApplication sharedApplication].keyWindow.rootViewController;

    // remove modals first if needed
    if(rootViewController.presentedViewController) {
        [rootViewController dismissViewControllerAnimated:NO completion:completion];
    } else {
        ehi_call(completion)();
    }
}

@end
