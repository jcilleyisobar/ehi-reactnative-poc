//
//  UIView+Snapshotting.m
//  Enterprise
//
//  Created by Ty Cobb on 1/28/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "UIView+Snapshotting.h"

@implementation UIView (Snapshotting)

- (UIView *)ehi_snapshotViewAndFrameAfterScreenUpdates:(BOOL)afterScreenUpdates
{
    UIView *snapshot = [self snapshotViewAfterScreenUpdates:afterScreenUpdates];
    snapshot.frame = [self convertRect:self.bounds toView:self.superview];
    return snapshot;
}

@end
