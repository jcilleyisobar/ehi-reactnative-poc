//
//  EHICancellationFee.h
//  Enterprise
//
//  Created by Stu Buchbinder on 11/10/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIModel.h"
#import "EHIPrice.h"
#import "EHIPriceContext.h"

typedef NS_ENUM(NSInteger, EHICancellationFeeType) {
    EHICancellationFeeTypeFeeUnknown,
    EHICancellationFeeTypeFee,
    EHICancellationFeeTypeAdvance,
    EHICancellationFeeTypeNoShow
};

@interface EHICancellationFee : EHIModel <EHIPriceContext>

@property (assign, nonatomic, readonly) NSInteger feeDeadlineInHours;
@property (assign, nonatomic, readonly) BOOL apply;
@property (assign, nonatomic, readonly) EHICancellationFeeType type;
@property (copy  , nonatomic, readonly) NSString *feeDeadline;
@property (strong, nonatomic) EHIPrice *feePayment;
@property (strong, nonatomic) EHIPrice *feeView;
@property (strong, nonatomic) EHIPrice *refundAmount;
@property (strong, nonatomic) EHIPrice *refundAmountView;
@property (strong, nonatomic) EHIPrice *refundAmountPayment;

@end

EHIAnnotatable(EHICancellationFee)
