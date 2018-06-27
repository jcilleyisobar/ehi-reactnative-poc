//
//  EHIEnvironments.h
//  Enterprise
//
//  Created by Rafael Ramos on 07/08/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIMapTransformer.h"

typedef NS_ENUM(NSInteger, EHIEnvironmentType) {
    EHIEnvironmentTypeSvcsQaXqa1,
    EHIEnvironmentTypeRcQaInt1,
    EHIEnvironmentTypeHotHot,
    EHIEnvironmentTypePrdSuPqa,
    EHIEnvironmentTypePrdsup,
    EHIEnvironmentTypeDev,
    EHIEnvironmentTypeDevQa,
    EHIEnvironmentTypeRcDev,
    EHIEnvironmentTypeTmpEnv,
    EHIEnvironmentTypePrdSuPdev,
    EHIEnvironmentTypePenTest,
    EHIEnvironmentTypeBeta,
    EHIEnvironmentTypeProd,
    EHIEnvironmentTypeNumEnvironments
};

typedef NS_ENUM(NSInteger, EHIServicesEnvironmentType) {
    EHIServicesEnvironmentTypeNone,
    EHIServicesEnvironmentTypeGBOLocation,
    EHIServicesEnvironmentTypeGBORental,
    EHIServicesEnvironmentTypeGBOProfile,
    EHIServicesEnvironmentTypeAEM
};

typedef NS_ENUM(NSInteger, EHISearchEnvironmentType) {
    EHISearchEnvironmentTypeXqa1,
    EHISearchEnvironmentTypeXqa2,
    EHISearchEnvironmentTypeXqa3,
    EHISearchEnvironmentTypeInt1,
    EHISearchEnvironmentTypeInt2,
    EHISearchEnvironmentTypeProd,
    EHISearchEnvironmentTypeNumEnvironments,
};

NS_INLINE NSValueTransformer * EHISearchEnvironmentTypeTransformer()
{
    EHIMapTransformer *transformer = [[EHIMapTransformer alloc] initWithMap:@{
        @"SOLR XQA1" : @(EHISearchEnvironmentTypeXqa1),
        @"SOLR XQA2" : @(EHISearchEnvironmentTypeXqa2),
        @"SOLR XQA3" : @(EHISearchEnvironmentTypeXqa3),
        @"SOLR INT1" : @(EHISearchEnvironmentTypeInt1),
        @"SOLR INT2" : @(EHISearchEnvironmentTypeInt2),
        @"PROD"      : @(EHISearchEnvironmentTypeProd),
    }];

    // default to unknown
    transformer.defaultValue = @(EHISearchEnvironmentTypeXqa1);

    return transformer;
}

# pragma mark - Contracts

@protocol EHIServicesEnvironmentConfiguration <NSObject>
- (NSString *)domainURL;
- (NSString *)apiKey;
- (NSString *)name;

- (void)showEnvironmentSelectionAlertWithCompletion:(void(^ __nullable)(BOOL canceled, EHIEnvironmentType environmentType))handler;
//+ (NSString *)nameForEnvironment:(EHIEnvironmentType)environment;

@end
