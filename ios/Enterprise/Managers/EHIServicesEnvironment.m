//
//  EHIServicesEnvironment.m
//  Enterprise
//
//  Created by Rafael Ramos on 09/08/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIServicesEnvironment.h"

@interface EHIServicesEnvironment ()
@property (assign, nonatomic) EHIServicesEnvironmentType servicesType;
@property (assign, nonatomic) EHIEnvironmentType environmentType;
@end

@implementation EHIServicesEnvironment

+ (instancetype)serviceWithType:(EHIServicesEnvironmentType)servicesType forEnvironment:(EHIEnvironmentType)environmentType
{
    EHIServicesEnvironment *service = EHIServicesEnvironment.new;
    service.servicesType    = servicesType;
    service.environmentType = environmentType;

    return service;
}

- (NSString *)domainURL
{
    if(self.environmentType == EHIEnvironmentTypeNumEnvironments) {
        return nil;
    }

    return [self composeURLForService:self.servicesType usingEndPoint:self.environmentType];
}

- (NSString *)apiKey
{
    if(self.environmentType == EHIEnvironmentTypeNumEnvironments) {
        return nil;
    }

    return [self apiKeyForEndpoint:self.environmentType];
}

- (NSString *)composeURLForService:(EHIServicesEnvironmentType)service usingEndPoint:(EHIEnvironmentType)endpoint
{
    NSString *path      = [self pathFromService:service];
    NSString *subdomain = [self subdomainForEndpoint:endpoint];
    
    return [NSString stringWithFormat:@"https://www-%@%@.com/%@/api/v2", path, subdomain, path];
}

- (NSString *)pathFromService:(EHIServicesEnvironmentType)service
{
    switch(service) {
        case EHIServicesEnvironmentTypeGBOLocation: return @"gbo-location";
        case EHIServicesEnvironmentTypeGBORental:   return @"gbo-rental";
        case EHIServicesEnvironmentTypeGBOProfile:  return @"gbo-profile";
        case EHIServicesEnvironmentTypeNone:        return nil;
    }
}

- (NSString *)subdomainForEndpoint:(EHIEnvironmentType)endpoint
{
    switch(endpoint) {
        case EHIEnvironmentTypeSvcsQa:          return @"-svcsqa.gbo.csdev.ehiaws-nonprod";
        case EHIEnvironmentTypeRcQa:            return @"-rcqa.gbo.csdev.ehiaws-nonprod";
        case EHIEnvironmentTypeHotHot:          return @"-hh-qa.csdev.ehiaws";
        case EHIEnvironmentTypePrdSuPqa:        return @"-prdsupqa.gbo.csdev.ehiaws-nonprod";
        case EHIEnvironmentTypePrdsup:          return @"-prdsup.csdev.ehiaws";
        case EHIEnvironmentTypeDev:             return @"-dev.gbo.csdev.ehiaws-nonprod";
        case EHIEnvironmentTypeDevQa:           return @"-devqa.gbo.csdev.ehiaws-nonprod";
        case EHIEnvironmentTypeRcDev:           return @"-rcdev.csdev.ehiaws";
        case EHIEnvironmentTypeTmpEnv:          return @"-tmpenv.gbo.csdev.ehiaws-nonprod";
        case EHIEnvironmentTypePrdSuPdev:       return @"-prdsupdev.csdev.ehiaws";
        case EHIEnvironmentTypePenTest:         return @"-prdsupqa.csdev.ehiaws";
        case EHIEnvironmentTypeEast:            return @"-east.enterprise.ehiaws";
        case EHIEnvironmentTypeWest:            return @"-west.enterprise.ehiaws";
        case EHIEnvironmentTypeBeta:            return @".enterprise.ehiaws";
        case EHIEnvironmentTypeProd:            return @".enterprise.ehiaws";
        case EHIEnvironmentTypeNumEnvironments: return nil;
    }
}

- (NSString *)apiKeyForEndpoint:(EHIEnvironmentType)endpoint
{
    switch(endpoint) {
        case EHIEnvironmentTypeSvcsQa:          return @"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=";
        case EHIEnvironmentTypeRcQa:            return @"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=";
        case EHIEnvironmentTypeHotHot:          return @"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=";
        case EHIEnvironmentTypePrdSuPqa:        return @"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=";
        case EHIEnvironmentTypePrdsup:          return @"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=";
        case EHIEnvironmentTypeDev:             return @"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=";
        case EHIEnvironmentTypeDevQa:           return @"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=";
        case EHIEnvironmentTypeRcDev:           return @"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=";
        case EHIEnvironmentTypePrdSuPdev:       return @"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=";
        case EHIEnvironmentTypeTmpEnv:          return @"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=";
        case EHIEnvironmentTypePenTest:         return @"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=";
        case EHIEnvironmentTypeEast:            return @"adb6ks4jqnVGD/Gdn7JB2Cl1bSuEyh7lpF022l/sPLo=";
        case EHIEnvironmentTypeWest:            return @"adb6ks4jqnVGD/Gdn7JB2Cl1bSuEyh7lpF022l/sPLo=";
        case EHIEnvironmentTypeBeta:            return @"adb6ks4jqnVGD/Gdn7JB2Cl1bSuEyh7lpF022l/sPLo=";
        case EHIEnvironmentTypeProd:            return @"adb6ks4jqnVGD/Gdn7JB2Cl1bSuEyh7lpF022l/sPLo=";
        case EHIEnvironmentTypeNumEnvironments: return nil;
    }
}

@end
