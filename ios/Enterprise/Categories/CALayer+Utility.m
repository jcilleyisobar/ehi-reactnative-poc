//
//  CALayer+Utility.m
//  Enterprise
//
//  Created by Ty Cobb on 4/1/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "CALayer+Utility.h"

@implementation CALayer (Utility)

+ (CALayer *)ehi_layerWithFrame:(CGRect)frame
{
    CALayer *layer = [self new];
    layer.frame = frame;
    layer.backgroundColor = [UIColor blackColor].CGColor;
    
    return layer;
}

+ (void)ehi_performAnimated:(BOOL)animated transform:(void (^)(void))transform
{
    [self ehi_animate:animated duration:-1.0f transform:transform];
}

+ (void)ehi_performUnanimated:(void (^)(void))transform
{
    [self ehi_animate:NO duration:0.0f transform:transform];
}

+ (void)ehi_animate:(BOOL)animated duration:(NSTimeInterval)duration transform:(void (^)(void))transform
{
    [CATransaction begin];
   
    // disable animations if necessary
    [CATransaction setDisableActions:!animated];
    // update the duration if possible
    if(duration >= 0.0) {
        [CATransaction setAnimationDuration:duration];
    }
    
    transform();
    
    [CATransaction commit];
}

- (CAAnimation *)ehi_implicitAnimationForKey:(NSString *)key
{
    CABasicAnimation *animation = [CABasicAnimation animationWithKeyPath:key];
    animation.duration  = [CATransaction animationDuration] ?: 0.25;
    animation.fromValue = [self.presentationLayer valueForKeyPath:key];
    return animation;
}

@end
