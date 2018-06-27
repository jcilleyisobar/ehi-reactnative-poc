//
//  EHIMenuAnimation.m
//  Enterprise
//
//  Created by Ty Cobb on 1/12/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIMenuAnimation.h"
#import "EHIMenuAnimationProgress.h"
#import "EHIGeometry.h"

#define EHIDrawerGestureRecognitionTolerance (88.0f)

@interface EHIMenuAnimation () <UIGestureRecognizerDelegate>
@property (assign, nonatomic) BOOL isPanning;
@property (assign, nonatomic) BOOL wasVisible;
@property (assign, nonatomic) BOOL usedGesture;
@property (assign, nonatomic) EHIFloatRange translationRange;
@property (strong, nonatomic) UIGestureRecognizer *drawerGesture;
@end

@implementation EHIMenuAnimation

- (instancetype)init
{
    if(self = [super init]) {
        // setup the drawer pan gesture
        [self insertDrawerGesture];

        // wait until the view has been resized initially to calculate our range
        [[NSNotificationCenter defaultCenter] ehi_observeOnce:UIApplicationDidBecomeActiveNotification block:^(NSNotification *notification) {
            NSAssert(self.drawerView, @"drawer animation didn't have a drawer view by the time it became visible!");
            
            self.translationRange = (EHIFloatRange){ .length = self.drawerView.bounds.size.width };
            // update the views to their default state
            [self updateIsVisible:NO animated:NO completion:nil];
        }];
    }

    return self;
}

# pragma mark - Enabled

- (void)setIsEnabled:(BOOL)isEnabled
{
    self.drawerGesture.enabled = isEnabled;
}

- (BOOL)isEnabled
{
    return self.drawerGesture.isEnabled;
}

# pragma mark - NAVAnimation

- (void)updateIsVisible:(BOOL)isVisible animated:(BOOL)animated completion:(void (^)(void))completion
{
    // make sure we're visible before transitioning
    self.drawerView.hidden = NO;
    // get the translation value (0.0, 1.0) from our range
    CGFloat translation = EHIFloatRangeInterpolate(self.translationRange, isVisible);
    
    // make sure keyboard is hidden
    if(isVisible) {
        [[[UIApplication sharedApplication] keyWindow] endEditing:YES];
    }
        
    UIView.animate(animated).duration(0.6).damping(0.85)
        .options(UIViewAnimationOptionBeginFromCurrentState)
        .transform(^{
            [self applyTranslation:translation animated:animated];
        }).start(^(BOOL finished) {
            self.drawerView.hidden = !isVisible;
            if(self.usedGesture) {
                [[EHIMenuAnimationProgress sharedInstance] setDidAnimateUsingGesture:YES];
            }
            self.usedGesture = NO;
            ehi_call(completion)();
        });
}

- (void)applyTranslation:(CGFloat)translation animated:(BOOL)animated
{
    // constrain the offset if we're panning
    if(self.isPanning) {
        translation = [self absoluteTranslationForRelativeTranslation:translation];
        
        // if we dragged outside our range, slow the drag by 75%
        CGFloat overdrag = EHIFloatRangeDelta(self.translationRange, translation);
        if(overdrag) {
            translation -= overdrag * 0.75f;
        }
    }

    // update the content's position 
    self.contentView.layer.transform = CATransform3DMakeTranslation(translation, 0.0f, 0.0f);
   
    // notify our progress listeners of the completion percent
    CGFloat percentComplete = EHIFloatRangeNormalize(self.translationRange, translation);
    [[EHIMenuAnimationProgress sharedInstance] setPercentComplete:percentComplete animated:animated];
}

- (CGFloat)absoluteTranslationForRelativeTranslation:(CGFloat)translation
{
    // apply offset based on start state (min or max of translation range)
    translation = translation + (self.wasVisible ?
        EHIFloatRangeMax(self.translationRange) : self.translationRange.location);
   
    // can't into the left edge of the screen
    translation = MAX(translation, 0.0f);
    
    return translation;
}

# pragma mark - Gesture Handling

- (void)handlePanGesture:(UIPanGestureRecognizer *)gesture
{
    switch(gesture.state) {
        case UIGestureRecognizerStateBegan:
            [self handlePanGestureDidBegin:gesture]; break;
        case UIGestureRecognizerStateChanged:
            [self handlePanGestureDidContinue:gesture]; break;
        default:
            [self handlePanGestureDidEnd:gesture]; break;
    }
}

- (void)handlePanGestureDidBegin:(UIPanGestureRecognizer *)gesture
{
    self.isPanning  = YES;
    self.wasVisible = self.isVisible;
    self.drawerView.hidden = NO;
}

- (void)handlePanGestureDidContinue:(UIPanGestureRecognizer *)gesture
{
    self.usedGesture = YES;
    CGFloat translation = [gesture translationInView:gesture.view].x;
    [self applyTranslation:translation animated:NO];
}

- (void)handlePanGestureDidEnd:(UIPanGestureRecognizer *)gesture
{
    // update our state
    self.isPanning = NO;
    
    BOOL isVisible = [self snapsToVisibleForGesture:gesture];
    
    // just animate the snap if our state is already the snap state
    if(self.isVisible == isVisible) {
        [self updateIsVisible:isVisible animated:YES completion:nil];
    }
    // otherwise, let the animation handle it
    else {
        [self setIsVisible:isVisible animated:YES completion:nil];
    }
}

//
// Helpers
//

- (BOOL)snapsToVisibleForGesture:(UIPanGestureRecognizer *)gesture
{
    CGFloat velocity = [gesture velocityInView:self.contentView].x;
    
    // if the user reverses direction and we were hidden, then hide
    if(velocity < 0.0f && !self.wasVisible) {
        return NO;
    }
    // if the user reverses direction and we were visible, open
    else if(velocity > 0.0f && self.wasVisible) {
        return YES;
    }
    // if we maintained direction and velocity, flip the isVisible state
    else if(velocity != 0.0f) {
        return !self.wasVisible;
    }
    
    // otherwise, we ended with no velocity, so let's snap to the nearest state
    CGFloat relative    = [gesture translationInView:gesture.view].x;
    CGFloat translation = [self absoluteTranslationForRelativeTranslation:relative];
    CGFloat centerDelta = translation - EHIFloatRangeCenter(self.translationRange);
    
    return centerDelta > 0.0f;
}

# pragma mark - Gesture Configuration

- (void)insertDrawerGesture
{
    // add the drawer gesture to the root view so we can capture it regardless of drawer state
    UIView *gestureView = [[UIApplication sharedApplication].windows.firstObject rootViewController].view;
    [gestureView addGestureRecognizer:self.drawerGesture];
}

- (UIGestureRecognizer *)drawerGesture
{
    if(_drawerGesture) {
        return _drawerGesture;
    }
    
    _drawerGesture = [[UIPanGestureRecognizer alloc] initWithTarget:self action:@selector(handlePanGesture:)];
    _drawerGesture.delegate = self;
    
    return _drawerGesture;
}

# pragma mark - UIGestureRecognizerDelegate

- (BOOL)gestureRecognizerShouldBegin:(UIGestureRecognizer *)gesture
{
    CGPoint location = [gesture locationInView:gesture.view];
    
    // if we're visible, only start in the right gutter
    if(self.isVisible) {
        return location.x > CGRectGetMaxX(gesture.view.frame) - EHIDrawerGestureRecognitionTolerance;
    }
   
    // otherwise, only start in the left gutter
    return location.x < gesture.view.frame.origin.x + EHIDrawerGestureRecognitionTolerance;
}

- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gesture shouldRecognizeSimultaneouslyWithGestureRecognizer:(UIGestureRecognizer *)otherGesture
{
    return NO;
}

@end
