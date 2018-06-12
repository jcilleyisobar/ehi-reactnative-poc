//
//  EHIFormFieldDateMonthYearViewModelTests.m
//  Enterprise
//
//  Created by Alex Koller on 5/7/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHIFormFieldDateMonthYearViewModel.h"

SpecBegin(EHIFormFieldDateMonthYearViewModelTests)

describe(@"the date month year field", ^{
    
    __block EHIFormFieldDateMonthYearViewModel *viewModel;
    NSString *title = @"TITLE";
    
    beforeAll(^{
        viewModel = [EHIFormFieldDateMonthYearViewModel new];
        viewModel.title = title;
    });
    
    it(@"should have empty defaults", ^{
        expect(viewModel.inputValue).to.beNil();
        expect(viewModel.monthText).to.beNil();
        expect(viewModel.yearText).to.beNil();
    });
    
    it(@"should provide the input title", ^{
        expect(viewModel.title).to.equal(title);
    });
    
    it(@"should provide localized month and year placeholders", ^{
        expect(viewModel.monthPlaceholder).to.localizeFrom(@"form_field_date_month_placeholder");
        expect(viewModel.yearPlaceholder).to.localizeFrom(@"form_field_date_year_placeholder");
    });
    
    it(@"should provide all 12 months", ^{
        expect([viewModel numberOfRowsInComponent:EHIFormFieldDateMonthYearPickerComponentMonth]).to.equal(12);
    });
    
    it(@"should provide the current year plus 10 years ahead", ^{
        expect([viewModel numberOfRowsInComponent:EHIFormFieldDateMonthYearPickerComponentYear]).to.equal(11);

        NSInteger year = [[NSCalendar currentCalendar] component:NSCalendarUnitYear fromDate:[NSDate date]];
        NSInteger viewModelFirstYear = [[viewModel titleForRow:0 inComponent:EHIFormFieldDateMonthYearPickerComponentYear] integerValue];
        expect(viewModelFirstYear).to.equal(@(year));
    });
    
    context(@"when it has been given a date", ^{
        
        beforeAll(^{
            NSDateComponents *components = [[NSDateComponents alloc] init];
            [components setMonth:9];
            [components setYear:2017];
            viewModel.inputValue = [[NSCalendar currentCalendar] dateFromComponents:components];
        });
        
        it(@"should populate month and year text", ^{
            expect(viewModel.monthText).to.equal(@"09");
            expect(viewModel.yearText).to.equal(@"2017");
        });
    });
    
    context(@"when selecting rows from components", ^{
        
        const NSInteger year = [[NSCalendar currentCalendar] component:NSCalendarUnitYear fromDate:[NSDate date]];
        
        beforeAll(^{
            // selected row is offset by actual month by 1
            [viewModel didSelectRow:3 inComponent:EHIFormFieldDateMonthYearPickerComponentMonth];
            [viewModel didSelectRow:0 inComponent:EHIFormFieldDateMonthYearPickerComponentYear];
        });

        it(@"should update the displayed text", ^{
//            expect(viewModel.monthText).to.equal(@"04");
            expect(viewModel.yearText).to.equal([@(year) stringValue]);
        });
        
        it(@"should update the input value", ^{
            NSDateComponents *components = [[NSCalendar currentCalendar] components:NSCalendarUnitMonth|NSCalendarUnitYear fromDate:viewModel.inputValue];
            
//            expect([components month]).to.equal(9);
            expect([components year]).to.equal(year);
        });
    });
});

SpecEnd
