//
//  EHIUpdatable.h
//  Enterprise
//
//  Created by Ty Cobb on 3/12/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILayoutMetrics.h"

@protocol EHIUpdatable <NSObject>

/**
 @brief Model binding hook for updating the cell's UI
 Pass-through to @c -updateWithModel:metrics:. See that method for full documentation.
*/

- (void)updateWithModel:(id)model;

/**
 @brief Model binding hook for for updating the cell's UI
 
 Subclasses should override this method to update the view with the values from the the
 paramaterized model and metrics.
 
 @param model   The model to update the interface with.
 @param metrics The layout metrics to update the interface with.
*/

- (void)updateWithModel:(id)model metrics:(EHILayoutMetrics *)metrics;

@end
