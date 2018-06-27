//
//  EHIUserPaymentMethodTests.m
//  Enterprise
//
//  Created by Rafael Machado on 15/03/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHIUserPaymentMethod.h"

SpecBegin(EHIUserPaymentMethodTests)

describe(@"EHIUserPaymentMethod", ^{
    __block EHIUserPaymentMethod *payment;

    context(@"payment formatting", ^{
        before(^{
            payment = [EHIUserPaymentMethod modelWithDictionary:@{
                @"payment_type" : @"CREDIT_CARD",
                @"last_four"    : @"1111",
            }];
        });

        context(@"when credit card haven't alias", ^{
            it(@"should format just credit card number", ^{
                expect(payment.customDisplayName).equal(@"************1111");
            });
        });

        context(@"when credit card have alias", ^{
            before(^{
                [payment updateWithDictionary:@{
                   @"alias" : @"alias",
                }];
            });

            it(@"should format using alias", ^{
                expect(payment.customDisplayName).equal(@"alias ************1111");
            });
        });
    });

    context(@"billing formatting", ^{
        before(^{
            payment = [EHIUserPaymentMethod modelWithDictionary:@{
                @"payment_type"                : @"BUSINESS_ACCOUNT_APPLICANT",
            }];
        });

        context(@"when billing have alias and masked number", ^{
            before(^{
                [payment updateWithDictionary:@{
                    @"alias"                       : @"alias",
                    @"mask_billing_account_number" : @"****1111",
                }];
            });

            it(@"should format using masked number and alias", ^{
                expect(payment.customDisplayName).to.equal(@"alias (****1111)");
            });
        });

        context(@"when billing haven't alias and masked number", ^{
            before(^{
                [payment updateWithDictionary:@{
                    @"mask_billing_account_number" : @"****1111",
                }];
            });

            it(@"should format using masked number and alias", ^{
                expect(payment.customDisplayName).to.equal(@"****1111");
            });
        });

        context(@"when billing have alias and not masked number", ^{
            before(^{
                [payment updateWithDictionary:@{
                    @"alias" : @"alias",
                }];
            });

            it(@"should format using masked number and alias", ^{
                expect(payment.customDisplayName).to.equal(@"alias");
            });
        });
    });
});

SpecEnd
