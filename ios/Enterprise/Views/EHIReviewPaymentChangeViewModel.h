//
//  EHIReviewPaymentChangeViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 10/14/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIPriceContext.h"

@interface EHIReviewPaymentChangeViewModel : EHIViewModel <MTRReactive>
- (instancetype)initWithPriceContext:(id<EHIPriceContext>)context prepay:(BOOL)isPrepay;
@property (copy  , nonatomic, readonly) NSString *title;
@property (assign, nonatomic, readonly) BOOL hideCardIcon;

- (void)didTapChangePayment;
- (void)didTapMoreInfo;

@end
