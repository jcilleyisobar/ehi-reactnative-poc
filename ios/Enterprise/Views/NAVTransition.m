//
//  NAVTransition.m
//  Enterprise
//
//  Created by Alex Koller on 11/16/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "NAVTransition_Subclass.h"
#import "NAVStackTransition.h"
#import "NAVAnimationTransition.h"

@implementation NAVTransition

+ (instancetype)transitionWithType:(NAVTransitionType)type
{
    return [[[self subclassForType:type] alloc] initWithTransitionType:type];
}

+ (Class)subclassForType:(NAVTransitionType)type
{
    return type == NAVTransitionTypeAnimation ? [NAVAnimationTransition class] : [NAVStackTransition class];
}

- (instancetype)initWithTransitionType:(NAVTransitionType)type
{
    if(self = [super init]) {
        _type = type;
    }
    
    return self;
}

# pragma mark - Lifecycle

- (void)prepareWithController:(UIViewController *)controller
{
    
}

- (void)performWithCompletion:(void (^)(void))completion
{
    
}

# pragma mark - Accessors

- (BOOL)isStack
{
    return self.type == NAVTransitionTypeRoot
        || self.type == NAVTransitionTypePop
        || self.type == NAVTransitionTypePush
        || self.type == NAVTransitionTypeResolve
        || self.type == NAVTransitionTypeReplace;
}

- (BOOL)isAsync
{
    return NO;
}

@end