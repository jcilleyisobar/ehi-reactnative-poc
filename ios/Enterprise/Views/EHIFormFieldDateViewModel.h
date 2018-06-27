//
//  EHIFormFieldDateViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 5/4/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIFormFieldViewModel.h"

@interface EHIFormFieldDateViewModel : EHIFormFieldViewModel <MTRReactive>

/** String version of @c inputValue shown to user */
@property (copy  , nonatomic) NSString *dateString;
/** Placeholder text for embedded text field when @c inputValue is nil */
@property (copy  , nonatomic) NSString *placeholder;
/** The mode used by the presented @c UIDatePicker */
@property (assign, nonatomic) UIDatePickerMode pickerMode;
/** Minimum date allowed to be entered into this field */
@property (copy  , nonatomic) NSDate *minimumDate;
/** Maximum date allowed to be entered into this field */
@property (copy  , nonatomic) NSDate *maximumDate;

@end
