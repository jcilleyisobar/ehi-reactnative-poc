//
//  EHIDateTimeComponentViewModelTests.m
//  Enterprise
//
//  Created by Rafael Ramos on 5/3/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHIDateTimeComponentViewModel.h"

SpecBegin(EHIDateTimeComponentViewModelTests)

describe(@"EHIDateTimeComponentViewModel", ^{
    __block EHIDateTimeComponentViewModel *model = [EHIDateTimeComponentViewModel new];
    
    it(@"when in the map view should be", ^{
        model = [[EHIDateTimeComponentViewModel alloc] initWithModel:@(EHIDateTimeComponentLayoutMap)];
        expect(model.pickupTitle).to.localizeFrom(@"locations_map_pickup_label");
        expect(model.returnTitle).to.localizeFrom(@"locations_map_return_label");
    });
    
    it(@"should hide date/time when there's no data", ^{
        model = [EHIDateTimeComponentViewModel new];
        
        expect(model.hasData).to.beFalsy();
    });
    
    it(@"should show date/time when there's pickup data", ^{
        model = [EHIDateTimeComponentViewModel new];
        [model setDate:NSDate.new inSection:EHIDateTimeComponentSectionPickupDate];
        
        expect(model.hasData).to.beTruthy();
    });
    
    it(@"should show date/time when there's pickup time", ^{
        model = [EHIDateTimeComponentViewModel new];
        [model setDate:NSDate.new inSection:EHIDateTimeComponentSectionPickupTime];
        
        expect(model.hasData).to.beTruthy();
    });
    
    it(@"should show date/time when there's return data", ^{
        model = [EHIDateTimeComponentViewModel new];
        [model setDate:NSDate.new inSection:EHIDateTimeComponentSectionReturnDate];
        
        expect(model.hasData).to.beTruthy();
    });
    
    it(@"should show date/time when there's return time", ^{
        model = [EHIDateTimeComponentViewModel new];
        [model setDate:NSDate.new inSection:EHIDateTimeComponentSectionReturnTime];
        
        expect(model.hasData).to.beTruthy();
    });
});

SpecEnd
