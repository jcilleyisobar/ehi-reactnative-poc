//
//  EHIReservationViewController.m
//  Enterprise
//
//  Created by mplace on 3/10/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservationViewController.h"
#import "EHIReservationRouter.h"
#import "EHIReservationBuilder.h"
#import "EHINavigationTransition.h"
#import "EHIConfirmationViewModel.h"

@interface EHIReservationViewController () <UINavigationControllerDelegate>
/** Main navigation controller backing the UI */
@property (strong, nonatomic) UINavigationController *navigationController;
/** Custom transition driving the navigation controller's animations */
@property (strong, nonatomic) EHINavigationTransition *navigationTransition;
/** Optional user object to modify landing screen in res flow */
@property (strong, nonatomic) id confirmationModel;
@end

@implementation EHIReservationViewController

- (void)updateWithAttributes:(NAVAttributes *)attributes
{
    self.confirmationModel = attributes.userObject;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
   
    self.navigationTransition = [EHINavigationTransition new];
    self.navigationTransition.gestureRecognizer = self.navigationController.interactivePopGestureRecognizer;
   
    // become the shared res router's transition performer
    EHIReservationRouter.router.transitionPerformer = self;
    
    // begin on itinerary or confirmation
    NSString *screen = self.confirmationModel ? EHIScreenConfirmation : EHIScreenReservationItinerary;
    id object        = self.confirmationModel ? [[EHIConfirmationViewModel alloc] initWithModel:self.confirmationModel] : nil;

    // setup initial screen
    EHIReservationRouter.router.transition
        .root(screen).object(object).start(nil);
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    if(self.isBeingPresented) {
        [[EHIReservationBuilder sharedInstance] setIsActive:YES];
    }
}

- (void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];
    
    if(!self.presentedViewController) {
        // leaving the reservation flow, set the builder inactive
        [[EHIReservationBuilder sharedInstance] setIsActive:NO];
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
    return EHIScreenReservation;
}

@end
