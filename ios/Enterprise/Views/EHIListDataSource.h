//
//  EHIListDataSource.h
//  Enterprise
//
//  Created by Ty Cobb on 1/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIListCell.h"

@protocol EHIListDataSource <NSObject> @optional

/**
 @brief The cell class to dequeue for this section element
 
 Cells of this type are dequeued by the collection view for every element in the list
 of @c models.
*/

@property (strong, nonatomic) Class<EHIListCell> klass;

/**
 @brief Determines whether cells in the section size themselves based on their model data
 
 A YES value will allow the collection to size each cell in the section to be sized based on its
 model data
 
 @note Cell must be layed out in a nib with the same name as the class
*/

@property (assign, nonatomic) BOOL isDynamicallySized;

/**
 @brief Optional layout metrics to override the class' standard metrics
 
 If these are not supplied, the dequeued cells will use their class-level metrics. Metrics
 allow you to customize the identifier, theme, etc.
*/

@property (strong, nonatomic) EHILayoutMetrics *metrics;

/**
 @brief The models backing this element.
 
 This determines the number of items in the section element. As cells are dequeued for
 the element, they are updated with the corresponding model from this list.
*/

@property (copy, nonatomic) NSArray *models;

/**
 @brief Computed property for interacting with a data source that has a single model.
 
 This is syntactic sugar for @c source.models = @[ model ]. Correspondingly, any existing
 models will be destroyed.
*/

@property (strong, nonatomic) id model;

@end
