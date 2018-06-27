//
//  EHIFormFieldViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 5/1/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIFormFieldViewModel.h"

@interface EHIFormFieldViewModel ()
@property (strong, nonatomic) NSMutableArray *validations;
@property (assign, nonatomic) BOOL showsError;
@end

@implementation EHIFormFieldViewModel

# pragma mark - Accessors

- (EHIFormFieldType)type
{
    return EHIFormFieldTypeUnknown;
}

- (BOOL)hidesTitle
{
    return self.title == nil && self.attributedTitle == nil;
}

- (BOOL)hidesSubtitle
{
    return self.subtitle == nil;
}

- (NSString *)title
{
    [self appendRequiredIfNeeded];
    
    return _title;
}

# pragma mark - Setters

- (void)setInputValue:(id)inputValue
{
    _inputValue = inputValue;
    
    // invalidate error state
    [self setShowsError:NO];
    
    // notify delegate
    [_delegate formFieldViewModelDidChangeValue:self];
}

- (void)setIsRequired:(BOOL)isRequiredInfo
{
    _isRequired = isRequiredInfo;
    
    [self appendRequiredIfNeeded];
}

# pragma mark - Validations

- (NSMutableArray *)validations
{
    if(!_validations) {
        _validations = [NSMutableArray new];
    }
    
    return _validations;
}

- (BOOL)validate
{
    return [self validate:NO];
}

- (BOOL)validate:(BOOL)showErrors
{
    for(EHIFormFieldValidation validation in self.validations) {
        BOOL valid = validation(self.inputValue);
        
        // stop as soon as a validation fails
        if(!valid) {
            self.showsError = showErrors ?: self.showsError;
            
            return NO;
        }
    }
    
    return YES;
}

- (void)validates:(EHIFormFieldValidation)validation
{
    NSAssert(validation, @"Form field validation cannot be nil");
    
    [self.validations addObject:validation];
}

- (void)clearValidations
{
    [self.validations removeAllObjects];
}

- (void)appendRequiredIfNeeded
{
    BOOL shoudlAppend = _title
                    && _isRequired
                    && ![[_title ehi_last:1] isEqualToString:@"*"];
    
    if(shoudlAppend) {
        _title = [_title ehi_appendComponent:@" *"];
    }
}

@end
