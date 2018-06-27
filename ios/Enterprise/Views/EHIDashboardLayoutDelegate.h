//
//  EHIDashboardLayoutDelegate.h
//  Enterprise
//
//  Created by Ty Cobb on 5/29/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@protocol EHIDashboardLayoutDelegate <UICollectionViewDelegateFlowLayout>

/**
 @brief Called by the layout to determine whether it should perform auto-snapping
 
 If the receiver returns @c NO, the layout will short-circuit it's snapping behavor.
*/

- (BOOL)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)layout shouldSnapWithProposedOffset:(CGPoint)offset;

/**
 @brief Called by the layout when it has determine it's about to snap
 
 The collection view can use this hook to modify its properties, such as its deceleration
 rate, so that snapping can work properly.
*/

- (void)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)layout willSnapToOffset:(CGPoint)offset;

@end
