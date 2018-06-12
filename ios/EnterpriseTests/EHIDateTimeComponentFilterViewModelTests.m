//
//  EHIDateTimeComponentFilterViewModelTests.m
//  Enterprise
//
//  Created by Rafael Ramos on 5/4/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHIDateTimeComponentFilterViewModel.h"

SpecBegin(EHIDateTimeComponentFilterViewModelTests)

describe(@"EHIDateTimeComponentFilterViewModel", ^{
    __block EHIDateTimeComponentFilterViewModel *model = [EHIDateTimeComponentFilterViewModel new];

    context(@"when in the filter view, return and pick label", ^{
        it(@"should be", ^{
            model = [[EHIDateTimeComponentFilterViewModel alloc] initWithModel:@(EHIDateTimeComponentLayoutFilter)];
                
            expect(model.pickupTitle).to.localizeFrom(@"locations_map_closed_pickup");
            expect(model.returnTitle).to.localizeFrom(@"locations_map_closed_return");
        });
    });
    
    it(@"should hide time section by default", ^{
        model = [[EHIDateTimeComponentFilterViewModel alloc] initWithModel:@(EHIDateTimeComponentLayoutFilter)];

        expect(model.hidePickupTimeSection).to.beTruthy();
        expect(model.hideReturnTimeSection).to.beTruthy();
    });
    
    it(@"should show time section when have pickup date", ^{
        model = [[EHIDateTimeComponentFilterViewModel alloc] initWithModel:@(EHIDateTimeComponentLayoutFilter)];
        
        [model setDate:[NSDate new] inSection:EHIDateTimeComponentSectionPickupDate];
        
        expect(model.hidePickupTimeSection).to.beFalsy();
        expect(model.hideReturnTimeSection).to.beTruthy();
    });
    
    it(@"should show time section when have return date", ^{
        model = [[EHIDateTimeComponentFilterViewModel alloc] initWithModel:@(EHIDateTimeComponentLayoutFilter)];
        
        [model setDate:[NSDate new] inSection:EHIDateTimeComponentSectionReturnDate];
        
        expect(model.hideReturnTimeSection).to.beFalsy();
        expect(model.hidePickupTimeSection).to.beTruthy();
    });
    
    it(@"should show time section when have both dates", ^{
        model = [[EHIDateTimeComponentFilterViewModel alloc] initWithModel:@(EHIDateTimeComponentLayoutFilter)];
        
        [model setDate:[NSDate new] inSection:EHIDateTimeComponentSectionReturnDate];
        [model setDate:[NSDate new] inSection:EHIDateTimeComponentSectionPickupDate];
        
        expect(model.hideReturnTimeSection).to.beFalsy();
        expect(model.hidePickupTimeSection).to.beFalsy();
    });
});

SpecEnd
