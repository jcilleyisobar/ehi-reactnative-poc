//
//  EHICalendarLayoutDelegate.h
//  Enterprise
//
//  Created by Ty Cobb on 3/23/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

// while we aren't a flow layout, we're going to steal its delegate methods
@protocol EHICalendarLayoutDelegate <UICollectionViewDelegateFlowLayout>

/**
 Queries the row index for a given month. The month header will be laid out so that it overlaps
 the given row.
 
 @param indexPath The index path of the row to overlap
*/

- (NSInteger)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)layout monthRowForMonthIndexPath:(NSIndexPath *)indexPath;

/**
 Queries the row for the start of the month that contains the day at the given index path. Correspondingly,
 each day in a given month will return the same row.
 
 @param indexPath The index path for the day
*/

- (NSInteger)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)layout monthRowForDayIndexPath:(NSIndexPath *)indexPath;

/**
 Queries the delegate for the amount to offset the sticky header by. This value should be relative
 to the top of the months collection view.
 
 @return A amount of offset the sticky header by in points
*/

- (CGFloat)collectionView:(UICollectionView *)collectionView offsetForStickyHeaderOfLayout:(UICollectionViewLayout *)layout;

/**
 Queries the delegate for the height of the header's container. This value is used to parallax headers
 as they're entering / exiting the header container.
 
 @return The height of the header's container in points
*/

- (CGFloat)collectionView:(UICollectionView *)collectionView containerHeightForStickyHeaderOfLayout:(UICollectionViewLayout *)layout;

@end
