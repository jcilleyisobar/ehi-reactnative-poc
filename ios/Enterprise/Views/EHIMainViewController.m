//
//  EHIMainViewController.m
//  Enterprise
//
//  Created by Ty Cobb on 1/5/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIMainViewController.h"
#import "EHIMenuViewController.h"
#import "EHIWelcomeViewController.h"
#import "EHIReachabilityViewController.h"
#import "EHIMenuAnimation.h"
#import "EHIMenuButton.h"
#import "EHIMenuAnimationProgress.h"
#import "EHINavigationTransition.h"
#import "EHIBarButtonItem.h"
#import "EHIReachability.h"
#import "EHISettings.h"
#import "EHIAnalytics.h"
#import "EHISigninMainViewController.h"
#import "NAVTransitionFactory.h"

#define EHISegueEmbedWelcomeScreenId @"EHISegueEmbedWelcomeController"

@interface EHIMainViewController () <EHIReachabilityListener, NAVRouterDelegate, EHIMenuAnimationProgressListener, EHIMenuActions, EHIWelcomeDelegate, UINavigationControllerDelegate>
/** Returns the nav controller's visible view controller, cast to our custom subclass */
@property (nonatomic, readonly) EHIViewController *visibleViewController;
/** Main navigation controller backing the UI */
@property (strong, nonatomic) UINavigationController *navigationController;
/** Custom transition driving the navigation controller's animations */
@property (strong, nonatomic) EHINavigationTransition *navigationTransition;
/** Animation backing the side menu transition; interactive */
@property (strong, nonatomic) EHIMenuAnimation *menuAnimation;
/** The view controller that drives the drawer menu */
@property (strong, nonatomic) EHIMenuViewController *menuViewController;
/** The view controller shown on first launch */
@property (strong, nonatomic) EHIWelcomeViewController *welcomeViewController;
/** The view controller containing the reachability overlay */
@property (strong, nonatomic) EHIReachabilityViewController *reachabilityViewController;
/** Fixed position button that toggles the drawer menu */
@property (weak, nonatomic) IBOutlet EHIMenuButton *menuButton;
/** Fixed position button that calls for availability */
@property (weak, nonatomic) IBOutlet EHIButton *phoneButton;
/** Container view for the welcome controller */
@property (weak, nonatomic) IBOutlet UIView *welcomeContainer;
/** Container view for the navigation controller */
@property (weak, nonatomic) IBOutlet UIView *navigationContainer;
/** Container view for the drawer menu */
@property (weak, nonatomic) IBOutlet UIView *menuContainer;

@property (assign, nonatomic) BOOL menuButtonTriggered;
@end

@implementation EHIMainViewController

# pragma mark - Segues

- (BOOL)shouldPerformSegueWithIdentifier:(NSString *)identifier sender:(id)sender
{
    // only embed the welcome screen on first run
    if([identifier isEqualToString:EHISegueEmbedWelcomeScreenId]) {
        BOOL shouldEmbed = [EHISettings sharedInstance].isFirstRun;
        if(!shouldEmbed) {
            [self.welcomeContainer removeFromSuperview];
        }
    }
    
    return YES;
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    [super prepareForSegue:segue sender:sender];
    
    if([segue.identifier isEqualToString:@"EHISegueEmbedNavigationController"]) {
        [self didEmbedNavigationController:segue.destinationViewController];
    } else if([segue.identifier isEqualToString:@"EHISegueEmbedMenuViewController"]) {
        [self didEmbedMenuViewController:segue.destinationViewController];
    } else if([segue.identifier isEqualToString:EHISegueEmbedWelcomeScreenId]) {
        [self didEmbedWelcomeViewController:segue.destinationViewController];
    }
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.navigationTransition = [EHINavigationTransition new];
    self.navigationTransition.gestureRecognizer = self.navigationController.interactivePopGestureRecognizer;
    
    // associate router with this navigation stack
    EHIMainRouter.router.delegate = self;
    EHIMainRouter.router.transitionPerformer = self;
    
    // transition to the app's root screen
    if(![EHISettings sharedInstance].isFirstRun) {
        [self setDashboardAsRoot];
    }
    
    // configure the fixed phone button
    self.phoneButton.tintColor = [UIColor whiteColor];
    self.phoneButton.type = EHIButtonTypePhone;
    
    // listen for menu animation progress so we can display it at the correct time
    [[EHIMenuAnimationProgress sharedInstance] addListener:self];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];

    [self navigationController:self.navigationController updateMenuButtonsDuringTransitionAnimated:animated hasFinished:NO];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    
    [self navigationController:self.navigationController updateMenuButtonsDuringTransitionAnimated:animated hasFinished:YES];
}

- (void)viewDidLayoutSubviews
{
    [super viewDidLayoutSubviews];
   
    // add ourselves as a reachability listener; redundant calls are filtered so this
    // only has any effect the first time
    [[EHIReachability sharedInstance] addListener:self];
}

# pragma mark - Interface Actions

- (IBAction)didTapMenuButton:(UIButton *)button
{
    BOOL isVisible = self.menuAnimation.isVisible;

    EHIMainRouter.router.transition
        .animate(EHIScreenMenu, !isVisible).start(nil);

    [self refreshAnalytics:YES];
}

- (IBAction)didTapPhoneButton:(UIButton *)button
{
    EHIMainRouter.router.transition
        .present(EHIScreenCallSupport).start(nil);
}

# pragma mark - Welcome

- (void)didEmbedWelcomeViewController:(EHIWelcomeViewController *)welcomeViewController
{
    self.welcomeViewController = welcomeViewController;
    self.welcomeViewController.delegate = self;
 
    if([EHISettings sharedInstance].isFirstRun) {
        self.welcomeContainer.alpha = 1.0f;
    } else {
        self.welcomeContainer.alpha = 0.0f;
        [self dismissWelcomeContainer];
    }
}

//
// EHIWelcomeDelegate
//

- (void)welcomeViewControllerDidSelectContinue:(EHIWelcomeViewController *)viewController
{
    [self setDashboardAsRoot];
    [self dismissWelcomeContainer];
}

- (void)welcomeViewControllerDidSelectSignin:(EHIWelcomeViewController *)viewController
{
    __weak typeof(self) welf = self;
    EHIMainRouter.router.transition
        .present(EHIScreenMainSignin).handler(^ {
            [welf setDashboardAsRoot];
    }).start(nil);
    
    [self dismissWelcomeContainer];
}

- (void)welcomeViewControllerDidSelectJoin:(EHIWelcomeViewController *)viewController
{
    __weak typeof(self) welf = self;
    EHIMainRouter.router.transition
        .push(EHIScreenEnrollmentStepOne).animated(NO).handler(^ {
            [welf setDashboardAsRoot];
    }).start(nil);
    
    [self dismissWelcomeContainer];
}

- (void)dismissWelcomeContainer
{
    UIView.animate(YES).duration(0.15f).transform(^{
        self.welcomeContainer.alpha = 0.0f;
    }).start(^(BOOL finished) {
        // goodbye welcome view
        
        [self.welcomeViewController removeFromParentViewController];
        self.welcomeViewController = nil;
        
        [self.welcomeContainer removeFromSuperview];
        self.welcomeContainer = nil;
        
        // run analytics for whatever's underneath
        if ([EHISettings sharedInstance].isFirstRun){
            [self invalidateAnalyticsContextForVisibleViewController];
        }
    });
    
}

# pragma mark - Analytics

- (void)refreshAnalytics:(BOOL)fromTap
{
    if(fromTap) {
        NSString *action = self.menuAnimation.isVisible ? EHIAnalyticsMenuActionShow : EHIAnalyticsMenuActionHide;
        [EHIAnalytics trackAction:action handler:nil];
    }
    if(self.menuAnimation.isVisible) {
        [self.menuViewController invalidateAnalyticsContext];
    } else {
        [self invalidateAnalyticsContextForVisibleViewController];
    }
}

- (void)invalidateAnalyticsContextForVisibleViewController
{
    EHIViewController *controller = (id)self.navigationController.topViewController;
    if([controller isKindOfClass:[EHIViewController class]]) {
        [controller invalidateAnalyticsContext];
    }
}

# pragma mark - Menu

- (void)didEmbedMenuViewController:(EHIMenuViewController *)menuViewController
{
    self.menuViewController = menuViewController;
    self.menuAnimation.drawerView = self.menuContainer;
}

- (EHIMenuAnimation *)menuAnimation
{
    if(_menuAnimation) {
        return _menuAnimation;
    }
    
    _menuAnimation = [EHIMenuAnimation new];
    
    // TODO(R): There has to be a better place
    [NAVTransitionFactory registerAnimation:_menuAnimation forScreen:EHIScreenMenu];
    
    return _menuAnimation;
}

# pragma mark - EHIMenuAnimationProgressListener

- (void)menuAnimationDidFinishAnimating:(EHIMenuAnimationProgress *)progress
{
    CGFloat percentComplete = progress.percentComplete == 1.0f;
    self.phoneButton.alpha = self.visibleViewController.showsPhoneButton ? 1.0f : percentComplete;
}

- (void)menuAnimationDidFinishAnimatingUsingGesture
{
    [self refreshAnalytics:NO];
}

# pragma mark - EHIMenuActions

- (void)menuSelectedSameIndex
{
    [self invalidateAnalyticsContextForVisibleViewController];
}

# pragma mark - EHIReachabilityListener

- (void)reachability:(EHIReachability *)reachability didChange:(BOOL)isReachable
{
    if (!self.reachabilityViewController && [EHIConfiguration configuration].isReady) {
        return;
    }

    UIView *overlay = [self insertOverlayIfNecessaryForReachability:reachability];
    
    UIView.animate(!reachability.isReachabilityUnknown).duration(0.25).transform(^{
        overlay.alpha = isReachable ? 0.0f : 1.0f;
        [self setNeedsStatusBarAppearanceUpdate];
    }).start(^(BOOL finished) {
        // if we're reachable now, then throw this away
        if(isReachable) {
            [overlay removeFromSuperview];
            [self setReachabilityViewController:nil];
        }
    });
}

//
// Helpers
//

- (void)setDashboardAsRoot
{
    EHIMainRouter.router.transition
        .root(EHIScreenDashboard).animated(NO).start(nil);
}

- (UIView *)insertOverlayIfNecessaryForReachability:(EHIReachability *)reachability
{
    // if we're not reachable and haven't inserted the overlay, then do so
    if(!self.reachabilityViewController.view && !reachability.isReachable) {
        self.reachabilityViewController = [EHIReachabilityViewController instance];
        
        // add the view to the window
        self.reachabilityViewController.view.alpha = 0.0f;
        
        UIView *superview = [UIApplication.sharedApplication.windows firstObject];
        [superview addSubview:self.reachabilityViewController.view];
    }

    // return the overlay, if we have it
    return self.reachabilityViewController.view;
}

# pragma mark - UINavigationController

- (void)didEmbedNavigationController:(UINavigationController *)controller
{
    self.navigationController = controller;
    self.navigationController.delegate = self;
   
    // give the menu animation the view to translate to show the menu
    self.menuAnimation.contentView = self.navigationContainer;
}

//
// UINavigationControllerDelegate
//

- (void)navigationController:(UINavigationController *)navigationController willShowViewController:(UIViewController *)viewController animated:(BOOL)animated
{
    // update menu buttons at the beginning of the transition
    [self navigationController:navigationController updateMenuButtonsDuringTransitionAnimated:animated hasFinished:NO];
}

- (void)navigationController:(UINavigationController *)navigationController didShowViewController:(UIViewController *)viewController animated:(BOOL)animated
{
    // disable menu animation when we're not at the root level
    self.menuAnimation.isEnabled = navigationController.viewControllers.count == 1;
    // update the menu buttons at the end of the transition
    [self navigationController:navigationController updateMenuButtonsDuringTransitionAnimated:animated hasFinished:YES];
}

- (id<UIViewControllerAnimatedTransitioning>)navigationController:(UINavigationController *)navigationController animationControllerForOperation:(UINavigationControllerOperation)operation
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

# pragma mark - NAVRouterDelegate

- (void)router:(NAVRouter *)router didPerformTransition:(NAVTransition *)transition
{
    BOOL shoulUpdatePhoneButton = [transition.destination isKindOfClass:self.class];

    if(shoulUpdatePhoneButton) {
        [self navigationController:self.navigationController updateMenuButtonsDuringTransitionAnimated:transition.isAnimated hasFinished:YES];
    }
}

//
// Helpers
//

- (BOOL)shouldRunInteractivePopGestureWithRecognizer:(UIGestureRecognizer *)gesture
{
    return !(gesture.state == UIGestureRecognizerStatePossible || gesture.state == UIGestureRecognizerStateFailed);
}

- (void)navigationController:(UINavigationController *)navigationController updateMenuButtonsDuringTransitionAnimated:(BOOL)animated hasFinished:(BOOL)finished
{
    // update the menu button visibility
    BOOL showsMenuButton = navigationController.viewControllers.count <= 1;

    // only run the animation if the finished state matches the button vis, i.e. show when animation finishes,
    // and hide when animation starts
    if(finished == showsMenuButton) {
        UIView.animate(animated).duration(0.25).transform(^{
            self.menuButton.alpha = showsMenuButton ? 1.0f : 0.0f;
            self.phoneButton.alpha = self.visibleViewController.showsPhoneButton ? 1.0f : 0.0f;
        }).start(nil);
    }
    
    // add menu placeholder to root before it appears to avoid title overlap on menu bar button
    if(!finished && showsMenuButton) {
        EHIViewController *newController  = [navigationController.viewControllers firstObject];
        EHIBarButtonItem *menuPlaceholder = [EHIBarButtonItem placeholder:30.0f];
        
        newController.navigationItem.leftBarButtonItem = menuPlaceholder;
    }
}

# pragma mark - Accessors

- (EHIViewController *)visibleViewController
{
    return (EHIViewController *)self.navigationController.visibleViewController;
}

# pragma mark - UIViewController

- (UIStatusBarStyle)preferredStatusBarStyle
{
    return UIStatusBarStyleLightContent;
}

@end
