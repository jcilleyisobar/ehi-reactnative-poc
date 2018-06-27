//
//  EHIListCollectionViewLayoutDelegate.h
//  Enterprise
//
//  Created by Ty Cobb on 3/5/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@protocol EHIListCollectionViewLayoutDelegate <UICollectionViewDelegateFlowLayout> @optional
- (NSInteger)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)layout itemsPerRowInSection:(NSInteger)section;
- (BOOL)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)layout isLoadingItemAtIndexPath:(NSIndexPath *)indexPath;
@end
