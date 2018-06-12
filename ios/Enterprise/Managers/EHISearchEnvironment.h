//
//  EHISearchEnvironment.h
//  Enterprise
//
//  Created by Rafael Ramos on 07/08/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIEnvironments.h"

#define EHISearchEnvironmentTypeKey @"EHISearchEnvironmentTypeKey"

@interface EHISearchEnvironment : NSObject
@property (assign, nonatomic, readonly) EHISearchEnvironmentType environmentType;
@property (copy  , nonatomic, readonly) NSString *displayName;
@property (copy  , nonatomic, readonly) NSString *domainURL;
@property (copy  , nonatomic, readonly) NSString *basePath;
@property (copy  , nonatomic, readonly) NSString *serviceURL;
@property (copy  , nonatomic, readonly) NSString *apiKey;

+ (instancetype)environmentWithType:(EHISearchEnvironmentType)environment;
+ (EHISearchEnvironment *)unarchive;

- (void)setType:(EHISearchEnvironmentType)environmentType;

@end
