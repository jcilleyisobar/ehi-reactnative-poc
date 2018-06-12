//
//  EHIFormFieldActionButtonViewModel.m
//  Enterprise
//
//  Created by Rafael Machado on 8/4/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIFormFieldActionButtonViewModel.h"

@implementation EHIFormFieldActionButtonViewModel

- (EHIFormFieldType)type
{
    return EHIFormFieldTypeActionButton;
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
