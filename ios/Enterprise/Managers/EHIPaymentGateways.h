//
//  EHIPaymentProcessors.h
//  Enterprise
//
//  Created by Rafael Ramos on 10/24/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIMapTransformer.h"

typedef NS_ENUM(NSInteger, EHIPaymentGatewayProcessor) {
    EHIPaymentGatewayProcessorUnknown,
    EHIPaymentGatewayProcessorPangui,
    EHIPaymentGatewayProcessorFareOffice
};

NS_INLINE NSValueTransformer * EHIPaymentGatewayTransformer()
{
    EHIMapTransformer *transformer = [[EHIMapTransformer alloc] initWithMap:@{
        @"PANGUI" : @(EHIPaymentGatewayProcessorPangui),
        @"FOSPS"  : @(EHIPaymentGatewayProcessorFareOffice)
    }];

    transformer.defaultValue = @(EHIPaymentGatewayProcessorUnknown);
    
    return transformer;
}
