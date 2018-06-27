//
//  EHIReviewPrepayPaymentViewModel.h
//  Enterprise
//
//  Created by cgross on 1/18/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"

@interface EHIReviewPrepayPaymentViewModel : EHIViewModel <MTRReactive>
/** Title for the prepay payment */
@property (copy  , nonatomic, readonly) NSString *prepayTitle;
/** Title of the credit card button */
@property (copy  , nonatomic, readonly) NSString *creditCardButtonTitle;
/** Title of remove credit card button */
@property (copy  , nonatomic, readonly) NSString *removeCreditCardButton;
/** Title for the terms & conditions button */
@property (copy  , nonatomic, readonly) NSString *termsTitle;

@end
