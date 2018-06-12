//
//  EHILocationDetailsHoursViewModelTests.m
//  Enterprise
//
//  Created by Ty Cobb on 3/4/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHILocationDetailsHoursViewModel.h"
#import "EHILocationTimes.h"
#import "NSDate+Utility.h"

SpecBegin(EHILocationDetailsHoursViewModelTests)

describe(@"the location details hours view model", ^{
   
    __block EHILocationTimes *day;
    __block EHILocationTimesSlice *times;
    __block EHILocationDetailsHoursViewModel *model;
    
    beforeAll(^{
        model = [EHILocationDetailsHoursViewModel new];
       
        // mock the day
        day = mock(EHILocationTimes.class);
        [given(day.date) willReturn:[[NSDate ehi_today] ehi_addDays:1]];
        [given(day.displayText) willReturn:@"Thu, Mar 03"];
       
        // mock the times
        times = mock(EHILocationTimesSlice.class);
        [given(times.times) willReturn:day];
        [given(times.isFirstSlice) willReturnBool:YES];
        [given(times.displayText) willReturn:@"09:00 - 10:00"];
    });
    
    beforeEach(^{
        [model updateWithModel:times];
    });
    
    context(@"when it's a standard time", ^{
        
        it(@"should display the formatted day text", ^{
            expect(model.date).toNot.beNil();
//            expect(model.date).to.equal(day.displayText);
        });
        
        it(@"should display the formatted time text", ^{
            expect(model.time).toNot.beNil();
            expect(model.time).to.equal(times.displayText);
        });
    
    });
    
    context(@"when it's closed", ^{
        
        beforeAll(^{
            [given(day.isClosedAllDay) willReturnBool:YES];
        });
        
        it(@"should show display the hours as closed", ^{
            expect(model.time).to.localizeFrom(@"location_details_hours_closed");
        });
        
    });
    
    context(@"when it's today", ^{
        
        beforeAll(^{
            [given(day.isToday) willReturnBool:YES];
        });
        
        it(@"should mark itself as today", ^{
            expect(model.isToday).to.beTruthy();
        });
        
        it(@"should show the today date title", ^{
//            expect(model.date).to.localizeFrom(@"location_details_today");
        });
        
    });
    
    context(@"when it's not the first in day", ^{
        
        beforeAll(^{
            [given(times.isFirstSlice) willReturnBool:NO];
        });
        
        it(@"should not display the date title", ^{
            expect(model.date).to.beNil();
        });
        
    });
    
});

SpecEnd
