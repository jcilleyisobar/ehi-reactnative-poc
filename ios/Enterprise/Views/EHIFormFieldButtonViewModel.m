//
//  EHIFormFieldButtonViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 6/16/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIFormFieldButtonViewModel.h"
#import "EHIViewModel_Subclass.h"

@implementation EHIFormFieldButtonViewModel

- (void)performButtonAction
{
    [self.delegate formFieldViewModelButtonTapped:self];
}

- (EHIFormFieldType)type
{
    return EHIFormFieldTypeButton;
}

- (id)inputValue
{
    return nil;
}

- (BOOL)isUneditable
{
    return YES;
}

# pragma mark - Validation

- (BOOL)validate:(BOOL)showErrors
{
    return YES;
}

@end
