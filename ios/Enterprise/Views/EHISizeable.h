//
//  EHISizeable.h
//  Enterprise
//
//  Created by Ty Cobb on 7/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@protocol EHISizeable <NSObject>

/**
 @brief Calculates the cell's size using the its base metrics
 
 Subclasses should not override this method, and should implement @c +sizeForContainerSize:metrics:
 instead to perform custom sizing.
 
 @param size The size of the container
 
 @return An expected size for this cell
 */

+ (CGSize)sizeForContainerSize:(CGSize)size;

/**
 @brief Calculates the cell's expected size
 
 Calles can pass in a custom @c EHILayoutMetrics instance to override the cell's default layout
 behavior. The default implementations returns the result of calling @c -sizeForContainerSize: on
 the @c metrics.
 
 @param size    The size of the container
 @param metrics The metrics to apply to the container
 
 @return An expected size for this cell
 */

+ (CGSize)sizeForContainerSize:(CGSize)size metrics:(EHILayoutMetrics *)metrics;

/**
 @brief Calculates the cell's expected size using a dynamic model driven height
 
 Uses the model to determine a dynamic height for each cell. The resulting height is used
 to update the metrics and passed along to @c +sizeForContainerSize:metrics: to provide
 container based width.
 
 @param size    The size of the container
 @param metrics The metrics to apply to the container
 @param model   The model that will be used to lay out the cell
 
 @return An expected size for this cell
 */

+ (CGSize)dynamicSizeForContainerSize:(CGSize)size metrics:(EHILayoutMetrics *)metrics model:(id)model;

@end
