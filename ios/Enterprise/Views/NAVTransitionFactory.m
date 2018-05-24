//
//  NAVTransitionControllerFactory.m
//  Enterprise
//
//  Created by Alex Koller on 11/16/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

@import ObjectiveC;

#import "NAVTransitionFactory.h"
#import "NAVViewController.h"

@implementation NAVTransitionFactory

# pragma mark - Controllers

+ (NAVViewController *)controllerForTransition:(NAVTransition *)transition
{
    // destination should be a controller class in this case
    Class<NAVViewController> klass = [self classForTransition:transition];
    NAVViewController *controller  = [klass instance];
    
    [controller updateWithAttributes:transition.attributes];
    
    return controller;
}

+ (Class<NAVViewController>)classForTransition:(NAVTransition *)transition
{
    Class klass = [self.routingList objectForKey:transition.data];
    
    NSAssert(klass != nil, @"No class defined for transition");
    
    return klass;
}

+ (NAVAnimation *)animationForTransition:(NAVTransition *)transition
{
    return self.animationCache[transition.data];
}

# pragma mark - Animation Caching

+ (void)registerAnimation:(NAVAnimation *)animation forScreen:(NSString *)screen
{
    self.animationCache[screen] = animation;
}

//
// Helpers
//

+ (NSMutableDictionary *)animationCache
{
    NSMutableDictionary *animationCache = objc_getAssociatedObject(self, _cmd);
    
    if(!animationCache) {
        animationCache = [NSMutableDictionary new];
        objc_setAssociatedObject(self, _cmd, animationCache, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
    }
    
    return animationCache;
}

# pragma mark - Routing

+ (NSDictionary *)routingList
{
    NSDictionary *routingList = objc_getAssociatedObject(self, _cmd);
    
    if(routingList) {
        return routingList;
    }
    
    NSMutableDictionary *newRoutingList = [NSMutableDictionary new];
    
    NSArray *subclasses = [self viewControllerSubclasses];
    for(Class<NAVViewController> klass in subclasses) {
        NSString *route = [klass screenName];
        
        if(route) {
            newRoutingList[route] = klass;
        }
    }
    
    routingList = [newRoutingList copy];
    
    objc_setAssociatedObject(self, _cmd, routingList, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
    
    return routingList;
}

//
// Helpers
//

+ (NSArray<NAVViewController> *)viewControllerSubclasses
{
    Class *classes; uint count;
    classes = objc_copyClassList(&count);
    
    NSMutableArray *result = [NSMutableArray array];
    for(NSInteger i=0 ; i<count ; i++) {
        Class klass = classes[i];

        // search superclasses for our desired view controller class
        Class superClass = class_getSuperclass(klass);
        while(superClass && superClass != NAVViewController.class) {
            superClass = class_getSuperclass(superClass);
        }
        
        // mark subclasses
        if(superClass == NAVViewController.class) {
            [result addObject:klass];
        }
    }
    
    free(classes);
    
    return [result copy];
}

@end
