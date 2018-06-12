//
//  EHIFormFieldDateMonthYearViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 5/7/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIFormFieldViewModel.h"

typedef NS_ENUM(NSUInteger, EHIFormFieldDateMonthYearPickerComponent) {
    EHIFormFieldDateMonthYearPickerComponentMonth,
    EHIFormFieldDateMonthYearPickerComponentYear
};

@interface EHIFormFieldDateMonthYearViewModel : EHIFormFieldViewModel <MTRReactive>

/** The value held by this input field */
@property (strong, nonatomic) NSDate *inputValue;
/** Month text displayed to user */
@property (copy, nonatomic) NSString *monthText;
/** Placeholder text when @c monthText is @c nil */
@property (copy, nonatomic) NSString *monthPlaceholder;
/** Year text displayed to user */
@property (copy, nonatomic) NSString *yearText;
/** Placeholder text when @c yearText is @c nil */
@property (copy, nonatomic) NSString *yearPlaceholder;

/** The amount of rows to be shown within a picker representing the date @c component */
- (NSInteger)numberOfRowsInComponent:(EHIFormFieldDateMonthYearPickerComponent)component;
/** The title to be shown at a given picker @c row within the given date @c component */
- (NSString *)titleForRow:(NSInteger)row inComponent:(EHIFormFieldDateMonthYearPickerComponent)component;
/** Called when a @c row in the given date @c component is selected */
- (void)didSelectRow:(NSInteger)row inComponent:(EHIFormFieldDateMonthYearPickerComponent)component;

@end
