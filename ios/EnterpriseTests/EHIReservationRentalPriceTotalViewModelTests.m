//
//  EHIReservationRentalPriceTotalViewModelTests.m
//  Enterprise
//
//  Created by Rafael Ramos on 30/03/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

//EHIReservationRentalPriceTotalViewModelTests

#import "EHITests.h"
#import "EHIReservationRentalPriceTotalViewModel.h"

SpecBegin(EHIReservationRentalPriceTotalViewModelTests)

describe(@"the reservation rental total price cancel modal", ^{
    __block EHIReservationRentalPriceTotalViewModel *model = [EHIReservationRentalPriceTotalViewModel new];
    __block EHIReservationRentalPriceTotalLayout layout    = EHIReservationRentalPriceTotalLayoutReview;
    __block BOOL prepay = NO;
    __block BOOL showOtherOption = NO;
    
    context(@"default texts", ^{
        expect(model.updatedTotalTitle).to.localizeFrom(@"review_payment_updated_total_title");
        expect(model.paidAmountTitle).to.localizeFrom(@"review_payment_paid_amount_title");
        expect(model.originalTotal).to.localizeFrom(@"review_payment_original_total_title");
    });

    context(@"Given a reservation prepay or not", ^{
        context(@"When there it is in secret rate scenario", ^{
            beforeAll(^{
                model = [[EHIReservationRentalPriceTotalViewModel alloc] initWithModel:nil
                                                                        prepaySelected:prepay
                                                                            paidAmount:nil
                                                                          actualAmount:nil
                                                                       showOtherOption:showOtherOption
                                                                                layout:layout
                                                                          isSecretRate:YES];
            });
            
            it(@"Then the title and transparency title should be set as net rate", ^{
                expect(model.total.string).to.localizeFrom(@"reservation_price_unavailable");
                expect(model.transparency.string).to.localizeFrom(@"reservation_price_unavailable");
            });
        });
    });
    
    context(@"paylater res", ^{
        beforeAll(^{
            prepay = NO;
        });
        
        context(@"invoice layout", ^{
           beforeAll(^{
               layout = EHIReservationRentalPriceTotalLayoutInvoice;
            });
            
            context(@"showing other option", ^{
                beforeAll(^{
                    showOtherOption = YES;
                    model = [[EHIReservationRentalPriceTotalViewModel alloc] initWithModel:nil
                                                                            prepaySelected:prepay
                                                                                paidAmount:nil
                                                                              actualAmount:nil
                                                                           showOtherOption:showOtherOption
                                                                                    layout:layout
                                                                              isSecretRate:NO];
                });
                it(@"", ^{
                    expect(model.showTopDivider).to.beTruthy();
                    expect(model.totalTitle).to.localizeFrom(@"trip_summary_final_total");
                    expect(model.paidAmountLabel).to.beNil();
                    expect(model.endOfRental).to.beNil();
                    expect(model.updatedTotalTitle).to.localizeFrom(@"review_payment_updated_total_title");
                    expect(model.updatedTotalLabel.string).to.equal(@"$0.00");
                    expect(model.paidAmountTitle).to.localizeFrom(@"review_payment_paid_amount_title");
                    expect(model.originalTotal).to.localizeFrom(@"review_payment_original_total_title");
                    expect(model.transparencyTitle).to.localizeFromMap(@"car_class_details_transparency_total", @{
                        @"currency_code" : @""
                    });
                    expect(model.total.string).to.equal(@"$0.00");
                    expect(model.transparency.string).to.equal(@"$0.00");

                    NSString *localized = EHILocalizedString(@"reservation_review_pay_now_na", @"", @"");
                    localized = [localized ehi_applyReplacementMap:@{
                        @"amount" : @"$0.00"
                    }].lowercaseString;

                    expect(model.otherPaymentOptionTotal).equal(localized);
                });
            });

            context(@"hiding other option", ^{
                beforeAll(^{
                    showOtherOption = NO;
                    model = [[EHIReservationRentalPriceTotalViewModel alloc] initWithModel:nil
                                                                            prepaySelected:prepay
                                                                                paidAmount:nil
                                                                              actualAmount:nil
                                                                           showOtherOption:showOtherOption
                                                                                    layout:layout
                                                                              isSecretRate:NO];
                });
                it(@"", ^{
                    expect(model.showTopDivider).to.beTruthy();
                    expect(model.totalTitle).to.localizeFrom(@"trip_summary_final_total");
                    expect(model.paidAmountLabel).to.beNil();
                    expect(model.endOfRental).to.beNil();
                    expect(model.updatedTotalTitle).to.localizeFrom(@"review_payment_updated_total_title");
                    expect(model.updatedTotalLabel.string).to.equal(@"$0.00");
                    expect(model.paidAmountTitle).to.localizeFrom(@"review_payment_paid_amount_title");
                    expect(model.originalTotal).to.localizeFrom(@"review_payment_original_total_title");
                    expect(model.transparencyTitle).to.localizeFromMap(@"car_class_details_transparency_total", @{
                        @"currency_code" : @""
                    });
                    expect(model.total.string).to.equal(@"$0.00");
                    expect(model.transparency.string).to.equal(@"$0.00");
                    expect(model.otherPaymentOptionTotal).to.beNil();
                });
            });
            
            context(@"with total price", ^{
                beforeAll(^{
                    EHIPrice *price = [EHIPrice modelWithDictionary:@{
                        @"amount" : @"31.31",
                        @"code"   : @"USD",
                        @"symbol" : @"$"
                    }];
                    showOtherOption = NO;
                    model = [[EHIReservationRentalPriceTotalViewModel alloc] initWithModel:price
                                                                            prepaySelected:prepay
                                                                                paidAmount:nil
                                                                              actualAmount:nil
                                                                           showOtherOption:showOtherOption
                                                                                    layout:layout
                                                                              isSecretRate:NO];
                });
                it(@"", ^{
                    expect(model.showTopDivider).to.beTruthy();
                    expect(model.totalTitle).to.localizeFrom(@"trip_summary_final_total");
                    expect(model.paidAmountLabel).to.beNil();
                    expect(model.endOfRental).to.beNil();
                    expect(model.updatedTotalTitle).to.localizeFrom(@"review_payment_updated_total_title");
                    expect(model.updatedTotalLabel.string).to.equal(@"$0.00");
                    expect(model.paidAmountTitle).to.localizeFrom(@"review_payment_paid_amount_title");
                    expect(model.originalTotal).to.localizeFrom(@"review_payment_original_total_title");
                    expect(model.transparencyTitle).to.localizeFromMap(@"car_class_details_transparency_total", @{
                        @"currency_code" : @""
                    });
                    expect(model.total.string).to.equal(@"$31.31");
                    expect(model.transparency.string).to.equal(@"$0.00");
                    expect(model.otherPaymentOptionTotal).to.beNil();
                });
            });
        });
        
        context(@"review layout", ^{
            beforeAll(^{
                layout = EHIReservationRentalPriceTotalLayoutReview;
            });

            context(@"showing other option", ^{
                beforeAll(^{
                    showOtherOption = YES;
                    model = [[EHIReservationRentalPriceTotalViewModel alloc] initWithModel:nil
                                                                            prepaySelected:prepay
                                                                                paidAmount:nil
                                                                              actualAmount:nil
                                                                           showOtherOption:showOtherOption
                                                                                    layout:layout
                                                                              isSecretRate:NO];
                });
                it(@"", ^{
                    expect(model.showTopDivider).to.beFalsy();
                    expect(model.paidAmountLabel).to.beNil();
                    expect(model.endOfRental).to.beNil();
                    expect(model.totalTitle).to.localizeFrom(@"reservation_review_estimated_total_title");
                    expect(model.updatedTotalTitle).to.localizeFrom(@"review_payment_updated_total_title");
                    expect(model.updatedTotalLabel.string).to.equal(@"$0.00");
                    expect(model.paidAmountTitle).to.localizeFrom(@"review_payment_paid_amount_title");
                    expect(model.paidAmountLabel).to.beNil();
                    expect(model.originalTotal).to.localizeFrom(@"review_payment_original_total_title");
                    expect(model.endOfRental).to.beNil();
                    expect(model.transparencyTitle).to.localizeFromMap(@"car_class_details_transparency_total", @{
                        @"currency_code" : @""
                    });
                    expect(model.total.string).to.equal(@"$0.00");
                    expect(model.transparency.string).to.equal(@"$0.00");

                    NSString *localized = EHILocalizedString(@"reservation_review_pay_now_na", @"", @"");
                    localized = [localized ehi_applyReplacementMap:@{
                        @"amount" : @"$0.00"
                    }].lowercaseString;
                    expect(model.otherPaymentOptionTotal).equal(localized);
                });
            });

            context(@"hiding other option", ^{
                beforeAll(^{
                    showOtherOption = NO;
                    model = [[EHIReservationRentalPriceTotalViewModel alloc] initWithModel:nil
                                                                            prepaySelected:prepay
                                                                                paidAmount:nil
                                                                              actualAmount:nil
                                                                           showOtherOption:showOtherOption
                                                                                    layout:layout
                                                                              isSecretRate:NO];
                });
                it(@"", ^{
                    expect(model.showTopDivider).to.beFalsy();
                    expect(model.paidAmountLabel).to.beNil();
                    expect(model.endOfRental).to.beNil();
                    expect(model.totalTitle).to.localizeFrom(@"reservation_review_estimated_total_title");
                    expect(model.updatedTotalTitle).to.localizeFrom(@"review_payment_updated_total_title");
                    expect(model.updatedTotalLabel.string).to.equal(@"$0.00");
                    expect(model.paidAmountTitle).to.localizeFrom(@"review_payment_paid_amount_title");
                    expect(model.paidAmountLabel).to.beNil();
                    expect(model.originalTotal).to.localizeFrom(@"review_payment_original_total_title");
                    expect(model.endOfRental).to.beNil();
                    expect(model.transparencyTitle).to.localizeFromMap(@"car_class_details_transparency_total", @{
                        @"currency_code" : @""
                    });
                    expect(model.total.string).to.equal(@"$0.00");
                    expect(model.transparency.string).to.equal(@"$0.00");
                    expect(model.otherPaymentOptionTotal).to.beNil();
                });
            });

            context(@"CA to USA, with car class model", ^{
                beforeAll(^{
                    EHICarClass *carClass = [EHICarClass modelWithDictionary:@{
                        @"vehicle_rates": @[
                            @{
                                @"charge_type": @"PAYLATER",
                                @"price_summary" : @{
                                    @"total_charged" : @"23.73",
                                    @"estimated_total_view": @{
                                        @"code"   : @"CAD",
                                        @"symbol" : @"$",
                                        @"amount" : @"23.73"
                                    },
                                    @"estimated_total_payment": @{
                                        @"code"   : @"USD",
                                        @"symbol" : @"$",
                                        @"amount" : @"23.73"
                                    }
                                }
                            }
                        ]
                    }];

                    showOtherOption = YES;
                    model = [[EHIReservationRentalPriceTotalViewModel alloc] initWithModel:carClass
                                                                            prepaySelected:prepay
                                                                                paidAmount:nil
                                                                              actualAmount:nil
                                                                           showOtherOption:showOtherOption
                                                                                    layout:layout
                                                                              isSecretRate:NO];
                });
                it(@"", ^{
                    expect(model.showTopDivider).to.beFalsy();
                    expect(model.paidAmountLabel).to.beNil();
                    expect(model.endOfRental).to.beNil();
                    expect(model.totalTitle).to.localizeFrom(@"reservation_review_estimated_total_title");
                    expect(model.updatedTotalTitle).to.localizeFrom(@"review_payment_updated_total_title");
                    expect(model.updatedTotalLabel.string).to.equal(@"CA$23.73");
                    expect(model.paidAmountTitle).to.localizeFrom(@"review_payment_paid_amount_title");
                    expect(model.paidAmountLabel).to.beNil();
                    expect(model.originalTotal).to.localizeFrom(@"review_payment_original_total_title");
                    expect(model.endOfRental).to.beNil();
                    expect(model.transparencyTitle).to.localizeFromMap(@"car_class_details_transparency_total_na", @{
                        @"currency_code" : @"USD"
                    });
                    expect(model.total.string).to.equal(@"CA$23.73");
                    expect(model.transparency.string).to.equal(@"$23.73");

                    NSString *localized = EHILocalizedString(@"reservation_review_pay_now_na", @"", @"");
                    localized = [localized ehi_applyReplacementMap:@{
                        @"amount" : @"$0.00"
                    }].lowercaseString;
                    expect(model.otherPaymentOptionTotal).equal(localized);
                });
            });

            context(@"USA to CA, with car class model", ^{
                beforeAll(^{
                    EHICarClass *carClass = [EHICarClass modelWithDictionary:@{
                        @"vehicle_rates": @[
                            @{
                                @"charge_type": @"PAYLATER",
                                @"price_summary" : @{
                                    @"total_charged" : @"23.73",
                                    @"estimated_total_view": @{
                                        @"code"   : @"USD",
                                        @"symbol" : @"$",
                                        @"amount" : @"23.73"
                                    },
                                    @"estimated_total_payment": @{
                                        @"code"   : @"CAD",
                                        @"symbol" : @"$",
                                        @"amount" : @"23.73"
                                    }
                                }
                            }
                        ]
                    }];

                    showOtherOption = YES;
                    model = [[EHIReservationRentalPriceTotalViewModel alloc] initWithModel:carClass
                                                                            prepaySelected:prepay
                                                                                paidAmount:nil
                                                                              actualAmount:nil
                                                                           showOtherOption:showOtherOption
                                                                                    layout:layout
                                                                              isSecretRate:NO];
                });
                it(@"", ^{
                    expect(model.showTopDivider).to.beFalsy();
                    expect(model.paidAmountLabel).to.beNil();
                    expect(model.endOfRental).to.beNil();
                    expect(model.totalTitle).to.localizeFrom(@"reservation_review_estimated_total_title");
                    expect(model.updatedTotalTitle).to.localizeFrom(@"review_payment_updated_total_title");
                    expect(model.updatedTotalLabel.string).to.equal(@"$23.73");
                    expect(model.paidAmountTitle).to.localizeFrom(@"review_payment_paid_amount_title");
                    expect(model.paidAmountLabel).to.beNil();
                    expect(model.originalTotal).to.localizeFrom(@"review_payment_original_total_title");
                    expect(model.endOfRental).to.beNil();
                    expect(model.transparencyTitle).to.localizeFromMap(@"car_class_details_transparency_total_na", @{
                        @"currency_code" : @"CAD"
                    });
                    expect(model.total.string).to.equal(@"$23.73");
                    expect(model.transparency.string).to.equal(@"CA$23.73");

                    NSString *localized = EHILocalizedString(@"reservation_review_pay_now_na", @"", @"");
                    localized = [localized ehi_applyReplacementMap:@{
                        @"amount" : @"$0.00"
                    }].lowercaseString;
                    expect(model.otherPaymentOptionTotal).equal(localized);
                });
            });
        });
        
        context(@"refund layout", ^{
            beforeAll(^{
                layout = EHIReservationRentalPriceTotalLayoutUnpaidRefund;
            });

            context(@"showing other option", ^{
                beforeAll(^{
                    showOtherOption = YES;
                    model = [[EHIReservationRentalPriceTotalViewModel alloc] initWithModel:nil
                                                                            prepaySelected:prepay
                                                                                paidAmount:nil
                                                                              actualAmount:nil
                                                                           showOtherOption:showOtherOption
                                                                                    layout:layout
                                                                              isSecretRate:NO];
                });
                it(@"", ^{
                    expect(model.showTopDivider).to.beFalsy();
                    expect(model.totalTitle).to.localizeFrom(@"review_payment_unpaid_amount_title");
                    expect(model.paidAmountLabel).to.beNil();
                    expect(model.endOfRental).to.localizeFrom(@"review_payment_unpaid_at_end_title");
                    expect(model.updatedTotalTitle).to.localizeFrom(@"review_payment_updated_total_title");
                    expect(model.updatedTotalLabel.string).to.equal(@"$0.00");
                    expect(model.paidAmountTitle).to.localizeFrom(@"review_payment_paid_amount_title");
                    expect(model.originalTotal).to.localizeFrom(@"review_payment_original_total_title");
                    expect(model.transparencyTitle).to.localizeFromMap(@"car_class_details_transparency_total", @{
                        @"currency_code" : @""
                    });
                    expect(model.total.string).to.equal(@"$0.00");
                    expect(model.transparency.string).to.equal(@"$0.00");

                    NSString *localized = EHILocalizedString(@"reservation_review_pay_now_na", @"", @"");
                    localized = [localized ehi_applyReplacementMap:@{
                        @"amount" : @"$0.00"
                    }].lowercaseString;
                    expect(model.otherPaymentOptionTotal).equal(localized);
                });
            });

            context(@"hiding other option", ^{
                beforeAll(^{
                    showOtherOption = NO;
                    model = [[EHIReservationRentalPriceTotalViewModel alloc] initWithModel:nil
                                                                            prepaySelected:prepay
                                                                                paidAmount:nil
                                                                              actualAmount:nil
                                                                           showOtherOption:showOtherOption
                                                                                    layout:layout
                                                                              isSecretRate:NO];
                });
                it(@"", ^{
                    expect(model.showTopDivider).to.beFalsy();
                    expect(model.totalTitle).to.localizeFrom(@"review_payment_unpaid_amount_title");
                    expect(model.paidAmountLabel).to.beNil();
                    expect(model.endOfRental).to.localizeFrom(@"review_payment_unpaid_at_end_title");
                    expect(model.updatedTotalTitle).to.localizeFrom(@"review_payment_updated_total_title");
                    expect(model.updatedTotalLabel.string).to.equal(@"$0.00");
                    expect(model.paidAmountTitle).to.localizeFrom(@"review_payment_paid_amount_title");
                    expect(model.originalTotal).to.localizeFrom(@"review_payment_original_total_title");
                    expect(model.transparencyTitle).to.localizeFromMap(@"car_class_details_transparency_total", @{
                        @"currency_code" : @""
                    });
                    expect(model.total.string).to.equal(@"$0.00");
                    expect(model.transparency.string).to.equal(@"$0.00");
                    expect(model.otherPaymentOptionTotal).to.beNil();
                });
            });
        });
    });

    context(@"paynow res", ^{
        beforeAll(^{
            prepay = YES;
        });
        
        context(@"invoice layout", ^{
            beforeAll(^{
                layout = EHIReservationRentalPriceTotalLayoutInvoice;
            });

            context(@"showing other option", ^{
                beforeAll(^{
                    showOtherOption = YES;
                    model = [[EHIReservationRentalPriceTotalViewModel alloc] initWithModel:nil
                                                                            prepaySelected:prepay
                                                                                paidAmount:nil
                                                                              actualAmount:nil
                                                                           showOtherOption:showOtherOption
                                                                                    layout:layout
                                                                              isSecretRate:NO];
                });
                it(@"", ^{
                    expect(model.showTopDivider).to.beTruthy();
                    expect(model.totalTitle).to.localizeFrom(@"trip_summary_final_total");
                    expect(model.paidAmountLabel).to.beNil();
                    expect(model.endOfRental).to.beNil();
                    expect(model.updatedTotalTitle).to.localizeFrom(@"review_payment_updated_total_title");
                    expect(model.updatedTotalLabel.string).to.equal(@"$0.00");
                    expect(model.paidAmountTitle).to.localizeFrom(@"review_payment_paid_amount_title");
                    expect(model.originalTotal).to.localizeFrom(@"review_payment_original_total_title");
                    expect(model.transparencyTitle).to.localizeFromMap(@"car_class_details_transparency_total", @{
                        @"currency_code" : @""
                    });
                    expect(model.total.string).to.equal(@"$0.00");
                    expect(model.transparency.string).to.equal(@"$0.00");

                    NSString *localized = EHILocalizedString(@"reservation_review_pay_later_na", @"", @"");
                    localized = [localized ehi_applyReplacementMap:@{
                        @"amount" : @"$0.00"
                    }].lowercaseString;
                    expect(model.otherPaymentOptionTotal).equal(localized);
                });
            });

            context(@"hiding other option", ^{
                beforeAll(^{
                    showOtherOption = NO;
                    model = [[EHIReservationRentalPriceTotalViewModel alloc] initWithModel:nil
                                                                            prepaySelected:prepay
                                                                                paidAmount:nil
                                                                              actualAmount:nil
                                                                           showOtherOption:showOtherOption
                                                                                    layout:layout
                                                                              isSecretRate:NO];
                });
                it(@"", ^{
                    expect(model.showTopDivider).to.beTruthy();
                    expect(model.totalTitle).to.localizeFrom(@"trip_summary_final_total");
                    expect(model.paidAmountLabel).to.beNil();
                    expect(model.endOfRental).to.beNil();
                    expect(model.updatedTotalTitle).to.localizeFrom(@"review_payment_updated_total_title");
                    expect(model.updatedTotalLabel.string).to.equal(@"$0.00");
                    expect(model.paidAmountTitle).to.localizeFrom(@"review_payment_paid_amount_title");
                    expect(model.originalTotal).to.localizeFrom(@"review_payment_original_total_title");
                    expect(model.transparencyTitle).to.localizeFromMap(@"car_class_details_transparency_total", @{
                        @"currency_code" : @""
                    });
                    expect(model.total.string).to.equal(@"$0.00");
                    expect(model.transparency.string).to.equal(@"$0.00");
                    expect(model.otherPaymentOptionTotal).to.beNil();
                });
            });
        });

        context(@"review layout", ^{
            beforeAll(^{
                layout = EHIReservationRentalPriceTotalLayoutReview;
            });

            context(@"showing other option", ^{
                beforeAll(^{
                    showOtherOption = YES;
                    model = [[EHIReservationRentalPriceTotalViewModel alloc] initWithModel:nil
                                                                            prepaySelected:prepay
                                                                                paidAmount:nil
                                                                              actualAmount:nil
                                                                           showOtherOption:showOtherOption
                                                                                    layout:layout
                                                                              isSecretRate:NO];
                });
                it(@"", ^{
                    expect(model.showTopDivider).to.beFalsy();
                    expect(model.paidAmountLabel).to.beNil();
                    expect(model.endOfRental).to.beNil();
                    expect(model.totalTitle).to.localizeFrom(@"reservation_review_prepay_total_title");
                    expect(model.updatedTotalTitle).to.localizeFrom(@"review_payment_updated_total_title");
                    expect(model.updatedTotalLabel.string).to.equal(@"$0.00");
                    expect(model.paidAmountTitle).to.localizeFrom(@"review_payment_paid_amount_title");
                    expect(model.paidAmountLabel).to.beNil();
                    expect(model.originalTotal).to.localizeFrom(@"review_payment_original_total_title");
                    expect(model.endOfRental).to.beNil();
                    expect(model.transparencyTitle).to.localizeFromMap(@"car_class_details_transparency_total", @{
                        @"currency_code" : @""
                    });
                    expect(model.total.string).to.equal(@"$0.00");
                    expect(model.transparency.string).to.equal(@"$0.00");

                    NSString *localized = EHILocalizedString(@"reservation_review_pay_later_na", @"", @"");
                    localized = [localized ehi_applyReplacementMap:@{
                        @"amount" : @"$0.00"
                    }].lowercaseString;
                    expect(model.otherPaymentOptionTotal).equal(localized);
                });
            });

            context(@"hiding other option", ^{
                beforeAll(^{
                    showOtherOption = NO;
                    model = [[EHIReservationRentalPriceTotalViewModel alloc] initWithModel:nil
                                                                            prepaySelected:prepay
                                                                                paidAmount:nil
                                                                              actualAmount:nil
                                                                           showOtherOption:showOtherOption
                                                                                    layout:layout
                                                                              isSecretRate:NO];
                });
                it(@"", ^{
                    expect(model.showTopDivider).to.beFalsy();
                    expect(model.paidAmountLabel).to.beNil();
                    expect(model.endOfRental).to.beNil();
                    expect(model.totalTitle).to.localizeFrom(@"reservation_review_prepay_total_title");
                    expect(model.updatedTotalTitle).to.localizeFrom(@"review_payment_updated_total_title");
                    expect(model.updatedTotalLabel.string).to.equal(@"$0.00");
                    expect(model.paidAmountTitle).to.localizeFrom(@"review_payment_paid_amount_title");
                    expect(model.paidAmountLabel).to.beNil();
                    expect(model.originalTotal).to.localizeFrom(@"review_payment_original_total_title");
                    expect(model.endOfRental).to.beNil();
                    expect(model.transparencyTitle).to.localizeFromMap(@"car_class_details_transparency_total", @{
                        @"currency_code" : @""
                    });
                    expect(model.total.string).to.equal(@"$0.00");
                    expect(model.transparency.string).to.equal(@"$0.00");
                    expect(model.otherPaymentOptionTotal).to.beNil();
                });
            });
        });

        context(@"refund layout", ^{
            beforeAll(^{
                layout = EHIReservationRentalPriceTotalLayoutUnpaidRefund;
            });

            context(@"showing other option", ^{
                beforeAll(^{
                    showOtherOption = YES;
                    EHIPrice *actualAmount = [EHIPrice modelWithDictionary:@{
                        @"code"   : @"USD",
                        @"symbol" : @"$",
                        @"amount" : @"203.73"
                    }];
                    model = [[EHIReservationRentalPriceTotalViewModel alloc] initWithModel:nil
                                                                            prepaySelected:prepay
                                                                                paidAmount:nil
                                                                              actualAmount:actualAmount
                                                                           showOtherOption:showOtherOption
                                                                                    layout:layout
                                                                              isSecretRate:NO];
                });
                
                it(@"", ^{
                    expect(model.showTopDivider).to.beFalsy();
                    expect(model.totalTitle).to.localizeFrom(@"review_payment_unpaid_amount_title");
                    expect(model.paidAmountLabel).to.beNil();
                    expect(model.endOfRental).to.localizeFrom(@"review_payment_unpaid_at_end_title");
                    expect(model.actualAmount).to.localizeFromMap(@"reservation_currency_refund", @{
                        @"refund" : @"$203.73"
                    });
                    expect(model.updatedTotalTitle).to.localizeFrom(@"review_payment_updated_total_title");
                    expect(model.updatedTotalLabel.string).to.equal(@"$0.00");
                    expect(model.paidAmountTitle).to.localizeFrom(@"review_payment_paid_amount_title");
                    expect(model.originalTotal).to.localizeFrom(@"review_payment_original_total_title");
                    expect(model.transparencyTitle).to.localizeFromMap(@"car_class_details_transparency_total", @{
                        @"currency_code" : @""
                    });
                    expect(model.total.string).to.equal(@"$0.00");
                    expect(model.transparency.string).to.equal(@"$0.00");

                    NSString *localized = EHILocalizedString(@"reservation_review_pay_later_na", @"", @"");
                    localized = [localized ehi_applyReplacementMap:@{
                        @"amount" : @"$0.00"
                    }].lowercaseString;
                    expect(model.otherPaymentOptionTotal).equal(localized);
                });
            });

            context(@"hiding other option", ^{
                beforeAll(^{
                    EHICarClass *carClass = [EHICarClass modelWithDictionary:@{
                        @"price_differences" : @[
                            @{
                                @"difference_type" : @"UNPAID_REFUND_AMOUNT",
                                @"difference_amount_view" : @{
                                    @"code"   : @"CAD",
                                    @"symbol" : @"$",
                                    @"amount" : @"10.00"
                                },
                                @"difference_amount_payment": @{
                                    @"code"   : @"USD",
                                    @"symbol" : @"$",
                                    @"amount" : @"10.00"
                                }
                            }
                        ],
                        @"vehicle_rates": @[
                            @{
                                @"charge_type": @"PAYNOW",
                                @"price_summary" : @{
                                    @"total_charged" : @"23.73",
                                    @"estimated_total_view": @{
                                        @"code"   : @"CAD",
                                        @"symbol" : @"$",
                                        @"amount" : @"23.73"
                                    },
                                    @"estimated_total_payment": @{
                                        @"code"   : @"USD",
                                        @"symbol" : @"$",
                                        @"amount" : @"23.73"
                                    }
                                }
                            }
                        ]
                    }];
                    
                    EHIPrice *paidAmount = [EHIPrice modelWithDictionary:@{
                        @"code"   : @"CAD",
                        @"symbol" : @"$",
                        @"amount" : @"-23.73"
                    }];
                    EHIPrice *actualAmount = [EHIPrice modelWithDictionary:@{
                        @"code"   : @"USD",
                        @"symbol" : @"$",
                        @"amount" : @"203.73"
                    }];

                    showOtherOption = NO;
                    model = [[EHIReservationRentalPriceTotalViewModel alloc] initWithModel:carClass
                                                                            prepaySelected:prepay
                                                                                paidAmount:paidAmount
                                                                              actualAmount:actualAmount
                                                                           showOtherOption:showOtherOption
                                                                                    layout:layout
                                                                              isSecretRate:NO];
                });
                
                it(@"", ^{
                    expect(model.showTopDivider).to.beFalsy();
                    expect(model.totalTitle).to.localizeFrom(@"review_payment_unpaid_amount_title");
                    expect(model.paidAmountLabel.string).to.equal(@"CA$23.73");
                    expect(model.endOfRental).to.localizeFrom(@"review_payment_unpaid_at_end_title");
                    expect(model.updatedTotalTitle).to.localizeFrom(@"review_payment_updated_total_title");
                    expect(model.updatedTotalLabel.string).to.equal(@"$0.00");
                    expect(model.paidAmountTitle).to.localizeFrom(@"review_payment_paid_amount_title");
                    expect(model.originalTotal).to.localizeFrom(@"review_payment_original_total_title");
                    expect(model.transparencyTitle).to.localizeFromMap(@"car_class_details_transparency_total", @{
                        @"currency_code" : @""
                    });
                    expect(model.total.string).to.equal(@"CA$10.00");
                    expect(model.transparency.string).to.equal(@"$0.00");
                    expect(model.otherPaymentOptionTotal).to.beNil();
                });
            });
        });
    });
});

SpecEnd
