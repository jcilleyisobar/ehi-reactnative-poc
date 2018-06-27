//
//  EHIDashboardLoyaltyViewModelTests.m
//  Enterprise
//
//  Created by Ty Cobb on 1/27/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHIDashboardLoyaltyViewModel.h"
#import "EHIDataStore.h"

SpecBegin(EHIDashboardLoyaltyViewModelTests)

describe(@"the loyalty view model", ^{
    
    __block EHIDashboardLoyaltyViewModel *model = [EHIDashboardLoyaltyViewModel new];
    
    beforeAll(^{
        [EHIDataStore purge:[EHIUser class] handler:nil];
    });
    
    it(@"should display the enterprise plus title", ^{
        expect(model.title).to.localizeFrom(@"dashboard_loyalty_cell_title");
    });
    
    it(@"should display the points title", ^{
        expect(model.pointsTitle).to.localizeFrom(@"dashboard_loyalty_cell_authenticated_points_title");
    });
    
    context(@"it should pre-load existing user, if exists", ^{
        EHIUser *user = [EHIUser mock:@"user_profile"];

        beforeAll(^{
            [EHIUserManager loginEnterprisePlusTestUser];
        });

        context(@"saved user should show", ^{
            it(@"should display the greetings title", ^{
                expect(model.greetingTitle).to.localizeFrom(@"dashboard_loyalty_cell_authenticated_greeting_title");
            });
            
            it(@"should be authenticated", ^{
                expect(model.isAuthenticated).to.beTruthy();
            });
            
            it(@"should be use saved user data", ^{
                expect(model.greetingSubtitle).to.equal(user.firstName);
                expect(model.pointsSubtitle).to.equal(user.loyaltyPoints);
            });
        });
    });
    
    context(@"it is unauthenticated", ^{
        
        it(@"should not render any points", ^{
            expect(model.pointsSubtitle).to.beNil;
            expect(model.greetingSubtitle).to.beNil;
        });
        
    });
    
    context(@"it is authenticated", ^{
       
        it(@"should render the user's points", ^{
            expect(model.pointsTitle).to.localizeFrom(@"dashboard_loyalty_cell_authenticated_points_title");
        });
    });
    
    afterAll(^{
        [EHIDataStore purge:[EHIUser class] handler:nil];
    });
});

SpecEnd
