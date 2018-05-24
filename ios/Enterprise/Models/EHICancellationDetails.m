//
//  EHICancellationDetails.m
//  Enterprise
//
//  Created by Rafael Ramos on 2/12/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHICancellationDetails.h"

@interface EHICancellationDetails ()
@property (strong, nonatomic) EHICancellationFee *cancellationFee;
@end

@implementation EHICancellationDetails

# pragma mark - Mappings

+ (NSDictionary *)mappings:(EHICancellationDetails *)model
{
    return @{
        @"cancel_fee_details" : @key(model.cancelFeeDetails)
    };
}

#pragma mark - Accessors

- (BOOL)feeApply
{
    return self.cancellationFee.apply;
}

- (EHIPrice *)feePayment
{
    return self.cancellationFee.feePayment;
}

- (EHIPrice *)feeView
{
    return self.cancellationFee.feeView;
}

- (EHIPrice *)refundView
{
    return self.cancellationFee.refundAmountView;
}

- (EHIPrice *)refundPayment
{
    return self.cancellationFee.refundAmountPayment;
}

- (EHIPrice *)originalAmoutView
{
    return self.cancellationFee.viewPrice;
}

- (EHIPrice *)originalAmoutPayment
{
    return self.cancellationFee.paymentPrice;
}

- (EHICancellationFee *)cancellationFee
{
    if(!_cancellationFee) {
        _cancellationFee = (self.cancelFeeDetails ?: @[]).find(^(EHICancellationFee *fee){
            return fee.apply;
        });
    }
    
    return _cancellationFee;
}

@end
