//
//  EHIAnalytics.h
//  Enterprise
//
//  Created by Ty Cobb on 5/28/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIAnalyticsContext.h"
#import "EHIAnalyticsKeys.h"
#import "EHIAnalyticsAttributes.h"

#ifdef DEBUG
#define EHILocalyticsLogging 0
#else 
#define EHILocalyticsLogging 0
#endif

NS_ASSUME_NONNULL_BEGIN

@interface EHIAnalytics : NSObject

/** 
 Bootstraps the analytics system.
 @param options The launch options provided to the application delegate
*/

+ (void)prepareToLaunch;

/**
 Toggles data collection by the analytics system 
*/

+ (void)enableDataCollection:(BOOL)enabled;

+ (void)forgetMe;

@end

@interface EHIAnalytics (Tracking)

/**
 AppSee specific configuration
 
 Marks @c view as sensitive.
 The view will be blanked out in the video (and also the keyboard attached to it).
 */

+ (void)markViewAsSensitive:(UIView *)view;

/**
 @brief Tracks an "on load" state given the current analytics context
 
 The context should be fully configured, save the operations in the @c handler, before
 the state tracking call is issued.
 
 The operations in the @c handler are only applied to this call.
 
 @brief handler A block that applies reversible changes to the context
*/

+ (void)trackState:(nullable EHIAnalyticsContextHandler)handler;

/**
 @brief Tracks an @c action within the current context
 
 This method is a pass-through to @c +trackAction:type: with @c type as @c EHIAnalyticsActionTypeTap.
 See that method for complete documentation.
*/

+ (void)trackAction:(NSString *)action handler:(nullable EHIAnalyticsContextHandler)handler;

/**
 @brief Tracks an @c action within the current context
 
 The context should be fully configured, save the operations in the @c handler, before the action
 tracking call is issued. A full list of actions is available in @c EHIAnalyticsKeys.h.
 
 The operations in the @c handler are only applied to this call.
 
 @param action  The name of the action to track
 @param type    The means of interaction that triggered the action
 @param handler The block that applies reversible changes to the context
*/

+ (void)trackAction:(NSString *)action type:(EHIAnalyticsActionType)type handler:(nullable EHIAnalyticsContextHandler)handler;

/**
 @brief tracks, from where the session is started (for example if app was opened through a notification)
 
*/
+ (void)updateSessionSource:(NSString *)source;

/** Generates an action string from multiple actions */
extern NSString * ehi_serializeActions(NSString *action, ...);

@end

@interface EHIAnalytics (Context)

/**
 @brief Updates the current screen
 
 This is a pass-through to the @c +changeScreen:state: method, with @c state as
 @c nil. See that method for complete documentation.
*/

+ (void)changeScreen:(NSString *)screen;

/**
 @brief Updates the current screen / state
 
 The parameterized @c screen should be the key for the @em router screen.
 
 The caller should not rely on context information before this method is 
 called, as it may be destroyed at this point.
 
 If state is specief
 
 @brief screen The @em router screen to change to
 @brief state  The router screen to update the state from, or @c nil
*/

+ (void)changeScreen:(NSString *)screen state:(nullable NSString *)state;


/**
 @brief Updates the current screen / state
 For the watch screen names and states are not mapped to any router screen names and states
 */

+ (void)changeWatchScreen:(NSString *)screen state:(NSString *)state;

/**
 @brief The current analytics context
 
 The context should be updated with relevant data before the analytics call 
 is made. At the time of the call, the context information is serialized and 
 then discarded, and it must be repopulated.
*/

+ (EHIAnalyticsContext *)context;

@end

@interface EHIAnalytics (Debug)

+ (NSDictionary *)optOutStatus;

@end

NS_ASSUME_NONNULL_END
