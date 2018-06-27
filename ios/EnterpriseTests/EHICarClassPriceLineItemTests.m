//
//  EHICarClassPriceLineItemTests.m
//  Enterprise
//
//  Created by Rafael Ramos on 23/04/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHICarClassPriceLineItem.h"

SpecBegin(EHICarClassPriceLineItemTests)

describe(@"EHICarClassPriceLineItem", ^{
    __block EHICarClassPriceLineItem *model = EHICarClassPriceLineItem.new;
    context(@"when line item type is rated by day", ^{
        before(^{
            model = [EHICarClassPriceLineItem modelWithDictionary:@{
                @"rate_amount_view": @{
                    @"code": @"USD",
                    @"symbol": @"$",
                    @"amount": @"125.00"
                },
                @"category": @"VEHICLE_RATE",
                @"rate_type": @"DAY",
                @"rate_quantity": @(4),
                @"description": @"TIME & DISTANCE"
            }];
        });
        it(@"the it should format using title and amount", ^{
            expect(model.title).equal(@"TIME & DISTANCE");
            expect(model.formattedType).equal(@"$125.00 / Day");
        });
    });
    context(@"when line item type is rated by percentage", ^{
        before(^{
            model = [EHICarClassPriceLineItem modelWithDictionary:@{
                @"rate_amount_view": @{
                    @"code": @"USD",
                    @"symbol": @"$",
                    @"amount": @"5.00"
                },
                @"category": @"SAVINGS",
                @"rate_type": @"PERCENT",
                @"rate_quantity": @(0),
                @"description": @"DISCOUNT"
            }];
        });
        it(@"the it should format using title and percet", ^{
            expect(model.title).equal(@"Discount");
            expect(model.formattedType).equal(@"5.00 %");
        });
    });
    context(@"when line item type is rated by percentage (no category set)", ^{
        before(^{
            model = [EHICarClassPriceLineItem modelWithDictionary:@{
                @"rate_amount_view": @{
                    @"code": @"USD",
                    @"symbol": @"$",
                    @"amount": @"11.11"
                },
                @"rate_type": @"PERCENT",
                @"rate_quantity": @(0),
                @"description": @"CONCESSION RECOUP FEE 11.11 PCT"
            }];
        });
        it(@"the it should format using title and percet", ^{
            expect(model.title).equal(@"CONCESSION RECOUP FEE 11.11 PCT");
            expect(model.formattedType).equal(@"11.11 %");
        });
    });
});

SpecEnd
