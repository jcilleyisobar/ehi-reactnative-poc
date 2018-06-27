//
//  EHIFormFieldTextViewModelTests.m
//  Enterprise
//
//  Created by Alex Koller on 5/4/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHIFormFieldTextViewModel.h"

SpecBegin(EHIFormFieldTextViewModelTests)

describe(@"the text field form", ^{
    
    __block EHIFormFieldTextViewModel *viewModel;
    NSString *title = @"Default Title";
    NSString *placeholder = @"Default Placeholder";
    EHIFormFieldValidation validation1 = ^BOOL(NSString *input) {
        return input.length != 0;
    };
    
    EHIFormFieldValidation validation2 = ^BOOL(NSString *input) {
        return input ? !NSRangeIsNull([input rangeOfString:@"@"]) : NO;
    };
    
    beforeAll(^{
        viewModel = [EHIFormFieldTextViewModel new];
        viewModel.title = title;
        viewModel.placeholder = placeholder;
        [viewModel validates:validation1];
        [viewModel validates:validation2];
    });
    
    it(@"should have no default input", ^{
//        expect(viewModel.inputValue).to.beNil();
    });
    
    it(@"should default to not allow category selection", ^{
        expect(viewModel.allowsCategorySelection).to.beFalsy();
    });
    
    it(@"should provide the input title", ^{
        expect(viewModel.title).to.equal(title);
    });
    
    it(@"should provide the input placeholder", ^{
//        expect(viewModel.placeholder).to.equal(placeholder);
    });
    
    context(@"when it allows category selection", ^{
        
        NSArray *categories = @[@"Category 1", @"Category 2", @"Category 3"];
        NSArray *placeholders = @[@"This is cat 1", @"This is cat 2", @"This is cat 3"];
        
        beforeAll(^{
            viewModel.categoryOptions = categories;
            viewModel.categoryOptionPlaceholders = placeholders;
        });
        
        it(@"should default to the first category", ^{
//            expect(viewModel.selectedCategory).to.equal(@0);
//            expect(viewModel.selectedCategoryName).to.equal(categories[0]);
        });
        
        it(@"should use category placeholders", ^{
//            expect(viewModel.placeholder).to.equal(placeholders[0]);
        });
        
        context(@"when changing categories", ^{
            
            const NSUInteger selection = 1;
            
            beforeAll(^{
                viewModel.selectedCategory = selection;
            });
            
            it(@"should change placeholders", ^{
                expect(viewModel.placeholder).to.equal(placeholders[selection]);
            });
            
            it(@"should change display names", ^{
                expect(viewModel.selectedCategoryName).to.equal(categories[selection]);
            });
        });
    });
    
    context(@"when given valid input", ^{
        
        it(@"should pass validations", ^{
            viewModel.inputValue = @"user@example.org";
            expect([viewModel validate]).to.beTruthy();
        });
    });
    
    context(@"when given invalid input", ^{
        
        it(@"should fail validations", ^{
            viewModel.inputValue = nil;
            expect([viewModel validate]).to.beFalsy();
            viewModel.inputValue = @"";
            expect([viewModel validate]).to.beFalsy();
            viewModel.inputValue = @"invalid input";
            expect([viewModel validate]).to.beFalsy();
        });
    });
    
});

SpecEnd
