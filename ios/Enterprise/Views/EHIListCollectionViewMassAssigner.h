//
//  EHIListCollectionViewMassAssigner.h
//  Enterprise
//
//  Created by Ty Cobb on 2/25/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIListCollectionViewSectionAdapter.h"
#import "EHIListDataSource.h"

@interface EHIListCollectionViewMassAssigner : NSObject <EHIListDataSource>

/** Initializes a mass assigner for the section element @c kind; if @c nil, assigns to the section's primary element */
- (instancetype)initWithElementKind:(NSString *)kind adapter:(id<EHIListCollectionViewSectionAdapter>)adapter;

@end
