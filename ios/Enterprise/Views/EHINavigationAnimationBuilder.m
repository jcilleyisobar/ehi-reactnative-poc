//
//  EHINavigationAnimationBuilder.m
//  Enterprise
//
//  Created by Ty Cobb on 2/12/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHINavigationAnimationBuilder.h"
#import "EHINavigationAnimation.h"

@interface EHINavigationAnimationBuilder ()
@property (strong, nonatomic) EHINavigationAnimation *animation;
@property (strong, nonatomic) NSMutableArray *animationBlocksB;
@end

@implementation EHINavigationAnimationBuilder

- (instancetype)init
{
    return [self initWithTarget:nil shouldProxy:NO];
}

- (instancetype)initWithTarget:(UIView *)target shouldProxy:(BOOL)shouldProxy
{
    if(self = [super init]) {
        _animation = [EHINavigationAnimation new];
        _animation.target = target;
        _animation.shouldProxyTarget = shouldProxy;
        _animation.reversesOrderOnUnwind = YES;
        
        _animationBlocksB = [NSMutableArray new];
    }
    
    return self;
}

- (EHINavigationAnimation *)build
{
    self.animation.animationBlocks = [self.animationBlocksB copy];
    return self.animation;
}

# pragma mark - Animation Generators

- (EHINavigationAnimationBuilder *)translationRange:(EHIFloatRangeVector)rangeVector reverse:(BOOL)shouldReverse
{
    if(shouldReverse) {
        rangeVector = EHIFloatRangeVectorInvert(rangeVector);
    }
    
    [self.animationBlocksB addObject:^(UIView *view, CGFloat percentComplete) {
        // calculate a 3D transform from the vector
        CATransform3D transform = CATransform3DIdentity;
        transform.m34 = -1.0f / 1000.0f;
        transform = CATransform3DTranslate(transform,
            EHIFloatRangeInterpolate(rangeVector.x, percentComplete),
            EHIFloatRangeInterpolate(rangeVector.y, percentComplete),
            EHIFloatRangeInterpolate(rangeVector.z, percentComplete)
        );
        
        // update the layer's transform
        view.layer.transform = transform;
    }];
    
    return self;
}

- (EHINavigationAnimationBuilder *)alphaRange:(EHIFloatRange)range reverse:(BOOL)shouldReverse
{
    if(shouldReverse) {
        range = EHIFloatRangeInvert(range);
    }
    
    [self.animationBlocksB addObject:^(UIView *view, CGFloat percentComplete) {
        view.alpha = EHIFloatRangeInterpolate(range, percentComplete);
    }];
    
    return self;
}

- (EHINavigationAnimationBuilder *)fromColor:(UIColor *)fromColor toColor:(UIColor *)toColor
{
    // capture color information for color interpolation
    CGFloat fromR, fromG, fromB, fromA;
    CGFloat toR, toG, toB, toA;
    
    [fromColor getRed:&fromR green:&fromG blue:&fromB alpha:&fromA];
    [toColor getRed:&toR green:&toG blue:&toB alpha:&toA];
    
    [self.animationBlocksB addObject:^(UIView *view, CGFloat percentComplete) {
        // interpolate between the 2 colors
        CGFloat r = fromR + (toR - fromR) * percentComplete;
        CGFloat g = fromG + (toG - fromG) * percentComplete;
        CGFloat b = fromB + (toB - fromB) * percentComplete;
        CGFloat a = fromA + (toA - fromA) * percentComplete;
        UIColor *transitionColor = [UIColor colorWithRed:r green:g blue:b alpha:a];
        
        view.backgroundColor = transitionColor;
    }];
    
    return self;
}

- (EHINavigationAnimationBuilder *)frame:(CGRect)destination reverse:(BOOL)shouldReverse
{
    UIView *target = self.animation.target;
    
    // get source frame in the window
    CGRect source = [target convertRect:target.bounds toView:target.window];
    
    // calculate the animation ranges based on our direction
    EHIFloatRange translation = (EHIFloatRange){
        .length = destination.origin.y - source.origin.y
    };
    EHIFloatRange width = (EHIFloatRange){
        .length = destination.size.width - source.size.width
    };
    EHIFloatRange height = (EHIFloatRange){
        .length = destination.size.height - source.size.height
    };
    
    // invert all the ranges if the invert flag is specified
    if(shouldReverse) {
        EHIFloatRange * ranges[] = { &translation, &width, &height };
        for(int index=0 ; index<3 ; index++) {
            *ranges[index] = EHIFloatRangeInvert(*ranges[index]);
        }
    }
  
    // add an animation to adjust the frame
    [self.animationBlocksB addObject:^(UIView *view, CGFloat percentComplete) {
        // get the base center and size
        CGPoint center = CGRectGetCenter(source);
        CGSize  size   = source.size;
        
        // offset them based on the percent complete
        center.y    += EHIFloatRangeInterpolate(translation, percentComplete);
        size.width  += EHIFloatRangeInterpolate(width, percentComplete);
        size.height += EHIFloatRangeInterpolate(height, percentComplete);
       
        // update the view
        CGRect frame = CGRectWithCenterAndSize(center, size);
        view.frame   = frame;
    }];
    
    return self;
}

- (EHINavigationAnimationBuilder *)block:(void(^)(id, CGFloat))block;
{
    NSParameterAssert(block);
    
    [self.animationBlocksB addObject:^(UIView *view, CGFloat percentComplete) {
        block(view, percentComplete);
    }];
    
    return self;
}

# pragma mark - Builder Hooks

- (EHINavigationAnimationBuilder *(^)(EHIFloatVector))translation
{
    return ^(EHIFloatVector vector) {
        return [self translationRange:EHIFloatRangeVectorFromVector(vector) reverse:NO];
    };
}

- (EHINavigationAnimationBuilder *(^)(EHIFloatVector))reverseTranslation
{
    return ^(EHIFloatVector vector) {
        return [self translationRange:EHIFloatRangeVectorFromVector(vector) reverse:YES];
    };
}

- (EHINavigationAnimationBuilder *(^)(EHIFloatRangeVector))translationRange
{
    return ^(EHIFloatRangeVector rangeVector) {
        return [self translationRange:rangeVector reverse:NO];
    };   
}

- (EHINavigationAnimationBuilder *(^)(CGFloat))alpha
{
    return ^(CGFloat alpha) {
        return [self alphaRange:(EHIFloatRange){ .length = alpha } reverse:NO];
    };
}

- (EHINavigationAnimationBuilder *(^)(CGFloat))reverseAlpha
{
    return ^(CGFloat alpha) {
        return [self alphaRange:(EHIFloatRange){ .length = alpha } reverse:YES];
    };
}

- (EHINavigationAnimationBuilder *(^)(EHIFloatRange))alphaRange
{
    return ^(EHIFloatRange range) {
        return [self alphaRange:range reverse:YES];
    };
}

- (EHINavigationAnimationBuilder *(^)(UIColor *fromColor, UIColor *toColor))colorRange
{
    return ^(UIColor *fromColor, UIColor *toColor) {
        return [self fromColor:fromColor toColor:toColor];
    };
}

- (EHINavigationAnimationBuilder *(^)(UIColor *color))color
{
    return ^(UIColor *toColor) {
        return [self fromColor:[UIColor whiteColor] toColor:toColor];
    };
}

- (EHINavigationAnimationBuilder *(^)(CGRect))frame
{
    return ^(CGRect frame) {
        return [self frame:frame reverse:NO];
    };
}

- (EHINavigationAnimationBuilder *(^)(CGRect))reverseFrame
{
    return ^(CGRect frame) {
        return [self frame:frame reverse:YES];
    };
}

- (EHINavigationAnimationBuilder *(^)(void (^)(id, CGFloat)))block
{
    return ^(void(^block)(id, CGFloat)) {
        return [self block:block];
    };
}

- (EHINavigationAnimationBuilder *(^)(NSTimeInterval))delay
{
    return ^(NSTimeInterval delay) {
        self.animation.delay = delay;
        return self;
    };
}

- (EHINavigationAnimationBuilder *(^)(NSTimeInterval))duration
{
    return ^(NSTimeInterval duration) {
        self.animation.duration = duration;
        return self;
    };
}

- (EHINavigationAnimationBuilder *(^)(BOOL))forcesLayout
{
    return ^(BOOL forcesLayout) {
        self.animation.forcesLayout = forcesLayout;
        return self;
    };
}

- (EHINavigationAnimationBuilder *(^)(BOOL))reversesOrderOnUnwind
{
    return ^(BOOL reversesOrderOnUnwind) {
        self.animation.reversesOrderOnUnwind = reversesOrderOnUnwind;
        return self;
    };
}

- (EHINavigationAnimationBuilder *(^)(UIViewAnimationOptions))options
{
    return ^(UIViewAnimationOptions options) {
        self.animation.options = options;
        return self;
    };
}


@end

@implementation EHINavigationAnimationBuilder (Special)

- (EHINavigationAnimationBuilder *)autoreversingNavigationBar
{
    __weak EHINavigationAnimation *animation = self.animation;
    
    return self.block(^(UINavigationBar *bar, CGFloat percentComplete) {
        if(!percentComplete || ![UIView areAnimationsEnabled]) {
            return;
        }
     
        CGRect originalFrame = bar.frame;
        
        UIView.animate(YES)
            .duration(0.35).delay(animation.delay)
            .transform(^{
                bar.frame = CGRectOffset(originalFrame, 0.0f, -CGRectGetMaxY(bar.frame));
            })
            .thenAnimate(YES)
            .duration(0.35).delay(0.25)
            .transform(^{
                bar.frame = originalFrame;
            })
            .start(nil);
    });
}

@end
