//
//  EHIModalOverlayTransitioningDelegate.h
//  Enterprise
//
//  Created by Alex Koller on 4/1/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@protocol EHIModalOverlayTransitioningCallbacks;
@interface EHIModalOverlayTransitioningDelegate : NSObject <UIViewControllerTransitioningDelegate>
/** @c YES if the transition should force the presented view to be its maximum height */
@property (assign, nonatomic) BOOL forcesMaximumHeight;
/** @c YES if the transition should dismiss when the user taps outside the view */
@property (assign, nonatomic) BOOL needsAutoDismiss;

@property (weak  , nonatomic) id<EHIModalOverlayTransitioningCallbacks> callback;
@end

@protocol EHIModalOverlayTransitioningCallbacks <NSObject>
- (void)overlayTransitionDidTapOverlayContainer:(EHIModalOverlayTransitioningDelegate *)delegate;
@end
