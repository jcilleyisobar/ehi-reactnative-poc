//
//  EHICollectionViewCell.h
//  Enterprise
//
//  Created by Ty Cobb on 1/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIListCell.h"
#import "EHIViewModel.h"

@interface EHICollectionViewCell : UICollectionViewCell <EHIListCell>

/** 
 @brief The view model backing this cell
 Subclasses should redeclare this property in their class extension to provide their custom subtype.
*/

@property (strong, nonatomic) EHIViewModel *viewModel;

/**
 @brief @c YES if cell is the last in its section
 
 The cell can use this information to lay itself out according to its position in the section. If 
 the only behavior desired is to hide a divider, the subclass should hook up the @c divider property.
*/

@property (assign, nonatomic) BOOL isLastInSection;

/**
 @brief Optional divider view
 
 If set, the divider view will be shown/hidden appropriately depending on whether or not this
 cell is the last in its section.
*/

@property (weak, nonatomic) IBOutlet UIView *divider;

/**
 @brief Setup all the accessibility identifiers
 Subclasses should properly fill the identifiers for itself and subviews, if necessary
 */

- (void)registerAccessibilityIdentifiers;

@end

@interface UICollectionViewCell (SubclassingHooks)

/**
 @brief Called when a new viewModel is about to be bound to this cell
 
 @param viewModel The view model to be attached to this cell
 */

- (void)prepareToBindViewModel:(id)viewModel;

/**
 @brief A list of all @c EHIView within this cell
 
 Cell sizing relies on permanently off screen reusable cells to calculate
 height. @c EHIView have their own view model lifecycle based on visibility
 so @EHICollectionViewCell requires mocking the visibility lifecycle of custom
 views in order to size correctly.
 */

- (NSArray *)customSubviews;

@end

@interface UICollectionView (EHICollectionViewCell)

/**
 @brief Dequeues a new cell for the given class and updates it with the model

 See @c -ehi_dequeueReusableCellWithClass:metrics:model:atIndexPath for full documentation.
*/

- (id)ehi_dequeueReusableCellWithClass:(Class<EHIListCell>)klass model:(id)model atIndexPath:(NSIndexPath *)indexPath;

/**
 @brief Dequeues a new cell for the given class and updates it with the model
 
 If no identifier is supplied via the metrics, the result of the cell class' @c +identifier 
 method is used instead. The dequeued cell is updated via @c -updateWithModel:metrics:.
 
 @param klass     The class of cell to dequeue
 @param metrics   Optional custom cell metrics
 @param model     The model to bind to the cell
 @param indexPath The index path for the cell
 
 @return A reusable instance of the paramaterized cell class
*/

- (id)ehi_dequeueReusableCellWithClass:(Class<EHIListCell>)klass metrics:(EHILayoutMetrics *)metrics model:(id)model atIndexPath:(NSIndexPath *)indexPath;

/**
 @brief Called by the dequeue helper after the cell class is determined
 @param klass The class about to be dequeued
*/

- (void)ehi_prepareToDequeueResuableCellWithClass:(Class<EHIListCell>)klass;

@end

@interface EHIDisposable (EHICollectionViewCell)

/** Accessor that returns a disposable's @c element as an @c EHICollectionViewCell */
- (EHICollectionViewCell *)cell;

@end