//
//  EHIListCollectionViewSections.h
//  Enterprise
//
//  Created by Ty Cobb on 2/25/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIListCollectionViewSectionAdapter.h"

@interface EHIListCollectionViewSections : NSObject <EHIListCollectionViewSectionAdapter, EHIListDataSourceSection>

/**
 @brief Batch creates a number of sections in the collection view
 
 The dictionary should contain a map of section index -> cell class. The section
 won't be rendered unless the @c models property is also populated.
 
 @param The map of klasses for each section
*/

- (void)construct:(NSDictionary *)klassMap;

@end
