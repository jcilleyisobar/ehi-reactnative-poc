//
//  EHIFormFieldDropdownViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 5/4/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIFormFieldDropdownViewModel.h"

@implementation EHIFormFieldDropdownViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _selectedOption = EHIFormFieldDropdownValueNone;
        
        if ([model isKindOfClass:[NSArray class]]) {
            self.options = model;
        }
    }
    return self;
}

# pragma mark - Accessors

- (EHIFormFieldType)type
{
    return EHIFormFieldTypeDropdown;
}

# pragma mark - Setters

- (void)setInputValue:(NSString *)inputValue
{
    [super setInputValue:inputValue];
    
    // update internal selection
    NSUInteger selection = (self.options ?: @[]).indexOf(inputValue);
    self.selectedOption = selection != NSNotFound ? selection : EHIFormFieldDropdownValueNone;
}

- (void)setSelectedOption:(NSUInteger)selectedOption
{
    // filter redundant updates
    if(_selectedOption == selectedOption) {
        return;
    }
    
    _selectedOption = selectedOption;
    
    // clear input if no options or no value
    self.inputValue = self.options.count && _selectedOption != EHIFormFieldDropdownValueNone
        ? [self.options ehi_safelyAccess:_selectedOption]
        : nil;
}

@end
