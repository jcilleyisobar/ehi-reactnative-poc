//
//  NAVTransitionAnimation.m
//  Enterprise
//
//  Created by Alex Koller on 12/1/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "NAVAnimation.h"

@implementation NAVAnimation

# pragma mark - NAVTransitionDestination

- (void)updateWithAttributes:(id)attributes
{
    
}

# pragma mark - Setters

- (void)setIsVisible:(BOOL)isVisible
{
    [self setIsVisible:isVisible animated:NO completion:nil];
}

- (void)setIsVisible:(BOOL)isVisible animated:(BOOL)animated completion:(void (^)(void))completion
{
    if(_isVisible == isVisible) {
        ehi_call(completion)();
        return;
    }
    
    _isVisible = isVisible;
    
    [self updateIsVisible:isVisible animated:animated completion:^{
        [self didFinishAnimationToVisible:isVisible];
        ehi_call(completion)();
    }];
}

# pragma mark - Lifecycle

- (void)updateIsVisible:(BOOL)isVisible animated:(BOOL)animated completion:(void (^)(void))completion
{
    
}

- (void)didFinishAnimationToVisible:(BOOL)isVisible
{
    [self.delegate animation:self didUpdateIsVisible:isVisible];
}

@end