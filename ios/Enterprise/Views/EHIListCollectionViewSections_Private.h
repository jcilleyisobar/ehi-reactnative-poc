//
//  EHIListCollectionViewSections_Private.h
//  Enterprise
//
//  Created by Ty Cobb on 2/25/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIListCollectionViewSections.h"
#import "EHIListDataSourceSection_Private.h"

@interface EHIListCollectionViewSections ()

/** The delegate is assigned to any newly created sections */
@property (weak, nonatomic) id<EHIListDataSourceSectionDelegate> sectionDelegate;
/** Computed property for the number of visible sections */
@property (nonatomic, readonly) NSInteger count;

/** Returns the section corresponding to the given index path; does not lazy-load sections */
- (EHIListDataSourceSection *)sectionForIndexPath:(NSIndexPath *)indexPath;
/** Removes the section for the given index */
- (void)removeSection:(NSInteger)index;
/** Resets the data source, throwing away all sections */
- (void)reset;

@end
