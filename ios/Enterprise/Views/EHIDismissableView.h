//
//  EHIDismissableView.h
//  Enterprise
//
//  Created by Ty Cobb on 1/30/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILayoutable.h"

@protocol EHIDismissableViewDelegate;

@interface EHIDismissableView : UIView

/** Receives dismissal lifecycle events */
@property (weak, nonatomic) id<EHIDismissableViewDelegate> delegate;
/** @c YES if the dismissable view is visible; defaults to @c NO. */
@property (assign, nonatomic) BOOL isVisible;
/** The dismissable view will automatically insert and size its content as a subview. */
@property (weak, nonatomic) UIView<EHILayoutable> *contentView;

/** Sets the the content view with custom metrics; if @c metrics are @c nil, the class' metrics are used */
- (void)setContentView:(UIView<EHILayoutable> *)contentView metrics:(EHILayoutMetrics *)metrics;

@end

@protocol EHIDismissableViewDelegate <NSObject> @optional

/**
 Called just before the view begins its dismissal animation.
 @param view The dismissable view being dismissed
*/
- (void)dismissableViewWillDismiss:(EHIDismissableView *)view;

/**
 Called after the view finishes its dismissal animation
 @param view The dismissable view just dismissed
*/

- (void)dismissableViewDidDismmiss:(EHIDismissableView *)view;

@end
