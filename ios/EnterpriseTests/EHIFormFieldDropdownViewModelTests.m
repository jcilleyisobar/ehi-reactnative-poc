//
//  EHIFormFieldDropdownViewModelTests.m
//  Enterprise
//
//  Created by Alex Koller on 5/4/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHIFormFieldDropdownViewModel.h"

SpecBegin(EHIFormFieldDropdownViewModelTests)

describe(@"the dropdown form", ^{
    
    __block EHIFormFieldDropdownViewModel *viewModel;
    NSString *title = @"Default Title";
    NSString *placeholder = @"Default Placeholder";
    NSArray *options = @[@"Option 1", @"Option 2", @"Option 3"];
    
    beforeAll(^{
        viewModel = [EHIFormFieldDropdownViewModel new];
        viewModel.title = title;
        viewModel.placeholder = placeholder;
        viewModel.options = options;
    });
    
    it(@"should have empty defaults", ^{
        expect(viewModel.inputValue).to.beNil();
        expect(viewModel.selectedOption).to.equal(EHIFormFieldDropdownValueNone);
    });
    
    it(@"should provide the input title", ^{
        expect(viewModel.title).to.equal(title);
    });
    
    it(@"should provide the input placeholder", ^{
        expect(viewModel.placeholder).to.equal(placeholder);
    });
    
    context(@"on option selection", ^{
        
        const NSUInteger selection = 2;
        
        beforeAll(^{
            viewModel.selectedOption = selection;
        });
        
        it(@"should have an input value corresponding to selection", ^{
            expect(viewModel.inputValue).to.equal(options[selection]);
        });
    });
});

SpecEnd
