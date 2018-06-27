//
//  EHINavigationAnimation.m
//  Enterprise
//
//  Created by Ty Cobb on 1/29/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHINavigationAnimation.h"

@interface EHINavigationAnimation ()
@property (weak  , nonatomic) UIView *existingSuperview;
@property (strong, nonatomic) NSArray *existingConstraints;
@property (assign, nonatomic) CGRect existingFrame;
@property (assign, nonatomic) CATransform3D existingTransform;
@property (assign, nonatomic) BOOL existsingTranslatesAutoresizingMasks;
@end

@implementation EHINavigationAnimation

- (void)prepareToAnimateWithContainer:(UIView *)container
{
    self.existingTransform = self.target.layer.transform;
    
    if(self.shouldProxyTarget) {
        CGRect startFrame = [self.target convertRect:self.target.bounds toView:container.window];
        
        // capture the existing superview and any constraints involving this view
        self.existingFrame = self.target.frame;
        self.existingSuperview = self.target.superview;
        self.existingConstraints = [self.existingSuperview ehi_removeConstraintsInvolvingView:self.target];
        self.existsingTranslatesAutoresizingMasks = self.target.translatesAutoresizingMaskIntoConstraints;
        
        // stick the content view into to root hierarchy
        [container.window addSubview:self.target];
        
        self.target.translatesAutoresizingMaskIntoConstraints = YES;
        self.target.frame = startFrame;
    }
}

- (void)setPercentComplete:(CGFloat)percentComplete
{
    // only force layout when we're completing
    BOOL forcesLayout = self.forcesLayout && percentComplete == 1.0f;
    
    // we'll flip the percent complete if this view is exiting
    if(!self.isEntering) {
        percentComplete = 1.0 - percentComplete;
    }
    
    // update the target's state via the animation blocks
    for(EHINavigationAnimationBlock animation in self.animationBlocks) {
        animation(self.target, percentComplete);
    }
    
    // force the target to layout if necessary
    if(forcesLayout) {
        [self.target setNeedsUpdateConstraints];
        [self.target setNeedsLayout];
        [self.target layoutIfNeeded];
    }
}

- (void)didFinishAnimating
{

}

- (void)transitionDidComplete
{
    // unwind all of our animations if we aren't entering
    if(!self.isEntering) {
        [UIView performWithoutAnimation:^{
            for(EHINavigationAnimationBlock animation in self.animationBlocks) {
                animation(self.target, 1.0f);
            }
        }];
    }
   
    // reset our view to it's initial state
    self.target.layer.transform = self.existingTransform;
    
    if(self.shouldProxyTarget) {
        // restore the view to its former glory
        [self.existingSuperview addSubview:self.target];
        [self.existingSuperview addConstraints:self.existingConstraints];
        
        self.target.translatesAutoresizingMaskIntoConstraints = self.existsingTranslatesAutoresizingMasks;
        self.target.frame = self.existingFrame;
       
        // throw away our temporary storage
        self.existingConstraints = nil;
    }
}

# pragma mark - Timing

- (NSTimeInterval)delayForTotalDuration:(NSTimeInterval)duration
{
    CGFloat delay = self.delay;
   
    // if popping, complement the delay unless otherwise specified
    if(!self.isPush && self.reversesOrderOnUnwind) {
        delay = duration - delay - self.duration;
    }
    
    return delay;
}

- (NSTimeInterval)terminalTimeForTotalDuration:(NSTimeInterval)duration
{
    return [self delayForTotalDuration:duration] + self.duration;
}

@end

@implementation EHINavigationAnimation (Building)

+ (EHINavigationAnimationBuilder *(^)(UIView *))target
{
    return ^(UIView *target) {
        return [[EHINavigationAnimationBuilder alloc] initWithTarget:target shouldProxy:NO];
    };
}

+ (EHINavigationAnimationBuilder *(^)(UIView *))proxy
{
    return ^(UIView *target) {
        return [[EHINavigationAnimationBuilder alloc] initWithTarget:target shouldProxy:YES];
    };
}

@end
