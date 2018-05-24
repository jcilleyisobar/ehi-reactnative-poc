//
//  EHIReservationBookStateBuilderTests.m
//  Enterprise
//
//  Created by Rafael Ramos on 22/03/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHIReservationBookStateBuilder.h"
#import "EHILocalization.h"

SpecBegin(EHIReservationBookStateBuilderTests)

describe(@"the book button title and subtitle", ^{
    __block NSString *title;
    __block NSString *subtitle;
    
    context(@"the title" , ^{
        context(@"on regular flow, paylter, no payment method", ^{
            beforeAll(^{
                title = EHIReservationBookStateBuilder.new.title;
            });
            
            it(@"should show add", ^{
                expect(title).to.localizeFrom(@"reservations_review_book_button_title");
            });
        });
        
        context(@"on regular flow, prepay, no payment method", ^{
            beforeAll(^{
                title    = EHIReservationBookStateBuilder.new.prepay(YES).title;
                subtitle = EHIReservationBookStateBuilder.new.prepay(YES).subtitle;
            });
            
            it(@"should show add", ^{
                expect(title).to.localizeFrom(@"reservations_review_add_payment_button_title");
                expect(subtitle).to.beNil();
            });
        });
        
        context(@"on modify flow", ^{
            beforeAll(^{
                title = EHIReservationBookStateBuilder.new.modify(YES).title;
            });

            it(@"should show modify", ^{
                expect(title).to.localizeFrom(@"reservations_modify_review_book_button_title");
            });
        });
        
        context(@"on prepay modify flow", ^{
            beforeAll(^{
                title = EHIReservationBookStateBuilder.new.modify(YES).title;
            });
            
            it(@"should show modify", ^{
                expect(title).to.localizeFrom(@"reservations_modify_review_book_button_title");
            });
        });
    });
    
    context(@"the subtitle", ^{
        context(@"when paylater", ^{
            beforeAll(^{
                subtitle = EHIReservationBookStateBuilder.new.subtitle;
            });
            
            it(@"should show pay at pickup", ^{
                expect(subtitle).to.localizeFrom(@"reservations_review_book_button_subtitle");
            });
        });
        
        context(@"when prepay using a credit card", ^{
            beforeAll(^{
                subtitle = EHIReservationBookStateBuilder.new.prepay(YES).addedCreditCard(YES).subtitle;
            });
            
            it(@"should show pay now", ^{
                expect(subtitle).to.localizeFrom(@"review_prepay_pay_now");
            });
        });
        
        context(@"when prepay has unpaid", ^{
            __block EHICarClassPriceDifference *priceDifference;
            
            beforeAll(^{
                priceDifference = [EHICarClassPriceDifference modelWithDictionary:@{
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
                 }];
                
                subtitle = EHIReservationBookStateBuilder.new.prepay(YES).priceDifference(priceDifference).subtitle;
            });
            
            it(@"should show unpaid amount", ^{
                expect(subtitle).to.localizeFromMap(@"review_payment_unpaid_amount_action", @{
                    @"amount" : @"€10.00"
                });
            });
        });
        
        context(@"when prepay has refund", ^{
            __block EHICarClassPriceDifference *priceDifference;
            
            beforeAll(^{
                priceDifference = [EHICarClassPriceDifference modelWithDictionary:@{
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
                 }];
                
                subtitle = EHIReservationBookStateBuilder.new.prepay(YES).priceDifference(priceDifference).subtitle;
            });
            
            it(@"should show refund amount", ^{
                expect(subtitle).to.localizeFromMap(@"review_payment_refund_amount_action", @{
                    @"amount" : @"€10.00"
                });
            });
        });

        context(@"when prepay has price difference and have different currency", ^{
            __block EHICarClassPriceDifference *priceDifference;

            beforeAll(^{
                priceDifference = [EHICarClassPriceDifference modelWithDictionary:@{
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
                }];

                EHICarClassPriceSummary *price = [EHICarClassPriceSummary modelWithDictionary:@{
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
                }];

                subtitle = EHIReservationBookStateBuilder.new.prepay(YES).priceDifference(priceDifference).currencyConversion(price).subtitle;
            });

            it(@"should show unpaid amount", ^{
                expect(subtitle).to.localizeFromMap(@"review_payment_unpaid_amount_action", @{
                    @"amount" : @"€10.00"
                });
            });
        });

        context(@"when booking on North America countries", ^{
            beforeAll(^{
                EHICarClassPriceSummary *price = [EHICarClassPriceSummary modelWithDictionary:@{
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
                }];
                
                subtitle = EHIReservationBookStateBuilder.new.currencyConversion(price).subtitle;
            });
            
            it(@"should show conversion rates on subtitle", ^{
                expect(subtitle).to.localizeFromMap(@"review_prepay_na_book_button_subtitle", @{
                    @"amount" : @"CA$23.73",
                });
            });
        });
        
        context(@"when payment method is custom billing", ^{
            __block EHIUserPaymentMethod *paymentMethod;
            
            beforeAll(^{
                paymentMethod = [EHIUserPaymentMethod customBillingMethod];
                
                subtitle = EHIReservationBookStateBuilder.new.paymentMethod(paymentMethod).subtitle;
            });
            
            it(@"should show bill to payment method name", ^{
                expect(subtitle).to.localizeFromMap(@"reservations_review_book_button_billing_subtitle", @{
                    @"account" : paymentMethod.alias
               });
            });
        });
        
        context(@"when payment method is custom billing, on a business trip", ^{
            __block EHIUserPaymentMethod *paymentMethod;
            
            beforeAll(^{
                paymentMethod = [EHIUserPaymentMethod customBillingMethod];
                
                subtitle = EHIReservationBookStateBuilder.new.paymentMethod(paymentMethod).businessTrip(YES).subtitle;
            });
            
            it(@"should show bill to payment method name", ^{
                expect(subtitle).to.localizeFromMap(@"reservations_review_book_button_billing_subtitle", @{
                    @"account" : paymentMethod.alias
                });
            });
        });
        
        context(@"when payment method is existing billing, on a business trip", ^{
            __block EHIUserPaymentMethod *paymentMethod;
            
            beforeAll(^{
                paymentMethod = [EHIUserPaymentMethod existingBillingMethod];
                [paymentMethod updateWithDictionary:@{ @"alias" : @"alias" }];
                
                subtitle = EHIReservationBookStateBuilder.new.paymentMethod(paymentMethod).businessTrip(YES).subtitle;
            });
            
            it(@"should show bill to payment method name", ^{
                expect(subtitle).to.localizeFromMap(@"reservations_review_book_button_billing_subtitle", @{
                    @"account" : paymentMethod.alias
                });
            });
        });
        
        context(@"when payment method is used and has a discount attached", ^{
            __block EHIUserPaymentMethod *paymentMethod;
            __block NSString *discount = @"Discount";
            
            beforeAll(^{
                paymentMethod = [EHIUserPaymentMethod existingBillingMethod];
                
                subtitle = EHIReservationBookStateBuilder.new.paymentMethod(paymentMethod).businessTrip(YES).discount(discount).subtitle;
            });
            
            it(@"should show bill to payment method name", ^{
                expect(subtitle).to.localizeFromMap(@"reservations_review_book_button_billing_subtitle", @{
                    @"account" : discount
                });
            });
        });
        
        context(@"when payment method is credit card", ^{
            __block EHIUserPaymentMethod *paymentMethod;
            
            beforeAll(^{
                paymentMethod = [EHIUserPaymentMethod mock:@"user_payment_method"];
                
                subtitle = EHIReservationBookStateBuilder.new.paymentMethod(paymentMethod).addedCreditCard(YES).subtitle;
            });
            
            it(@"should show unpaid amount", ^{
                expect(subtitle).to.localizeFromMap(@"review_prepay_credit_card_book_button_subtitle", @{
                    @"method" : paymentMethod.customDisplayName
                });
            });
        });
        
        context(@"when payment method is credit card, and has unpaid amount", ^{
            __block EHIUserPaymentMethod *paymentMethod;
            __block EHICarClassPriceDifference *priceDifference;
            
            beforeAll(^{
                paymentMethod   = [EHIUserPaymentMethod mock:@"user_payment_method"];
                priceDifference = [EHICarClassPriceDifference modelWithDictionary:@{
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
                  }];
                
                subtitle = EHIReservationBookStateBuilder.new.paymentMethod(paymentMethod).priceDifference(priceDifference).subtitle;
            });
            
            it(@"should show unpaid amount", ^{
                expect(subtitle).to.localizeFromMap(@"review_payment_unpaid_amount_action", @{
                    @"amount" : @"€10.00"
                });
            });
        });
        
        context(@"when payment method is credit card, and has refund amount", ^{
            __block EHIUserPaymentMethod *paymentMethod;
            __block EHICarClassPriceDifference *priceDifference;
            beforeAll(^{
                paymentMethod   = [EHIUserPaymentMethod mock:@"user_payment_method"];
                priceDifference = [EHICarClassPriceDifference modelWithDictionary:@{
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
                 }];
                
                subtitle = EHIReservationBookStateBuilder.new.paymentMethod(paymentMethod).priceDifference(priceDifference).subtitle;
            });
            
            it(@"should show unpaid amount", ^{
                expect(subtitle).to.localizeFromMap(@"review_payment_refund_amount_action", @{
                    @"amount" : @"€10.00"
                });
            });
        });
    });
});

SpecEnd
