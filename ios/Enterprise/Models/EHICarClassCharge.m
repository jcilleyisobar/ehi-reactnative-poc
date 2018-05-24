//
//  EHICarClassPrice.m
//  Enterprise
//
//  Created by mplace on 2/19/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICarClassCharge.h"
#import "EHIModel_Subclass.h"

@implementation EHICarClassCharge

# pragma mark - Mappings

+ (NSDictionary *)mappings:(EHICarClassCharge *)model
{
    return @{
        @"charge_type"         : @key(model.type),
        @"total_price_view"    : @key(model.viewTotal),
        @"total_price_payment" : @key(model.paymentTotal),
    };
}

+ (void)registerTransformers:(EHICarClassCharge *)model
{
    [self key:@key(model.type) registerTransformer:EHICarClassChargeTypeTransformer()];
}

# pragma mark - EHIPriceContext

- (BOOL)eligibleForCurrencyConvertion
{
    BOOL usaCA = [self.viewPrice.code isEqualToString:EHICurrencyCodeUSA] && [self.paymentPrice.code isEqualToString:EHICurrencyCodeCA];
    BOOL caUSA = [self.viewPrice.code isEqualToString:EHICurrencyCodeCA] && [self.paymentPrice.code isEqualToString:EHICurrencyCodeUSA];
    
    return usaCA || caUSA;
}

- (BOOL)viewCurrencyDiffersFromSourceCurrency
{
    return ![self.viewTotal.code isEqualToString:self.paymentTotal.code];
}

- (EHIPrice *)viewPrice
{
    return self.viewTotal;
}

- (EHIPrice *)paymentPrice
{
    return self.paymentTotal;
}

@end
