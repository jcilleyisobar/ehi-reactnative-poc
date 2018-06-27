//
//  EHIPaymentOptionCellViewModelTests.m
//  Enterprise
//
//  Created by Rafael Ramos on 11/04/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHIPaymentOptionCellViewModel.h"

SpecBegin(EHIPaymentOptionCellViewModelTests)

describe(@"the payment option cell view model", ^{
    __block EHIPaymentOptionCellViewModel *model;
    
    beforeAll(^{
        [EHIUserManager loginEnterprisePlusTestUser];
    });
    
    context(@"when there's a price difference", ^{
		before(^{
			EHICarClass *carClass = [EHICarClass modelWithDictionary:@{
				@"price_differences" : @[
					@{
						@"difference_type" : @"PREPAY",
						@"difference_amount_view" : @{
							@"code"   : @"USD",
						    @"symbol" : @"$",
						    @"amount" : @"10.00"
					    }
					}
				],
				@"charges": @[
					@{
						@"charge_type": @"PREPAY",
                        @"rates": @[
                            @{
                                @"unit_amount_view": @{
                                    @"code"  : @"USD",
                                    @"symbol": @"$",
                                    @"amount": @"47.00"
                                },
                                @"unit_amount_payment": @{
                                    @"code"  : @"USD",
                                    @"symbol": @"$",
                                    @"amount":  @"47.00"
                                },
                                @"unit_rate_type": @"DAILY"
                            }
                        ]
				    }
				]
			}];

			model = [EHIPaymentOptionCellViewModel new];
		   	[model configureWithPaymentOption:EHIReservationPaymentOptionPayNow carClass:carClass];
       	});
		it(@"should show the discount price", ^{
			expect(model.discount).to.localizeFromMap(@"reservation_pay_now_savings", @{
				@"amount" : @"$10.00"
			});
		});
    });

	context(@"when there's a negative price difference", ^{
		before(^{
			EHICarClass *carClass = [EHICarClass modelWithDictionary:@{
				@"price_differences" : @[
					@{
						@"difference_type" : @"PREPAY",
						@"difference_amount_view" : @{
							@"code"   : @"USD",
							@"symbol" : @"$",
							@"amount" : @"-10.00"
						}
					}
				],
				@"charges": @[
					@{
						@"charge_type": @"PREPAY",
                        @"rates": @[
                            @{
                                @"unit_amount_view": @{
                                    @"code"  : @"USD",
                                    @"symbol": @"$",
                                    @"amount": @"47.00"
                                },
                                @"unit_amount_payment": @{
                                    @"code"  : @"USD",
                                    @"symbol": @"$",
                                    @"amount":  @"47.00"
                                },
                                @"unit_rate_type": @"DAILY"
                            }
                        ]
					}
				]
			}];

			model = [EHIPaymentOptionCellViewModel new];
			[model configureWithPaymentOption:EHIReservationPaymentOptionPayNow carClass:carClass];
		});
		it(@"should show the discount abs price", ^{
			expect(model.discount).to.localizeFromMap(@"reservation_pay_now_savings", @{
				@"amount" : @"$10.00"
			});
		});
	});
    
    context(@"when there's no paylater rates", ^{
        before(^{
            EHICarClass *carClass = [EHICarClass modelWithDictionary:@{
               @"charges": @[
                    @{
                        @"charge_type": @"PAYLATER",
                        @"total_price_view": @{
                            @"code": @"USD",
                                @"symbol": @"$",
                                @"amount": @"115.57"
                        },
                        @"total_price_payment": @{
                            @"code": @"USD",
                            @"symbol": @"$",
                            @"amount": @"115.57"
                        },
                        @"rates": @[ @{ } ]
                    }
                ]
            }];
            
            model = [EHIPaymentOptionCellViewModel new];
            [model configureWithPaymentOption:EHIReservationPaymentOptionPayLater carClass:carClass];
        });
        it(@"then paylater button layout should be disabled", ^{
            expect(model.layoutType).to.equal(EHIPaymentOptionLayoutDisabled);
        });
    });
    
    context(@"when there's no prepay rates", ^{
        before(^{
            EHICarClass *carClass = [EHICarClass modelWithDictionary:@{
               @"charges": @[
                    @{
                        @"charge_type": @"PREPAY",
                        @"total_price_view": @{
                            @"code": @"USD",
                            @"symbol": @"$",
                            @"amount": @"115.57"
                        },
                        @"total_price_payment": @{
                            @"code": @"USD",
                            @"symbol": @"$",
                            @"amount": @"115.57"
                        },
                        @"rates": @[ @{ } ]
                    }
                ]
            }];
            
            model = [EHIPaymentOptionCellViewModel new];
            [model configureWithPaymentOption:EHIReservationPaymentOptionPayNow carClass:carClass];
        });
        it(@"then prepay button layout should be disabled", ^{
            expect(model.layoutType).to.equal(EHIPaymentOptionLayoutDisabled);
        });
    });
    
    context(@"when there's points", ^{
        before(^{
            EHICarClass *carClass = [EHICarClass modelWithDictionary:@{
               @"eplus_max_redemption_days_reason" : @"DURATION_LIMIT",
               @"redemption_points"                : @(10),
               @"charges": @[
                    @{
                        @"charge_type": @"PAYLATER",
                        @"total_price_view": @{
                            @"code": @"USD",
                            @"symbol": @"$",
                            @"amount": @"115.57"
                        },
                        @"total_price_payment": @{
                            @"code": @"USD",
                            @"symbol": @"$",
                            @"amount": @"115.57"
                        },
                        @"rates": @[
                            @{
                                @"total_price_view": @{
                                    @"code"  : @"USD",
                                    @"symbol": @"$",
                                    @"amount": @"47.00"
                                },
                                @"total_price_payment": @{
                                    @"code"  : @"USD",
                                    @"symbol": @"$",
                                    @"amount":  @"47.00"
                                }
                            }
                        ]
                    }
                ]
            }];
            
            model = [EHIPaymentOptionCellViewModel new];
            [model configureWithPaymentOption:EHIReservationPaymentOptionRedeemPoints
                                     carClass:carClass];
        });
        it(@"then prepay button layout should be enabled", ^{
            expect(model.price.string).to.equal(@"10\nPOINTS/DAY");
            expect(model.layoutType).to.equal(EHIPaymentOptionLayoutEnabled);
        });
    });

    context(@"when there's no points", ^{
        before(^{
            EHICarClass *carClass = [EHICarClass modelWithDictionary:@{
               @"redemption_points" : @(1000),
               @"charges": @[
                    @{
                        @"charge_type": @"PAYLATER",
                        @"total_price_view": @{
                            @"code": @"USD",
                            @"symbol": @"$",
                            @"amount": @"115.57"
                        },
                        @"total_price_payment": @{
                            @"code": @"USD",
                            @"symbol": @"$",
                            @"amount": @"115.57"
                        },
                        @"rates": @[
                            @{
                                @"total_price_view": @{
                                    @"code"  : @"USD",
                                    @"symbol": @"$",
                                    @"amount": @"47.00"
                                },
                                @"total_price_payment": @{
                                    @"code"  : @"USD",
                                    @"symbol": @"$",
                                    @"amount":  @"47.00"
                                }
                            }
                        ]
                    }
                ]
            }];
            
            model = [EHIPaymentOptionCellViewModel new];
            [model configureWithPaymentOption:EHIReservationPaymentOptionRedeemPoints carClass:carClass];
        });
        it(@"then prepay button layout should be disabled", ^{
            expect(model.price.string).to.localizeFrom(@"choose_your_rate_prepay_unavailable");
            expect(model.layoutType).to.equal(EHIPaymentOptionLayoutDisabled);
        });
    });
});

SpecEnd
