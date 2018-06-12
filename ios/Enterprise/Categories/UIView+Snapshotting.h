//
//  UIView+Snapshotting.h
//  Enterprise
//
//  Created by Ty Cobb on 1/28/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@interface UIView (Snapshotting)

/**
 @brief Snapshots the view using and preserves its frame.
 
 Snapshotting is delegated to the system snapshotting functionality. The frame is captured
 in the windows coordinate space.
 
 @param afterScreenUpdates @c YES if the view should be snapshotted after the next draw cycle
 @return A snapshot of this view
*/

- (UIView *)ehi_snapshotViewAndFrameAfterScreenUpdates:(BOOL)afterScreenUpdates;

@end
