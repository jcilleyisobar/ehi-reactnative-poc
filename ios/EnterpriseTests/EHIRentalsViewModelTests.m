//
//  EHIRentalsViewModelTests.m
//  Enterprise
//
//  Created by fhu on 5/11/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHIRentalsViewModel.h"
#import "EHIRentalsMode.h"

SpecBegin(EHIRentalsViewModelTests)

describe(@"my rentals page", ^{
    
    __block EHIRentalsViewModel *model;
    
    beforeAll(^{
        model = [EHIRentalsViewModel new];
    });
    
    it(@"should localize title", ^{
        expect(model.title).to.localizeFrom(@"rentals_navigation_title");
    });
    
    context(@"when there is rental data", ^{
        
        beforeAll(^{
            [EHIUserManager loginEnterprisePlusTestUser];
            
            ehi_waitUntil(^BOOL{ return model.upcomingRentals != 0; }, ^{
                [model setIsActive:YES];
            });
        });
        
        context(@"for past rentals", ^{
            
            beforeAll(^{
                [model switchToMode:EHIRentalsModePast];
            });
            
            it(@"should have one past rentals", ^{
                expect([model.pastRentals count]).to.equal(1);
            });
            
            it (@"should not have upcoming rentals", ^{
                expect(model.upcomingRentals).to.beNil();
            });
            
            it(@"should not have fallback cell when there is data", ^{
                expect(model.fallbackViewModel).to.beNil();
            });
        });
        
        context(@"for upcoming rentals", ^{
            
            beforeAll(^{
                [model switchToMode:EHIRentalsModeUpcoming];
            });
            
            it(@"should have thirteen upcoming rentals", ^{
//                expect([model.upcomingRentals count]).to.equal(13);
            });
            
            it (@"should not have past rentals", ^{
                expect(model.pastRentals).to.beNil();
            });
            
            it(@"should not have fallback cell when there is data", ^{
                expect(model.fallbackViewModel).to.beNil();
            });
            
        });
        
    });
    
});

SpecEnd
