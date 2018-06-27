//
//  EHIAEMEnvironment.m
//  Enterprise
//
//  Created by Rafael Ramos on 18/06/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIAEMEnvironment.h"

@interface EHIAEMEnvironment ()
@property (assign, nonatomic) EHIEnvironmentType environmentType;
@end

@implementation EHIAEMEnvironment

+ (instancetype)serviceWithEnvironment:(EHIEnvironmentType)environmentType
{
    EHIAEMEnvironment *service = EHIAEMEnvironment.new;
    service.environmentType    = environmentType;

    return service;
}

- (NSString *)domainURL
{
    NSString *subdomain = [self subdomainForEndpoint:self.environmentType];
    
    return [NSString stringWithFormat:@"https://%@enterprise.com", subdomain];
}
//https://enterprise-int1-aem.enterprise.com/bin/ecom/deals.US.en.json
- (NSString *)subdomainForEndpoint:(EHIEnvironmentType)endpoint
{
    switch(endpoint) {
        case EHIEnvironmentTypeSvcsQaXqa1: return @"enterprise-xqa1-aem.";
        case EHIEnvironmentTypeRcQaInt1:   return @"enterprise-int1-aem.";
        case EHIEnvironmentTypeBeta:       return @"www.enterprise.com";
        case EHIEnvironmentTypeProd:       return @"www.enterprise.com";
        default:                           return nil;
    }
}

- (NSString *)apiKey
{
    return nil;
}

- (NSString *)name
{
    return [EHIAEMEnvironment nameForEnvironment:self.environmentType];
}

# pragma mark - Debug

- (void)showEnvironmentSelectionAlertWithCompletion:(void(^ __nullable)(BOOL canceled, EHIEnvironmentType environmentType))handler
{
#if defined(DEBUG) || defined(UAT)
    EHIAlertViewBuilder *alertView = EHIAlertViewBuilder.new
            .title(@"Set Environment")
            .message(@"Which environment should be used?");

    @(0).upTo(EHIEnvironmentTypeNumEnvironments).map(^(NSNumber *number) {
        return [EHIAEMEnvironment nameForEnvironment:(EHIEnvironmentType)number.integerValue];
    }).select(^(NSString *title){
        return title != nil;
    }).each(^(NSString *title){
        alertView.button(title);
    });

    alertView.cancelButton(@"Cancel");

    alertView.show(^(NSInteger index, BOOL canceled) {
        // ignore and call handler immediately on cancel
        if(canceled) {
            ehi_call(handler)(YES, EHIEnvironmentTypeNumEnvironments);
            return;
        }

        // attempt to update environment
        EHIEnvironmentType updatedType = (EHIEnvironmentType)index;
        ehi_call(handler)(NO, updatedType);
    });
#endif
}

+ (NSString *)nameForEnvironment:(EHIEnvironmentType)environment
{
    switch(environment) {
        case EHIEnvironmentTypeSvcsQaXqa1:
            return @"XQA1";
        case EHIEnvironmentTypeRcQaInt1:
            return @"INT1";
        case EHIEnvironmentTypeBeta:
        case EHIEnvironmentTypeProd:
            return @"PROD";
        default:
            return nil;
    }
}

@end
