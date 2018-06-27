//
//  EHIReservationClassSelectViewModelTests.m
//  Enterprise
//
//  Created by Alex Koller on 5/1/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHIReservationBuilder.h"
#import "EHIClassSelectViewModel.h"
#import "EHIFilters.h"
#import "EHILocalization.h"

SpecBegin(EHIReservationClassSelectViewModelTests)

describe(@"the reservation class select view model", ^{
    
    __block EHIClassSelectViewModel *model;
    __block EHIReservationBuilder *builder = [EHIReservationBuilder sharedInstance];
    
    beforeAll(^{
        [builder initiateReservationWithHandler:nil];
        model = [EHIClassSelectViewModel new];
    });
    
    it(@"should provide a title", ^{
        expect(model.title).to.localizeFrom(@"reservation_class_select_navigation_title");
    });
    
    it(@"should provide car class view models", ^{
        expect(model.carClassModels).toNot.beNil();
    });
    
    context(@"when filters exist", ^{
        
        beforeAll(^{
            model.activeFilters = @[[EHIFilters placeholder], [EHIFilter placeholder]];
        });
        
        it(@"should not have active filters after clearing", ^{
            [model clearFilters];
            expect(model.activeFilters).to.beNil();
        });
    });
});

SpecEnd
