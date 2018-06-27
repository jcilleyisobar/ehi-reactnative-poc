//
//  EHIMenuAnimationProgress.h
//  Enterprise
//
//  Created by Ty Cobb on 3/31/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@protocol EHIMenuAnimationProgressListener;

@interface EHIMenuAnimationProgress : NSObject

/** The current state of the menu animation */
@property (assign, nonatomic, readonly) CGFloat percentComplete;
/** @c YES if the menu is currently animating */
@property (assign, nonatomic, readonly) BOOL isAnimating;

/** Returns the singleton progress clearinghouse */
+ (instancetype)sharedInstance;

/** Adds a listener who receives events about the menu animation's progress */
- (void)addListener:(id<EHIMenuAnimationProgressListener>)listener;

/**
 Updates the current percent complete of the menu animation. Any active listeners
 are notified immediately.
 
 @param percentComplete The current percent complete
 @param animated        @c YES if the update is animated
*/

- (void)setPercentComplete:(CGFloat)percentComplete animated:(BOOL)animated;
- (void)setDidAnimateUsingGesture:(BOOL)usedGesture;

@end

@protocol EHIMenuAnimationProgressListener <NSObject> @optional

/**
 Notifies the delegate that the animation updated its percent complete, either via
 programmatic animation or via user gesture.
 
 @param progress The animation progress instance
*/

- (void)menuAnimationDidUpdate:(EHIMenuAnimationProgress *)progress;

/**
 Notifies the delegate when a menu animation finishes. This is only called when the animation's
 @c percentComplete is called is @c animated set to @c YES.
 
 @param progress The animation progress instance.
*/

- (void)menuAnimationDidFinishAnimating:(EHIMenuAnimationProgress *)progress;

- (void)menuAnimationDidFinishAnimatingUsingGesture;

@end
