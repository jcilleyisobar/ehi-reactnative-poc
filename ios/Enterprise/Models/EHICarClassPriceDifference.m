//
//  EHICarClassPriceDifference.m
//  Enterprise
//
//  Created by Alex Koller on 11/20/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHIModel_Subclass.h"
#import "EHICarClassPriceDifference.h"

@implementation EHICarClassPriceDifference

# pragma mark - Mappings

+ (NSDictionary *)mappings:(EHICarClassPriceDifference *)model
{
    return @{
        @"difference_type"           : @key(model.type),
        @"selected_car_class"        : @key(model.selectedCarClass),
        @"difference_amount_payment" : @key(model.paymentDifference),
        @"difference_amount_view"    : @key(model.viewDifference),
    };
}

+ (void)registerTransformers:(EHICarClassPriceDifference *)model
{
    [self key:@key(model.type) registerMap:@{
        @"CONTRACT"                     : @(EHICarClassPriceDifferenceTypeContract),
        @"PREPAY"                       : @(EHICarClassPriceDifferenceTypePrepay),
        @"UPGRADE"                      : @(EHICarClassPriceDifferenceTypeUpgrade),
        @"UPGRADE_PAYLATER_TO_PAYLATER" : @(EHICarClassPriceDifferenceTypeUpgrade),
        @"UPGRADE_PREPAY_TO_PREPAY"     : @(EHICarClassPriceDifferenceTypeUpgradePrepay),
        @"UNPAID_REFUND_AMOUNT"         : @(EHICarClassPriceDifferenceTypeUnpaidRefundAmount),

    } defaultValue:@(EHICarClassPriceDifferenceTypeUnknown)];
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
    return ![self.viewDifference.code isEqualToString:self.paymentDifference.code];
}

- (EHIPrice *)viewPrice
{
    return self.viewDifference;
}

- (EHIPrice *)paymentPrice
{
    return self.paymentDifference;
}

@end
