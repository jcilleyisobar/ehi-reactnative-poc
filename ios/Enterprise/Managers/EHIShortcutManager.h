//
//  EHIShortcutManager.h
//  Enterprise
//
//  Created by Alex Koller on 9/30/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

@interface EHIShortcutManager : NSObject

+ (void)performActionForShortcutItem:(UIApplicationShortcutItem *)item completion:(void (^)(BOOL))completionHandler;

@end
