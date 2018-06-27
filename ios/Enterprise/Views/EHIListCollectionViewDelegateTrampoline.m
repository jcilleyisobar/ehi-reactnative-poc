//
//  EHIListCollectionViewDelegateTrampoline.m
//  Enterprise
//
//  Created by Ty Cobb on 1/29/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIListCollectionViewDelegateTrampoline.h"
#import "EHIRefreshControlCell.h"

@implementation EHIListCollectionViewDelegateTrampoline

- (instancetype)init
{
    if(self = [super init]) {
        _refreshControlSection = EHIRefreshControlSectionNil;
    }
    
    return self;
}

# pragma mark - EHIListCollectionViewDelegate (Sizing)

- (CGSize)collectionView:(EHIListCollectionView *)collectionView layout:(UICollectionViewLayout *)layout sizeForItemAtIndexPath:(NSIndexPath *)indexPath
{
    // start with a nil size
    CGSize result = EHILayoutSizeNil;
    
    // if the actual delegate wants to return a custom size, allow it to do so
    if([self.target respondsToSelector:_cmd]) {
        result = [self.target collectionView:collectionView layout:layout sizeForItemAtIndexPath:indexPath];
    }
   
    // if the size is invalid, apply automatic sizing rules
    if(CGSizeEqualToSize(result, EHILayoutSizeNil)) {
        EHIListDataSourceSection *section = collectionView.sections[indexPath.section];
      
        // get the corresponding model and the correct subclass
        id model = [section.models ehi_safelyAccess:indexPath.item];
        Class<EHIListCell> klass = [section.klass subclassForModel:model];
       
        // get the metrics, and note whether or not this cell is selected
        EHILayoutMetrics *metrics = section.metrics ?: [klass metrics];
        
        // if section is dynamically sized, pass in the model to determine a dynamic size based on content
        if(section.isDynamicallySized) {
            result = [klass dynamicSizeForContainerSize:collectionView.bounds.size metrics:metrics model:model];
        }
        // otherwise, just return the size for the cell with the given metrics
        else {
            result = [klass sizeForContainerSize:collectionView.bounds.size metrics:metrics];
        }
    }
  
    // allow the collection view to perform a final sizing pass
    if([self.target respondsToSelector:@selector(collectionView:actualSizeForItemWithProposedSize:atIndexPath:)]) {
        result = [self.target collectionView:collectionView actualSizeForItemWithProposedSize:result atIndexPath:indexPath];
    }
    
    return result;
}

- (CGSize)collectionView:(EHIListCollectionView *)collectionView layout:(UICollectionViewLayout *)layout referenceSizeForHeaderInSection:(NSInteger)section
{
    // start with a nil size
    CGSize result = EHILayoutSizeNil;
    
    // if the actual delegate wants to return a custom size, allow it to do so
    if([self.target respondsToSelector:_cmd]) {
        result = [self.target collectionView:collectionView layout:layout referenceSizeForHeaderInSection:section];
    }
    
    // if the size is invalid, apply automatic sizing rules
    if(CGSizeEqualToSize(result, EHILayoutSizeNil)) {
        id<EHIListDataSource> header = collectionView.sections[section].header;
        result = [self collectionView:collectionView sizeForAccessory:header inSection:section];
    }
    
    return result;
}

- (CGSize)collectionView:(EHIListCollectionView *)collectionView layout:(UICollectionViewLayout *)layout referenceSizeForFooterInSection:(NSInteger)section
{
    // start with a nil size
    CGSize result = EHILayoutSizeNil;
    
    // if the actual delegate wants to return a custom size, allow it to do so
    if([self.target respondsToSelector:_cmd]) {
        result = [self.target collectionView:collectionView layout:layout referenceSizeForFooterInSection:section];
    }
    
    // if the size is invalid, apply automatic sizing rules
    if(CGSizeEqualToSize(result, EHILayoutSizeNil)) {
        id<EHIListDataSource> footer = collectionView.sections[section].footer;
        result = [self collectionView:collectionView sizeForAccessory:footer inSection:section];
    }
    
    return result;
}

- (UIEdgeInsets)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)layout insetForSectionAtIndex:(NSInteger)section
{
    if(section == self.refreshControlSection) {
        // if the section is inset totally off-screen, flow layout will not render it, hence the 1.0f
        return (UIEdgeInsets){ .top = -[EHIRefreshControlCell metrics].fixedSize.height + 1.0f };
    }
    else if([self.target respondsToSelector:_cmd]) {
        return [self.target collectionView:collectionView layout:layout insetForSectionAtIndex:section];
    }
    
    return UIEdgeInsetsZero;
}

//
// Helpers
//

- (CGSize)collectionView:(EHIListCollectionView *)collectionView sizeForAccessory:(id<EHIListDataSource>)accessory inSection:(NSInteger)section
{
    if(!collectionView.sections[section].models.count || accessory.model == nil) {
        return CGSizeZero;
    } else if(accessory.isDynamicallySized) {
        return [accessory.klass dynamicSizeForContainerSize:collectionView.bounds.size metrics:accessory.metrics model:accessory.model];
    } else {
        return [accessory.klass sizeForContainerSize:collectionView.bounds.size metrics:accessory.metrics];
    }
}

# pragma mark - EHIListCollectionViewDelegate (Lifecycle)

- (void)collectionView:(UICollectionView *)collectionView didEndDisplayingCell:(EHICollectionViewCell *)cell forItemAtIndexPath:(NSIndexPath *)indexPath
{
    [cell didEndDisplaying];
}

- (void)collectionView:(UICollectionView *)collectionView didEndDisplayingSupplementaryView:(EHICollectionReusableView *)view forElementOfKind:(NSString *)kind atIndexPath:(NSIndexPath *)indexPath
{
    [view didEndDisplaying];
}

# pragma mark - UIScrollViewDelegate

- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    // call the target first if possible
    if([self.target respondsToSelector:_cmd]) {
        [self.target scrollViewDidScroll:scrollView];
    }
  
    // update the refresh control percent complete if it exists
    if(self.refreshControl) {
        CGFloat offset = -MIN(scrollView.contentOffset.y, 0.0f);
        CGFloat length = [EHIRefreshControlCell metrics].fixedSize.height;
        
        EHIFloatRange progressRange = (EHIFloatRange) {
            .location = length * 0.5f,
            .length   = length * 0.5f,
        };
        
        self.refreshControl.percentComplete = EHIFloatRangeNormalize(progressRange, offset);
    }
}

- (void)scrollViewDidEndDragging:(UICollectionView *)scrollView willDecelerate:(BOOL)decelerate
{
    // call the target first if possible
    if([self.target respondsToSelector:_cmd]) {
        [self.target scrollViewDidEndDragging:scrollView willDecelerate:decelerate];
    }
   
    // if the refresh control completed dispatch the delegate callback
    if(self.refreshControl.percentComplete == 1.0f) {
        if([self.target respondsToSelector:@selector(collectionViewDidPullToRefresh:)]) {
            [self.target collectionViewDidPullToRefresh:scrollView];
        }
    }
}

# pragma mark - Forwarding

- (BOOL)respondsToSelector:(SEL)selector
{
    // ensure we can forward messages onto the target
    return [super respondsToSelector:selector] || [self.target respondsToSelector:selector];
}

- (id)forwardingTargetForSelector:(SEL)selector
{
    // if we can trampoline to our target, then do so
    if([self.target respondsToSelector:selector]) {
        return self.target;
    }
    
    return [super forwardingTargetForSelector:selector];
}

@end
