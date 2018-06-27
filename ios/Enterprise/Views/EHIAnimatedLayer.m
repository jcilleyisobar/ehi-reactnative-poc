//
//  EHIAnimatedLayer.m
//  Enterprise
//
//  Created by Ty Cobb on 2/13/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@import ObjectiveC;

#import "EHIAnimatedLayer.h"

@implementation EHIAnimatedLayer

- (id<CAAction>)actionForKey:(NSString *)event
{
    id<CAAction> action = [super actionForKey:event];

    // check if we have an action
    if(action) {
        return action;
    }
    // and if not, create a new implicit animation for this even
    else if([[self.class animatedKeys] containsObject:event]) {
        return [self ehi_implicitAnimationForKey:event];
    }
    
    return nil;
}

# pragma mark - Animated Keys

+ (NSSet *)animatedKeys
{
    static char animatedKeysKey;

    // get the cached keys
    NSSet *animatedKeys = objc_getAssociatedObject(self, &animatedKeysKey);
    
    if(!animatedKeys) {
        animatedKeys = [self animatedKeys:nil].ehi_set;
        objc_setAssociatedObject(self, &animatedKeysKey, animatedKeys, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
    }
    
    return animatedKeys;
}

+ (NSArray *)animatedKeys:(EHIAnimatedLayer *)layer
{
    return @[
        @key(layer.borderWidth),
        @key(layer.borderColor),
    ];
}

@end
