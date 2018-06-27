//
//  EHIPaymentCardScanViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 1/19/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@class CardIOCreditCardInfo;
@class EHICreditCard;
@interface EHIPaymentCardScanViewModel : EHIViewModel <MTRReactive>
@property (copy, nonatomic) NSString *scanInstructions;
@property (copy, nonatomic) void (^handler)(EHICreditCard *cardInfo);

- (void)didCancelCardScan;
- (void)didScanCreditCard:(CardIOCreditCardInfo *)cardInfo;

@end
