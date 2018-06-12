//
//  UICollectionView+Utility.h
//  Enterprise
//
//  Created by Ty Cobb on 1/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIListCell.h"

@interface UICollectionView (Utility)

@property (nonatomic, readonly) NSIndexPath *ehi_indexPathForSelectedItem;

- (void)ehi_reloadWithSelection:(BOOL)preservesSelection;
- (void)ehi_invalidateLayoutAnimated:(BOOL)animated;
- (void)ehi_invalidateLayoutAnimated:(BOOL)animated completion:(void(^)(BOOL finished))completion;

- (void)ehi_registerNibForCellWithClass:(Class<EHIListCell>)klass deviceify:(BOOL)shouldDeviceify;
- (void)ehi_registerNibForSupplementaryViewWithClass:(Class<EHIListCell>)klass deviceify:(BOOL)shouldDeviceify;
- (void)ehi_registerNibForSupplementaryViewWithClass:(Class<EHIListCell>)klass deviceify:(BOOL)shouldDeviceify kind:(NSString *)kind;

- (CGRect)ehi_rectForCellAtLocation:(CGPoint)location;
- (CGRect)ehi_expectedFrameForCellAtIndexPath:(NSIndexPath *)indexPath;

- (void)ehi_selectItemAtIndexPath:(NSIndexPath *)indexPath animated:(BOOL)animated scrollPosition:(UICollectionViewScrollPosition)position;

- (NSArray *)ehi_visibleCellsMatchingPredicate:(BOOL (^)(NSIndexPath *indexPath))predicate;
- (NSArray *)ehi_visibleCellsInSection:(NSInteger)section;

- (void)ehi_scrollToTop;
- (void)ehi_scrollToTopAnimated:(BOOL)animated;
- (void)ehi_revealExpandedCellAtIndexPath:(NSIndexPath *)indexPath completion:(void (^)(BOOL finished))completion;

- (void)ehi_advanceFirstResponderToNextCellInSectionAfterIndexPath:(NSIndexPath *)indexPath;

@end
