//
//  EHIApplicationBootstrap.m
//  Enterprise
//
//  Created by Rafael Ramos on 06/04/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIApplicationBootstrap.h"
#import "EHIKeychainSerializer.h"
#import "EHISettings.h"
#import "EHIAnalytics.h"
#import "EHISurvey.h"
#import "EHIFavoritesManager.h"
#import "EHIUserManager.h"
#import "EHIWatchConnectivityManager.h"
#import "EHINotificationManager.h"
#import "EHIGeofenceManager.h"
#import "EHIShortcutManager.h"
#import "EHICrashManager.h"

@implementation EHIApplicationBootstrap

+ (void)prepareToLaunch:(nullable NSDictionary *)launchOptions
{
#if EHIMockEnabled
	EHIWarn(@"WARNING -- Mock services may be enabled!");
#endif
    
    if(IS_DEVICE) {
        [EHICrashManager prepareToLauch];
    }

    [EHISurvey prepareToLaunch];
    [EHILayoutMetrics prepareToLaunch];
    [EHIUserManager prepareToLaunch];
    [EHISettings prepareToLaunch];
    [EHINotificationManager prepareToLaunch];
    [EHIGeofenceManager prepareToLaunch];
    [EHIAnalytics prepareToLaunch];
    [EHILogging prepareToLaunch];
    [EHIFavoritesManager prepareToLaunch];
    [EHIKeychainSerializer prepareToLaunch];

    [self launchWatch];
}

+ (void)handleWatchRequest:(nullable NSDictionary *)userInfo reply:(void (^ _Nullable)(NSDictionary * _Nullable))reply
{
    [self launchWatch];
}

+ (void)performActionForShortcutItem:(nullable UIApplicationShortcutItem *)shortcutItem completionHandler:(void (^ _Nullable)(BOOL))completionHandler
{
    [EHIShortcutManager performActionForShortcutItem:shortcutItem completion:completionHandler];
}

//
// Helpers
//

+ (void)launchWatch
{
    [EHIWatchConnectivityManager prepareToLaunch];
}

@end
