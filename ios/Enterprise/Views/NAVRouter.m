//
//  NAVRouter.m
//  Enterprise
//
//  Created by Ty Cobb on 6/25/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@import ObjectiveC;

#import "NAVRouter.h"
#import "NSMutableArray+Queue.h"

@interface NAVRouter () <NAVTransitionBuilderDelegate>
@property (strong, nonatomic) NSMutableArray *transitionQueue;
@property (assign, nonatomic) BOOL isReady;
@property (assign, nonatomic) BOOL isTransitioning;
@end

@implementation NAVRouter

- (instancetype)init
{
    if(self = [super init]) {
        _transitionQueue = [NSMutableArray new];
        
        // we won't run any transitions until the window can handle it
        [self waitOnWindowToBeReady:UIApplication.sharedApplication.keyWindow];
    }
    
    return self;
}

# pragma mark - Readiness

- (void)waitOnWindowToBeReady:(UIWindow *)window
{
    // we can't show modals, etc until we have a root view controller. so we'll wait until that point.
    self.isReady = window.rootViewController != nil;
    
    // if we're not ready, observe the notification that will let us know when the root view controller exists
    if(!self.isReady) {
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(windowDidBecomeReady:) name:UIWindowDidBecomeKeyNotification object:window];
    }
}

- (void)windowDidBecomeReady:(NSNotification *)notification
{
    self.isReady = YES;
    
    // after we receive this notification we should be ready, so we don't care anymore
    [[NSNotificationCenter defaultCenter] removeObserver:self name:UIWindowDidBecomeKeyNotification object:notification.object];
    
    // then we should try and run the initial transition
    [self dequeueTransition];
}

# pragma mark - Shared Instance

+ (instancetype)router
{
    NAVRouter *router = objc_getAssociatedObject(self, _cmd);
    
    // create class based shared instance if necessary
    if(!router) {
        router = [self new];
        objc_setAssociatedObject(self, _cmd, router, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
    }
    
    return router;
}

# pragma mark - NAVTransitionBuilderDelegate

- (void)enqueueTransitions:(NSArray *)transitions forBuilder:(NAVTransitionBuilder *)transitionBuilder
{
    self.transitionQueue.enqueueAll(transitions);
    
    [self dequeueTransition];
}

# pragma mark - Transitions

- (NAVTransitionBuilder *)transition
{
    NAVTransitionBuilder *transitionBuilder = [NAVTransitionBuilder new];
    transitionBuilder.delegate = self;
    return transitionBuilder;
}

# pragma mark - Transitions

- (void)dequeueTransition
{
    // can't dequeue if we're missing transitions or not ready
    if(self.isTransitioning || !self.transitionQueue.count || !self.isReady) {
        return;
    }
    
    NAVTransition *transition = self.transitionQueue.dequeue;

    // notify delegate we're about to transition
    if([self.delegate respondsToSelector:@selector(router:willPerformTransition:)]) {
        [self.delegate router:self willPerformTransition:transition];
    }
    
    // tell our performer to execute the transition and wait appropriately to dequeue next one
    if(transition.isAsync) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.transitionPerformer transition:transition completion:nil];
        });
        
        [self didPerformTransition:transition];
    }
    else {
        [self.transitionPerformer transition:transition completion:^{
            [self didPerformTransition:transition];
        }];
    }
}

- (void)didPerformTransition:(NAVTransition *)transition
{
    if([self.delegate respondsToSelector:@selector(router:didPerformTransition:)]) {
        [self.delegate router:self didPerformTransition:transition];
    }
    
    [self dequeueTransition];
}

@end

