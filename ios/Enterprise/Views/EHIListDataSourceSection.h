//
//  EHIListDataSourceSection.h
//  Enterprise
//
//  Created by Ty Cobb on 1/21/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIListDataSource.h"

@protocol EHIListDataSourceSection <EHIListDataSource>

/** @c The section element backing this section's header */
@property (nonatomic, readonly) id<EHIListDataSource> header;
/** @c The section element backing this section's footer */
@property (nonatomic, readonly) id<EHIListDataSource> footer;

/**
 @brief Returns the data source for the parameterized @c kind
 The @c kind should be the supplementary view kind corresponding to this data source
 @param kind A string kind for the supplementary view
 */

- (id<EHIListDataSource>)objectForKeyedSubscript:(NSString *)kind;

@end

@interface EHIListDataSourceSection : NSObject <EHIListDataSourceSection>

/** The index of this section in the collection view */
@property (assign, nonatomic, readonly) NSInteger index;

@end
