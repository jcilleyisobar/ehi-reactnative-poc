//
//  EHIAnalyticsContext+Mappings.h
//  Enterprise
//
//  Created by Ty Cobb on 5/29/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIAnalyticsContext.h"

NS_ASSUME_NONNULL_BEGIN

@interface EHIAnalyticsContext (Mappings)

/**
 @brief Maps the router screen to an analytics screen
 
 If no mapping is found, this is considered to be a configuration error and an
 exception is thrown.
*/

- (NSString *)screenFromRouterScreen:(NSString *)screen;

/**
 @brief Maps the router screen to an analytics state
 
 If no mapping is found, this is considered to be a configuration error and an
 exception is thrown.
*/

- (NSString *)stateFromRouterScreen:(NSString *)screen;

/**
 @brief Maps the action type into a string
 
 If no mapping is found, this method returns @c nil. This will always be the case
 for @c EHIAnalyticsActionTypeNone.
*/

- (nullable NSString *)stringFromActionType:(EHIAnalyticsActionType)type;

@end

NS_ASSUME_NONNULL_END
