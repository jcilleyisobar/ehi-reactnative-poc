//
//  NAVTransitionBuilder.m
//  Enterprise
//
//  Created by Alex Koller on 12/4/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "NAVTransitionBuilder.h"
#import "NAVStackTransition.h"

@interface NAVTransitionBuilder ()
// builder
@property (strong, nonatomic) NSMutableArray *transitions;
@property (assign, nonatomic) BOOL combineStackB;
// per transition
@property (assign, nonatomic) NAVTransitionType typeB;
@property (strong, nonatomic) id dataB;
@property (assign, nonatomic) BOOL animatedB;
@property (strong, nonatomic) id objectB;
@property (strong, nonatomic) id handlerB;
// animation
@property (assign, nonatomic) NAVAnimationOptions optionsB;
@end

@implementation NAVTransitionBuilder

- (instancetype)init
{
    if(self = [super init]) {
        _transitions   = [NSMutableArray new];
        _combineStackB = YES;
    }
    
    return self;
}

- (NAVTransitionBuilder *(^)(BOOL))animated
{
    return ^(BOOL animated) {
        self.animatedB = animated;
        
        return self;
    };
}

- (NAVTransitionBuilder *(^)(BOOL))combineStack
{
    return ^(BOOL combineStack) {
        self.combineStackB = combineStack;
        
        return self;
    };
}

- (NAVTransitionBuilder *(^)(id))object
{
    return ^(id object) {
        self.objectB = object;
        
        return self;
    };
}

- (NAVTransitionBuilder *(^)(id))handler
{
    return ^(id handler) {
        self.handlerB = handler;
        
        return self;
    };
}

# pragma mark - Stack Transitions

- (NAVTransitionBuilder *(^)(NSString *))root
{
    return ^(NSString *screen) {
        [self finalizeLastTransition];
        
        self.typeB = NAVTransitionTypeRoot;
        self.dataB = screen;
        
        return self;
    };
}

- (NAVTransitionBuilder *(^)(NSString *))push
{
    return ^(NSString *screen) {
        [self finalizeLastTransition];
        
        self.typeB   = NAVTransitionTypePush;
        self.dataB = screen;
        
        return self;
    };
}

- (NAVTransitionBuilder *(^)(NSUInteger))pop;
{
    return ^(NSUInteger count){
        [self finalizeLastTransition];
        
        self.typeB = NAVTransitionTypePop;
        self.dataB = @(count);
        
        return self;
    };
}

- (NAVTransitionBuilder *(^)(NSString *))resolve
{
    return ^(NSString *screen) {
        [self finalizeLastTransition];
        
        self.typeB = NAVTransitionTypeResolve;
        self.dataB = screen;
        
        return self;
    };
}

# pragma mark - Animation Transitions

- (NAVTransitionBuilder *(^)(NSString *))present
{
    return ^(NSString *screen) {
        return self.animateWithOptions(screen, NAVAnimationOptionsVisible|NAVAnimationOptionsModal);
    };
}

- (NAVTransitionBuilder *)dismiss
{
    return self.animateWithOptions(nil, NAVAnimationOptionsHidden|NAVAnimationOptionsModal);
}

- (NAVTransitionBuilder *(^)(NSString *, BOOL))animate
{
    return ^(NSString *screen, BOOL visible) {
        return self.animateWithOptions(screen, visible ? NAVAnimationOptionsVisible : NAVAnimationOptionsHidden);
    };
}

- (NAVTransitionBuilder *(^)(NSString *, NAVAnimationOptions))animateWithOptions
{
    return ^(NSString *screen, NAVAnimationOptions options) {
        [self finalizeLastTransition];
        
        self.typeB    = NAVTransitionTypeAnimation;
        self.dataB    = screen;
        self.optionsB = options;
        
        return self;
    };
}

# pragma mark - Construction

- (void)finalizeLastTransition
{
    // first or malformed transition
    if(self.typeB == NAVTransitionTypeUnknown) {
        [self resetProperties];
        return;
    }
    
    NAVTransition *transition = [self build];
    
    // append if can't combine
    if(!(self.combineStackB && [self combineTransition:transition])) {
        [self.transitions addObject:transition];
    }
    
    [self resetProperties];
}

- (void)resetProperties
{
    self.typeB     = NAVTransitionTypeUnknown;
    self.dataB     = nil;
    self.animatedB = YES;
    self.objectB   = nil;
    self.handlerB  = nil;
    self.optionsB  = 0;
}

- (NAVTransition *)build
{
    NSAssert(self.typeB != NAVTransitionTypeUnknown, @"Must have type to perform transition");
    
    NAVTransition *transition = [NAVTransition transitionWithType:self.typeB];
    
    NAVAttributes *attributes = [NAVAttributes new];
    attributes.userObject     = self.objectB;
    attributes.handler        = self.handlerB;
    
    transition.attributes = attributes;
    transition.data       = self.dataB;
    transition.isAnimated = self.animatedB;
    
    // set animation transition specific properties
    if([transition isKindOfClass:[NAVAnimationTransition class]]) {
        ((NAVAnimationTransition *)transition).options = self.optionsB;
    }
    
    return transition;
}

- (void (^)(void (^)(void)))start
{
    return ^(id completion) {
        NSAssert(self.delegate != nil, @"Must have delegate to run transitions");
        
        [self finalizeLastTransition];
        [self.transitions.lastObject setCompletion:completion];
        
        [self.delegate enqueueTransitions:[self.transitions copy] forBuilder:self];
    };
}

# pragma mark - Combining

- (BOOL)combineTransition:(NAVTransition *)newTransition
{
    NAVTransition *lastTransition = self.transitions.lastObject;
    
    if(!(TransitionIsCombinable(lastTransition) && TransitionIsCombinable(newTransition))) {
        return NO;
    }
    
    // add to replace transition array
    if(lastTransition.type == NAVTransitionTypeReplace) {
        NSArray *transitions      = lastTransition.data;
        lastTransition.data       = transitions.concat(@[newTransition]);
        lastTransition.isAnimated = newTransition.isAnimated;
    }
    
    // create a new replace transition combining these two transitions
    else {
        [self.transitions removeLastObject];
        
        NAVTransition *replaceTransition = [NAVStackTransition new];
        
        replaceTransition.type       = NAVTransitionTypeReplace;
        replaceTransition.data       = @[lastTransition, newTransition];
        replaceTransition.isAnimated = newTransition.isAnimated;
        
        [self.transitions addObject:replaceTransition];
    }

    return YES;
}

BOOL TransitionIsCombinable(NAVTransition *transition)
{
    return transition.type == NAVTransitionTypeRoot
        || transition.type == NAVTransitionTypePush
        || transition.type == NAVTransitionTypePop
        || transition.type == NAVTransitionTypeReplace;
}


@end