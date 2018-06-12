//
//  EHIReservationItineraryViewModelTests.m
//  Enterprise
//
//  Created by fhu on 5/4/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHIFavoritesManager.h"
#import "EHILocations.h"

#import "EHIReservationBuilder.h"
#import "EHIItineraryViewModel.h"

SpecBegin(EHIReservationItineraryTests)

describe(@"reservation itinerary", ^{
    
    __block EHIItineraryViewModel *model;
    
    beforeAll(^{
        [[EHIReservationBuilder sharedInstance] resetData];
        [[EHIReservationBuilder sharedInstance] setIsActive:YES];
        
        model = [EHIItineraryViewModel new];
        model.isActive = YES;
        
        [[EHIReservationBuilder sharedInstance] selectLocation:[EHILocation mock:@"city"]];
    });
    
    context(@"when the reservation is not one way", ^{
        
        it(@"should set the pickup header title for a single location", ^{
            expect(model.pickupHeaderTitle).to.beNil();
        });
        
        it(@"should set isOneWay property to NO", ^{
            expect(model.isOneWay).to.beFalsy();
        });
        
    });
    
    context(@"when the reservation is one way", ^{
        
        beforeAll(^{
            model.isActive = YES;
            [[EHIReservationBuilder sharedInstance] selectLocation:[EHILocation mock:@"city3"]];
            [[MTRReactor reactor] flush];
        });
        
        it(@"should set the pickup header title for a one way reservation", ^{
            expect(model.pickupHeaderTitle).to.beNil();
        });
        
        it(@"should set isOneWay property to NO", ^{
            expect(model.isOneWay).to.beFalsy();
        });
        
    });    
});

SpecEnd
