//
//  UICollectionView+Utility.m
//  Enterprise
//
//  Created by Ty Cobb on 1/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "UICollectionView+Utility.h"

@implementation UICollectionView (Utility)

- (void)ehi_reloadWithSelection:(BOOL)preservesSelection
{
    NSArray *selectedPaths =  self.indexPathsForSelectedItems;
    
    [self reloadData];
    
    if(preservesSelection) {
        for(NSIndexPath *indexPath in selectedPaths) {
            [self selectItemAtIndexPath:indexPath animated:NO scrollPosition:UICollectionViewScrollPositionNone];
        }
    }
}

- (void)ehi_invalidateLayoutAnimated:(BOOL)animated
{
    if(!animated) {
        [self.collectionViewLayout invalidateLayout];
    } else {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self performBatchUpdates:nil completion:nil];
        });
    }
}

- (void)ehi_invalidateLayoutAnimated:(BOOL)animated completion:(void(^)(BOOL finished))completion
{
    if(!animated) {
        [self.collectionViewLayout invalidateLayout];
    } else {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self performBatchUpdates:nil completion:completion];
        });
    }
}

# pragma mark - Selection

- (void)ehi_selectItemAtIndexPath:(NSIndexPath *)indexPath animated:(BOOL)animated scrollPosition:(UICollectionViewScrollPosition)position
{
    if(indexPath && indexPath.item != NSNotFound) {
        [self selectItemAtIndexPath:indexPath animated:animated scrollPosition:position];
    }
}

- (NSIndexPath *)ehi_indexPathForSelectedItem
{
    return self.indexPathsForSelectedItems.firstObject;
}

# pragma mark - Framing

- (CGRect)ehi_rectForCellAtLocation:(CGPoint)location
{
    NSIndexPath *indexPath     = [self indexPathForItemAtPoint:location];
    UICollectionViewCell *cell = [self cellForItemAtIndexPath:indexPath];
    
    return cell.frame;
}

- (CGRect)ehi_expectedFrameForCellAtIndexPath:(NSIndexPath *)indexPath
{
    // get the cell's current frame
    UICollectionViewCell *cell = [self cellForItemAtIndexPath:indexPath];
    CGRect expectedFrame = cell.frame;
    
    // and use the intrinsic content size we're going to resize it to
    expectedFrame.size = cell.intrinsicContentSize;
    
    return expectedFrame;
}

# pragma mark - Cell Filtering

- (NSArray *)ehi_visibleCellsMatchingPredicate:(BOOL (^)(NSIndexPath *indexPath))predicate
{
    return self.indexPathsForVisibleItems.select(predicate).map(^(NSIndexPath *indexPath) {
        return [self cellForItemAtIndexPath:indexPath];
    });
}

- (NSArray *)ehi_visibleCellsInSection:(NSInteger)section
{
    return [self ehi_visibleCellsMatchingPredicate:^BOOL(NSIndexPath *indexPath) {
        return indexPath.section == section;
    }];
}

# pragma mark - Registration

- (void)ehi_registerNibForCellWithClass:(Class<EHIListCell>)klass deviceify:(BOOL)shouldDeviceify
{
    NSString *identifier = [klass identifier];
    UINib *nib = [self ehi_nibWithName:identifier deviceify:shouldDeviceify];
    [self registerNib:nib forCellWithReuseIdentifier:identifier];
}

- (void)ehi_registerNibForSupplementaryViewWithClass:(Class<EHIListCell>)klass deviceify:(BOOL)shouldDeviceify
{
    // pull out the default kind if possible
    NSString *kind = nil;
    if([(id)klass respondsToSelector:@selector(kind)]) {
        kind = [klass kind];
    }

    [self ehi_registerNibForSupplementaryViewWithClass:klass deviceify:shouldDeviceify kind:kind];
}

- (void)ehi_registerNibForSupplementaryViewWithClass:(Class<EHIListCell>)klass deviceify:(BOOL)shouldDeviceify kind:(NSString *)kind
{
    NSAssert(kind != nil, @"failed to provide kind when registering supplementary view: %@", klass);
    
    NSString *identifier = [klass identifier];
    UINib *nib = [self ehi_nibWithName:identifier deviceify:shouldDeviceify];
    [self registerNib:nib forSupplementaryViewOfKind:kind withReuseIdentifier:identifier];
}

- (UINib *)ehi_nibWithName:(NSString *)name deviceify:(BOOL)shouldDeviceify
{
    if(shouldDeviceify) {
        name = UIDeviceifyName(name);
    }
    return [UINib nibWithNibName:name bundle:[NSBundle mainBundle]];
}

# pragma mark - Animation

- (void)ehi_scrollToTop
{
    [self ehi_scrollToTopAnimated:NO];
}

- (void)ehi_scrollToTopAnimated:(BOOL)animated
{
    [self setContentOffset:(CGPoint){
        .x = -self.contentInset.left,
        .y = -self.contentInset.top
    } animated:YES];
}

- (void)ehi_revealExpandedCellAtIndexPath:(NSIndexPath *)indexPath completion:(void (^)(BOOL finished))completion
{
    [self performBatchUpdates:^{
        // compute the future frame of the cell
        CGRect expectedFrame = [self ehi_expectedFrameForCellAtIndexPath:indexPath];
        
        // determine the bottom position of the future frame and the current viewport
        CGFloat bottomOffset = CGRectGetMaxY(self.bounds);
        CGFloat expectedBottomOffset = CGRectGetMaxY(expectedFrame);
        
        // if the cell extends below the viewport, scroll it on-screen
        if(expectedBottomOffset > bottomOffset) {
            CGFloat offsetHeight = CGRectGetMinY(expectedFrame) - CGRectGetMinY(self.bounds);
            
            //if offset is past bounds of collection view, scroll instead to the bottom of the cell
            if (offsetHeight + bottomOffset > expectedBottomOffset) {
                offsetHeight = expectedBottomOffset - bottomOffset;
            }
            
            CGPoint updatedOffset = CGPointOffset(self.contentOffset, 0.0f, offsetHeight);
            
            [self setContentOffset:updatedOffset animated:YES];
        }
    } completion:completion];
}

# pragma mark - Responder

- (void)ehi_advanceFirstResponderToNextCellInSectionAfterIndexPath:(NSIndexPath *)indexPath
{
    // look through remaining cells to find next to take responder status
    NSInteger from = indexPath.item+1;
    NSInteger to   = [self numberOfItemsInSection:indexPath.section];
    NSInteger section = indexPath.section;
    
    // keep looking up until find a section that `canBecomeFirstResponder` or reaches the end
    BOOL keepLookUp = ![self nextResponderFromIndex:from toIndex:to inSection:section];
    if(keepLookUp) {
        
        NSInteger numberOfSections = self.numberOfSections;
        
        // avoid going beyond the bounds of the collection view
        NSInteger nextSection = MIN(numberOfSections, indexPath.section + 1);
        
        // look through remaining sections
        for(NSInteger section = nextSection ; section < numberOfSections ; section++) {
            NSInteger itemsInSection = [self numberOfItemsInSection:section];
            
            // stop when a cell `canBecomeFirstResponder`
            if([self nextResponderFromIndex:0 toIndex:itemsInSection inSection:section]) {
                break;
            }
        }
    }
}

- (BOOL)nextResponderFromIndex:(NSInteger)fromIndex toIndex:(NSInteger)toIndex inSection:(NSInteger)section
{
    for(NSInteger i=fromIndex ; i < toIndex ; i++) {
        NSIndexPath *nextIndexPath = [NSIndexPath indexPathForItem:i inSection:section];
        if([[self cellForItemAtIndexPath:nextIndexPath] becomeFirstResponder]) {
            return YES;
        }
    }
    return NO;
}

@end
