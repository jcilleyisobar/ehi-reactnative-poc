//
//  EHIModalOverlayTransitioningDelegate.m
//  Enterprise
//
//  Created by Alex Koller on 4/1/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModalOverlayTransitioningDelegate.h"

#define EHITransitionDuration 0.5
#define EHIFullscreenMultiplier 0.9f

@interface EHIModalOverlayTransitioningDelegate () <UIViewControllerAnimatedTransitioning>
@property (assign, nonatomic) BOOL isPresenting;
@property (strong, nonatomic) UIView *overlayView;
@property (weak  , nonatomic) UIViewController *presentingViewController;
@end

@implementation EHIModalOverlayTransitioningDelegate

# pragma mark - UIViewControllerTransitioningDelegate

- (id<UIViewControllerAnimatedTransitioning>)animationControllerForPresentedController:(UIViewController *)presented presentingController:(UIViewController *)presenting sourceController:(UIViewController *)source
{
    self.presentingViewController = presenting;
    self.isPresenting = YES;
    return self;
}

- (id<UIViewControllerAnimatedTransitioning>)animationControllerForDismissedController:(UIViewController *)dismissed
{
    self.isPresenting = NO;
    return self;
}

# pragma mark - UIViewControllerAnimatedTransitioning

- (NSTimeInterval)transitionDuration:(id<UIViewControllerContextTransitioning>)transitionContext
{
    return EHITransitionDuration;
}

- (void)animateTransition:(id<UIViewControllerContextTransitioning>)transitionContext
{
    UIViewController *sourceViewController = [transitionContext viewControllerForKey:UITransitionContextFromViewControllerKey];
    UIViewController *destinationViewController = [transitionContext viewControllerForKey:UITransitionContextToViewControllerKey];
    
    UIView *container = [transitionContext containerView];
    UIView *sourceView = sourceViewController.view;
    UIView *destinationView = destinationViewController.view;
    
    if(self.isPresenting) {
        // layout overlay view
        self.overlayView = [[UIView alloc] initWithFrame:container.bounds];
        self.overlayView.backgroundColor = [UIColor colorWithWhite:0.5f alpha:0.7f];
        [container addSubview:self.overlayView];
        
        if(self.needsAutoDismiss) {
            UITapGestureRecognizer *gesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(dismiss)];
            [self.overlayView addGestureRecognizer:gesture];
        }
        
        // if the destination view contains a scroll view, the modal can be either presented fullscreen or
        // have the scrollView tell auto layout its content view's height:
        // http://stackoverflow.com/questions/18498098/how-to-make-uitableviews-height-dynamic-with-autolayout-feature
        
        // layout destination view
        [container addSubview:destinationView];
        [destinationView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.width.equalTo(container);
            make.center.equalTo(container);
           
            // force the height to be the maximum size if neccessary
            if(self.forcesMaximumHeight) {
                make.height.equalTo(container).multipliedBy(EHIFullscreenMultiplier);
            }
            // otherwise, cap the height to the maximum
            else {
                CGFloat preferredHeight = destinationViewController.preferredContentSize.height;
                if(preferredHeight != 0.0f) {
                    make.height.equalTo(@(destinationViewController.preferredContentSize.height)).with.priorityMedium();
                }
                
                make.height.lessThanOrEqualTo(container).multipliedBy(1.0f);
            }
        }];
        
        // prepare for animations
        self.overlayView.alpha = 0.0;
        destinationView.layer.transform = [self transformForModalView:destinationView withContainer:container isVisible:NO];
    }

    UIView *presentedView = self.isPresenting ? destinationView : sourceView;
   
    UIView.animate(YES)
        .duration(EHITransitionDuration).damping(0.9)
        .transform(^{
            self.overlayView.alpha = self.isPresenting ? 1.0 : 0.0;
            presentedView.layer.transform = [self transformForModalView:presentedView withContainer:container isVisible:self.isPresenting];
        }).start(^(BOOL finished) {
            self.isPresenting ?: [self.overlayView removeFromSuperview];
            [transitionContext completeTransition:YES];
        });
}

- (void)dismiss
{
    [self.callback overlayTransitionDidTapOverlayContainer:self];
}

//
// Helpers
//

- (CATransform3D)transformForModalView:(UIView *)modal withContainer:(UIView *)container isVisible:(BOOL)isVisible
{
    CGFloat translation = 0.0f;

    // if we're hidden, push this view off the bottom of the screen
    if(!isVisible) {
        translation = (container.bounds.size.height + modal.bounds.size.height) / 2.0f;
    }
    
    return CATransform3DMakeTranslation(0.0f, translation, 0.0f);
}

@end
