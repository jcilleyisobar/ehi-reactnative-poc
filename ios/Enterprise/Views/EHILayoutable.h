//
//  EHILayoutable.h
//  Enterprise
//
//  Created by Ty Cobb on 1/26/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILayoutMetrics.h"

@protocol EHILayoutable <NSObject>

/**
 @brief Creates the class' layout metrics
 
 When metrics for a class are accessed via @c +metricsForClass:, if the metrics do not yet exist
 this method is called to generate them. An exception will be thrown if this method returns @c nil.
 
 @return The new layout metrics for this class
 */

+ (EHILayoutMetrics *)defaultMetrics;

@optional

/**
 @brief Pass-through to @c +[EHILayoutMetrics metricsForclass:]
 
 This method is marked @c optional to avoid having to re-implement it, but its implementation is
 provided automatically for any classes that conform to layoutable.
 
 @return The shared metrics for this class
*/

+ (EHILayoutMetrics *)metrics;

@end
