//
//  EHIFormFieldValidations.m
//  Enterprise
//
//  Created by Alex Koller on 5/14/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIFormFieldValidations.h"

# pragma mark - Text Validations

EHIFormFieldValidation EHIFormFieldValidationNotEmpty = ^BOOL(id inputValue) {
    return inputValue != nil;
};

EHIFormFieldValidation EHIFormFieldValidationNotEmptyOrSpaces = ^BOOL(id inputValue) {
    if([inputValue isKindOfClass:[NSString class]]) {
        return [inputValue stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]].length > 0;
    }
    
    return inputValue != nil;
};

EHIFormFieldValidation EHIFormFieldValidationAtSymbol = ^BOOL(NSString *inputValue) {
    return [inputValue rangeOfString:@"@"].location != NSNotFound;
};

# pragma mark - Date Validations

EHIFormFieldValidation EHIFormFieldValidationIsPast = ^BOOL(NSDate *date) {
    return [date ehi_isBefore:[NSDate date]];
};

EHIFormFieldValidation EHIFormFieldValidationIsFuture = ^BOOL(NSDate *date) {
    return [date ehi_isAfter:[NSDate date]];
};