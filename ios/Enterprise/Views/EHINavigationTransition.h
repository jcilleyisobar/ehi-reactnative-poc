//
//  EHINavigationTransition.h
//  Enterprise
//
//  Created by Ty Cobb on 1/28/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@interface EHINavigationTransition : UIPercentDrivenInteractiveTransition <UIViewControllerAnimatedTransitioning>

/** Set to @c YES if the tranistion is executing an animated push */
@property (assign, nonatomic) BOOL isPush;

/** Allows the transition to hook into a gesture recognizer */
- (void)setGestureRecognizer:(UIGestureRecognizer *)gestureRecognizer;

@end
