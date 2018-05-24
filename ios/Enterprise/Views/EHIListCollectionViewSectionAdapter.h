//
//  EHIListCollectionViewSectionAdapter.h
//  Enterprise
//
//  Created by Ty Cobb on 2/25/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIListDataSourceSection.h"

@protocol EHIListCollectionViewSectionAdapter <NSFastEnumeration>

/**
 @brief Acceses the section at this index
 
 Sections embed information about what cell class a particular section should display,
 what models drive the section, etc.
 
 Sections are created on demand, so this method cannot return nil.
 
 @param index The index of the section
 
 @return The section at this index
*/

- (EHIListDataSourceSection *)objectAtIndexedSubscript:(NSInteger)index;

@end
