//
//  EHIFormFieldDateMonthYearViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 5/7/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIFormFieldDateMonthYearViewModel.h"

@interface EHIFormFieldDateMonthYearViewModel ()
@property (strong, nonatomic) NSArray  *monthValues;
@property (strong, nonatomic) NSArray *yearValues;
@end

@implementation EHIFormFieldDateMonthYearViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _monthPlaceholder = EHILocalizedString(@"form_field_date_month_placeholder", @"MM", @"");
        _yearPlaceholder = EHILocalizedString(@"form_field_date_year_placeholder", @"YY", @"");
    }
    return self;
}

# pragma mark - Setters

- (void)setInputValue:(NSDate *)inputValue
{
    [super setInputValue:inputValue];
    
    if(inputValue) {
        NSDateComponents *components = [[NSCalendar currentCalendar] components:NSCalendarUnitMonth|NSCalendarUnitYear fromDate:inputValue];
        self.monthText = [self monthStringForMonth:[components month]];
        self.yearText = [@([components year]) stringValue];
    }
    else {
        self.monthText = nil;
        self.yearText = nil;
    }
}

# pragma mark - Picker View

- (NSInteger)numberOfRowsInComponent:(EHIFormFieldDateMonthYearPickerComponent)component
{
    return component == EHIFormFieldDateMonthYearPickerComponentMonth ? [self.monthValues count] : [self.yearValues count];
}

- (NSString *)titleForRow:(NSInteger)row inComponent:(EHIFormFieldDateMonthYearPickerComponent)component
{
    NSArray *titles = component == EHIFormFieldDateMonthYearPickerComponentMonth ? self.monthValues : self.yearValues;
    
    return titles[row];
}

- (void)didSelectRow:(NSInteger)row inComponent:(EHIFormFieldDateMonthYearPickerComponent)component
{
    NSString *title = [self titleForRow:row inComponent:component];
    
    if(component == EHIFormFieldDateMonthYearPickerComponentMonth) {
        self.monthText = title;
    } else {
        self.yearText = title;
    }
    
    // update the input value if both month and year are set
    if(self.monthText && self.yearText) {
        NSDateComponents *components = [[NSDateComponents alloc] init];
        [components setMonth:[self.monthText integerValue]];
        [components setYear:[self.yearText integerValue]];
        self.inputValue = [[NSCalendar currentCalendar] dateFromComponents:components];
    }
}

# pragma mark - Accessors

- (EHIFormFieldType)type
{
    return EHIFormFieldTypeDateMonthYear;
}

- (NSArray *)monthValues
{
    if(_monthValues) {
        return _monthValues;
    }
    
    _monthValues = @(1).upTo(12).map(^(NSNumber *month) {
        return [self monthStringForMonth:month.integerValue];
    });
    
    return _monthValues;
}

- (NSArray *)yearValues
{
    if(_yearValues) {
        return _yearValues;
    }
    
    // populate array with 10 years starting with current year
    NSInteger currentYear = [[NSCalendar currentCalendar] component:NSCalendarUnitYear fromDate:[NSDate date]];
    _yearValues = @(currentYear).upTo(currentYear + 10).map(^(NSNumber *year) {
        return [year stringValue];
    });
    
    return _yearValues;
}

//
// Helpers
//

- (NSString *)monthStringForMonth:(NSInteger)month
{
    // always 2 digits with 0 as padding
    return [NSString stringWithFormat:@"%02d", (int)month];
}

@end
