//
//  EHIGBOEnvironment.m
//  Enterprise
//
//  Created by Rafael Ramos on 18/06/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIGBOEnvironment.h"

@interface EHIGBOEnvironment ()
@property (assign, nonatomic) EHIServicesEnvironmentType servicesType;
@property (assign, nonatomic) EHIEnvironmentType environmentType;
@end

@implementation EHIGBOEnvironment

+ (instancetype)serviceWithType:(EHIServicesEnvironmentType)servicesType forEnvironment:(EHIEnvironmentType)environmentType
{
    EHIGBOEnvironment *service = EHIGBOEnvironment.new;
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

- (NSString *)name
{
    return [EHIGBOEnvironment nameForEnvironment:self.environmentType];
}

- (NSString *)composeURLForService:(EHIServicesEnvironmentType)service usingEndPoint:(EHIEnvironmentType)endpoint
{
    if(service == EHIServicesEnvironmentTypeAEM) {
        return @"https://enterprise-int1-aem.enterprise.com";
    }
    
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
        default: return nil;
    }
}

- (NSString *)subdomainForEndpoint:(EHIEnvironmentType)endpoint
{
    switch(endpoint) {
        case EHIEnvironmentTypeSvcsQaXqa1:          return @"-svcsqa.gbo.csdev.ehiaws-nonprod";
        case EHIEnvironmentTypeRcQaInt1:            return @"-rcqa.gbo.csdev.ehiaws-nonprod";
        case EHIEnvironmentTypeHotHot:          return @"-hh-qa.csdev.ehiaws";
        case EHIEnvironmentTypePrdSuPqa:        return @"-prdsupqa.gbo.csdev.ehiaws-nonprod";
        case EHIEnvironmentTypePrdsup:          return @"-prdsup.csdev.ehiaws";
        case EHIEnvironmentTypeDev:             return @"-dev.gbo.csdev.ehiaws-nonprod";
        case EHIEnvironmentTypeDevQa:           return @"-devqa.gbo.csdev.ehiaws-nonprod";
        case EHIEnvironmentTypeRcDev:           return @"-rcdev.csdev.ehiaws";
        case EHIEnvironmentTypeTmpEnv:          return @"-tmpenv.gbo.csdev.ehiaws-nonprod";
        case EHIEnvironmentTypePrdSuPdev:       return @"-prdsupdev.csdev.ehiaws";
        case EHIEnvironmentTypePenTest:         return @"-prdsupqa.csdev.ehiaws";
        case EHIEnvironmentTypeBeta:            return @".enterprise.ehiaws";
        case EHIEnvironmentTypeProd:            return @".enterprise.ehiaws";
        default:                                return nil;
    }
}

- (NSString *)apiKeyForEndpoint:(EHIEnvironmentType)endpoint
{
    switch(endpoint) {
        case EHIEnvironmentTypeSvcsQaXqa1:          return @"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=";
        case EHIEnvironmentTypeRcQaInt1:            return @"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=";
        case EHIEnvironmentTypeHotHot:          return @"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=";
        case EHIEnvironmentTypePrdSuPqa:        return @"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=";
        case EHIEnvironmentTypePrdsup:          return @"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=";
        case EHIEnvironmentTypeDev:             return @"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=";
        case EHIEnvironmentTypeDevQa:           return @"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=";
        case EHIEnvironmentTypeRcDev:           return @"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=";
        case EHIEnvironmentTypePrdSuPdev:       return @"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=";
        case EHIEnvironmentTypeTmpEnv:          return @"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=";
        case EHIEnvironmentTypePenTest:         return @"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=";
        case EHIEnvironmentTypeBeta:            return @"DmyKjglEVzHis5y0i2E/hFeHL+HasvVYgkATwHiDHyo=";
        case EHIEnvironmentTypeProd:            return @"DmyKjglEVzHis5y0i2E/hFeHL+HasvVYgkATwHiDHyo=";
        default:                                return nil;
    }
}

# pragma mark - Debug

- (void)showEnvironmentSelectionAlertWithCompletion:(void(^ __nullable)(BOOL canceled, EHIEnvironmentType environmentType))handler
{
#if defined(DEBUG) || defined(UAT)
    EHIAlertViewBuilder *alertView = EHIAlertViewBuilder.new
            .title(@"Set Environment")
            .message(@"Which environment should be used?");

    // the count is the number of envs less the final environment (for debug we are assuming last
    // env is unprotected PROD)
    @(0).upTo(EHIEnvironmentTypeNumEnvironments).map(^(NSNumber *number) {
        return [EHIGBOEnvironment nameForEnvironment:(EHIEnvironmentType)number.integerValue];
    }).select(^(NSString *title){
        return title != nil;
    }).each(^(NSString *title){
        alertView.button(title);
    });

    alertView.cancelButton(@"Cancel");

    alertView.show(^(NSInteger index, BOOL canceled) {
        if(canceled) {
            ehi_call(handler)(YES, EHIEnvironmentTypeNumEnvironments);
            return;
        }

        EHIEnvironmentType updatedType = (EHIEnvironmentType)index;
        ehi_call(handler)(NO, updatedType);
    });
#endif
}

+ (NSString *)nameForEnvironment:(EHIEnvironmentType)environment
{
    switch(environment) {
        case EHIEnvironmentTypeSvcsQaXqa1:
            return @"SVCSQA";
        case EHIEnvironmentTypeRcQaInt1:
            return @"RCQA";
        case EHIEnvironmentTypeHotHot:
            return @"HOT HOT";
        case EHIEnvironmentTypePrdSuPqa:
            return @"PRDSUPQA";
        case EHIEnvironmentTypeDev:
            return @"DEV";
        case EHIEnvironmentTypeDevQa:
            return @"DEV QA";
        case EHIEnvironmentTypeRcDev:
            return @"RCDEV";
        case EHIEnvironmentTypeTmpEnv:
            return @"TMPENV";
        case EHIEnvironmentTypePrdSuPdev:
            return @"PRDSUPDEV";
        case EHIEnvironmentTypePenTest:
            return @"PEN_TEST";
        case EHIEnvironmentTypeBeta:
        case EHIEnvironmentTypeProd:
            return @"PROD";
        case EHIEnvironmentTypePrdsup:
            return @"PRDSUP";
        default:
            return nil;
    }
}

@end
