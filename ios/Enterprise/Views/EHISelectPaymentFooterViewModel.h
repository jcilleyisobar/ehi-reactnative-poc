//
//  EHISelectPaymentFooterViewModel.h
//  Enterprise
//
//  Created by Stu Buchbinder on 10/27/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHISelectPaymentFooterViewModel : EHIViewModel <MTRReactive>
@property (copy  , nonatomic, readonly) NSAttributedString *terms;
@property (assign, nonatomic, readonly) BOOL continueButtonDisabled;
@property (copy  , nonatomic, readonly) NSString *continueTitle;
@property (assign, nonatomic, readonly) BOOL showTerms;
@property (weak  , nonatomic) EHIUserPaymentMethod *currentPaymentMethod;
@property (assign, nonatomic) BOOL termsRead;

- (void)didTapContinue;
- (void)toggleTermsRead;

@end
