//
//  EHIListCollectionView.h
//  Enterprise
//
//  Created by Ty Cobb on 1/21/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIListCollectionViewSections.h"
#import "EHICollectionViewCell.h"
#import "EHICollectionReusableView.h"
#import "EHIRefreshControlViewModel.h"

@protocol EHIListCollectionViewDelegate;

@interface EHIListCollectionView : UICollectionView

/** Computed property for the collection view's first section */
@property (nonatomic, readonly) EHIListDataSourceSection *section;
/** An interface for accessing the collection view's sections */
@property (nonatomic, readonly) EHIListCollectionViewSections *sections;
/** @c YES if the touches outside the collection view's cells should be ignored */
@property (assign, nonatomic) BOOL ignoreTouchesOutsideContent;
/** @c YES if the collection view reselects its items after reloading */
@property (assign, nonatomic) BOOL preservesSelectionOnReload;
/** Computed property which returns the list collection view's custom delegate */
@property (nonatomic, readonly) id<EHIListCollectionViewDelegate> customDelegate;

/** The index of the refresh control section, if any */
@property (assign, nonatomic) NSInteger refreshControlSection;
/** @c YES if the refresh control is enabled; only valid if @c refreshControlSection is set */
@property (nonatomic, readonly) EHIRefreshControlViewModel *ehiRefreshControl;

/** Optionally animates the updates. If not @c animated, the updates aren't batched and are run via a reload */
- (void)performAnimated:(BOOL)animated batchUpdates:(void (^)(void))updates completion:(void (^)(BOOL))completion;
/** Flushes any invalid sections, reloading/merging their models. */
- (void)flushWithCompletion:(void(^)(void))completion;
/** Destroys any lingering data, allow the collection view to be re-used */
- (void)prepareForReuse;

@end

@protocol EHIListCollectionViewDelegate <UICollectionViewDelegateFlowLayout> @optional

/** Called with the cell just before @c collectionView:cellForItemAtIndexPath: completes */
- (void)collectionView:(UICollectionView *)collectionView didDequeueCell:(EHICollectionViewCell *)cell atIndexPath:(NSIndexPath *)indexPath;
/** Called with the view just before @c collectionView:viewForSupplementaryElementOfKind:atIndexPath: completes */
- (void)collectionView:(UICollectionView *)collectionView didDequeueReusableView:(EHICollectionReusableView *)reusableView kind:(NSString *)kind atIndexPath:(NSIndexPath *)indexPath;
/** Called after perform the automatic sizing pass with the calculated size, allowing the view an opportunity to modify its value */
- (CGSize)collectionView:(UICollectionView *)collectionView actualSizeForItemWithProposedSize:(CGSize)size atIndexPath:(NSIndexPath *)indexPath;
/** Called after the collection view reloads its data or finishes performing updates */
- (void)collectionViewDidReload:(UICollectionView *)colleciton;
/** Called if the refresh control is enabled, and the control was complete when the user stopped dragging */
- (void)collectionViewDidPullToRefresh:(UICollectionView *)collectionView;

@end

