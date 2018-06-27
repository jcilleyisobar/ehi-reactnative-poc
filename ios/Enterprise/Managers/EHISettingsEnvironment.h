//
//  EHISettingsEnvironment.h
//  Enterprise
//
//  Created by Ty Cobb on 1/14/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIEnvironments.h"

#define EHISettingsEnvironmentChangedNotification @"EHISettingsEnvironmentChanged"

@interface EHISettingsEnvironment : NSObject

/** The base URL for search services */
@property (copy  , nonatomic, readonly) NSString *search;
/** The api key for search services */
@property (copy  , nonatomic, readonly) NSString *searchApiKey;
/** The name/key for the analytics environment */
@property (copy  , nonatomic, readonly) NSString *analyticsKey;
/** The key for the crittercism crash reporting environment */
@property (copy  , nonatomic, readonly) NSString *crittercismKey;
/** The key for the AppSee framework (screen capture analytics) */
@property (copy  , nonatomic, readonly) NSString *appSeeKey;
/** The iTunes url to this app */
@property (copy  , nonatomic, readonly) NSString *iTunesLink;
/** The Farepayment url */
@property (copy  , nonatomic, readonly) NSString *farepaymentUrl;

/** Unarchives the settings environment based on the stored type */
+ (instancetype)unarchive;

- (NSString *)displayNameForService:(EHIServicesEnvironmentType)service;

- (NSString *)serviceWithType:(EHIServicesEnvironmentType)servicesType;
- (NSString *)servicesApiKeyWithType:(EHIServicesEnvironmentType)servicesType;

// only for DEBUG and UAT builds
- (void)showEnvironmentSelectionAlertForService:(EHIServicesEnvironmentType)service withCompletion:(void(^ __nullable)(void))handler;
- (void)showSearchEnvironmentSelectionAlertWithCompletion:(void(^ __nullable)(void))handler;

/** Forced unarchive */
- (void)update;

@end
