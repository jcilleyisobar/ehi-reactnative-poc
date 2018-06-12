//
//  EHICancellationDetails.h
//  Enterprise
//
//  Created by Rafael Ramos on 2/12/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIModel.h"
#import "EHIPrice.h"
#import "EHICancellationFee.h"

@interface EHICancellationDetails : EHIModel

@property (copy  , nonatomic, readonly) NSArray<EHICancellationFee> *cancelFeeDetails;
@property (assign, nonatomic, readonly) BOOL feeApply;

// computed properties
@property (strong, nonatomic, readonly) EHICancellationFee *cancellationFee;
@property (strong, nonatomic, readonly) EHIPrice *feePayment;
@property (strong, nonatomic, readonly) EHIPrice *feeView;
@property (strong, nonatomic, readonly) EHIPrice *refundPayment;
@property (strong, nonatomic, readonly) EHIPrice *refundView;
@property (strong, nonatomic, readonly) EHIPrice *originalAmoutPayment;
@property (strong, nonatomic, readonly) EHIPrice *originalAmoutView;

@end
