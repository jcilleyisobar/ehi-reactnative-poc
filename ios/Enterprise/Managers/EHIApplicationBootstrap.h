//
//  EHIApplicationBootstrap.h
//  Enterprise
//
//  Created by Rafael Ramos on 06/04/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

@interface EHIApplicationBootstrap : NSObject

+ (void)prepareToLaunch:(nullable NSDictionary *)launchOptions;
+ (void)handleWatchRequest:(nullable NSDictionary *)userInfo reply:(void (^ _Nullable)(NSDictionary * _Nullable))reply;
+ (void)performActionForShortcutItem:(nullable UIApplicationShortcutItem *)shortcutItem completionHandler:(void (^ _Nullable)(BOOL))completionHandler;

@end
