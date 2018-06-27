//
//  EHIFormFieldDateViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 5/4/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIFormFieldDateViewModel.h"

#define EHIFormFieldDateTemplate @"yyyy MM dd"

@implementation EHIFormFieldDateViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _pickerMode = UIDatePickerModeDate;
        _placeholder = [[NSDate dateWithTimeIntervalSince1970:0] ehi_stringForTemplate:EHIFormFieldDateTemplate];
    }
    return self;
}

# pragma mark - Accessors

- (EHIFormFieldType)type
{
    return EHIFormFieldTypeDate;
}

# pragma mark - Setters

- (void)setInputValue:(id)inputValue
{
    [super setInputValue:inputValue];
    
    if([inputValue isKindOfClass:NSDate.class]) {
        self.dateString = [inputValue ehi_stringForTemplate:EHIFormFieldDateTemplate];
    } else {
        self.dateString = (NSString *)inputValue;
    }
}

- (void)setMinimumDate:(NSDate *)minimumDate
{
    _minimumDate = minimumDate;
    
    if([self.inputValue ehi_isBefore:_minimumDate]) {
        self.inputValue = nil;
    }
}

- (void)setMaximumDate:(NSDate *)maximumDate
{
    _maximumDate = maximumDate;
    
    if([self.inputValue ehi_isAfter:_maximumDate]) {
        self.inputValue = nil;
    }
}

@end
