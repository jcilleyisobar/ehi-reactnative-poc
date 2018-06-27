//
//  EHIReviewPaymentMethodViewModelTests.m
//  Enterprise
//
//  Created by Rafael Machado on 15/03/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHIReviewPaymentMethodViewModel.h"

SpecBegin(EHIReviewPaymentMethodViewModelTests)

describe(@"EHIReviewPaymentMethodViewModel", ^{
    EHIReviewPaymentMethodViewModel *model = EHIReviewPaymentMethodViewModel.new;

    context(@"when have a credit card number", ^{
        it(@"properly format it ", ^{
            EHIUserPaymentMethod *payment = [EHIUserPaymentMethod modelWithDictionary:@{
                @"payment_type" : @"CREDIT_CARD",
                @"last_four"    : @"1111",
            }];

            [model updateWithModel:payment];

            expect(model.paymentTitle).to.equal(payment.customDisplayName);
        });
    });
});

SpecEnd
