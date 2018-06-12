//
//  EHICreditCardPaymentContext.m
//  Enterprise
//
//  Created by Rafael Ramos on 9/28/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIModel_Subclass.h"
#import "EHICreditCardPaymentContext.h"

@implementation EHICreditCardPaymentContext

+ (NSDictionary *)mappings:(EHICreditCardPaymentContext *)model
{
    return @{
        @"source_system_id"         : @key(model.sourceSystemId),
        @"payment_processor"        : @key(model.gateway),
        @"calling_application_name" : @key(model.callingApplicationName),
        @"card_submission_url"      : @key(model.url)
    };
}

+ (void)registerTransformers:(EHICreditCardPaymentContext *)model
{
    [super registerTransformers:model];
    
    [self key:@key(model.gateway) registerTransformer:EHIPaymentGatewayTransformer()];
}

@end
