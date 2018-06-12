//
//  EHIAnalyticsUpdater.h
//  Enterprise
//
//  Created by Ty Cobb on 5/29/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIAnalyticsContext.h"
#import "EHIAnalyticsKeys.h"
#import "EHIAnalyticsAttributes.h"

@protocol EHIAnalyticsUpdater <NSObject>

/**
 @brief Provides a common interface to update the analytics context

 The receiver should provide the context with any necessary information
 here to send along in subsequent analytics tracking calls.
*/

- (void)updateAnalyticsContext:(EHIAnalyticsContext *)context;

@end
