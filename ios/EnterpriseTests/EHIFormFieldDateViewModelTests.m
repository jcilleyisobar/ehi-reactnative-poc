//
//  EHIFormFieldDateViewModelTests.m
//  Enterprise
//
//  Created by Alex Koller on 5/5/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHIFormFieldDateViewModel.h"
#import "NSDate+Formatting.h"

SpecBegin(EHIFormFieldDateViewModelTests)

describe(@"the date form field", ^{
    
    __block EHIFormFieldDateViewModel *viewModel;
    NSString *title = @"Default Title";
    NSString *placeholder = @"Default Placeholder";
    
    beforeAll(^{
        viewModel = [EHIFormFieldDateViewModel new];
        viewModel.title = title;
        viewModel.placeholder = placeholder;
    });
    
    it(@"should have proper defaults", ^{
        expect(viewModel.inputValue).to.beNil();
        expect(viewModel.pickerMode).to.equal(@(UIDatePickerModeDate));
    });
    
    it(@"should provide the input title", ^{
        expect(viewModel.title).to.equal(title);
    });
    
    it(@"should provide the input placeholder", ^{
        expect(viewModel.placeholder).to.equal(placeholder);
    });
    
    describe(@"when input with a date", ^{

        NSDate *inputDate = [NSDate date];
        NSString *dateFormat = @"yyyy MM dd";
        
        beforeAll(^{
            viewModel.inputValue = inputDate;
        });
        
        it(@"should provide a properly formatted date string", ^{
            expect(viewModel.dateString).to.equal([inputDate ehi_stringForTemplate:dateFormat]);
        });
    });
    
    describe(@"when input value is before set minimm date", ^{
        
        beforeAll(^{
            viewModel.minimumDate = [viewModel.inputValue dateByAddingTimeInterval:1];
        });
        
        it(@"should clear the input date", ^{
            expect(viewModel.inputValue).to.beNil();
        });
    });
    
    describe(@"when input value is after set maximum date", ^{
        
        beforeAll(^{
            viewModel.maximumDate = [viewModel.inputValue dateByAddingTimeInterval:-1];
        });
        
        it(@"should clear the input date", ^{
            expect(viewModel.inputValue).to.beNil();
        });
    });
});

SpecEnd
