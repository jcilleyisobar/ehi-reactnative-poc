//
//  EHISingleDateCalendarViewModelTests.m
//  Enterprise
//
//  Created by Rafael Ramos on 5/9/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHISingleDateCalendarViewModel.h"
#import "NSDate+Utility.h"
#import "NSDate+Formatting.h"
#import "EHISingleDateCalendarEnums.h"

SpecBegin(EHISingleDateCalendarViewModelTests)

describe(@"EHISingleDateCalendarViewModel", ^{
    __block EHISingleDateCalendarViewModel *model = [EHISingleDateCalendarViewModel new];
    __block NSArray *allDays = @[];
    
    beforeAll(^{
        NSDate *firstValidDate = [NSDate ehi_today];
        NSDate *lastValidDate  = [firstValidDate ehi_addDays:360];
        
        NSDate *firstDate = [[firstValidDate ehi_firstInMonth] ehi_weekday:EHIWeekdayFirst];
        NSDate *lastDate  = [lastValidDate ehi_lastInMonth];
        
        allDays = @(0).upTo([firstDate ehi_daysUntilDate:lastDate]);
    });
    
    context(@"action title", ^{
        it(@"", ^{
            expect(model.actionTitle).to.localizeFrom(@"time_picker_button_title");
        });
    });
    
    context(@"number of days", ^{
        it(@"should return total days in current year", ^{
            expect(model.numberOfDays).to.equal(@(allDays.count));
        });
    });
    
    context(@"on pickup date", ^{
        it(@"", ^{
            model = [[EHISingleDateCalendarViewModel alloc] initWithModel:@(EHISingleDateCalendarTypePickup)];
            expect(model.title).to.localizeFrom(@"date_select_pickup_title");
        });
    });
    
    context(@"on return date", ^{
        it(@"", ^{
            model = [[EHISingleDateCalendarViewModel alloc] initWithModel:@(EHISingleDateCalendarTypeReturn)];
            expect(model.title).to.localizeFrom(@"date_select_return_title");
        });
    });
});

describe(@"EHISingleDateCalendarViewModel", ^{
    __block EHISingleDateCalendarViewModel *model = [EHISingleDateCalendarViewModel new];

    NSDate *wed10May2017 = [NSDate dateWithTimeIntervalSince1970:1494411010];
    NSDate *mon15May2017 = [NSDate dateWithTimeIntervalSince1970:1494843010];
    
    context(@"when first selecting pickup date", ^{
        it(@"should be valid", ^{
            model = [[EHISingleDateCalendarViewModel alloc] initWithModel:@(EHISingleDateCalendarTypePickup)];
            model.pickupDate = wed10May2017;
            
            expect(model.validationMessage).to.beNil();
        });
    });
    
    context(@"on valid date range", ^{
        it(@"should be valid", ^{
            model = [[EHISingleDateCalendarViewModel alloc] initWithModel:@(EHISingleDateCalendarTypeReturn)];
            model.returnDate = mon15May2017;
            
            expect(model.validationMessage).to.beNil();
        });
    });
    
    context(@"on valid date range", ^{
        it(@"should be valid", ^{
            model = [[EHISingleDateCalendarViewModel alloc] initWithModel:@(EHISingleDateCalendarTypePickup)];
            model.pickupDate = wed10May2017;
            model.returnDate = mon15May2017;

            expect(model.validationMessage).to.beNil();
        });
    });
    
    context(@"on valid date range", ^{
        it(@"should be valid", ^{
            model = [[EHISingleDateCalendarViewModel alloc] initWithModel:@(EHISingleDateCalendarTypePickup)];
            model.pickupDate = [NSDate dateWithTimeIntervalSince1970:1494411010];
            model.returnDate = [NSDate dateWithTimeIntervalSince1970:1494411010];
            
            expect(model.validationMessage).to.beNil();
        });
        
        it(@"should be valid", ^{
            model = [[EHISingleDateCalendarViewModel alloc] initWithModel:@(EHISingleDateCalendarTypeReturn)];
            model.pickupDate = [NSDate dateWithTimeIntervalSince1970:1494411010];
            model.returnDate = [NSDate dateWithTimeIntervalSince1970:1494411010];
            
            expect(model.validationMessage).to.beNil();
        });
    });
    
    context(@"on invalid date range", ^{
        it(@"should be invalid", ^{
            model = [[EHISingleDateCalendarViewModel alloc] initWithModel:@(EHISingleDateCalendarTypePickup)];
            model.returnDate = wed10May2017;
            [model selectDay:[[EHICalendarDay alloc] initWithDate:mon15May2017]];
            
            expect(model.validationMessage).to.localizeFromMap(@"date_select_invalid_pickup_date", @{
                @"date" : [wed10May2017 ehi_stringForTemplate:@"MMM dd"]
            });
        });
    });

    context(@"on invalid date range", ^{
        it(@"should be invalid", ^{
            model = [[EHISingleDateCalendarViewModel alloc] initWithModel:@(EHISingleDateCalendarTypeReturn)];
            model.pickupDate = mon15May2017;
            [model selectDay:[[EHICalendarDay alloc] initWithDate:wed10May2017]];
            
            expect(model.validationMessage).to.localizeFromMap(@"date_select_invalid_return_date", @{
                @"date" : [mon15May2017 ehi_stringForTemplate:@"MMM dd"]
            });
        });
    });
});

SpecEnd
