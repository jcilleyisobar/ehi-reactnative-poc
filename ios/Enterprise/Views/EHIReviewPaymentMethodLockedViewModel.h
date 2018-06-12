//
//  EHIReviewPaymentMethodLockedViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 12/1/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIReviewPaymentMethodLockedViewModel : EHIViewModel <MTRReactive>
@property (copy, nonatomic, readonly) NSString *paymentTitle;
@property (copy, nonatomic, readonly) NSString *termsTitle;

- (void)showTerms;

@end
