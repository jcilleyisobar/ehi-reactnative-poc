//
//  EHIConfirmationPaymentOptionViewModel.h
//  Enterprise
//
//  Created by Michael Place on 7/7/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIConfirmationPaymentOptionViewModel : EHIViewModel <MTRReactive>

@property (copy  , nonatomic, readonly) NSString *title;
@property (copy  , nonatomic, readonly) NSString *paymentTitle;
@property (copy  , nonatomic, readonly) NSString *value;
@property (copy  , nonatomic, readonly) NSString *policies;
@property (copy  , nonatomic, readonly) NSString *cardImage;
@property (assign, nonatomic, readonly) BOOL hidePoliciesLink;

- (void)showPolicies;

@end
