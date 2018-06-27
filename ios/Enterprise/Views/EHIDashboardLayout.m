//
//  EHIDashboardLayout.m
//  Enterprise
//
//  Created by mplace on 5/19/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIDashboardLayout.h"
#import "EHIDashboardLayoutDelegate.h"
#import "EHIDashboardLayoutAttributes.h"
#import "EHIDashboardSections.h"

@interface EHIDashboardLayout ()
@property (nonatomic, readonly) NSIndexPath *loyaltyPath;
@property (nonatomic, readonly) NSIndexPath *searchPath;
@property (nonatomic, readonly) CGFloat quickstartTop;
@property (nonatomic, readonly) CGFloat quickstartDelta;
@property (nonatomic, readonly) id<EHIDashboardLayoutDelegate> delegate;
@end

@implementation EHIDashboardLayout

- (BOOL)shouldInvalidateLayoutForBoundsChange:(CGRect)newBounds
{
    return YES;
}

- (void)prepareLayout
{
    [super prepareLayout];
  
    // inset the content by that delta so that it can scroll all the way up
    UIEdgeInsets contentInset = self.collectionView.contentInset;
    contentInset.bottom = self.quickstartDelta;
    self.collectionView.contentInset = contentInset;
}

- (NSArray *)layoutAttributesForElementsInRect:(CGRect)rect
{
    NSArray *attributesList = [[super layoutAttributesForElementsInRect:rect] copy];

    // lazily modify cells that already exist
    for(UICollectionViewLayoutAttributes *attributes in attributesList) {
        if(attributes.indexPath.section == EHIDashboardSectionHero) {
            [self applyParallaxToAttributes:[attributes copy]];
        }
    }
    
    NSArray *stickyItems = @[
        [[self layoutAttributesForItemAtIndexPath:self.searchPath] copy],
        [[self layoutAttributesForItemAtIndexPath:self.loyaltyPath] copy]
    ];
    
    // apply sticky header behavior for our sticky cells
    [self applyStickynessToAttributesList:stickyItems];
   
    return attributesList.concat(stickyItems);
}

- (CGPoint)targetContentOffsetForProposedContentOffset:(CGPoint)proposedOffset withScrollingVelocity:(CGPoint)velocity
{
    // validate that we should snap at all first
    BOOL shouldSnap = [self.delegate collectionView:self.collectionView layout:self shouldSnapWithProposedOffset:proposedOffset];
    if(!shouldSnap) {
        return proposedOffset;
    }
   
    CGPoint quickstartOffset = (CGPoint){ .y = self.quickstartTop };
    CGPoint currentOffset    = self.collectionView.contentOffset;
    CGPoint snappingOffset   = proposedOffset;
    
    // if we're scrolling out of quickstart or we're in the gutter, snap to the top
    if(currentOffset.y < 0.0f || (velocity.y < 0.0f && proposedOffset.y < quickstartOffset.y)) {
        snappingOffset = CGPointZero;
    }
    // snap to the quickstart if we're scrolling towards it and we haven't gotten there yet
    if(velocity.y >= 0.0f && proposedOffset.y < quickstartOffset.y) {
        snappingOffset = quickstartOffset;
    }
  
    // notify our delegate if we're not using the proposed offset
    if(!CGPointEqualToPoint(snappingOffset, proposedOffset)) {
        [self.delegate collectionView:self.collectionView layout:self willSnapToOffset:snappingOffset];
    }
    
    return snappingOffset;
}

# pragma mark - Parallax

- (void)applyParallaxToAttributes:(UICollectionViewLayoutAttributes *)attributes
{
    CGPoint offset = self.collectionView.contentOffset;
    
    // if we're not scrolling beyond the top
    if(offset.y > 0.0f) {
        // parallax the attributes by the offset scaled by some speed
        const CGFloat parllaxSpeed = 0.3f;
        attributes.frame = CGRectOffset(attributes.frame, 0.0f, offset.y * parllaxSpeed);
    }
}

# pragma mark - Sticky

- (void)applyStickynessToAttributesList:(NSArray *)attributesList
{
    // elevate sticky cells above the rest of the content
    for(EHIDashboardLayoutAttributes *attributes in attributesList) {
        attributes.zIndex = attributes.indexPath == self.searchPath ? 1000 : 100;
    }
   
    EHIDashboardLayoutAttributes *previousAttributes = nil;
    
    for(EHIDashboardLayoutAttributes *attributes in attributesList) {
        // check the distance to the current content offset, and record it on the attributes
        CGFloat stickyOffset = self.collectionView.contentOffset.y - attributes.frame.origin.y;
        attributes.stickyOffset = stickyOffset;
        
         // if this cell is above the content offset, it needs to stick
        if(stickyOffset > 0.0f) {
            // update the attributes frame so it sticks to the top
            CGFloat position = self.collectionView.bounds.origin.y;
            
            // check overlap with the next header
            CGFloat overlap = 0.0f;
            if(previousAttributes) {
                overlap = MAX(position + attributes.size.height - previousAttributes.frame.origin.y, 0.0f);
            }
           
            // update the attributes of the sticky header, then terminate
            CGRect frame   = attributes.frame;
            frame.origin.y = position - overlap;
            attributes.frame = frame;
            break;
        }
       
        // store these attributes in case they overlap the next attributes
        previousAttributes = attributes;
    }
}

# pragma mark - Attributes

+ (Class)layoutAttributesClass
{
    return [EHIDashboardLayoutAttributes class];
}

# pragma mark - Accessors

- (NSIndexPath *)loyaltyPath
{
    return [NSIndexPath indexPathForItem:0 inSection:EHIDashboardSectionHeader];
}

- (NSIndexPath *)searchPath
{
    return [NSIndexPath indexPathForItem:0 inSection:EHIDashboardSectionSearch];
}

- (CGFloat)quickstartTop
{
    // get the y-position of the search cell
    return [self layoutAttributesForItemAtIndexPath:self.searchPath].frame.origin.y;
}

- (CGFloat)quickstartDelta
{
    // check the delta between the height of the quickstart section and the collection view height
    CGFloat quickstartHeight = self.collectionViewContentSize.height - self.quickstartTop;
    CGFloat quickstartDelta  = MAX(self.collectionView.bounds.size.height - quickstartHeight, 0.0f);
    
    return quickstartDelta;
}

- (id<EHIDashboardLayoutDelegate>)delegate
{
    return (id<EHIDashboardLayoutDelegate>)self.collectionView.delegate;
}

@end
