//
//  EHIListMergeFacilitator.h
//  Enterprise
//
//  Created by Ty Cobb on 1/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#ifdef DEBUG
#define EHIMergeFacilitatorLogging 0
#else
#define EHIMergeFacilitatorLogging 0
#endif

@interface EHIListMergeFaciliator : NSObject

/**
 @brief Diffs two sets of models to determine how to update the collection view
 
 The @c buffer parameter should be populated after this call, and can be passed to
 @c -resolveUpdates:againstCollectionView: at a later time to finish the update.
 
 @param updates      The update buffer
 @param sources      The models to diff from
 @param destinations The new list of models
*/

+ (void)processUpdates:(void *)updates fromModels:(NSArray *)sources toModels:(NSArray *)destinations;

/**
 @brief Executes a sequence of updates on the collection view
 
 This method should be called inside the collection view's @c -performBatchUpdates:completion.
 
 @param updates        The updates to run
 @param section        THe section to update
 @param collectionView The collection view to execute the updates on
*/

+ (void)resolveUpdates:(void *)updates inSection:(NSInteger)section againstCollectionView:(UICollectionView *)collectionView;

/** Allocates buffer for storing merge updates */
extern void * ehi_createUpdatesBuffer(NSUInteger count);

/** Frees merge update buffer */
extern void ehi_deleteUpdatesBuffer(void *buffer);

@end
