//
//  EHICalendarDaysLayout.m
//  Enterprise
//
//  Created by Ty Cobb on 3/23/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICalendarDaysLayout.h"
#import "EHICalendarLayoutDelegate.h"
#import "EHICalendarMonthCell.h"
#import "EHICalendarDividerView.h"

@interface EHICalendarDaysLayout ()
@property (nonatomic, readonly) CGFloat rowHeight;
@property (nonatomic, readonly) id<EHICalendarLayoutDelegate> delegate;
@end

@implementation EHICalendarDaysLayout

- (void)awakeFromNib
{
    [super awakeFromNib];

    [self registerClass:[EHICalendarDividerView class] forDecorationViewOfKind:[EHICalendarDividerView kind]];
}

- (NSArray *)layoutAttributesForElementsInRect:(CGRect)rect
{
    NSArray *attributesList = [[super layoutAttributesForElementsInRect:rect] copy];
 
    // track the rows we've already added decorations for
    NSMutableIndexSet *monthRows = [NSMutableIndexSet new];
    for(UICollectionViewLayoutAttributes *attributes in attributesList) {
        [monthRows addIndex:[self.delegate collectionView:self.collectionView layout:self monthRowForDayIndexPath:attributes.indexPath]];
    }
 
    // map the rows indices into decoration attributes
    NSArray *decorations = monthRows.map(^(NSUInteger index) {
        NSIndexPath *indexPath = [NSIndexPath indexPathForItem:index inSection:0];
        return [self layoutAttributesForDecorationViewOfKind:[EHICalendarDividerView kind] atIndexPath:indexPath];
    });
    
    return attributesList.concat(decorations);
}

- (UICollectionViewLayoutAttributes *)layoutAttributesForDecorationViewOfKind:(NSString *)kind atIndexPath:(NSIndexPath *)indexPath
{
    UICollectionViewLayoutAttributes *attributes =
        [UICollectionViewLayoutAttributes layoutAttributesForDecorationViewOfKind:kind withIndexPath:indexPath];
   
    // center the divider on the row border
    CGRect frame = CGRectZero;
    frame.size.height = 1.0f;
    frame.size.width  = self.collectionView.bounds.size.width;
    frame.origin.y    = self.rowHeight * indexPath.item - round(frame.size.height / 2.0f);
    
    // update the attributes
    attributes.frame = frame;
    attributes.zIndex = 20;
    
    // don't show a divider on the first row
    attributes.hidden = indexPath.item == 0;

    return attributes;
}

# pragma mark - Snapping

- (CGPoint)targetContentOffsetForProposedContentOffset:(CGPoint)proposedOffset withScrollingVelocity:(CGPoint)velocity
{
    // if we've reached the bottom, then we don't need to do any snapping
    if(proposedOffset.y == self.collectionView.contentSize.height - self.collectionView.bounds.size.height) {
        return proposedOffset;
    }
   
    // otherwise, snap to the nearest row
    CGFloat height = self.rowHeight;
    CGFloat inset  = self.collectionView.contentInset.top;
 
    return (CGPoint) {
        .y = roundf((proposedOffset.y + inset) / height) * height - inset
    };
}

# pragma mark - Rows

- (CGFloat)rowHeight
{
    // sample any old cell and get its size
    CGSize size = [self.delegate collectionView:self.collectionView layout:self sizeForItemAtIndexPath:[NSIndexPath indexPathForRow:0 inSection:0]];
    return size.height + self.minimumLineSpacing;
}

# pragma mark - Accessors

- (id<EHICalendarLayoutDelegate>)delegate
{
    return (id<EHICalendarLayoutDelegate>)self.collectionView.delegate;
}

@end
