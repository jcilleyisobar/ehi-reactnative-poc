//
//  EHIExtrasExtraViewModelTests.m
//  Enterprise
//
//  Created by Bruno Fernandes Campos on 2/26/18.
//Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHIExtrasExtraViewModel.h"
#import "EHICarClassExtra.h"

SpecBegin(EHIExtrasExtraViewModelTests)

describe(@"EHIExtrasExtraViewModel", ^{
    __block EHIExtrasExtraViewModel *extrasExtraViewModel;
    __block EHICarClassExtra *extra;
    __block EHICarClassPriceLineItem *lineItem;
    
    describe(@"Given the scenario of setting up extras", ^{
        context(@"when there are both extra and line item", ^{
            before(^{
                extrasExtraViewModel = [EHIExtrasExtraViewModel new];
                
                extra = [EHICarClassExtra modelWithDictionary:@{
                    @"code" : @"COF",
                    @"status" : @"OPTIONAL",
                    @"rate_type": @"DAILY",
                    @"selected_quantity": @(2),
                    @"max_quantity": @(2),
                    @"total_amount_view": @{
                            @"code": @"USD",
                            @"symbol": @"$",
                            @"amount": @(8.95)
                    },
                    @"total_amount_payment": @{
                            @"code": @"USD",
                            @"symbol": @"$",
                            @"amount": @(8.95)
                    },
                    @"rate_amount_view": @{
                            @"code": @"USD",
                            @"symbol": @"$",
                            @"amount": @(8.95)
                    },
                    @"rate_amount_payment": @{
                            @"code": @"USD",
                            @"symbol": @"$",
                            @"amount": @(8.95)
                    },
                }];
                
                lineItem = [EHICarClassPriceLineItem modelWithDictionary:@{
                    @"status": @"CHARGED",
                    @"total_amount_view": @{
                        @"code"  : @"USD",
                        @"symbol": @"$",
                        @"amount": @(17.9)
                    },
                    @"total_amount_payment": @{
                        @"code"  : @"USD",
                        @"symbol": @"$",
                        @"amount": @(17.9)
                    },
                    @"rate_amount_view": @{
                        @"code"  : @"USD",
                        @"symbol": @"$",
                        @"amount": @(8.95)
                    },
                    @"rate_amount_payment": @{
                        @"code"  : @"USD",
                        @"symbol": @"$",
                        @"amount": @(8.95)
                    },
                    @"category"     : @"EQUIPMENT",
                    @"rate_type"    : @"DAILY",
                    @"rate_quantity": @(1.0),
                    @"description"  : @"Child Safety Seat",
                    @"code"         : @"CST",
                    @"quantity"     : @(2)
                }
                ];
                
                [extrasExtraViewModel updateWithExtra:extra andPaymentLineItem:lineItem];
            });
            
            it(@"then we should use the line item total amount view to display price information", ^{
                expect(extrasExtraViewModel.totalText).to.equal(@"$17.90");
            });
        });
        
        
        context(@"when there are only extra provided and no payment line item", ^{
            before(^{
                extrasExtraViewModel = [EHIExtrasExtraViewModel new];
                
                extra = [EHICarClassExtra modelWithDictionary:@{
                    @"code" : @"COF",
                    @"status" : @"OPTIONAL",
                    @"rate_type": @"DAILY",
                    @"selected_quantity": @(2),
                    @"max_quantity": @(2),
                    @"total_amount_view": @{
                            @"code": @"USD",
                            @"symbol": @"$",
                            @"amount": @(8.95)
                            },
                    @"total_amount_payment": @{
                            @"code": @"USD",
                            @"symbol": @"$",
                            @"amount": @(8.95)
                            },
                    @"rate_amount_view": @{
                            @"code": @"USD",
                            @"symbol": @"$",
                            @"amount": @(8.95)
                            },
                    @"rate_amount_payment": @{
                            @"code": @"USD",
                            @"symbol": @"$",
                            @"amount": @(8.95)
                            },
                    }];
                
                [extrasExtraViewModel updateWithExtra:extra andPaymentLineItem:nil];
            });
            
            it(@"then we should fallback to extra total amount view price to display price information", ^{
                expect(extrasExtraViewModel.totalText).to.equal(@"$8.95");
            });
        });
    });
});

SpecEnd
