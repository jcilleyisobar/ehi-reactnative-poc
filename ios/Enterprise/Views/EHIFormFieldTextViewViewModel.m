//
//  EHIFormFieldTextViewViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 6/3/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIFormFieldTextViewViewModel.h"

@implementation EHIFormFieldTextViewViewModel

# pragma mark - Accessors

- (EHIFormFieldType)type
{
    return EHIFormFieldTypeTextView;
}

- (NSString *)inputValue
{
    NSString *value = [super inputValue];
    
    // filter empty strings to nil
    if(value.length == 0) {
        return nil;
    }
    
    return value;
}

# pragma mark - Setters

- (void)setInputValue:(NSString *)inputValue
{
    // apply max length
    if(self.maxLength != EHIFormFieldTextViewNoMaxLength && inputValue.length > self.maxLength) {
        return;
    }
    
    [super setInputValue:inputValue];
}

@end
