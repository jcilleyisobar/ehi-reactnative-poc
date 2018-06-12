//
//  EHIServices+Config.m
//  Enterprise
//
//  Created by Ty Cobb on 6/15/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIServices_Private.h"
#import "EHIServices+Config.h"

@implementation EHIServices (Config)

- (id<EHINetworkCancelable>)fetchConfigurationSupport:(EHIConfiguration *)configuration handler:(void(^)(id, EHIServicesError *))handler
{
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeGBORental get:@"reservations/%@/%@/support/contact/%@", kEHIServicesBrandPathKey, kEHIServicesChannelPathKey, NSLocale.ehi_region];
    return [self startRequest:request updateModel:configuration asynchronously:NO handler:handler];
}

- (id<EHINetworkCancelable>)fetchContentForType:(EHIWebContentType)type handler:(void(^)(EHIWebContent *, EHIServicesError *))handler
{
    switch(type) {
        case EHIWebContentTypePrivacy:
            return [self fetchContent:[NSString stringWithFormat:@"content/%@/%@/privacy", kEHIServicesBrandPathKey, kEHIServicesChannelPathKey] handler:handler];
        case EHIWebContentTypeTermsOfUse:
            return [self fetchContent:[NSString stringWithFormat:@"content/%@/%@/termsOfUse/%@", kEHIServicesBrandPathKey, kEHIServicesChannelPathKey, [NSLocale ehi_region]] handler:handler];
        case EHIWebContentTypeTermsAndConditions:
            return [self fetchContent:[NSString stringWithFormat:@"content/%@/%@/terms", kEHIServicesBrandPathKey, kEHIServicesChannelPathKey] handler:handler];
        case EHIWebContentTypePrepayTermsAndConditions:
            return [self fetchContent:[NSString stringWithFormat:@"content/%@/%@/prepayTermsAndConditions/%@", kEHIServicesBrandPathKey, kEHIServicesChannelPathKey, [NSLocale ehi_region]] handler:handler];
        case EHIWebContentTypeTaxes:
            return [self fetchContent:[NSString stringWithFormat:@"content/%@/%@/taxes", kEHIServicesBrandPathKey, kEHIServicesChannelPathKey] handler:handler];
        case EHIWebContentTypeLicenses:
            return [self fetchThirdPartyLicensesWithHandler:handler];
        default: return nil;
    }
}

//
// Helpers
//

- (id<EHINetworkCancelable>)fetchContent:(NSString *)path handler:(void(^)(EHIWebContent *, EHIServicesError *))handler
{
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeGBOProfile get:path];
    // kick off the request
    return [self startRequest:request parseModel:[EHIWebContent class] asynchronously:YES handler:^(EHIWebContent *content, EHIServicesError *error) {
        ehi_call(handler)(error ? nil : content, error);
    }];
}

- (id<EHINetworkCancelable>)fetchThirdPartyLicensesWithHandler:(void(^)(EHIWebContent *, EHIServicesError*))handler
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        NSString *fileName   = [[NSBundle mainBundle] pathForResource:@"3rd_party_licenses" ofType:@"html"];
        NSString *htmlString = [NSString stringWithContentsOfFile:fileName encoding:NSUTF8StringEncoding error:nil];
        
        EHIWebContent *content = [EHIWebContent modelWithDictionary:@{
            @key(content.body) : htmlString,
        }];
        
        dispatch_async(dispatch_get_main_queue(), ^{
            ehi_call(handler)(content, nil);
        });
    });
    
    return nil;
}

@end
