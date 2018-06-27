//
//  EHIListCollectionViewLayout.m
//  Enterprise
//
//  Created by Ty Cobb on 1/27/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIListCollectionViewLayout.h"
#import "EHIListCollectionViewLayoutDelegate.h"
#import "EHIListCell.h"

@interface EHIListCollectionViewLayout ()
@property (assign, nonatomic) BOOL hasUpdates;
@property (strong, nonatomic) NSArray *moveUpdates;
@property (strong, nonatomic) NSArray *cachedAttributes;
@property (strong, nonatomic) NSArray *transitioningAttributes;
@property (strong, nonatomic) NSIndexPath *firstInsertPath;
@property (strong, nonatomic) NSIndexPath *firstDeletePath;
@property (nonatomic, readonly) id<EHIListCollectionViewLayoutDelegate> delegate;
@end

@implementation EHIListCollectionViewLayout

# pragma mark - UICollectionViewLayout

- (void)prepareForCollectionViewUpdates:(NSArray *)updates
{
    [super prepareForCollectionViewUpdates:updates];
    
    // mark if we have any updates to run
    self.hasUpdates = updates.count != 0;
   
    // capture the move updates so we can animate them correctly
    self.moveUpdates = updates.select(^(UICollectionViewUpdateItem *update) {
        return update.updateAction == UICollectionUpdateActionMove;
    });
}

- (void)finalizeCollectionViewUpdates
{
    [super finalizeCollectionViewUpdates];
    
    // store the transitioning attribtues as the cached attributes when the updates complete
    if(self.hasUpdates) {
        self.cachedAttributes = self.transitioningAttributes;
        self.transitioningAttributes = nil;
    }
}

- (NSArray *)layoutAttributesForElementsInRect:(CGRect)rect
{
    NSArray *attributesList = [[super layoutAttributesForElementsInRect:rect] copy];
    
    // properly inset the suppplementary views
    [self applySectionInsetsToSupplementaryViewsForAttributes:attributesList];
 
    // store the attributes in our transitioning list if we're running batched updates, otherwise
    // just cache them immediately for future dismissal transitions
    if(self.hasUpdates) {
        self.transitioningAttributes = attributesList;
    } else {
        self.cachedAttributes = attributesList;
    }
    
    return attributesList;
}

# pragma mark - Animations

- (UICollectionViewLayoutAttributes *)initialLayoutAttributesForAppearingItemAtIndexPath:(NSIndexPath *)indexPath
{
    // if we don've have updates, then just run the default behavior (could be a layout change or an empty performBatchUpdates:)
    if(!self.hasUpdates) {
        return [super initialLayoutAttributesForAppearingItemAtIndexPath:indexPath];
    }
    
    // if we have a move update for this path, use that
    UICollectionViewUpdateItem *update = [self moveUpdateForPath:indexPath beforeUpdate:NO];
    if(update) {
        return [[self layoutAttributesForItemAtIndexPath:update.indexPathBeforeUpdate] copy];
    }
    
    // otherwise, animate this in from some distance below the cell
    UICollectionViewLayoutAttributes *attributes = [[self layoutAttributesForItemAtIndexPath:indexPath] copy];
    
    // update the attributes
    attributes.frame = CGRectOffset(attributes.frame, 0.0f, 40.0f);
    attributes.alpha = 0.0f;
    
    return attributes;
}

- (UICollectionViewLayoutAttributes *)initialLayoutAttributesForAppearingSupplementaryElementOfKind:(NSString *)kind atIndexPath:(NSIndexPath *)indexPath
{
    if(!self.hasUpdates) {
        return [super initialLayoutAttributesForAppearingSupplementaryElementOfKind:kind atIndexPath:indexPath];
    }

    // find the attributes for this supplementary view
    UICollectionViewLayoutAttributes *attributes = [self layoutAttributesForSupplementaryViewOfKind:kind atIndexPath:indexPath];
    
    // update the attributes with the pre-translation
    attributes.frame = CGRectOffset(attributes.frame, 0.0f, 40.0f);
    attributes.alpha = 0.0f;
    
    return attributes;
}

- (UICollectionViewLayoutAttributes *)finalLayoutAttributesForDisappearingItemAtIndexPath:(NSIndexPath *)indexPath
{
    // if we don've have updates, then just run the default behavior (could be a layout change or an empty performBatchUpdates:)
    if(!self.hasUpdates) {
        return [super finalLayoutAttributesForDisappearingItemAtIndexPath:indexPath];
    }
    
    // if we have a move update for this path, use that
    UICollectionViewUpdateItem *update = [self moveUpdateForPath:indexPath beforeUpdate:YES];
    if(update) {
        return [[self layoutAttributesForItemAtIndexPath:update.indexPathAfterUpdate] copy];
    }
    
    // check to see if we have cached attributes for this position
    UICollectionViewLayoutAttributes *attributes = [(self.cachedAttributes ?: @[]).find(^(UICollectionViewLayoutAttributes *attributes) {
        return [attributes.indexPath isEqual:indexPath];
    }) copy];
    
    // if not, then this cell is probably off-screen so run the default behavior
    if(!attributes) {
        return [super finalLayoutAttributesForDisappearingItemAtIndexPath:indexPath];
    }
    
    // otherwise, animate this back into z-space
    CATransform3D transform = CATransform3DIdentity;
    transform.m34 = -1.0f / 1000.0f;
    transform = CATransform3DTranslate(transform, 0.0f, 0.0f, -40.0f);
    
    attributes.alpha = 0.0f;
    attributes.transform3D = transform;
    
    return attributes;
}

//
// Helpers
//

- (UICollectionViewUpdateItem *)moveUpdateForPath:(NSIndexPath *)indexPath beforeUpdate:(BOOL)beforeUpdate
{
    return self.moveUpdates ? self.moveUpdates.find(^(UICollectionViewUpdateItem *update) {
        return beforeUpdate ? [update.indexPathBeforeUpdate isEqual:indexPath] : [update.indexPathAfterUpdate isEqual:indexPath];
    }) : nil;
}

# pragma mark - Attributes Mutation

- (void)applySectionInsetsToSupplementaryViewsForAttributes:(NSArray *)attributesList
{
    if(![self.delegate respondsToSelector:@selector(collectionView:layout:insetForSectionAtIndex:)]) {
        return;
    }
  
    for(UICollectionViewLayoutAttributes *attributes in attributesList) {
        if(attributes.representedElementCategory == UICollectionElementCategorySupplementaryView) {
            UIEdgeInsets insets = [self.delegate collectionView:self.collectionView layout:self insetForSectionAtIndex:attributes.indexPath.section];
            attributes.frame = CGRectOffset(attributes.frame, insets.left - insets.right, insets.top - insets.bottom);
        }
    }
}

# pragma mark - Accessors

- (id<EHIListCollectionViewLayoutDelegate>)delegate
{
    return (id<EHIListCollectionViewLayoutDelegate>)self.collectionView.delegate;
}

@end
