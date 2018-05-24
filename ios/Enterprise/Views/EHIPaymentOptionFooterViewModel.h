//
//  EHIPaymentOptionFooterViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 10/10/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIPaymentOptionFooterViewModel : EHIViewModel <MTRReactive>
@property (copy, nonatomic) NSString *prepayTitle;
@property (copy, nonatomic) NSString *termTitle;

- (void)showPrepay;
- (void)showTerms;

@end
