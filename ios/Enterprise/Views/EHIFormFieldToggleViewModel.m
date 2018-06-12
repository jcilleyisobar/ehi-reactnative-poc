//
//  EHIFormFieldCheckboxViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 6/2/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIFormFieldToggleViewModel.h"

NS_ASSUME_NONNULL_BEGIN

@implementation EHIFormFieldToggleViewModel

+ (instancetype)toggleFieldWithTitle:(NSString *)title input:(BOOL)input
{
    EHIFormFieldToggleViewModel *viewModel = [EHIFormFieldToggleViewModel new];
    viewModel.title = title;
    viewModel.toggleValue = input;
    viewModel.isLastInGroup = YES;
    
    return viewModel;
}

# pragma mark - Accessors

- (EHIFormFieldType)type
{
    return EHIFormFieldTypeToggle;
}

- (BOOL)isUneditable
{
    return YES;
}

- (BOOL)toggleValue
{
    return [self.inputValue boolValue];
}

# pragma mark - Setters

-(void)setToggleValue:(BOOL)toggleValue
{
    self.inputValue = @(toggleValue);
}

# pragma mark - Validation

- (BOOL)validate:(BOOL)showErrors
{
    return YES;
}

@end

NS_ASSUME_NONNULL_END