//
//  UIView+Animation.m
//  Enterprise
//
//  Created by Ty Cobb on 2/3/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "UIView+Animation.h"

@interface EHIAnimationBuilder ()
@property (strong, nonatomic) EHIAnimationBuilder *parent;
@property (assign, nonatomic) BOOL isAnimatedB;
@property (assign, nonatomic) BOOL dispatchesDelay;
@property (assign, nonatomic) NSTimeInterval durationB;
@property (assign, nonatomic) NSTimeInterval delayB;
@property (assign, nonatomic) CGFloat dampingB;
@property (assign, nonatomic) CGFloat initialVelocityB;
@property (assign, nonatomic) UIViewAnimationOptions optionsB;
@property (copy  , nonatomic) void(^beforeB)(void);
@property (copy  , nonatomic) void(^transformB)(void);
@end


@implementation EHIAnimationBuilder

- (instancetype)initWithIsAnimated:(BOOL)isAnimated
{
    if(self = [super init]) {
        _isAnimatedB = isAnimated;
    }
    
    return self;
}

- (void (^)(void(^)(BOOL)))start
{
    return ^(void(^completion)(BOOL)) {
        [self start:completion];
    };
}

- (void)start:(void(^)(BOOL))completion
{
    // if we have no parent, then just animate
    if(!self.parent) {
        [self delayedAnimateWithCompletion:completion];
    }
    // otherwise, wait until our parent completes
    else {
        [self.parent start:^(BOOL finished) {
            [self delayedAnimateWithCompletion:completion];
        }];
    }
}

- (void)delayedAnimateWithCompletion:(void(^)(BOOL))completion
{
    // if we don't dispatch our delay, run the animation
    if(!self.dispatchesDelay) {
        [self animateWithCompletion:completion];
    }
    // otherwise, capture the delay and dispatch the actual animation
    else {
        NSTimeInterval delay = self.delayB;
        self.delayB = 0.0f;
        
        dispatch_after_seconds(delay, ^{
            [self animateWithCompletion:completion];
        });
    }
}

- (void)animateWithCompletion:(void(^)(BOOL))completion
{
    // call the before block if it exists
    ehi_call(self.beforeB)();
    
    // if we're not animated, just call the transform
    if(!self.isAnimatedB) {
        [UIView animateWithDuration:self.durationB delay:self.delayB options:self.optionsB
                         animations:self.transformB completion:completion];
    }
    // we'll use a spring if either damping or velocity are set
    else if(self.dampingB || self.initialVelocityB) {
        [UIView animateWithDuration:self.durationB delay:self.delayB
             usingSpringWithDamping:self.dampingB initialSpringVelocity:self.initialVelocityB
                            options:self.optionsB animations:self.transformB completion:completion];
    }
    // otherwise, let's run a UIView animation
    else {
        [UIView animateWithDuration:self.durationB delay:self.delayB options:self.optionsB
                         animations:self.transformB completion:completion];
    }
}

# pragma mark - Chaining

- (EHIAnimationBuilder *(^)(EHIAnimationBuilder *))then
{
    return ^(EHIAnimationBuilder *next) {
        return [self chain:next];
    };
}

- (EHIAnimationBuilder *(^)(BOOL))thenAnimate
{
    return ^(BOOL isAnimated) {
        EHIAnimationBuilder *next = [EHIAnimationBuilder new];
        next.isAnimatedB = isAnimated;
        return [self chain:next];
    };
}

- (EHIAnimationBuilder *)chain:(EHIAnimationBuilder *)animation
{
    animation.parent = self;
    return animation;
}

# pragma mark - Builder

- (EHIAnimationBuilder *(^)(NSTimeInterval))duration
{
    return ^(NSTimeInterval duration) {
        self.durationB = duration;
        return self;
    };
}

- (EHIAnimationBuilder *(^)(NSTimeInterval))delay
{
    return ^(NSTimeInterval delay) {
        self.delayB = delay;
        return self;
    };
}

- (EHIAnimationBuilder *(^)(NSTimeInterval))wait
{
    return ^(NSTimeInterval delay) {
        self.delayB = delay;
        self.dispatchesDelay = YES;
        return self;
    };
}

- (EHIAnimationBuilder *(^)(CGFloat))damping
{
    return ^(CGFloat damping) {
        self.dampingB = damping;
        return self;
    };
}

- (EHIAnimationBuilder *(^)(CGFloat))initialVelocity
{
    return ^(CGFloat velocity) {
        self.initialVelocityB = velocity;
        return self;
    };
}

- (EHIAnimationBuilder *(^)(UIViewAnimationOptions))options
{
    return ^(UIViewAnimationOptions options) {
        self.optionsB = options;
        return self;
    };
}

- (EHIAnimationBuilder *(^)(UIViewAnimationOptions))option
{
    return ^(UIViewAnimationOptions options) {
        self.optionsB |= options;
        return self;
    };
}

- (EHIAnimationBuilder *(^)(void (^)(void)))before
{
    return ^(void(^before)(void)) {
        self.beforeB = before;
        return self;
    };
}

- (EHIAnimationBuilder *(^)(void (^)(void)))transform
{
    return ^(void(^transform)(void)) {
        self.transformB = transform;
        return self;
    };
}

@end

@implementation UIView (Animation)

+ (EHIAnimationBuilder *(^)(BOOL))animate
{
    return ^(BOOL isAnimated) {
        return [[EHIAnimationBuilder alloc] initWithIsAnimated:isAnimated];
    };
}

@end
