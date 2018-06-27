//
//  EHIReviewViewModelTests.m
//  Enterprise
//
//  Created by Rafael Ramos on 20/03/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHIAlertViewBuilder.h"
#import "EHIRouter.h"
#import "EHIReservationBuilder_Private.h"
#import "EHIReviewViewModel.h"
#import "EHILocalization.h"

SpecBegin(EHIReviewViewModelTests)

describe(@"the review view model book button", ^{
    __block EHIReservationBuilder *builder = [EHIReservationBuilder sharedInstance];
    __block EHIReviewViewModel *model      = [EHIReviewViewModel new];
    __block EHIReservation *reservation    = [EHIReservation mock:@"reservation"];
    __block EHIUserPaymentMethod *payment  = [EHIUserPaymentMethod mock:@"user_payment_method"];
    
    context(@"title on prepay reservations", ^{
        context(@"should show add title", ^{
            beforeAll(^{
                [builder setReservation:reservation];
                [model updateBookButton:nil];
            });
            
            it(@"on regular flow", ^{
                expect(model.bookButtonStringTitle).to.localizeFrom(@"reservations_review_add_payment_button_title");
            });
        });
        
        context(@"should show modify title", ^{
            beforeAll(^{
                [builder setReservation:reservation];
                
                [builder setCurrentFlow:EHIReservationBuilderFlowModify];
                [model updateBookButton:nil];
            });
            
            it(@"on modify flow", ^{
                expect(model.bookButtonStringTitle).to.localizeFrom(@"reservations_modify_review_book_button_title");
            });
            
            afterAll(^{
                [builder setCurrentFlow:EHIReservationBuilderFlowDefault];
            });
        });
    });
    
    context(@"title on pay later reservations", ^{
        context(@"should show add title", ^{
            beforeAll(^{
                // make reservation paylater
                [reservation updateWithDictionary:@{
                    @key(reservation.prepaySelected) : @(NO)
                 }];
                
                [builder setReservation:reservation];
                [model updateBookButton:nil];
            });
            
            it(@"on regular flow", ^{
                expect(model.bookButtonStringTitle).to.localizeFrom(@"reservations_review_book_button_title");
            });
        });
        
        context(@"should show modify title", ^{
            beforeAll(^{
                [builder setCurrentFlow:EHIReservationBuilderFlowModify];
                [model updateBookButton:nil];
            });
            
            it(@"on modify flow", ^{
                expect(model.bookButtonStringTitle).to.localizeFrom(@"reservations_modify_review_book_button_title");
            });
            
            afterAll(^{
                // make reservation paynow
                reservation = [EHIReservation mock:@"reservation"];
                [builder setReservation:reservation];
                [builder setCurrentFlow:EHIReservationBuilderFlowDefault];
            });
        });
    });
    
    context(@"subtitle on prepay reservations", ^{
        context(@"when res from USA to CA", ^{
            beforeAll(^{
                EHICarClass *carClass = reservation.selectedCarClass;
                [carClass updateWithDictionary:@{
                    @"vehicle_rates" : @[@{
                        @"charge_type" : @"PREPAY",
                            @"price_summary": @{
                                @"estimated_total_view": @{
                                    @"code"   : @"USD",
                                    @"symbol" : @"USD",
                                    @"amount" : @"23.73"
                                },
                                @"estimated_total_payment": @{
                                    @"code"   : @"CAD",
                                    @"symbol" : @"CAD",
                                    @"amount" : @"23.73"
                                }
                            },
                      }]
                }];
                
                [reservation updateWithDictionary:@{ @"car_class_details" : carClass }];
                
                [builder setReservation:reservation];
                [model updateCarClassPrice:nil];
                [model updateBookButton:nil];
            });
            
            it(@"should show conversion rates on subtitle", ^{
                expect([model.totalPrice eligibleForCurrencyConvertion]).to.beTruthy();
                expect(model.bookButtonStringSubtitle).to.localizeFromMap(@"review_prepay_na_book_button_subtitle", @{
                    @"amount" : @"CA$23.73",
                 });
            });
            
            afterAll(^{
                [builder setReservation:[EHIReservation mock:@"reservation"]];
                [model updateCarClassPrice:nil];
            });
        });
        
        context(@"when pays a prepay res with a payment method, using credit card", ^{
            beforeAll(^{
                [builder setReservation:[EHIReservation mock:@"reservation"]];
                [builder setPaymentMethod:payment];
                [model updateBookButton:nil];
            });
            
            it(@"should show the credit card masked number", ^{
                expect(model.bookButtonStringSubtitle).to.localizeFromMap(@"review_prepay_credit_card_book_button_subtitle", @{
                    @"method" : payment.customDisplayName,
                });
            });
            
            afterAll(^{
                [builder setPaymentMethod:nil];
            });
        });
        
        context(@"when pays a prepay res with a payment method, using billing", ^{
            beforeAll(^{
                [builder setReservation:[EHIReservation mock:@"reservation"]];
                
                [payment updateWithDictionary:@{
                    @"payment_type"         : @"BUSINESS_ACCOUNT_APPLICANT",
                    @"billing_account_type" : @"EXISTING"
                }];
                
                [builder setPaymentMethod:payment];
                [model updateBookButton:nil];
            });
            
            it(@"should show the credit card masked number", ^{
                expect(model.bookButtonStringSubtitle).to.localizeFromMap(@"reservations_review_book_button_billing_subtitle", @{
                    @"account" : payment.lastFour,
                });
            });
            
            afterAll(^{
                [builder setPaymentMethod:nil];
            });
        });
        
        context(@"when pays a prepay res with a payment method, and has unpaid amount", ^{
            beforeAll(^{
                EHICarClass *carClass = reservation.selectedCarClass;
                [carClass updateWithDictionary:@{
                    @"price_differences" : @[
                        @{
                            @"difference_type" : @"UNPAID_REFUND_AMOUNT",
                            @"difference_amount_view" : @{
                                @"code"   : @"EUR",
                                @"symbol" : @"EUR",
                                @"amount" : @"10.00"
                            },
                            @"difference_amount_payment": @{
                                @"code"   : @"EUR",
                                @"symbol" : @"EUR",
                                @"amount" : @"10.00"
                            }
                    }]
                }];

                [reservation updateWithDictionary:@{ @"car_class_details" : carClass }];
                [builder setReservation:reservation];
                [builder setPaymentMethod:[EHIUserPaymentMethod mock:@"user_payment_method"]];
                [model updateBookButton:nil];
            });
            
            it(@"should show unpaid amount", ^{
                expect(model.bookButtonStringSubtitle).to.localizeFromMap(@"review_payment_unpaid_amount_action", @{
                    @"amount" : @"€10.00",
                });
            });
        });
        
        context(@"when pays a prepay res witih a payment method, and has refund amount", ^{
            beforeAll(^{
                EHICarClass *carClass = reservation.selectedCarClass;
                [carClass updateWithDictionary:@{
                    @"price_differences" : @[
                        @{
                            @"difference_type" : @"UNPAID_REFUND_AMOUNT",
                            @"difference_amount_view" : @{
                                @"code"   : @"EUR",
                                @"symbol" : @"EUR",
                                @"amount" : @"-10.00"
                            },
                             @"difference_amount_payment": @{
                                 @"code"   : @"EUR",
                                 @"symbol" : @"EUR",
                                 @"amount" : @"-10.00"
                            }
                         }]
                }];
                
                [reservation updateWithDictionary:@{ @"car_class_details" : carClass }];
                [builder setReservation:reservation];
                [builder setPaymentMethod:[EHIUserPaymentMethod mock:@"user_payment_method"]];
                [model updateBookButton:nil];
            });
            
            it(@"should show unpaid amount", ^{
                expect(model.bookButtonStringSubtitle).to.localizeFromMap(@"review_payment_refund_amount_action", @{
                    @"amount" : @"€10.00",
                });
            });
        });
    });
    
    context(@"for paylater reservations without payment method", ^{
        beforeAll(^{
            reservation = [EHIReservation mock:@"reservation"];
            
            [reservation updateWithDictionary:@{
                @key(reservation.prepaySelected) : @(NO)
            }];
            [builder setReservation:reservation];
            [model updateBookButton:nil];
        });
        
        it(@"should show have the correct book button title", ^{
            expect(model.bookButtonStringTitle).to.localizeFrom(@"reservations_review_book_button_title");
        });
    });
    
    context(@"for paynow reservations with payment method", ^{
        beforeAll(^{
            reservation = [EHIReservation mock:@"reservation"];
            
            EHIUserPaymentMethod *payment = [EHIUserPaymentMethod modelWithDictionary:@{
                @"payment_reference_id": @"9640840001176",
                @"payment_type"        : @"CREDIT_CARD",
                @"card_type"           : @"VISA",
                @"expiration_date"     : @"2018-11",
                @"first_six"           : @"411111",
            }];
            
            [builder setReservation:reservation];
            [builder setPaymentMethod:payment];
            [model updateBookButton:nil];
        });
        
        it(@"should show have the correct book button title", ^{
            expect(model.bookButtonStringSubtitle).to.localizeFrom(@"review_prepay_pay_now");
        });
        
        afterAll(^{
            [builder setPaymentMethod:nil];
            [builder setReservation:nil];
        });
    });
    
    context(@"for paynow reservations with billing payment method", ^{
        beforeAll(^{
            reservation = [EHIReservation mock:@"reservation"];
            
            payment = [EHIUserPaymentMethod modelWithDictionary:@{
                @"payment_type"           : @"BUSINESS_ACCOUNT_APPLICANT",
                @"billing_account_number" : @"BILLING ACCOUNT",
                @"mask_billing_number"    : @"**********1234"
            }];
            
            [builder setReservation:reservation];
            [builder setPaymentMethod:payment];
            [model updateBookButton:nil];
        });
        
        it(@"should show have the correct book button title", ^{
            expect(model.bookButtonStringSubtitle).to.localizeFromMap(@"reservations_review_book_button_billing_subtitle", @{
                @"account" : payment.maskedBillingNumber,
            });
        });
    });
});


SpecEnd
