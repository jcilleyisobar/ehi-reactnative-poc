//
//  EHICollectionReusableView.h
//  Enterprise
//
//  Created by Ty Cobb on 1/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIListCell.h"
#import "EHIViewModel.h"

@interface EHICollectionReusableView : UICollectionReusableView <EHIListCell>

/**
 @brief The view model backing this cell
 Subclasses should redeclare this property in their class extension to provide their custom subtype.
 */

@property (strong, nonatomic) EHIViewModel *viewModel;

@end

@interface UICollectionView (EHICollectionReusableView)

/**
 @brief Dequeues a new supplementary view for the given class and updates it with the model
 
 See @c -ehi_dequeueReusableSupplementaryViewWithKind:class:identifier:model:atIndexPath: for
 complete documentation.
*/

- (id)ehi_dequeueReusableSupplementaryViewWithClass:(Class<EHIListCell>)klass model:(id)model atIndexPath:(NSIndexPath *)indexPath;

/**
 @brief Dequeues a new supplementary view for the given class and updates it with the model

 See @c -ehi_dequeueReusableSupplementaryViewWithKind:class:identifier:model:atIndexPath: for
 complete documentation.
*/

- (id)ehi_dequeueReusableSupplementaryViewWithKind:(NSString *)kind class:(Class<EHIListCell>)klass model:(id)model atIndexPath:(NSIndexPath *)indexPath;

/**
 @brief Dequeues a new supplementary view for the given class and updates it with the model

 If no identifier is supplied via the metrics, the result of the cell class' @c +identifier
 method is used instead. The dequeued view is updated via @c -updateWithModel:metrics:.
 
 @param kind      The kind for this supplementary view
 @param klass     The class of cell to dequeue
 @param metrics   Optional custom cell metrics
 @param model     The model to bind to the cell
 @param indexPath The index path for the cell
 
 @return A reusable instance of the paramaterized cell class
*/

- (id)ehi_dequeueReusableSupplementaryViewWithKind:(NSString *)kind class:(Class<EHIListCell>)klass metrics:(EHILayoutMetrics *)metrics model:(id)model atIndexPath:(NSIndexPath *)indexPath;


/**
 @brief Called by the dequeue helper after it the view class is determind
 
 @param kind  The kind for this supplementary view
 @param klass The class about to be dequeued
*/

- (void)ehi_prepareToDequeueResuableViewWithKind:(NSString *)kind class:(Class<EHIListCell>)klass;

@end

@interface EHIDisposable (EHICollectionReusableView)

/** Returns the receiver's @c element as a @c EHICollectionReusableView */
- (EHICollectionReusableView *)view;

@end
