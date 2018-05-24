//
//  EHIReviewModifyPrepayBannerViewModelTests.m
//  Enterprise
//
//  Created by Rafael Ramos on 13/03/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHIReviewModifyPrepayBannerViewModel.h"

SpecBegin(EHIReviewModifyPrepayBannerViewModelTests)

describe(@"the modify prepaid reservation view model", ^{
    
    EHIPrice *price = [EHIPrice modelWithDictionary:@{
        @"amount" : @(10.0),
        @"code"   : @"USD",
        @"symbol" : @"$"
    }];
    
    EHIReviewModifyPrepayBannerViewModel *model = [[EHIReviewModifyPrepayBannerViewModel alloc] initWithModel:price];
    
    context(@"default messages", ^{
        it(@"should show default warning text", ^{
            expect(model.title).to.localizeFrom(@"modify_reservation_prepay_default_warning_text");
            expect(model.subtitle).to.beNil();
            expect(model.totalAmount).to.beNil();
        });
    });
    
    context(@"messages when reservation is updated", ^{
        beforeAll(^{
            model.updated = YES;
        });

        it(@"should show prepay warning text", ^{
            expect(model.title).to.localizeFrom(@"modify_reservation_prepay_warning_text");
            expect(model.subtitle).to.localizeFrom(@"modify_reservation_prepay_original_amount");
            expect(model.totalAmount).to.equal(@"$10.00");
        });
    });
    
    context(@"messages when reservation is updated and locale is NA", ^{
        beforeAll(^{
            model.isNAAirport = YES;
            model.updated     = YES;
        });
        
        it(@"should show prepay NA airport warning text", ^{
            expect(model.title).to.localizeFrom(@"modify_reservation_prepay_naa_warning_text");
            expect(model.subtitle).to.beNil();
            expect(model.totalAmount).to.beNil();
        });
    });
});


SpecEnd
