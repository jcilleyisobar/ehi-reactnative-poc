//
//  EHIFormFieldTextViewViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 6/3/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIFormFieldViewModel.h"

#define EHIFormFieldTextViewNoMaxLength 0

@interface EHIFormFieldTextViewViewModel : EHIFormFieldViewModel <MTRReactive>

/** The value held by this input field */
@property (strong, nonatomic) NSString *inputValue;
/** Placeholder text for embedded text field when @c inputValue is @c nil */
@property (copy  , nonatomic) NSString *placeholder;
/** Type of keyboard to display when user is entering text */
@property (assign, nonatomic) UIKeyboardType keyboardType;
/** The maximum length of text allowed to be entered */
@property (assign, nonatomic) NSUInteger maxLength;

@end
