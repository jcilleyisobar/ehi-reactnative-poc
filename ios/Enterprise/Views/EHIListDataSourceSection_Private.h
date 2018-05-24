//
//  EHIListDataSourceSection_Private.h
//  Enterprise
//
//  Created by Ty Cobb on 1/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIListDataSourceSection.h"
#import "EHIListDataSourceElement.h"

@protocol EHIListDataSourceSectionDelegate;

@interface EHIListDataSourceSection () <EHIListDataSource>

/** Responds to the section being invalidated */
@property (weak, nonatomic) id<EHIListDataSourceSectionDelegate> delegate;
/** The element backing the section's cells */
@property (strong, nonatomic) EHIListDataSourceElement *primary;
/** The current map of elements */
@property (strong, nonatomic) NSMutableDictionary *elements;

/** Initializes a new section for the given index */
- (instancetype)initWithIndex:(NSInteger)index;

/** Notifies the delegate that the element was invalidated */
- (void)didInvalidateElement:(EHIListDataSourceElement *)element;

@end

@protocol EHIListDataSourceSectionDelegate <NSObject>

/**
 @brief Called whenever the section requires a refresh

 @param section    The section that changed
 @param element    The element that changed
*/

- (void)section:(EHIListDataSourceSection *)section didInvalidateElement:(EHIListDataSourceElement *)element;

@end
