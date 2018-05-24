//
//  EHICancellationFee.m
//  Enterprise
//
//  Created by Stu Buchbinder on 11/10/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHICancellationFee.h"
#import "EHIModel_Subclass.h"

@interface EHICancellationFee ()
@property (strong, nonatomic) EHIPrice *originalAmountView;
@property (strong, nonatomic) EHIPrice *originalAmountPayment;
@end

@implementation EHICancellationFee

+ (NSDictionary *)mappings:(EHICancellationFee *)model
{
    return @{
        @"cancel_fee_type"          : @key(model.type),
        @"fee_apply"                : @key(model.apply),
        @"fee_dead_line"            : @key(model.feeDeadline),
        @"fee_dead_line_in_hours"   : @key(model.feeDeadlineInHours),
        @"fee_amount_payment"       : @key(model.feePayment),
        @"fee_amount_view"          : @key(model.feeView),
        @"refund_amount"            : @key(model.refundAmount),
        @"refund_amount_view"       : @key(model.refundAmountView),
        @"refund_amount_payment"    : @key(model.refundAmountPayment),
        @"original_amount_view"     : @key(model.originalAmountView),
        @"original_amount_payment"  : @key(model.originalAmountPayment)
    };
}

+ (void)registerTransformers:(EHICancellationFee *)model
{
    // register a mapping for the extra status
    [self key:@key(model.type) registerMap:@{
        @"FEE"         : @(EHICancellationFeeTypeFee),
        @"ADVANCE_FEE" : @(EHICancellationFeeTypeAdvance),
        @"NO_SHOW_FEE" : @(EHICancellationFeeTypeNoShow),
    } defaultValue:@(EHICancellationFeeTypeFeeUnknown)];
}

# pragma mark - EHIPriceContext

- (EHIPrice *)viewPrice
{
    return self.originalAmountView;
}

- (EHIPrice *)paymentPrice
{
    return self.originalAmountPayment;
}

- (BOOL)viewCurrencyDiffersFromSourceCurrency
{
    return ![self.viewPrice.code isEqualToString:self.paymentPrice.code];
}

- (BOOL)eligibleForCurrencyConvertion
{
    BOOL usaCA = [self.viewPrice.code isEqualToString:EHICurrencyCodeUSA] && [self.paymentPrice.code isEqualToString:EHICurrencyCodeCA];
    BOOL caUSA = [self.viewPrice.code isEqualToString:EHICurrencyCodeCA] && [self.paymentPrice.code isEqualToString:EHICurrencyCodeUSA];
    
    return usaCA || caUSA;
}

@end
