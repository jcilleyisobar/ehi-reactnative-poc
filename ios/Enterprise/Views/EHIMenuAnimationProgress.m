//
//  EHIMenuAnimationProgress.m
//  Enterprise
//
//  Created by Ty Cobb on 3/31/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIMenuAnimationProgress.h"

@interface EHIMenuAnimationProgress ()
@property (assign, nonatomic) CGFloat percentComplete;
@property (assign, nonatomic) BOOL isAnimating;
@property (strong, nonatomic) NSHashTable *listeners;
@end

@implementation EHIMenuAnimationProgress

+ (instancetype)sharedInstance
{
    static EHIMenuAnimationProgress *sharedInstance;
    
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedInstance = [self new];
    });
    
    return sharedInstance;
}

- (instancetype)init
{
    if(self = [super init]) {
        _listeners = [NSHashTable weakObjectsHashTable];
    }
    
    return self;
}

# pragma mark - Public Methods

- (void)addListener:(id<EHIMenuAnimationProgressListener>)listener
{
    [self notifyListener:listener forEvent:@selector(menuAnimationDidUpdate:)];
    [self.listeners addObject:listener];
}

- (void)setPercentComplete:(CGFloat)percentComplete animated:(BOOL)animated
{
    self.percentComplete = percentComplete;
    self.isAnimating     = animated;
   
    UIView.animate(animated).duration(0.4).transform(^{
        [self notifyListenersForEvent:@selector(menuAnimationDidUpdate:)];
    }).start(^(BOOL finished) {
        self.isAnimating = NO;
        if(animated) {
            [self notifyListenersForEvent:@selector(menuAnimationDidFinishAnimating:)];
        }
    });
}

- (void)setDidAnimateUsingGesture:(BOOL)usedGesture
{
     [self notifyListenersForEvent:@selector(menuAnimationDidFinishAnimatingUsingGesture)];
}

# pragma mark - Notifiers

- (void)notifyListenersForEvent:(SEL)event
{
    for(id<EHIMenuAnimationProgressListener> listener in self.listeners) {
        [self notifyListener:listener forEvent:event];
    }
}

- (void)notifyListener:(id<EHIMenuAnimationProgressListener>)listener forEvent:(SEL)event
{
    if([listener respondsToSelector:event]) {
        IGNORE_PERFORM_SELECTOR_WARNING(
            [listener performSelector:event withObject:self];
        );
    }
}

@end
