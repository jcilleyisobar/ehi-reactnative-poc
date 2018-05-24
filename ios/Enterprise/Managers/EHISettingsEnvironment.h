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

/** The type for the environment; setting this property will repopulate the other properties */
@property (assign, nonatomic) EHIEnvironmentType type;

/** The base URL for search services */
@property (copy  , nonatomic, readonly) NSString *search;
/** The api key for search services */
@property (copy  , nonatomic, readonly) NSString *searchApiKey;
/** A display name for this environment **/
@property (copy  , nonatomic, readonly) NSString *displayName;
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
/** convenience method to convert int type enum to display string */
+ (NSString *)nameForEnvironment:(EHIEnvironmentType)environment;
/** Attempts to update the environment type; Calls back the handler with the validated type */
- (void)setType:(EHIEnvironmentType)type handler:(void(^)(EHIEnvironmentType type, BOOL didUpdate))handler;

- (NSString *)serviceWithType:(EHIServicesEnvironmentType)servicesType;
- (NSString *)servicesApiKeyWithType:(EHIServicesEnvironmentType)servicesType;

/** Forced unarchive */
- (void)update;

@end
