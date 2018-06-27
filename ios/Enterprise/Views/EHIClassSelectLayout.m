//
//  EHIClassSelectLayout.m
//  Enterprise
//
//  Created by fhu on 8/6/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIClassSelectLayout.h"
#import "EHIClassSelectLayoutDelegate.h"
#import "EHICarClassCell.h"
#import "EHIUserManager.h"

@interface EHIClassSelectLayout()
@property (strong, nonatomic, readonly) NSIndexPath *headerPath;
@property (assign, nonatomic) CGFloat scrollDifference;
@property (assign, nonatomic) CGFloat lastContentOffset;
@property (assign, nonatomic) CGFloat headerHeight;
@property (assign, nonatomic) CGFloat focusedCellOffsetDelta;
@property (weak  , nonatomic) id<EHIClassSelectLayoutDelegate> delegate;
@end

@implementation EHIClassSelectLayout

# pragma mark - Layout

- (BOOL)shouldInvalidateLayoutForBoundsChange:(CGRect)newBounds
{
    return YES;
}

- (NSArray *)layoutAttributesForElementsInRect:(CGRect)rect
{
    NSArray *attributesList = [[super layoutAttributesForElementsInRect:rect] copy];
    
    if(![self.delegate collectionView:self.collectionView layout:self shouldDisplayRedemptionAtIndexPath:self.headerPath]) {
        return attributesList;
    }
    
    self.focusedCellOffsetDelta = [self focusedCellOffsetDeltaInAttributes:attributesList];
    self.scrollDifference       = [self calculateScrollDifference];
    
    // sticky header
    UICollectionViewLayoutAttributes *stickyHeader = [[self layoutAttributesForItemAtIndexPath:self.headerPath] copy];
    
    // apply stickyness to the attributes
    stickyHeader.zIndex = 1;
    stickyHeader.frame = (CGRect){ .origin.y = self.collectionView.contentOffset.y, .size = stickyHeader.frame.size };
    
    [self applySlidingOffsetForStickyHeader:stickyHeader];
    
    return attributesList.concat(@[stickyHeader]);
}

- (CGPoint)targetContentOffsetForProposedContentOffset:(CGPoint)proposedContentOffset
{
    // maintain top cell position by adjusting offset based on height change of cells above the focused cell
    CGPoint updatedOffset = CGPointOffset(self.collectionView.contentOffset, 0.0f, self.focusedCellOffsetDelta);
    
    return updatedOffset;
}

//
// Helpers
//

- (CGFloat)calculateScrollDifference
{
    if(!self.shouldModifyHeaderOffset || ![self.delegate collectionView:self.collectionView layout:self shouldSlideRedemptionHeaderAtIndexPath:self.headerPath]) {
        return self.scrollDifference;
    }

    const CGFloat scrollingSpeed = 8.0f;
    BOOL goingDown               = self.lastContentOffset > self.collectionView.contentOffset.y;
    CGFloat increment            = self.headerHeight / scrollingSpeed;
    self.lastContentOffset       = self.collectionView.contentOffset.y;
    
    if(goingDown) {
        // bound value to show entire header
        return self.scrollDifference < 0.0f ? self.scrollDifference + increment : 0.0f;
    }
    
    else {
        // bound value to just barely hide header
        return self.scrollDifference > -self.headerHeight ? self.scrollDifference - increment : -self.headerHeight;
    }
}

- (CGFloat)focusedCellOffsetDeltaInAttributes:(NSArray *)attributes
{
    // the amount by which the top cell can be off-screen and still be considered the top cell
    const CGFloat overlapThreshold = 40.0;
    
    // our focused cell is the top car class cell on screen
    EHICarClassCell *topCell = self.collectionView.visibleCells.select(EHICarClassCell.class).sortBy(^(UICollectionViewCell *cell) {
        return [self.collectionView indexPathForCell:cell].row;
    }).find(^(EHICollectionViewCell *cell) {
        return cell.frame.origin.y + overlapThreshold > self.collectionView.contentOffset.y;
    });
    
    // find matching attributes in new attributes list
    NSIndexPath *topIndexPath = [self.collectionView indexPathForCell:topCell];
    UICollectionViewLayoutAttributes *topAttributes = attributes.find(^(UICollectionViewLayoutAttributes *attrs) {
        return [attrs.indexPath isEqual:topIndexPath];
    });
    
    // calculate from current cell and attributes to be applied
    CGFloat offsetDelta = topAttributes.frame.origin.y - topCell.frame.origin.y;
    
    return offsetDelta;
}

# pragma mark - Accessors

- (NSIndexPath *)headerPath
{
    return [NSIndexPath indexPathForItem:0 inSection:0];
}

- (CGFloat)headerHeight
{
    CGFloat newHeight = [self.collectionView cellForItemAtIndexPath:self.headerPath].intrinsicContentSize.height;
    
    if(newHeight > _headerHeight) {
        _headerHeight = newHeight;
    }
    
    return _headerHeight;
}

- (BOOL)shouldModifyHeaderOffset
{
    BOOL didScroll               = self.collectionView.contentOffset.y != self.lastContentOffset;
    BOOL hasContentBeneathHeader = self.collectionView.contentOffset.y > self.headerHeight;
    BOOL isOverscrollingBottom   = self.collectionView.contentSize.height - self.collectionView.contentOffset.y - self.collectionView.frame.size.height <= 0;
    
    return didScroll && hasContentBeneathHeader && !isOverscrollingBottom;
}

- (id<EHIClassSelectLayoutDelegate>)delegate
{
    return (id<EHIClassSelectLayoutDelegate>)self.collectionView.delegate;
}

# pragma mark - Sticky Header

- (void)applySlidingOffsetForStickyHeader:(UICollectionViewLayoutAttributes*)stickyHeader
{
    CGRect frame = stickyHeader.frame;
    
    BOOL isAtTop    = self.collectionView.contentOffset.y < self.headerHeight;
    BOOL isAtBottom = self.collectionView.contentOffset.y + self.collectionView.frame.size.height >= self.collectionView.contentSize.height;
    
    if(isAtTop) {
        frame.origin.y = self.collectionView.contentOffset.y;
    } else if(isAtBottom) {
        frame.origin.y = self.collectionView.contentOffset.y - self.headerHeight;
    } else {
        frame.origin.y  += self.scrollDifference + self.focusedCellOffsetDelta;
    }
    stickyHeader.frame = frame;
}

@end
