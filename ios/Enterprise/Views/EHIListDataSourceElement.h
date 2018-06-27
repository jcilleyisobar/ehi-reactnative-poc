//
//  EHIListDataSourceElement.h
//  Enterprise
//
//  Created by Ty Cobb on 1/21/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIListDataSource.h"

@class EHIListDataSourceSection;

@interface EHIListDataSourceElement : NSObject <EHIListDataSource>

/** The element kind; @c nil if the primary element */
@property (nonatomic, readonly) NSString *kind;
/** @c YES if this the the section's primary element */
@property (nonatomic, readonly) BOOL isPrimary;

/** Responds to the element being invalidated */
@property (weak, nonatomic) EHIListDataSourceSection *section;
/** Models before most recent update. Only valid while an element is invalidated */
@property (copy, nonatomic) NSArray *previousModels;
/** @c YES if the element is invalidated */
@property (assign, nonatomic) BOOL isInvalid;

/** Create an element for the specified kind */
- (instancetype)initWithKind:(NSString *)kind;

@end
