//
//  EHIReviewPaymentMethodViewModel.h
//  Enterprise
//
//  Created by Stu Buchbinder on 11/3/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIReviewPaymentMethodViewModel : EHIViewModel <MTRReactive>
@property (copy  , nonatomic, readonly) NSString *title;
@property (copy  , nonatomic, readonly) NSString *paymentTitle;
@property (copy  , nonatomic, readonly) NSString *subtitle;
@property (copy  , nonatomic, readonly) NSString *cardImage;
@property (copy  , nonatomic, readonly) NSString *terms;
@property (copy  , nonatomic, readonly) NSAttributedString *readTermsTitle;
@property (assign, nonatomic, readonly) BOOL shouldHandleTouches;
@property (assign, nonatomic) BOOL showTermsToggle;
@property (assign, nonatomic) BOOL readTerms;
@property (assign, nonatomic) BOOL isCorporateFlow;

- (void)showTerms;
- (instancetype)initWithModel:(EHIUserPaymentMethod *)model forCorporateFlow:(BOOL)isCorporateFlow inModify:(BOOL)modify;

@end
