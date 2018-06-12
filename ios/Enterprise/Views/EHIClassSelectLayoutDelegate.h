//
//  EHIClassSelectLayoutDelegate.h
//  Enterprise
//
//  Created by Alex Koller on 8/31/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@protocol EHIClassSelectLayoutDelegate <UICollectionViewDelegateFlowLayout>

/**
 @brief Called by the layout to determine if the sticky redemption header should
        be inserted into the layout
 
 If @c YES, a sticky redemption is inserted at the top of the layout. The header will
 slide up/down in response to offset changes.
 */

- (BOOL)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)layout shouldDisplayRedemptionAtIndexPath:(NSIndexPath *)indexPath;

/**
 @brief Called by the layout to determine if the sticky redemption header should slide
 
 @c YES if the redemption header should slide in response to collection view offset changes. Use
 this to short-circuit unwanted sliding due to programatic offset changes.
 */

- (BOOL)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)layout shouldSlideRedemptionHeaderAtIndexPath:(NSIndexPath *)indexPath;

@end