//
//  EHIFormFieldDropdownViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 5/4/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIFormFieldViewModel.h"

#define EHIFormFieldDropdownValueNone NSUIntegerMax

@interface EHIFormFieldDropdownViewModel : EHIFormFieldViewModel <MTRReactive>

/** The value held by this input field */
@property (strong, nonatomic) NSString *inputValue;
/** Placeholder text for embedded text field when @c inputValue is nil */
@property (copy  , nonatomic) NSString *placeholder;
/** The selectable categories shown in the dropdown */
@property (copy  , nonatomic) NSArray *options;
/** Index of selected option corresponding to @c options */
@property (assign, nonatomic) NSUInteger selectedOption;

@end
