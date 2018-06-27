//
//  EHIProfilePaymentMethodModifyActionsViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 9/27/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIProfilePaymentMethodModifyActionsViewModel : EHIViewModel <MTRReactive>

@property (copy  , nonatomic) NSString *editTitle;
@property (copy  , nonatomic) NSString *paymentTitle;
@property (copy  , nonatomic) NSString *preferedTitle;
@property (copy  , nonatomic) NSString *expiredTitle;
@property (copy  , nonatomic) NSString *cardImage;
@property (assign, nonatomic) BOOL isPreferred;
@property (assign, nonatomic) BOOL isExpired;

@end
