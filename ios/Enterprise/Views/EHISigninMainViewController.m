//
//  EHISigninMainViewController.mViewController
//  Enterprise
//
//  Created by Rafael Machado on 8/6/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHISigninMainViewController.h"
#import "EHISigninRouterManager.h"
#import "EHISigninRouter.h"
#import "EHINavigationTransition.h"
#import "EHISigninViewController.h"
#import "EHIUserManager.h"

@interface EHISigninMainViewController () <UINavigationControllerDelegate>
/** Main navigation controller backing the UI */
@property (strong, nonatomic) UINavigationController *navigationController;
/** Custom transition driving the navigation controller's animations */
@property (strong, nonatomic) EHINavigationTransition *navigationTransition;
/** Optional user object from caller */
@property (strong, nonatomic) id userObject;
/** Optional handler object from caller */
@property (copy  , nonatomic) id handler;
@end

@implementation EHISigninMainViewController

- (void)updateWithAttributes:(NAVAttributes *)attributes
{
    self.userObject = attributes.userObject;
    self.handler    = attributes.handler;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.navigationTransition = [EHINavigationTransition new];
    self.navigationTransition.gestureRecognizer = self.navigationController.interactivePopGestureRecognizer;
    
    // become the shared res router's transition performer
    EHISigninRouter.router.transitionPerformer = self;
    
    // setup initial screen
    EHISigninRouter.router.transition.root(EHIScreenSignin).object(self.userObject).handler(self.handler).start(nil);
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    if(self.isBeingPresented) {
        [[EHISigninRouterManager sharedInstance] setIsActive:YES];
    }
}

- (void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];
    
    if(!self.presentedViewController) {
        // leaving the reservation flow, set the builder inactive
        [[EHISigninRouterManager sharedInstance] setIsActive:NO];
        // wipe out any existing enrollment data
        [[EHIUserManager sharedInstance] resetEnrollment];
    }
}

# pragma mark - UINavigationControllerDelegate

- (id<UIViewControllerAnimatedTransitioning>)navigationController:(UINavigationController *)navigationController
                                  animationControllerForOperation:(UINavigationControllerOperation)operation
                                               fromViewController:(UIViewController *)fromVC toViewController:(UIViewController *)toVC
{
    // let's notify the transition which way it should travel
    self.navigationTransition.isPush = operation == UINavigationControllerOperationPush;
    
    return self.navigationTransition;
}

- (id<UIViewControllerInteractiveTransitioning>)navigationController:(UINavigationController *)navigationController
                         interactionControllerForAnimationController:(id<UIViewControllerAnimatedTransitioning>)animationController
{
    if([self shouldRunInteractivePopGestureWithRecognizer:navigationController.interactivePopGestureRecognizer]) {
        return self.navigationTransition;
    }
    
    return nil;
}

//
// Helpers
//

- (BOOL)shouldRunInteractivePopGestureWithRecognizer:(UIGestureRecognizer *)gesture
{
    return !(gesture.state == UIGestureRecognizerStatePossible || gesture.state == UIGestureRecognizerStateFailed);
}

# pragma mark - Segues

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    [super prepareForSegue:segue sender:sender];
    
    if([segue.identifier isEqualToString:@"EHISegueEmbedNavigationController"]) {
        UINavigationController *controller = segue.destinationViewController;
        self.navigationController = controller;
        self.navigationController.delegate = self;
    }
}

- (UIStatusBarStyle)preferredStatusBarStyle
{
    return UIStatusBarStyleLightContent;
}


# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenMainSignin;
}

@end
