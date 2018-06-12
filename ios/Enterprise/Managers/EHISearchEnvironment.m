//
//  EHISearchEnvironment.m
//  Enterprise
//
//  Created by Rafael Ramos on 07/08/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHISearchEnvironment.h"

#ifdef UAT
EHISearchEnvironmentType const EHIDefaultSearchEnvironmentTypeKey = EHISearchEnvironmentTypeXqa1;
#elif DEBUG
EHISearchEnvironmentType const EHIDefaultSearchEnvironmentTypeKey = EHISearchEnvironmentTypeXqa1;
#elif PENTEST
EHISearchEnvironmentType const EHIDefaultSearchEnvironmentTypeKey = EHISearchEnvironmentTypeXqa1;
#else
EHISearchEnvironmentType const EHIDefaultSearchEnvironmentTypeKey = EHISearchEnvironmentTypeProd;
#endif

@interface EHISearchEnvironment ()
@property (assign, nonatomic) EHISearchEnvironmentType environmentType;
@end

@implementation EHISearchEnvironment

+ (instancetype)environmentWithType:(EHISearchEnvironmentType)environment
{
    EHISearchEnvironment *instance = EHISearchEnvironment.new;
    instance.type = environment;

    return instance;
}


# pragma mark - Accessors

+ (EHISearchEnvironment *)unarchive
{
    EHISearchEnvironment *environment = EHISearchEnvironment.new;
    EHISearchEnvironmentType environmentType;
    // if we are not DEBUG or UAT, we never archive our environmentType -- we should always be PROD
#if !defined(DEBUG) && !defined(UAT)
    environmentType = EHIDefaultSearchEnvironmentTypeKey;
#else
    NSNumber *storedType = [[NSUserDefaults standardUserDefaults] objectForKey:EHISearchEnvironmentTypeKey];

    // if we don't have a stored type yet, use the defaults
    if(!storedType) {
        environmentType = EHIDefaultSearchEnvironmentTypeKey;
    } else {
        environmentType = (EHISearchEnvironmentType)storedType.integerValue;
    }
#endif

    environment.internalEnvironmentType = environmentType;

    return environment;
}

- (void)setType:(EHISearchEnvironmentType)environmentType
{
    self.internalEnvironmentType = environmentType;
}

- (void)setInternalEnvironmentType:(EHISearchEnvironmentType)environmentType
{
    _environmentType = environmentType;

    [NSUserDefaults.standardUserDefaults setObject:@(environmentType) forKey:EHISearchEnvironmentTypeKey];
}

- (NSString *)displayName
{
    return [EHISearchEnvironmentTypeTransformer() reverseTransformedValue:@(self.environmentType)];
}

- (NSString *)domainURL
{
    switch(self.environmentType) {
        case EHISearchEnvironmentTypeXqa1: return @"https://xqa1.location.enterprise.com";
        case EHISearchEnvironmentTypeXqa2: return @"https://xqa2.location.enterprise.com";
        case EHISearchEnvironmentTypeXqa3: return @"https://xqa3.location.enterprise.com";
        case EHISearchEnvironmentTypeInt1: return @"https://int1.location.enterprise.com";
        case EHISearchEnvironmentTypeInt2: return @"https://int2.location.enterprise.com";
        case EHISearchEnvironmentTypeProd: return @"https://prd.location.enterprise.com";
        default: return nil;
    }
}

- (NSString *)basePath
{
    return @"/enterprise-sls/search/location/mobile";
}

- (NSString *)serviceURL
{
    return [NSString stringWithFormat:@"%@%@", self.domainURL, self.basePath];
}

- (NSString *)apiKey
{
    return @"bd6dfa74-8881-4db5-8268-1de81dd504e8";
}

@end
