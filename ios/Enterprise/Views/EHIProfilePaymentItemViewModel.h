//
//  EHIProfilePaymentItemViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 9/22/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIProfilePaymentItemViewModel : EHIViewModel <MTRReactive>
@property (copy  , nonatomic) NSString *title;
@property (copy  , nonatomic) NSString *paymentTitle;
@property (copy  , nonatomic) NSAttributedString *paymentSubtitle;
@property (copy  , nonatomic) NSString *preferredTitle;
@property (copy  , nonatomic) NSString *cardImage;
@property (assign, nonatomic) BOOL isFirst;
@property (assign, nonatomic) BOOL isLast;
@property (assign, nonatomic) BOOL isPreferred;
@property (assign, nonatomic) BOOL isExpired;
@end
