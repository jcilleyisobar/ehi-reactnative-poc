//
//  EHICalendarMonthsLayout.m
//  Enterprise
//
//  Created by Ty Cobb on 3/24/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICalendarMonthsLayout.h"
#import "EHICalendarLayoutDelegate.h"

@interface EHICalendarMonthsLayout ()
@property (strong, nonatomic) NSArray *attributes;
@property (nonatomic, readonly) CGFloat parallaxThreshold;
@property (nonatomic, readonly) id<EHICalendarLayoutDelegate> delegate;
@end

@implementation EHICalendarMonthsLayout

# pragma mark - UICollectionViewLayout

- (CGSize)collectionViewContentSize
{
    UICollectionViewLayoutAttributes *attributes = [self.attributes lastObject];
    
    return (CGSize) {
        .width  = self.collectionView.bounds.size.width,
        .height = CGRectGetMaxY(attributes.frame)
    };
}

- (void)prepareLayout
{
    [super prepareLayout];
    
    [self applyParallaxToNearestMonths];
}

- (NSArray *)layoutAttributesForElementsInRect:(CGRect)rect
{
    return self.attributes;
}

# pragma mark - Months

- (NSArray *)attributes
{
    if(_attributes) {
        return _attributes;
    }
    
    // get the number of months
    NSInteger months = [self.collectionView.dataSource collectionView:self.collectionView numberOfItemsInSection:0];
   
    // generate fixed attributes for all the months
    _attributes = @(0).upTo(months - 1).map(^(NSNumber *month) {
        return [self attributesForMonthHeaderWithItem:month.integerValue];
    });
    
    return _attributes;
}

- (UICollectionViewLayoutAttributes *)attributesForMonthHeaderWithItem:(NSInteger)item
{
    // generate the base attributes
    NSIndexPath *indexPath = [NSIndexPath indexPathForItem:item inSection:0];
    UICollectionViewLayoutAttributes *attributes =
        [UICollectionViewLayoutAttributes layoutAttributesForCellWithIndexPath:indexPath];
    
    // pull the size from our delegate
    CGRect frame = (CGRect){
        .size = [self.delegate collectionView:self.collectionView layout:self sizeForItemAtIndexPath:indexPath]
    };
   
    // position the item based on the row returned by our delegate
    NSInteger row  = [self.delegate collectionView:self.collectionView layout:self monthRowForMonthIndexPath:indexPath];
    frame.origin.x = [self.delegate collectionView:self.collectionView layout:self insetForSectionAtIndex:0].left;
    frame.origin.y = [self rowHeight] * row;
    
    // update the attributes
    attributes.frame = frame;
    
    return attributes;
}


# pragma mark - Parallax

- (void)applyParallaxToNearestMonths
{
    // we're going to parallax months as they approach the sticky header
    UICollectionViewLayoutAttributes *pinnedMonth = nil;
    UICollectionViewLayoutAttributes *approachingMonth = nil;
    
    // find the attributes that we're parallaxing
    [self findPinnedAttributes:&pinnedMonth approachingAttributes:&approachingMonth];
    
    // capture relevant offsets locally
    CGPoint contentOffset     = self.collectionView.contentOffset;
    CGFloat containerHeight   = [self.delegate collectionView:self.collectionView containerHeightForStickyHeaderOfLayout:self];
    CGFloat parallaxThreshold = self.parallaxThreshold;
    
    // pre-calculate the final position for the pinned header
    CGFloat stickyOffset   = [self.delegate collectionView:self.collectionView offsetForStickyHeaderOfLayout:self];
    CGFloat stickyPosition = contentOffset.y + stickyOffset;
    
    // track how much the approaching month overlaps the container so we can push the
    // pinned month away correspondingly
    CGFloat containerOverlap = 0.0f;
    
    // first parallax the approaching month, if possible
    if(approachingMonth) {
        // there's probably a way to calculate this so the month always lands on the header properly
        const CGFloat parallaxSpeed = 1.65f;
        // calculate the delta to the parallax threshold
        CGFloat delta = approachingMonth.frame.origin.y - (contentOffset.y + parallaxThreshold);
        // scale it so this month appears to scroll a bit faster, but don't let it pass the sticking point
        delta = MAX(delta * parallaxSpeed, stickyPosition - approachingMonth.frame.origin.y);
        
        approachingMonth.transform = CGAffineTransformMakeTranslation(0.0f, delta);
        containerOverlap = MAX(contentOffset.y + containerHeight - approachingMonth.frame.origin.y, 0.0f);
    }
    
    // offset the pinned attributes as well
    CGFloat delta = stickyPosition - pinnedMonth.frame.origin.y - containerOverlap;
    pinnedMonth.transform = CGAffineTransformMakeTranslation(0.0f, delta);
    pinnedMonth.alpha     = 1.0f - (containerOverlap / pinnedMonth.bounds.size.height);
}

//
// Helpers
//

- (void)findPinnedAttributes:(UICollectionViewLayoutAttributes **)pinned approachingAttributes:(UICollectionViewLayoutAttributes **)approaching
{
    CGPoint offset = self.collectionView.contentOffset;
    CGFloat parallaxThreshold = self.parallaxThreshold;
    
    // find the attributes to float
    *pinned = self.attributes.firstObject;
    
    // flags to track when we find the attributes, since we need to reset all the transforms regardless
    BOOL foundAttributes = NO;
    
    for(UICollectionViewLayoutAttributes *attributes in self.attributes) {
        // we want to reset the transform of all the attributes
        attributes.transform = CGAffineTransformIdentity;
        
        // if this month is on screen, mark that we found the attributes
        if(attributes.frame.origin.y > offset.y) {
            foundAttributes = YES;
            
            // these attributes might be approaching; check if they're near enough to start parallaxing
            BOOL isPinned = attributes == *pinned;
            if(!isPinned && attributes.frame.origin.y < offset.y + parallaxThreshold) {
                *approaching = attributes;
            }
        }
        // otherwise, this month might be the last non on-screen month so record it
        else if(!foundAttributes) {
            *pinned = attributes;
        }
    }
}

- (CGFloat)parallaxThreshold
{
    return self.collectionView.contentInset.top + self.rowHeight;
}

# pragma mark - Rows

- (CGFloat)rowHeight
{
    // sample any old cell and get its size
    CGSize size = [self.delegate collectionView:self.collectionView layout:self sizeForItemAtIndexPath:[NSIndexPath indexPathForRow:0 inSection:0]];
    return size.height + [self.delegate collectionView:self.collectionView layout:self minimumLineSpacingForSectionAtIndex:0];
}

# pragma mark - Accessors

- (id<EHICalendarLayoutDelegate>)delegate
{
    return (id<EHICalendarLayoutDelegate>)self.collectionView.delegate;
}

@end
