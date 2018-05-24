//
//  EHIWatchConnectivityManager.h
//  Enterprise
//
//  Created by Michael Place on 10/4/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import <Foundation/Foundation.h>

@class EHIUser;

@interface EHIWatchConnectivityManager : NSObject

+ (instancetype)sharedInstance;

/** Spins up a WCSession with a connected watch if supported */
+ (void)prepareToLaunch;

/** updates our application context with useful information from the user */
- (void)updateContextWithUser:(EHIUser *)user;

@end
