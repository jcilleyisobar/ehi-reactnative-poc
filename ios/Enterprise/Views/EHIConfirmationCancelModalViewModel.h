//
//  EHIConfirmationCancelModalViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 2/16/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIInfoModalViewModel.h"

@class EHIPrice;
@class EHICancellationFee;
@interface EHIConfirmationCancelModalViewModel : EHIInfoModalViewModel <MTRReactive>
@property (copy, nonatomic, readonly) NSAttributedString *subtitle;
@property (copy, nonatomic, readonly) NSString *conversionSubtitle;
@property (copy, nonatomic, readonly) NSString *originalAmountTile;
@property (copy, nonatomic, readonly) NSString *cancellationFeeTitle;
@property (copy, nonatomic, readonly) NSString *refundedAmountTitle;

@property (copy, nonatomic, readonly) NSString *originalAmount;
@property (copy, nonatomic, readonly) NSString *cancellationFee;
@property (copy, nonatomic, readonly) NSString *convertedRefund;
@property (copy, nonatomic, readonly) NSAttributedString *refundedAmount;

- (instancetype)initWithPrice:(EHIPrice *)originalPrice
					cancelFee:(EHIPrice *)cancelFee
			  cancellationFee:(EHICancellationFee *)cancellationFee
					   refund:(EHIPrice *)refundAmount;

@end
