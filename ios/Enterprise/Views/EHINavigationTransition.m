//
//  EHINavigationTransition.m
//  Enterprise
//
//  Created by Ty Cobb on 1/28/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHINavigationTransition.h"
#import "EHIViewController.h"

@interface EHINavigationTransition () <UIGestureRecognizerDelegate>
@property (strong, nonatomic) NSArray *animations;
@property (assign, nonatomic) CGFloat translationOffset;
@property (assign, nonatomic) CGFloat lastPercentComplete;
@property (assign, nonatomic) CGFloat lastPercentDelta;
/** We need our own flag the because the transition context isn't setup properly soon enough */
@property (assign, nonatomic) BOOL isInteractive;
@end

@implementation EHINavigationTransition

- (id)init
{
    if(self = [super init]) {
        self.completionSpeed = 0.999f; // see: http://stackoverflow.com/questions/19626374/ios-7-custom-transition-glitch
    }
    
    return self;
}

# pragma mark - Gesture

- (void)setGestureRecognizer:(UIGestureRecognizer *)gestureRecognizer
{
    [gestureRecognizer setDelegate:self];
    [gestureRecognizer addTarget:self action:@selector(handleNavigationTransition:)];
}

- (void)handleNavigationTransition:(UIGestureRecognizer *)gesture
{
    switch(gesture.state) {
        case UIGestureRecognizerStateBegan:
            [self gestureDidStart:gesture]; break;
        case UIGestureRecognizerStateChanged:
            [self gestureDidContinue:gesture]; break;
        case UIGestureRecognizerStateEnded:
            [self gestureDidFinish:gesture cancelled:NO]; break;
        default: // UIGestureRecognizerStateCancelled should be the only option
            [self gestureDidFinish:gesture cancelled:YES]; break;
    }
}

//
// Gesture States
//

- (void)gestureDidStart:(UIGestureRecognizer *)gesture
{
    self.lastPercentComplete = 0.0f;
    self.translationOffset   = [gesture locationInView:gesture.view].x;
}

- (void)gestureDidContinue:(UIGestureRecognizer *)gesture
{
    CGFloat percentComplete = [self percentCompleteForGestureRecognizer:gesture];
    CGFloat percentDelta    = percentComplete - self.lastPercentComplete;
    
    // we want to accumulate our percent shift in a single direction, to get an idea of which way the user was last swiping
    if(percentDelta * self.lastPercentDelta < 0) { // so, if we've switched direction
        self.lastPercentDelta = 0.0f;              // then reset the delta
    }
    
    self.lastPercentDelta += percentDelta;
    self.lastPercentComplete = percentComplete;
    
    [self updateInteractiveTransition:percentComplete];
}

- (void)gestureDidFinish:(UIGestureRecognizer *)gesture cancelled:(BOOL)cancelled
{
    if(!cancelled) {
        cancelled = [self determineTransitionCancelationFromGesture:gesture];
    }
    
    if(cancelled) {
        [self cancelInteractiveTransition];
    } else {
        [self finishInteractiveTransition];
    }
   
    // we should no longer be interactive (if we were)
    self.isInteractive = NO;
}

//
// Helpers
//

- (CGFloat)percentCompleteForGestureRecognizer:(UIGestureRecognizer *)gestureRecognizer
{
    CGFloat translation = [gestureRecognizer locationInView:gestureRecognizer.view].x - self.translationOffset;
    return translation / gestureRecognizer.view.bounds.size.width;
}

- (CGFloat)determineTransitionCancelationFromGesture:(UIGestureRecognizer *)gestureRecognizer
{
    // if the user has been panning in one direction for this percentage of the screen, we'll consider that to mean they
    // intend to navigate in that direction
    const CGFloat motivatedPanningThreshold = 0.15f;
    if(fabs(self.lastPercentDelta) > motivatedPanningThreshold) {
        return self.lastPercentDelta < 0.0f; // if they've been going backwards, they're trying to cancel
    }
    
    // otherwise, we're just going to guess based on the pan's current position
    CGFloat percentComplete = [self percentCompleteForGestureRecognizer:gestureRecognizer];
    return percentComplete < 0.5f;
}

//
// UIGestureRecognizerDelegate
//

- (BOOL)gestureRecognizerShouldBegin:(UIGestureRecognizer *)gestureRecognizer
{
    // YES for now, but allow for customizability if necessary
    BOOL result = YES;
   
    // we're interactive if the gesture is going to fire
    self.isInteractive = result;
    return result;
}

# pragma mark - Transition

- (NSTimeInterval)transitionDuration:(id<UIViewControllerContextTransitioning>)context
{
    // load up the animations before we return duration the first time
    if(!self.animations) {
        [self prepareToAnimateWithContext:context];
    }
    
    NSTimeInterval duration = 0.0f;
    for(EHINavigationAnimation *animation in self.animations) {
        duration = MAX(duration, animation.delay + animation.duration);
    }
    
    return duration;
}

- (void)prepareToAnimateWithContext:(id<UIViewControllerContextTransitioning>)context
{
    // add the destination view into the hierarchy; animations may rely on views being
    // in the window
    EHIViewController *destination = (id)[context viewControllerForKey:UITransitionContextToViewControllerKey];
    if(self.isPush) {
        [context.containerView addSubview:destination.view];
    } else {
        [context.containerView insertSubview:destination.view atIndex:0];
    }
   
    // load the animations
    self.animations = [self animationsFromContext:context];
}

- (void)animateTransition:(id<UIViewControllerContextTransitioning>)context
{
    // ensure we run animations in a white canvas
    context.containerView.backgroundColor = UIColor.whiteColor;
    
    // allow all the animations to set pre-animated state
    for(EHINavigationAnimation *animation in self.animations) {
        [animation prepareToAnimateWithContainer:context.containerView];
        [animation setPercentComplete:0.0f];
    }
   
    // capture the overall transition duration
    CGFloat duration = [self transitionDuration:context];
  
    // execute each animation in turn
    self.animations.sortBy(^(EHINavigationAnimation *animation) {
        return @([animation terminalTimeForTotalDuration:duration]);
    }).each(^(EHINavigationAnimation *animation, NSInteger index) {
        // determine if this is the last animation
        BOOL isLastAnimation = index == self.animations.count - 1;
        // calculate the actual delay for this animation
        CGFloat delay = [animation delayForTotalDuration:duration];
        
        UIView.animate(YES)
            .delay(delay).duration(animation.duration)
            .options(animation.options).transform(^{
                [animation setPercentComplete:1.0f];
            }).start(^(BOOL finished) {
                [animation didFinishAnimating];
                if(isLastAnimation) {
                    [self completeTransitionWithContext:context finished:finished];
                }
            });    
    });
}

- (void)completeTransitionWithContext:(id<UIViewControllerContextTransitioning>)context finished:(BOOL)finished
{
    // let the animations do any final cleanup
    for(EHINavigationAnimation *animation in self.animations) {
        [animation transitionDidComplete];
    }
    
    // throw away our animations
    self.animations = nil;
   
    // complete the context
    finished &= ![context transitionWasCancelled];
    [context completeTransition:finished];
}

# pragma mark - Animations

- (NSArray *)animationsFromContext:(id<UIViewControllerContextTransitioning>)context
{
    // get the animations for both sides of the transition
    NSArray *sourceAnimations      = [self animationsFromContext:context isEntering:NO];
    NSArray *destinationAnimations = [self animationsFromContext:context isEntering:YES];
    
    // merge & sort the animations ascending completion time
    NSArray *animations = sourceAnimations
        .concat(destinationAnimations)
        .sortBy(^(EHINavigationAnimation *animation) {
            return @(animation.delay + animation.duration);
        });
    
    for(EHINavigationAnimation *animation in animations) {
        animation.isPush = self.isPush;
    }
    
    return animations;
}

- (NSArray *)animationsFromContext:(id<UIViewControllerContextTransitioning>)context isEntering:(BOOL)isEntering
{
    // determine the source / dest key based on the whether this side of the animation is appearing / disappearing
    NSString *sourceKey      = isEntering ? UITransitionContextToViewControllerKey   : UITransitionContextFromViewControllerKey;
    NSString *destinationKey = isEntering ? UITransitionContextFromViewControllerKey : UITransitionContextToViewControllerKey;
    
    // grab the view controllers from the context
    EHIViewController *source      = (id)[context viewControllerForKey:sourceKey];
    EHIViewController *destination = (id)[context viewControllerForKey:destinationKey];
   
    // if this transition uses custom animations (and is not interactive), return those
    BOOL usesCustomAnimations = !self.isInteractive && [source executesCustomAnimationsForTransitionToViewController:destination isEntering:isEntering];
   
    // pull the correct set of animations and configure them correctly
    NSArray *animations = usesCustomAnimations
        ? [self customAnimationsFromController:source toController:destination isEntering:isEntering]
        : [self defaultAnimationsFromController:source toController:destination isEntering:isEntering];
    
    for(EHINavigationAnimation *animation in animations) {
        animation.isEntering = isEntering;
    }
    
    return animations;
}

- (NSArray *)customAnimationsFromController:(EHIViewController *)source toController:(EHIViewController *)destination isEntering:(BOOL)isEntering
{
    NSArray *animations = [source animationsForTransitionToViewController:destination isEntering:isEntering];
    
    // convert the builders into animations
    animations = animations.map(^(EHINavigationAnimationBuilder *builder) {
        return builder.build;
    });
    
    return animations;
}

- (NSArray *)defaultAnimationsFromController:(EHIViewController *)source toController:(EHIViewController *)destination isEntering:(BOOL)isEntering
{
    // otherwise, return the default transition
    // we want to translate the bottomost view in the transition by a fraction of total width
    BOOL isTopmostView    = isEntering == self.isPush;
    CGFloat parallaxScale = isTopmostView ? 1.0f : -0.4f;
    
    // update the initial state of these views
    source.view.layer.ehi_showsShadow = isTopmostView;
    
    // create an animation that translates the view by some potentially parallaxed amount
    EHINavigationAnimation *animation = EHINavigationAnimation.target(source.view)
        .reverseTranslation((EHIFloatVector){ .x = parallaxScale * source.view.bounds.size.width })
        .duration(0.4).build;
    
    return @[ animation ];
}

@end
