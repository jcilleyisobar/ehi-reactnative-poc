//
//  EHIAppDelegate.m
//  Enterprise
//
//  Created by Ty Cobb on 1/5/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIAppDelegate.h"
#import "EHIApplicationBootstrap.h"

@implementation EHIAppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    self.window.tintColor = [UIColor ehi_greenColor];
	[EHIApplicationBootstrap prepareToLaunch:launchOptions];
    
    return YES;
}

- (void)application:(UIApplication *)application handleWatchKitExtensionRequest:(NSDictionary *)userInfo reply:(void (^)(NSDictionary * _Nullable))reply
{
	[EHIApplicationBootstrap handleWatchRequest:userInfo reply:reply];
}

- (void)application:(UIApplication *)application performActionForShortcutItem:(UIApplicationShortcutItem *)shortcutItem completionHandler:(void (^)(BOOL))completionHandler
{
    [EHIApplicationBootstrap performActionForShortcutItem:shortcutItem completionHandler:completionHandler];
}

@end
