//
//  EHIConfirmationPaymentOptionViewModelTests.m
//  Enterprise
//
//  Created by Rafael Ramos on 7/13/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHIConfirmationPaymentOptionViewModel.h"
#import "EHIReservationPaymentMethod.h"

SpecBegin(EHIConfirmationPaymentOptionViewModelTests)

describe(@"EHIConfirmationPaymentOptionViewModel", ^{
	context(@"on user payment method", ^{
		it(@"with billing account", ^{
            EHIConfirmationPaymentOptionViewModel *model = EHIConfirmationPaymentOptionViewModel.new;
            EHIUserPaymentMethod *paymentMethod = [EHIUserPaymentMethod modelWithDictionary:@{
				@"alias"       : @"ALIAS",
				@"payment_type": @"BUSINESS_ACCOUNT_APPLICANT",
			}];

			[model updateWithModel:paymentMethod];

			expect(model.paymentTitle).to.localizeFrom(@"reservation_confirmation_payment_billing_title");
			expect(model.value).to.equal(paymentMethod.customDisplayName);
			expect(model.policies).to.beNil();
			expect(model.cardImage).to.beNil();
			expect(model.hidePoliciesLink).to.beTruthy();
		});

		it(@"with empty account", ^{
            EHIConfirmationPaymentOptionViewModel *model = EHIConfirmationPaymentOptionViewModel.new;
            [model updateWithModel:[EHIUserPaymentMethod emptyPaymentMethod]];

			expect(model.paymentTitle).to.localizeFrom(@"reservation_confirmation_payment_pick_up_title");
			expect(model.value).to.localizeFrom(@"review_payment_options_payment_subtitle");
			expect(model.policies).to.beNil();
			expect(model.cardImage).to.beNil();
			expect(model.hidePoliciesLink).to.beTruthy();
		});
	});
    
    context(@"on reservation payment method", ^{
		it(@"with credit card", ^{
            EHIConfirmationPaymentOptionViewModel *model = EHIConfirmationPaymentOptionViewModel.new;
			EHIReservationPaymentMethod *paymentMethod = [EHIReservationPaymentMethod modelWithDictionary:@{
				@"card_details": @{
					@"card_type": @"VISA",
					@"number"   : @"4057629998",
				}
			}];

			[model updateWithModel:paymentMethod];

			expect(model.paymentTitle).to.localizeFrom(@"reservation_confirmation_prepay_payment_title");
			expect(model.value).to.equal(@"************9998");
			expect(model.policies).to.localizeFrom(@"general_prepay_policies");
			expect(model.cardImage).to.equal(@"creditcard_01_visa");
			expect(model.hidePoliciesLink).to.beFalsy();
		});
	});
});

SpecEnd
